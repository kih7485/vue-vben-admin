<template>
  <div class="bg-white p-2 w-full border rounded">
    <DatePicker v-model="selected.date">
      <template #default="{ togglePopover, hidePopover }">
        <div class="flex flex-wrap">
          <button
            v-for="date in dates"
            :key="date.date.getTime()"
            class="flex items-center bg-indigo-100 hover:bg-indigo-200 text-sm text-indigo-600 font-semibold h-8 px-2 m-1 rounded-lg border-2 border-transparent focus:border-indigo-600 focus:outline-none"
            @click.stop="dateSelected($event, date, togglePopover)"
            ref="button"
          >
            {{ date.date.toLocaleDateString() }}
            <svg
              class="w-4 h-4 text-gray-600 hover:text-indigo-600 ml-1 -mr-1"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
              @click.stop="removeDate(date, hidePopover)"
            >
              <path d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </template>
    </DatePicker>
    <button
      class="text-sm text-indigo-600 font-semibold hover:text-indigo-500 px-2 h-8 focus:outline-none"
      @click.stop="addDate"
    >
      + 일정 추가
    </button>
  </div>
  <h6 class="text-gray-600">중복된 일자는 1건으로 등록됩니다.</h6>
</template>

<script lang="ts">
  import { defineComponent, nextTick, onMounted, reactive, ref } from 'vue';
  import { DatePicker } from 'v-calendar';
  import 'v-calendar/dist/style.css';

  export default defineComponent({
    name: 'DateButtons',
    components: {
      DatePicker,
    },
    setup() {
      const event = reactive({
        dates: [{ date: new Date() }],
        selected: {},
      });
      const dates = ref([{ date: new Date() }]);
      const selected = ref({});
      const button = ref(null);
      onMounted(() => {
        // DOM 요소는 초기 렌더링 후에 ref에 할당합니다.
        console.log(button.value); // <div>This is a root element</div>
      });
      function addDate(e) {
        e.preventDefault();

        dates.value.push({
          date: new Date(),
        });
        nextTick(() => {
          // const btn = button.value[button.value.length - 1];
          // btn.click();
        });
      }
      function removeDate(date, hide) {
        dates.value = dates.value.filter((d) => d !== date);
        hide();
      }
      function dateSelected(e, date, toggle) {
        e.preventDefault();
        console.log(selected.value, 'selected.value');
        selected.value = date;
        toggle({ ref: e.target });
      }
      return {
        dates,
        selected,
        event,
        addDate,
        removeDate,
        dateSelected,
        button,
      };
    },
  });
</script>

<style scoped></style>
