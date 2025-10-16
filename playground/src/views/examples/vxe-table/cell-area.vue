<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { onMounted } from 'vue';

import { Page } from '@vben/common-ui';

import { Button, message, Space } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';

import DocButton from '../doc-button.vue';

interface RowType {
  address: string;
  age: number;
  date: string;
  id: number;
  name: string;
  score: number;
}

const gridOptions: VxeGridProps<RowType> = {
  border: true,
  columns: [
    { field: 'id', title: 'ID', width: 80 },
    { field: 'name', title: 'Name', width: 120 },
    { field: 'age', title: 'Age', width: 100 },
    { field: 'score', title: 'Score', width: 100 },
    { field: 'date', title: 'Date', width: 150 },
    {
      field: 'address',
      minWidth: 200,
      showOverflow: true,
      title: 'Address',
    },
  ],
  data: [
    {
      address: 'New York No. 1 Lake Park',
      age: 28,
      date: '2024-01-01',
      id: 1,
      name: 'John Brown',
      score: 85,
    },
    {
      address: 'London No. 1 Lake Park',
      age: 32,
      date: '2024-01-02',
      id: 2,
      name: 'Jim Green',
      score: 90,
    },
    {
      address: 'Sydney No. 1 Lake Park',
      age: 42,
      date: '2024-01-03',
      id: 3,
      name: 'Joe Black',
      score: 78,
    },
    {
      address: 'Berlin No. 1 Lake Park',
      age: 25,
      date: '2024-01-04',
      id: 4,
      name: 'Jim Red',
      score: 95,
    },
    {
      address: 'Paris No. 1 Lake Park',
      age: 29,
      date: '2024-01-05',
      id: 5,
      name: 'Jack White',
      score: 88,
    },
    {
      address: 'Tokyo No. 1 Lake Park',
      age: 35,
      date: '2024-01-06',
      id: 6,
      name: 'Tom Blue',
      score: 92,
    },
    {
      address: 'Moscow No. 1 Lake Park',
      age: 38,
      date: '2024-01-07',
      id: 7,
      name: 'Lucy Yellow',
      score: 87,
    },
    {
      address: 'Rome No. 1 Lake Park',
      age: 41,
      date: '2024-01-08',
      id: 8,
      name: 'Peter Purple',
      score: 91,
    },
    {
      address: 'Beijing No. 1 Lake Park',
      age: 33,
      date: '2024-01-09',
      id: 9,
      name: 'David Green',
      score: 89,
    },
    {
      address: 'Shanghai No. 1 Lake Park',
      age: 27,
      date: '2024-01-10',
      id: 10,
      name: 'Sarah Black',
      score: 93,
    },
  ],
  height: 500,
  pagerConfig: {
    enabled: false,
  },
  showOverflow: true,
  stripe: true,
};

const [Grid, gridApi] = useVbenVxeGrid<RowType>({
  gridOptions,
});

onMounted(async () => {
  // Wait for grid to be fully mounted
  await new Promise(resolve => setTimeout(resolve, 500));

  console.log('Grid instance:', gridApi.grid);
  console.log('Grid $el:', gridApi.grid?.$el);

  // Enable cell area features (selection, copy/paste, fill)
  const manager = await gridApi.enableCellArea({
    enableClipboard: true,
    enableFillHandle: true,
    enableKeyboard: true,
    enableSelection: true,
    selectionStyle: {
      backgroundColor: 'rgba(24, 144, 255, 0.1)',
      borderColor: '#1890ff',
      fillHandleColor: '#1890ff',
    },
  });

  console.log('Cell Area Manager:', manager);
  console.log('Cell Area Manager initialized:', !!manager);

  if (manager) {
    message.success('Cell Area features enabled! Try selecting cells with mouse or keyboard.');
  } else {
    message.error('Failed to initialize Cell Area features');
  }
});

function handleCopy() {
  gridApi.cellAreaManager?.copySelection();
  message.success('Selected cells copied to clipboard');
}

function handlePaste() {
  gridApi.cellAreaManager?.pasteFromClipboard();
  message.success('Pasted from clipboard');
}

function handleGetSelection() {
  const selection = gridApi.cellAreaManager?.getSelection();
  if (selection) {
    message.info(
      `Selection: Row ${selection.startRow}-${selection.endRow}, Col ${selection.startCol}-${selection.endCol}`,
    );
  } else {
    message.warning('No selection');
  }
}

function handleClearSelection() {
  gridApi.cellAreaManager?.clearSelection();
  message.info('Selection cleared');
}
</script>

<template>
  <Page
    description="Excel-like cell selection, copy/paste, and drag-fill features for VXE Table"
    title="Cell Area Extension"
  >
    <template #extra>
      <DocButton
        path="/examples/vxe-table/cell-area"
        href="https://github.com/vbenjs/vue-vben-admin/tree/main/packages/effects/plugins/src/vxe-table/extensions/cell-area"
      />
    </template>

    <div class="mb-4">
      <Space>
        <Button @click="handleCopy">Copy (Ctrl+C)</Button>
        <Button @click="handlePaste">Paste (Ctrl+V)</Button>
        <Button @click="handleGetSelection">Get Selection</Button>
        <Button @click="handleClearSelection">Clear Selection</Button>
      </Space>
    </div>

    <div class="mb-4 rounded bg-blue-50 p-4">
      <h3 class="mb-2 font-semibold">Features:</h3>
      <ul class="ml-4 list-disc space-y-1 text-sm">
        <li><strong>Cell Selection:</strong> Click and drag to select cells, or Shift+Click to extend selection</li>
        <li><strong>Keyboard Navigation:</strong> Use arrow keys to move selection, Shift+Arrow to extend</li>
        <li><strong>Copy/Paste:</strong> Press Ctrl+C to copy, Ctrl+V to paste (works with Excel!)</li>
        <li><strong>Drag Fill:</strong> Drag the blue square at bottom-right corner to fill cells</li>
        <li><strong>Tab Navigation:</strong> Press Tab to move right, Shift+Tab to move left</li>
        <li><strong>Enter Navigation:</strong> Press Enter to move down, Shift+Enter to move up</li>
      </ul>
    </div>

    <Grid />
  </Page>
</template>
