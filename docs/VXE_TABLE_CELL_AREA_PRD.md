# VXE Table Cell Area Extension PRD
## 제품 요구사항 정의서 (Product Requirements Document)

### 📋 문서 정보
- **작성일**: 2025-10-16
- **프로젝트**: Vue Vben Admin - VXE Table Cell Area Extension
- **버전**: 1.0.0
- **목적**: VXE Table에 Excel-like 셀 영역 선택, 복사/붙여넣기, 드래그 필 기능 커스텀 구현

---

## 1. 프로젝트 개요

### 1.1 배경
- Vue Vben Admin 프로젝트는 VXE Table을 사용하여 그리드 기능을 제공
- VXE Table의 `extendCellArea` 플러그인은 유료이므로 커스텀 구현 필요
- Excel과 유사한 사용자 경험을 제공하여 생산성 향상

### 1.2 목표
Excel과 유사한 셀 조작 기능을 VXE Table에 통합하여 사용자 편의성 극대화

### 1.3 범위
**구현 범위 (In Scope)**:
- 셀 영역 선택 (단일 셀, 범위 셀)
- 키보드 네비게이션 (방향키, Tab, Enter)
- 셀 복사/붙여넣기 (Ctrl+C/Ctrl+V)
- 드래그 필 (Fill Handle) - 마우스 드래그로 셀 값 복제
- 클립보드 API 통합
- 시각적 피드백 (선택 영역 하이라이트)

**구현 제외 (Out of Scope)**:
- 수식 계산 (Formula Calculation)
- 복잡한 셀 병합 (Cell Merging)
- 실행 취소/다시 실행 (Undo/Redo)
- 셀 서식 복사 (Format Painter)

---

## 2. 기능 요구사항

### 2.1 셀 선택 (Cell Selection)

#### 2.1.1 단일 셀 선택
- **설명**: 사용자가 단일 셀을 클릭하여 선택
- **동작**:
  - 마우스 클릭 시 해당 셀 선택
  - 선택된 셀에 시각적 표시 (border 강조)
- **검증**:
  - 클릭한 셀의 행/열 인덱스 정확히 추적
  - 이전 선택 해제 후 새 셀 선택

#### 2.1.2 범위 셀 선택
- **설명**: 마우스 드래그 또는 Shift+클릭으로 영역 선택
- **동작**:
  - **마우스 드래그**: mousedown → mousemove → mouseup
  - **Shift+클릭**: 시작 셀 + Shift+종료 셀
  - 선택 영역을 사각형 범위로 표시
- **검증**:
  - startRow, startCol, endRow, endCol 계산
  - 역방향 선택 지원 (우→좌, 하→상)

#### 2.1.3 키보드 네비게이션
| 키 | 동작 |
|---|---|
| ↑/↓/←/→ | 선택 셀 이동 |
| Shift + 방향키 | 범위 확장 선택 |
| Tab | 다음 셀로 이동 (좌→우) |
| Shift + Tab | 이전 셀로 이동 (우→좌) |
| Enter | 아래 셀로 이동 |
| Shift + Enter | 위 셀로 이동 |
| Ctrl + A | 전체 선택 (선택적) |

---

### 2.2 복사/붙여넣기 (Copy & Paste)

#### 2.2.1 셀 복사 (Ctrl+C)
- **설명**: 선택된 셀 데이터를 클립보드에 복사
- **동작**:
  1. 선택 영역의 셀 데이터 추출
  2. TSV(Tab-Separated Values) 형식으로 변환
  3. Clipboard API를 통해 클립보드에 저장
- **데이터 형식**:
  ```
  셀1\t셀2\t셀3
  셀4\t셀5\t셀6
  ```
- **검증**:
  - 단일 셀, 다중 행, 다중 열 모두 지원
  - 빈 셀은 빈 문자열로 처리

#### 2.2.2 셀 붙여넣기 (Ctrl+V)
- **설명**: 클립보드 데이터를 선택된 셀에 붙여넣기
- **동작**:
  1. Clipboard API에서 텍스트 데이터 읽기
  2. TSV 파싱하여 2차원 배열 생성
  3. 선택된 시작 셀부터 데이터 삽입
- **규칙**:
  - 붙여넣기 영역이 테이블 범위를 초과하면 경고 또는 무시
  - 읽기 전용 셀은 스킵
  - 데이터 타입 검증 (숫자, 문자열 등)
- **검증**:
  - Excel에서 복사한 데이터 붙여넣기 호환
  - 행/열 개수 불일치 처리

---

### 2.3 드래그 필 (Fill Handle)

#### 2.3.1 Fill Handle UI
- **설명**: 선택된 셀 우하단에 작은 사각형 표시 (Fill Handle)
- **위치**: 선택 영역의 우하단 모서리
- **크기**: 6x6px
- **색상**: Primary Color (예: `#1890ff`)

#### 2.3.2 드래그 필 동작
- **설명**: Fill Handle을 드래그하여 인접 셀에 데이터 복제
- **동작**:
  1. Fill Handle mousedown 감지
  2. mousemove로 드래그 방향 및 범위 추적
  3. mouseup 시 데이터 복제 실행
- **복제 규칙**:
  - **단일 값**: 동일 값 반복
  - **순차 패턴**: 숫자 시퀀스 인식 (1, 2, 3 → 4, 5, 6)
  - **날짜 패턴**: 날짜 증가 (2024-01-01 → 2024-01-02)
- **방향**:
  - 우측 드래그: 행 방향 복제
  - 하단 드래그: 열 방향 복제
- **검증**:
  - 드래그 범위 실시간 시각적 표시
  - 테이블 경계 제한

---

## 3. 기술 사양

### 3.1 아키텍처

#### 3.1.1 파일 구조
```
packages/effects/plugins/src/vxe-table/
├── extensions/
│   ├── cell-area/
│   │   ├── index.ts              # 진입점
│   │   ├── types.ts              # 타입 정의
│   │   ├── cell-selector.ts      # 셀 선택 로직
│   │   ├── clipboard-handler.ts  # 복사/붙여넣기
│   │   ├── fill-handle.ts        # 드래그 필
│   │   ├── keyboard-handler.ts   # 키보드 네비게이션
│   │   └── style.css             # 스타일시트
```

#### 3.1.2 통합 방식
- **방법 1**: `VxeGridApi` 클래스에 메서드 추가
- **방법 2**: VXE Table 커스텀 훅 (`use-cell-area.ts`)
- **방법 3**: VXE Table 렌더러 확장 (`extends.ts` 수정)

**권장**: 방법 2 (독립성, 재사용성 우수)

### 3.2 핵심 클래스 설계

#### 3.2.1 CellAreaManager
```typescript
export class CellAreaManager {
  private grid: VxeGridInstance;
  private selection: CellSelection | null = null;
  private isDragging = false;
  private isFilling = false;

  constructor(grid: VxeGridInstance) {
    this.grid = grid;
    this.init();
  }

  private init(): void {
    this.bindMouseEvents();
    this.bindKeyboardEvents();
    this.bindClipboardEvents();
  }

  // 셀 선택
  selectCell(row: number, col: number): void;
  selectRange(startRow: number, startCol: number, endRow: number, endCol: number): void;

  // 복사/붙여넣기
  copySelection(): Promise<void>;
  pasteFromClipboard(): Promise<void>;

  // 드래그 필
  startFillDrag(row: number, col: number): void;
  updateFillRange(row: number, col: number): void;
  endFillDrag(): void;

  // 키보드 네비게이션
  moveSelection(direction: 'up' | 'down' | 'left' | 'right', extend: boolean): void;

  // 정리
  destroy(): void;
}
```

#### 3.2.2 CellSelection Interface
```typescript
export interface CellSelection {
  startRow: number;
  startCol: number;
  endRow: number;
  endCol: number;
}

export interface CellRange {
  rows: number[];
  cols: number[];
  cells: Array<{ row: number; col: number; value: any }>;
}
```

### 3.3 이벤트 처리

#### 3.3.1 마우스 이벤트
```typescript
// 셀 선택
grid.on('cell-click', (event) => {
  if (event.shiftKey) {
    extendSelection(event.row, event.column);
  } else {
    selectCell(event.row, event.column);
  }
});

// 드래그 선택
grid.on('cell-mousedown', (event) => {
  startDragSelection(event.row, event.column);
});

grid.on('cell-mousemove', (event) => {
  if (isDragging) {
    updateSelection(event.row, event.column);
  }
});

grid.on('cell-mouseup', (event) => {
  endDragSelection();
});
```

#### 3.3.2 키보드 이벤트
```typescript
document.addEventListener('keydown', (event) => {
  if (event.ctrlKey && event.key === 'c') {
    event.preventDefault();
    copySelection();
  }
  if (event.ctrlKey && event.key === 'v') {
    event.preventDefault();
    pasteFromClipboard();
  }
  // 방향키 처리
  if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(event.key)) {
    event.preventDefault();
    moveSelection(event.key, event.shiftKey);
  }
});
```

### 3.4 클립보드 API 통합

#### 3.4.1 복사
```typescript
async function copySelection(): Promise<void> {
  const data = getSelectionData(); // 2D Array
  const tsv = data.map(row => row.join('\t')).join('\n');

  try {
    await navigator.clipboard.writeText(tsv);
    showToast('복사되었습니다');
  } catch (error) {
    console.error('클립보드 복사 실패:', error);
    fallbackCopy(tsv);
  }
}
```

#### 3.4.2 붙여넣기
```typescript
async function pasteFromClipboard(): Promise<void> {
  try {
    const text = await navigator.clipboard.readText();
    const data = parseTSV(text); // string[][] 파싱
    insertData(selection.startRow, selection.startCol, data);
    grid.refreshData();
  } catch (error) {
    console.error('클립보드 읽기 실패:', error);
  }
}
```

### 3.5 스타일링

#### 3.5.1 선택 영역 하이라이트
```css
.vxe-cell-area-selected {
  position: relative;
  background-color: rgba(24, 144, 255, 0.1);
}

.vxe-cell-area-selected::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid #1890ff;
  pointer-events: none;
}
```

#### 3.5.2 Fill Handle
```css
.vxe-fill-handle {
  position: absolute;
  width: 6px;
  height: 6px;
  background-color: #1890ff;
  cursor: crosshair;
  border: 1px solid white;
  z-index: 10;
}

.vxe-fill-handle:hover {
  width: 8px;
  height: 8px;
}
```

---

## 4. 사용자 인터페이스

### 4.1 시각적 피드백

| 상태 | UI 표현 |
|---|---|
| 단일 셀 선택 | 2px 파란색 테두리 |
| 범위 선택 | 반투명 파란색 배경 + 테두리 |
| 드래그 필 활성 | Crosshair 커서 |
| 복사 완료 | Toast 알림 "복사됨" |
| 붙여넣기 실패 | Toast 경고 "붙여넣기 실패" |

### 4.2 반응형 고려사항
- 모바일: 터치 이벤트 지원 (`touchstart`, `touchmove`, `touchend`)
- 드래그 필: 모바일에서는 long-press로 활성화

---

## 5. 성능 요구사항

### 5.1 성능 목표
- **초기 로드**: 추가 300ms 이하
- **셀 선택 응답**: 16ms 이하 (60fps)
- **복사/붙여넣기**: 10,000 셀 기준 500ms 이하
- **드래그 필**: 1,000 셀 복제 기준 200ms 이하

### 5.2 최적화 전략
- **Virtual Scrolling**: VXE Table 기본 가상 스크롤 활용
- **Debounce/Throttle**: mousemove 이벤트 최적화
- **Batch Update**: 여러 셀 업데이트 시 일괄 처리
- **Web Worker**: 대용량 데이터 파싱 시 백그라운드 처리 (선택적)

---

## 6. 테스트 계획

### 6.1 단위 테스트
- `CellAreaManager` 클래스 메서드 테스트
- 데이터 파싱 로직 (TSV → 2D Array)
- 셀 선택 범위 계산 로직

### 6.2 통합 테스트
- VXE Table과 통합 시나리오
- 키보드 네비게이션 플로우
- 복사/붙여넣기 전체 프로세스

### 6.3 E2E 테스트
- Playwright 또는 Cypress 사용
- 실제 사용자 시나리오 재현:
  1. 셀 클릭 → 범위 선택 → 복사 → 붙여넣기
  2. 드래그 필로 데이터 복제
  3. 키보드 네비게이션

### 6.4 브라우저 호환성 테스트
- Chrome, Firefox, Safari, Edge (최신 2버전)
- Clipboard API 폴백 동작 검증

---

## 7. 보안 고려사항

### 7.1 클립보드 권한
- Clipboard API는 HTTPS 필수
- 권한 거부 시 사용자 친화적 에러 메시지

### 7.2 데이터 검증
- 붙여넣기 데이터 XSS 검증
- 대용량 데이터 DoS 방지 (크기 제한)

### 7.3 읽기 전용 셀 보호
- `readonly` 컬럼은 붙여넣기/드래그 필 차단

---

## 8. 개발 일정 (추정)

| 단계 | 작업 내용 | 예상 시간 |
|---|---|---|
| Phase 1 | 아키텍처 설계 및 타입 정의 | 4h |
| Phase 2 | 셀 선택 로직 구현 | 8h |
| Phase 3 | 복사/붙여넣기 구현 | 6h |
| Phase 4 | 드래그 필 구현 | 8h |
| Phase 5 | 키보드 네비게이션 구현 | 4h |
| Phase 6 | 스타일링 및 UI 피드백 | 4h |
| Phase 7 | 단위/통합 테스트 | 6h |
| Phase 8 | E2E 테스트 | 4h |
| Phase 9 | 문서화 | 2h |
| **총계** | | **46시간 (약 6일)** |

---

## 9. 리스크 및 대응

| 리스크 | 영향도 | 대응 방안 |
|---|---|---|
| VXE Table 버전 업데이트 시 호환성 | 높음 | 테스트 자동화, 버전 고정 |
| 클립보드 API 브라우저 지원 | 중간 | Fallback 구현 (execCommand) |
| 대용량 데이터 성능 저하 | 중간 | Virtual Scrolling, 페이지네이션 |
| 모바일 터치 이벤트 복잡성 | 낮음 | 점진적 구현, 데스크톱 우선 |

---

## 10. 향후 확장 가능성

### 10.1 Phase 2 기능 (선택적)
- 다중 선택 (Ctrl+클릭)
- 컨텍스트 메뉴 (우클릭)
- 실행 취소/다시 실행
- 셀 서식 복사
- 조건부 서식

### 10.2 외부 통합
- Excel 파일 직접 import/export
- Google Sheets 스타일 공유 링크

---

## 11. 성공 기준

### 11.1 기능적 기준
- ✅ 모든 핵심 기능 정상 동작
- ✅ E2E 테스트 100% 통과
- ✅ 주요 브라우저 호환성 확보

### 11.2 비기능적 기준
- ✅ 성능 목표 달성 (5.1 참조)
- ✅ 접근성 기준 충족 (키보드 네비게이션)
- ✅ 코드 커버리지 80% 이상

### 11.3 사용자 경험 기준
- ✅ 사용자 피드백 긍정률 90% 이상
- ✅ 학습 곡선 최소화 (Excel 사용자 즉시 이해)

---

## 12. 참고 자료

### 12.1 VXE Table 문서
- 공식 문서: https://vxeui.com/
- GitHub: https://github.com/x-extends/vxe-table

### 12.2 유사 구현 참고
- ag-Grid Cell Range Selection
- Handsontable Copy/Paste
- Excel.js Library

### 12.3 Web API
- [Clipboard API (MDN)](https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API)
- [Selection API (MDN)](https://developer.mozilla.org/en-US/docs/Web/API/Selection)

---

## 13. 승인 및 검토

| 역할 | 이름 | 승인 일자 |
|---|---|---|
| 작성자 | Claude AI | 2025-10-16 |
| 검토자 | - | - |
| 최종 승인자 | - | - |

---

**변경 이력**
| 버전 | 일자 | 변경 내용 | 작성자 |
|---|---|---|---|
| 1.0.0 | 2025-10-16 | 초안 작성 | Claude AI |
