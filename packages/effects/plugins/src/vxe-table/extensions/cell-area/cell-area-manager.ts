/* cSpell:ignore colid rowid */
import type { VxeGridInstance } from 'vxe-table';

import type {
  CellAreaOptions,
  CellSelection,
  Direction,
  ICellAreaManager,
} from './types';

const DEFAULT_OPTIONS: CellAreaOptions = {
  enableClipboard: true,
  enableFillHandle: false,
  enableKeyboard: true,
  enableSelection: true,
  readonlyColumns: [],
  selectionStyle: {
    backgroundColor: 'rgba(24, 144, 255, 0.1)',
    borderColor: '#1890ff',
    fillHandleColor: '#1890ff',
  },
};

export class CellAreaManager implements ICellAreaManager {
  private boundHandlers: Map<string, EventListener> = new Map();
  private copyAnimationTimeout: null | number = null;
  private grid: VxeGridInstance;
  private isSelecting = false;
  private options: CellAreaOptions;
  private overlayElement: HTMLElement | null = null;
  private selection: CellSelection | null = null;
  private startCell: null | { col: number; row: number } = null;

  constructor(grid: VxeGridInstance, options: CellAreaOptions = {}) {
    this.grid = grid;
    this.options = { ...DEFAULT_OPTIONS, ...options };
  }

  clearSelection(): void {
    this.selection = null;
    this.updateOverlay();
  }

  async copySelection(): Promise<void> {
    if (!this.selection) return;

    // Normalize selection
    const startRow = Math.min(this.selection.startRow, this.selection.endRow);
    const endRow = Math.max(this.selection.startRow, this.selection.endRow);
    const startCol = Math.min(this.selection.startCol, this.selection.endCol);
    const endCol = Math.max(this.selection.startCol, this.selection.endCol);

    const tableData = this.grid.getTableData().fullData;
    const columns = this.grid.getColumns();

    console.warn('[CellAreaManager] copySelection:', {
      columns: columns.length,
      endCol,
      endRow,
      startCol,
      startRow,
      tableData: tableData.length,
    });

    // Build TSV format data
    const rows: string[] = [];
    for (let row = startRow; row <= endRow; row++) {
      const rowData = tableData[row];
      if (!rowData) {
        console.warn('[CellAreaManager] No data for row:', row);
        continue;
      }

      const cells: string[] = [];
      for (let col = startCol; col <= endCol; col++) {
        const column = columns[col];
        if (!column || !column.field) {
          console.warn('[CellAreaManager] No column for col:', col);
          continue;
        }

        const value = rowData[column.field] ?? '';
        cells.push(String(value));
      }
      rows.push(cells.join('\t'));
    }

    const text = rows.join('\n');

    console.warn('[CellAreaManager] Copy text:', text);
    console.warn('[CellAreaManager] Copy text length:', text.length);

    try {
      await navigator.clipboard.writeText(text);
      console.warn('[CellAreaManager] Copied to clipboard successfully');

      // Show copy animation
      this.showCopyAnimation();
    } catch (error) {
      console.error('[CellAreaManager] Copy failed:', error);
    }
  }

  destroy(): void {
    this.unbindAllEvents();
    this.removeOverlay();
    this.selection = null;
  }

  endFillDrag(): void {
    // Fill drag not implemented yet
  }

  getSelection(): CellSelection | null {
    return this.selection;
  }

  moveSelection(direction: Direction, extend: boolean): void {
    if (!this.selection) return;

    const { endCol, endRow, startCol, startRow } = this.selection;
    const tableData = this.grid.getTableData().fullData;
    const columns = this.grid.getColumns();

    let newRow = endRow;
    let newCol = endCol;

    switch (direction) {
      case 'down': {
        newRow = Math.min(tableData.length - 1, endRow + 1);
        break;
      }
      case 'left': {
        newCol = Math.max(0, endCol - 1);
        break;
      }
      case 'right': {
        newCol = Math.min(columns.length - 1, endCol + 1);
        break;
      }
      case 'up': {
        newRow = Math.max(0, endRow - 1);
        break;
      }
    }

    if (extend) {
      this.selectRange(startRow, startCol, newRow, newCol);
    } else {
      this.selectCell(newRow, newCol);
    }
  }

  init(): void {
    console.warn('[CellAreaManager] Initializing...', {
      grid: this.grid,
      gridElement: this.grid.$el,
    });

    if (this.options.enableSelection) {
      this.setupMouseSelection();
    }

    if (this.options.enableKeyboard) {
      this.setupKeyboardNavigation();
    }

    if (this.options.enableClipboard) {
      this.setupClipboard();
    }

    this.createOverlay();
    console.warn('[CellAreaManager] Initialization complete');
  }

  async pasteFromClipboard(): Promise<void> {
    if (!this.selection) return;

    try {
      const text = await navigator.clipboard.readText();
      const rows = text.split('\n').map((row) => row.split('\t'));

      const { startCol, startRow } = this.selection;
      const tableData = this.grid.getTableData().fullData;
      const columns = this.grid.getColumns();

      for (const [i, row] of rows.entries()) {
        const targetRow = startRow + i;
        if (targetRow >= tableData.length) break;

        const rowData = tableData[targetRow];
        for (const [j, value] of row.entries()) {
          const targetCol = startCol + j;
          if (targetCol >= columns.length) break;

          const column = columns[targetCol];
          if (column?.field) {
            rowData[column.field] = value;
          }
        }
      }

      await this.grid.reloadData(tableData);
      console.warn('[CellAreaManager] Paste complete');
    } catch (error) {
      console.error('[CellAreaManager] Paste failed:', error);
    }
  }

  selectCell(row: number, col: number): void {
    this.selection = {
      endCol: col,
      endRow: row,
      startCol: col,
      startRow: row,
    };
    this.updateOverlay();
  }

  selectRange(
    startRow: number,
    startCol: number,
    endRow: number,
    endCol: number,
  ): void {
    this.selection = {
      endCol,
      endRow,
      startCol,
      startRow,
    };
    this.updateOverlay();
  }

  startFillDrag(_row: number, _col: number): void {
    // Fill drag not implemented yet
  }

  updateFillRange(_row: number, _col: number): void {
    // Fill drag not implemented yet
  }

  private createOverlay(): void {
    const gridElement = this.grid.$el as HTMLElement;
    if (!gridElement) return;

    const bodyWrapper = gridElement.querySelector('.vxe-table--body-wrapper');
    if (!bodyWrapper) return;

    this.overlayElement = document.createElement('div');
    this.overlayElement.className = 'vxe-cell-area-selection-overlay';
    this.overlayElement.style.cssText = `
      position: absolute;
      pointer-events: none;
      z-index: 10;
      border: 2px solid ${this.options.selectionStyle?.borderColor || '#1890ff'};
      background-color: ${this.options.selectionStyle?.backgroundColor || 'rgba(24, 144, 255, 0.1)'};
      display: none;
    `;

    bodyWrapper.append(this.overlayElement);
  }

  private getCellPosition(
    element: HTMLElement,
  ): null | { col: number; row: number } {
    // Find td element
    const td = element.closest('td.vxe-body--column');
    if (!td) return null;

    // Find tr element
    const tr = td.closest('tr.vxe-body--row');
    if (!tr) return null;

    // Get colid and rowid for storing
    const colid = td.getAttribute('colid');
    const rowid = tr.getAttribute('rowid');

    if (!colid || !rowid) return null;

    // Get actual array index by counting siblings
    const tbody = tr.parentElement;
    if (!tbody) return null;

    const allRows = [
      ...tbody.querySelectorAll('tr.vxe-body--row'),
    ] as HTMLElement[];
    const rowIndex = allRows.indexOf(tr as HTMLElement);

    const allCells = [
      ...tr.querySelectorAll('td.vxe-body--column'),
    ] as HTMLElement[];
    const colIndex = allCells.indexOf(td as HTMLElement);

    if (rowIndex === -1 || colIndex === -1) return null;

    console.warn('[CellAreaManager] getCellPosition:', {
      colIndex,
      colid,
      rowIndex,
      rowid,
    });

    return { col: colIndex, row: rowIndex };
  }

  private getSelectionRect(
    selection: CellSelection,
  ): null | { height: number; left: number; top: number; width: number } {
    const gridElement = this.grid.$el as HTMLElement;
    if (!gridElement) return null;

    const bodyWrapper = gridElement.querySelector('.vxe-table--body-wrapper');
    if (!bodyWrapper) return null;

    // Normalize selection (ensure start <= end)
    const startRow = Math.min(selection.startRow, selection.endRow);
    const endRow = Math.max(selection.startRow, selection.endRow);
    const startCol = Math.min(selection.startCol, selection.endCol);
    const endCol = Math.max(selection.startCol, selection.endCol);

    // Find cells by array index
    const allRows = [
      ...bodyWrapper.querySelectorAll('tr.vxe-body--row'),
    ] as HTMLElement[];
    const startTr = allRows[startRow];
    const endTr = allRows[endRow];

    console.warn('[CellAreaManager] getSelectionRect - finding rows:', {
      allRowsCount: allRows.length,
      endRow,
      endTr,
      startRow,
      startTr,
    });

    if (!startTr || !endTr) {
      console.error('[CellAreaManager] Could not find start or end row');
      return null;
    }

    const startCells = [
      ...startTr.querySelectorAll('td.vxe-body--column'),
    ] as HTMLElement[];
    const endCells = [
      ...endTr.querySelectorAll('td.vxe-body--column'),
    ] as HTMLElement[];

    const startTd = startCells[startCol];
    const endTd = endCells[endCol];

    console.warn('[CellAreaManager] getSelectionRect - finding cells:', {
      endCellsCount: endCells.length,
      endCol,
      endTd,
      startCellsCount: startCells.length,
      startCol,
      startTd,
    });

    if (!startTd || !endTd) {
      console.error('[CellAreaManager] Could not find start or end cell');
      return null;
    }

    const startRect = startTd.getBoundingClientRect();
    const endRect = endTd.getBoundingClientRect();
    const bodyRect = bodyWrapper.getBoundingClientRect();

    const result = {
      height: endRect.bottom - startRect.top,
      left: startRect.left - bodyRect.left + bodyWrapper.scrollLeft,
      top: startRect.top - bodyRect.top + bodyWrapper.scrollTop,
      width: endRect.right - startRect.left,
    };

    console.warn('[CellAreaManager] getSelectionRect - result:', result);

    return result;
  }

  private removeOverlay(): void {
    if (this.overlayElement) {
      this.overlayElement.remove();
      this.overlayElement = null;
    }
  }

  private setupClipboard(): void {
    const handleCopy = (event: ClipboardEvent) => {
      if (!this.selection) return;

      // Check if we're focused on the grid
      const activeElement = document.activeElement;
      const gridElement = this.grid.$el as HTMLElement;
      if (!gridElement?.contains(activeElement)) return;

      event.preventDefault();
      void this.copySelection();
    };

    const handlePaste = (event: ClipboardEvent) => {
      if (!this.selection) return;

      const activeElement = document.activeElement;
      const gridElement = this.grid.$el as HTMLElement;
      if (!gridElement?.contains(activeElement)) return;

      event.preventDefault();
      void this.pasteFromClipboard();
    };

    document.addEventListener('copy', handleCopy as EventListener);
    document.addEventListener('paste', handlePaste as EventListener);

    this.boundHandlers.set('copy', handleCopy as EventListener);
    this.boundHandlers.set('paste', handlePaste as EventListener);
  }

  private setupKeyboardNavigation(): void {
    const gridElement = this.grid.$el as HTMLElement;
    if (!gridElement) return;

    // Make grid focusable
    const bodyWrapper = gridElement.querySelector(
      '.vxe-table--body-wrapper',
    ) as HTMLElement;
    if (bodyWrapper) {
      bodyWrapper.setAttribute('tabindex', '0');
      bodyWrapper.style.outline = 'none';
    }

    const handleKeydown = (event: KeyboardEvent) => {
      if (!this.selection) return;

      // Check if grid is focused or selection exists
      const activeElement = document.activeElement;
      if (!gridElement.contains(activeElement)) return;

      const { endCol, endRow, startCol, startRow } = this.selection;
      const tableData = this.grid.getTableData().fullData;
      const columns = this.grid.getColumns();

      let newRow = endRow;
      let newCol = endCol;
      let handled = false;

      console.warn('[CellAreaManager] Key pressed:', event.key, {
        current: { col: endCol, row: endRow },
        shiftKey: event.shiftKey,
      });

      switch (event.key) {
        case 'ArrowDown': {
          newRow = Math.min(tableData.length - 1, endRow + 1);
          handled = true;
          break;
        }
        case 'ArrowLeft': {
          newCol = Math.max(0, endCol - 1);
          handled = true;
          break;
        }
        case 'ArrowRight': {
          newCol = Math.min(columns.length - 1, endCol + 1);
          handled = true;
          break;
        }
        case 'ArrowUp': {
          newRow = Math.max(0, endRow - 1);
          handled = true;
          break;
        }
        case 'Enter': {
          newRow = Math.min(tableData.length - 1, endRow + 1);
          handled = true;
          break;
        }
        case 'Tab': {
          newCol = event.shiftKey
            ? Math.max(0, endCol - 1)
            : Math.min(columns.length - 1, endCol + 1);
          handled = true;
          break;
        }
      }

      if (handled) {
        event.preventDefault();
        event.stopPropagation();

        if (
          event.shiftKey &&
          ['ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowUp'].includes(
            event.key,
          )
        ) {
          // Extend selection
          console.warn('[CellAreaManager] Extending selection to:', {
            col: newCol,
            row: newRow,
          });
          this.selectRange(startRow, startCol, newRow, newCol);
        } else {
          // Move selection
          console.warn('[CellAreaManager] Moving selection to:', {
            col: newCol,
            row: newRow,
          });
          this.selectCell(newRow, newCol);
        }
      }
    };

    document.addEventListener('keydown', handleKeydown as EventListener);
    this.boundHandlers.set('keydown', handleKeydown as EventListener);
  }

  private showCopyAnimation(): void {
    if (!this.overlayElement) return;

    // Clear any existing animation timeout
    if (this.copyAnimationTimeout) {
      clearTimeout(this.copyAnimationTimeout);
    }

    // Add dashed border with animation
    this.overlayElement.style.borderStyle = 'dashed';
    this.overlayElement.style.animation =
      'vxe-cell-area-copy-pulse 0.6s ease-in-out';

    // Reset after animation
    this.copyAnimationTimeout = window.setTimeout(() => {
      if (this.overlayElement) {
        this.overlayElement.style.borderStyle = 'solid';
        this.overlayElement.style.animation = '';
      }
      this.copyAnimationTimeout = null;
    }, 2000);
  }

  private setupMouseSelection(): void {
    const gridElement = this.grid.$el as HTMLElement;
    if (!gridElement) {
      console.error('[CellAreaManager] No grid element');
      return;
    }

    const bodyWrapper = gridElement.querySelector('.vxe-table--body-wrapper');
    if (!bodyWrapper) {
      console.error('[CellAreaManager] No body wrapper');
      return;
    }

    console.warn(
      '[CellAreaManager] Setting up mouse selection on:',
      bodyWrapper,
    );

    // Handle mouse down - start selection
    const handleMouseDown = (event: MouseEvent) => {
      const target = event.target as HTMLElement;

      // Ignore if clicking on input elements
      if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA') {
        return;
      }

      const position = this.getCellPosition(target);
      if (!position) return;

      console.warn('[CellAreaManager] Mouse down on cell:', position);

      // Focus the grid to enable keyboard navigation
      if (bodyWrapper instanceof HTMLElement) {
        bodyWrapper.focus();
      }

      // Handle Shift+Click for range extension
      if (event.shiftKey && this.selection) {
        console.warn('[CellAreaManager] Shift+Click - extending selection');
        this.selectRange(
          this.selection.startRow,
          this.selection.startCol,
          position.row,
          position.col,
        );
        event.preventDefault();
        return;
      }

      this.isSelecting = true;
      this.startCell = position;
      this.selectCell(position.row, position.col);

      event.preventDefault();
    };

    // Handle mouse move - extend selection
    const handleMouseMove = (event: MouseEvent) => {
      if (!this.isSelecting || !this.startCell) return;

      const target = event.target as HTMLElement;
      const position = this.getCellPosition(target);

      if (!position) return;

      this.selectRange(
        this.startCell.row,
        this.startCell.col,
        position.row,
        position.col,
      );
    };

    // Handle mouse up - end selection
    const handleMouseUp = () => {
      if (this.isSelecting) {
        console.warn('[CellAreaManager] Selection complete:', this.selection);
      }
      this.isSelecting = false;
      this.startCell = null;
    };

    bodyWrapper.addEventListener('mousedown', handleMouseDown as EventListener);
    bodyWrapper.addEventListener('mousemove', handleMouseMove as EventListener);
    document.addEventListener('mouseup', handleMouseUp as EventListener);

    this.boundHandlers.set('mousedown', handleMouseDown as EventListener);
    this.boundHandlers.set('mousemove', handleMouseMove as EventListener);
    this.boundHandlers.set('mouseup', handleMouseUp as EventListener);
  }

  private unbindAllEvents(): void {
    for (const [name, handler] of this.boundHandlers) {
      if (
        name === 'mouseup' ||
        name === 'keydown' ||
        name === 'copy' ||
        name === 'paste'
      ) {
        document.removeEventListener(name, handler);
      } else {
        const gridElement = this.grid.$el as HTMLElement;
        const bodyWrapper = gridElement?.querySelector(
          '.vxe-table--body-wrapper',
        );
        bodyWrapper?.removeEventListener(name, handler);
      }
    }
    this.boundHandlers.clear();
  }

  private updateOverlay(): void {
    if (!this.overlayElement || !this.selection) {
      if (this.overlayElement) {
        this.overlayElement.style.display = 'none';
      }
      return;
    }

    const rect = this.getSelectionRect(this.selection);
    if (!rect) {
      this.overlayElement.style.display = 'none';
      return;
    }

    this.overlayElement.style.display = 'block';
    this.overlayElement.style.left = `${rect.left}px`;
    this.overlayElement.style.top = `${rect.top}px`;
    this.overlayElement.style.width = `${rect.width}px`;
    this.overlayElement.style.height = `${rect.height}px`;

    console.warn('[CellAreaManager] Overlay updated:', rect);
  }
}
