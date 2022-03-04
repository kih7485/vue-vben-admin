<template>
  <DatePicker v-model="date" is-expanded @dayclick="onDayClick" />
</template>

<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { DatePicker } from 'v-calendar';
  import 'v-calendar/dist/style.css';

  export default defineComponent({
    components: {
      DatePicker,
    },
    emits: ['selectDate'],
    setup(_, { emit }) {
      const selectDate = ref(new Date());
      function onDayClick(day) {
        selectDate.value = day.date;

        console.log(selectDate, 'click days');
        emit('selectDate', selectDate.value);
      }
      return {
        selectDate,
        onDayClick,
      };
    },
  });
</script>
<style scoped></style>
