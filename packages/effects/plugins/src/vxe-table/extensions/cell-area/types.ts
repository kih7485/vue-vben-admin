import type { VxeTableDefines } from 'vxe-table';

/**
 * 셀 선택 정보
 */
export interface CellSelection {
  startRow: number;
  startCol: number;
  endRow: number;
  endCol: number;
}

/**
 * 셀 범위 정보
 */
export interface CellRange {
  rows: number[];
  cols: number[];
  cells: CellData[];
}

/**
 * 셀 데이터
 */
export interface CellData {
  row: number;
  col: number;
  value: any;
  rowData?: any;
  column?: VxeTableDefines.ColumnInfo;
}

/**
 * 방향
 */
export type Direction = 'down' | 'left' | 'right' | 'up';

/**
 * 드래그 상태
 */
export interface DragState {
  isDragging: boolean;
  currentCell: { col: number; row: number } | null;
  startCell: { col: number; row: number } | null;
}

/**
 * Fill Handle 상태
 */
export interface FillState {
  isFilling: boolean;
  sourceRange: CellSelection | null;
  targetRange: CellSelection | null;
}

/**
 * Cell Area 설정
 */
export interface CellAreaOptions {
  /** 셀 선택 활성화 */
  enableSelection?: boolean;
  /** 복사/붙여넣기 활성화 */
  enableClipboard?: boolean;
  /** 드래그 필 활성화 */
  enableFillHandle?: boolean;
  /** 키보드 네비게이션 활성화 */
  enableKeyboard?: boolean;
  /** 읽기 전용 컬럼 필터 */
  readonlyColumns?: string[];
  /** 선택 영역 스타일 커스터마이징 */
  selectionStyle?: {
    backgroundColor?: string;
    borderColor?: string;
    fillHandleColor?: string;
  };
}

/**
 * Cell Area Manager 인터페이스
 */
export interface ICellAreaManager {
  /** 초기화 */
  init(): void;
  /** 셀 선택 */
  selectCell(row: number, col: number): void;
  /** 범위 선택 */
  selectRange(
    startRow: number,
    startCol: number,
    endRow: number,
    endCol: number,
  ): void;
  /** 선택 영역 복사 */
  copySelection(): Promise<void>;
  /** 클립보드에서 붙여넣기 */
  pasteFromClipboard(): Promise<void>;
  /** Fill Handle 드래그 시작 */
  startFillDrag(row: number, col: number): void;
  /** Fill Handle 드래그 업데이트 */
  updateFillRange(row: number, col: number): void;
  /** Fill Handle 드래그 종료 */
  endFillDrag(): void;
  /** 선택 이동 */
  moveSelection(direction: Direction, extend: boolean): void;
  /** 선택 해제 */
  clearSelection(): void;
  /** 현재 선택 영역 가져오기 */
  getSelection(): CellSelection | null;
  /** 정리 */
  destroy(): void;
}

/**
 * 클립보드 데이터 형식
 */
export interface ClipboardData {
  text: string;
  html?: string;
  rows: string[][];
}

/**
 * 셀 업데이트 정보
 */
export interface CellUpdate {
  row: number;
  col: number;
  field: string;
  value: any;
}
