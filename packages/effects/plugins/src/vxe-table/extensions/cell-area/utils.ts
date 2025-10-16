import type { VxeGridInstance } from 'vxe-table';

import type {
  CellData,
  CellRange,
  CellSelection,
  ClipboardData,
} from './types';

/**
 * 정규화된 셀 선택 범위 반환 (시작/끝 정렬)
 */
export function normalizeSelection(selection: CellSelection): CellSelection {
  return {
    startRow: Math.min(selection.startRow, selection.endRow),
    startCol: Math.min(selection.startCol, selection.endCol),
    endRow: Math.max(selection.startRow, selection.endRow),
    endCol: Math.max(selection.startCol, selection.endCol),
  };
}

/**
 * 선택 영역의 모든 셀 데이터 추출
 */
export function getCellsInRange(
  grid: VxeGridInstance,
  selection: CellSelection,
): CellRange {
  const normalized = normalizeSelection(selection);
  const { startRow, startCol, endRow, endCol } = normalized;

  const rows: number[] = [];
  const cols: number[] = [];
  const cells: CellData[] = [];

  const tableData = grid.getTableData().fullData;
  const columns = grid.getColumns();

  for (let row = startRow; row <= endRow; row++) {
    if (!rows.includes(row)) {
      rows.push(row);
    }

    for (let col = startCol; col <= endCol; col++) {
      if (!cols.includes(col)) {
        cols.push(col);
      }

      const rowData = tableData[row];
      const column = columns[col];

      if (rowData && column) {
        const value = rowData[column.field];
        cells.push({
          row,
          col,
          value,
          rowData,
          column,
        });
      }
    }
  }

  return { rows, cols, cells };
}

/**
 * TSV 형식으로 데이터 변환
 */
export function convertToTSV(data: any[][]): string {
  return data.map((row) => row.join('\t')).join('\n');
}

/**
 * TSV 파싱하여 2D 배열 변환
 */
export function parseTSV(tsv: string): string[][] {
  return tsv.split('\n').map((row) => row.split('\t'));
}

/**
 * 클립보드 데이터 생성
 */
export function createClipboardData(
  grid: VxeGridInstance,
  selection: CellSelection,
): ClipboardData {
  const normalized = normalizeSelection(selection);
  const cellRange = getCellsInRange(grid, normalized);

  const rows: string[][] = [];
  const rowCount = normalized.endRow - normalized.startRow + 1;

  for (let i = 0; i < rowCount; i++) {
    rows.push([]);
  }

  cellRange.cells.forEach((cell) => {
    const rowIndex = cell.row - normalized.startRow;
    const value = cell.value ?? '';
    rows[rowIndex]?.push(String(value));
  });

  const text = convertToTSV(rows);

  return {
    text,
    rows,
  };
}

/**
 * 컬럼이 읽기 전용인지 확인
 */
export function isColumnReadonly(
  column: any,
  readonlyColumns: string[] = [],
): boolean {
  if (!column || !column.field) return false;
  return (
    column.editRender === null ||
    column.editRender === undefined ||
    readonlyColumns.includes(column.field)
  );
}

/**
 * 두 선택 영역이 동일한지 확인
 */
export function isSameSelection(
  a: CellSelection | null,
  b: CellSelection | null,
): boolean {
  if (!a || !b) return false;
  return (
    a.startRow === b.startRow &&
    a.startCol === b.startCol &&
    a.endRow === b.endRow &&
    a.endCol === b.endCol
  );
}

/**
 * 숫자 시퀀스 패턴 감지
 */
export function detectNumberSequence(values: any[]): {
  isSequence: boolean;
  step: number;
} {
  if (values.length < 2) return { isSequence: false, step: 0 };

  const numbers = values
    .map((v) => Number.parseFloat(v))
    .filter((n) => !Number.isNaN(n));

  if (numbers.length < 2) return { isSequence: false, step: 0 };

  const step = numbers[1] - numbers[0];

  for (let i = 2; i < numbers.length; i++) {
    if (
      Math.abs((numbers[i] ?? 0) - (numbers[i - 1] ?? 0) - step) >
      0.0001
    ) {
      return { isSequence: false, step: 0 };
    }
  }

  return { isSequence: true, step };
}

/**
 * 날짜 패턴 감지
 */
export function detectDateSequence(values: any[]): {
  dayStep: number;
  isDateSequence: boolean;
} {
  if (values.length < 2) return { isDateSequence: false, dayStep: 0 };

  const dates = values
    .map((v) => {
      const date = new Date(v);
      return Number.isNaN(date.getTime()) ? null : date;
    })
    .filter((d) => d !== null) as Date[];

  if (dates.length < 2) return { dayStep: 0, isDateSequence: false };

  const dayStep = Math.round(
    ((dates[1]?.getTime() ?? 0) - (dates[0]?.getTime() ?? 0)) /
      (1000 * 60 * 60 * 24),
  );

  for (let i = 2; i < dates.length; i++) {
    const currentStep = Math.round(
      ((dates[i]?.getTime() ?? 0) - (dates[i - 1]?.getTime() ?? 0)) /
        (1000 * 60 * 60 * 24),
    );
    if (currentStep !== dayStep) {
      return { dayStep: 0, isDateSequence: false };
    }
  }

  return { dayStep, isDateSequence: true };
}

/**
 * Fill 값 생성
 */
export function generateFillValues(
  sourceValues: any[],
  targetCount: number,
): any[] {
  if (sourceValues.length === 0) return [];

  // 숫자 시퀀스 체크
  const numberSeq = detectNumberSequence(sourceValues);
  if (numberSeq.isSequence) {
    const lastValue = Number.parseFloat(sourceValues[sourceValues.length - 1]);
    const result: any[] = [];
    for (let i = 0; i < targetCount; i++) {
      result.push(lastValue + numberSeq.step * (i + 1));
    }
    return result;
  }

  // 날짜 시퀀스 체크
  const dateSeq = detectDateSequence(sourceValues);
  if (dateSeq.isDateSequence) {
    const lastDate = new Date(sourceValues[sourceValues.length - 1]);
    const result: any[] = [];
    for (let i = 0; i < targetCount; i++) {
      const newDate = new Date(lastDate);
      newDate.setDate(lastDate.getDate() + dateSeq.dayStep * (i + 1));
      result.push(newDate.toISOString().split('T')[0]);
    }
    return result;
  }

  // 기본: 단순 반복
  const result: any[] = [];
  for (let i = 0; i < targetCount; i++) {
    result.push(sourceValues[i % sourceValues.length]);
  }
  return result;
}
