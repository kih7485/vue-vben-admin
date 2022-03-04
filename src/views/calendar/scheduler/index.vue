<template>
  <div class="demo-app">
    <div class="demo-app-sidebar">
      <SmallCalendar @select-date="onSelectSmallDate" />
      <Labels />
    </div>
    <div class="demo-app-main">
      <FullCalendar class="demo-app-calendar" ref="fullCalendar" :options="calendarOptions">
        <template #eventContent="arg">
          <b>{{ arg.timeText }}11</b>
          <i>{{ arg.event.title }}</i>
        </template>
      </FullCalendar>
    </div>
    <RegisterModal
      :calendarProps="calendarProps"
      @register="register1"
      v-model:visible="modalVisible"
      @add-events="addCalandarEvent"
    />
  </div>
</template>

<script lang="ts">
  import { defineComponent, onMounted, onUnmounted, onUpdated, reactive, ref } from 'vue';
  import '@fullcalendar/core/vdom'; // solve problem with Vite
  import FullCalendar, {
    CalendarOptions,
    EventApi,
    DateSelectArg,
    EventClickArg,
  } from '@fullcalendar/vue3';
  import dayGridPlugin from '@fullcalendar/daygrid';
  import timeGridPlugin from '@fullcalendar/timegrid';
  import listPlugin from '@fullcalendar/list';
  import interactionPlugin from '@fullcalendar/interaction';
  import { INITIAL_EVENTS } from './event';
  import { useModal } from '/@/components/Modal';
  import RegisterModal from './RegisterModal.vue';
  import { SmallCalendar, Labels } from '/@/components/Calendar';
  import dayjs from 'dayjs';

  const Demo = defineComponent({
    components: {
      FullCalendar,
      RegisterModal,
      SmallCalendar,
      Labels,
    },
    setup() {
      const [register1] = useModal();
      const fullCalendar = ref(null);
      const calendarProps = ref({});
      const modalVisible = ref<Boolean>(false);
      onMounted(() => console.log(INITIAL_EVENTS, 'component mounted'));
      onUnmounted(() => console.log(INITIAL_EVENTS, 'component onUnmounted'));

      //재 렌더링 후 호출.
      onUpdated(() => {
        console.log(INITIAL_EVENTS, 'component onUpdated');
      });
      const calendarOptions = reactive({
        plugins: [
          dayGridPlugin,
          timeGridPlugin,
          listPlugin,
          interactionPlugin, // needed for dateClick
        ],
        locale: 'ko',
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
        },
        initialView: 'dayGridMonth',
        events: INITIAL_EVENTS,
        // initialEvents: INITIAL_EVENTS, // alternatively, use the `events` setting to fetch from a feed
        editable: true,
        selectable: true,
        selectMirror: true,
        dayMaxEvents: true,
        weekends: true,
        select: handleDateSelect,
        eventClick: handleEventClick,
        eventsSet: handleEvents,
        /* you can update a remote database when these fire:
        eventAdd:
        eventChange:
        eventRemove:
        */
      }) as CalendarOptions;
      let currentEvents = [] as EventApi[];

      // function send() {
      //   openModal4(true, {
      //     data: 'content',
      //     info: 'Info',
      //   });
      // }

      // function openModal(index) {
      //   nextTick(() => {
      //     // open the target modal
      //     modalVisible.value = true;
      //   });
      // }

      // function handleWeekendsToggle() {
      //   calendarOptions.weekends = !calendarOptions.weekends; // update a property
      // }

      function handleDateSelect(selectInfo: DateSelectArg) {
        calendarProps.value = { ...selectInfo };
        modalVisible.value = true;
        // let title = prompt('Please enter a new title for your event');
        // let calendarApi = selectInfo.view.calendar;
        // calendarApi.unselect(); // clear date selection
        // if (title) {
        //   calendarApi.addEvent({
        //     id: createEventId(),
        //     title,
        //     start: selectInfo.startStr,
        //     end: selectInfo.endStr,
        //     allDay: selectInfo.allDay,
        //   });
        // }
      }
      function handleEventClick(clickInfo: EventClickArg) {
        if (confirm(`Are you sure you want to delete the event '${clickInfo.event.title}'`)) {
          clickInfo.event.remove();
        }
      }
      function handleEvents(events: EventApi[]) {
        currentEvents = events;
      }
      function onSelectSmallDate(day) {
        const calendarApi = fullCalendar.value.getApi();
        const formatDate = dayjs(day).format('YYYY-MM-DD');
        calendarApi.gotoDate(formatDate);
      }

      function addCalandarEvent(event) {
        const calendarApi = fullCalendar.value.getApi();
        console.log(event, 'event');
        calendarApi.addEvent(event);
        modalVisible.value = false;
      }
      return {
        calendarOptions,
        currentEvents,
        modalVisible,
        fullCalendar,
        calendarProps,
        onSelectSmallDate,
        addCalandarEvent,
        register1,
      };
    },
  });
  export default Demo;
</script>

<style lang="less">
  h2 {
    margin: 0;
    font-size: 16px;
  }

  ul {
    margin: 0;
    padding: 0 0 0 1.5em;
  }

  li {
    margin: 1.5em 0;
    padding: 0;
  }

  b {
    /* used for event dates/times */
    margin-right: 3px;
  }

  .demo-app {
    display: flex;
    max-height: 100%;
    font-family: Arial, Helvetica Neue, Helvetica, sans-serif;
    font-size: 14px;
  }

  .demo-app-sidebar {
    width: 300px;
    line-height: 1.5;
    background: #ffffff;
    border-right: 1px solid #d3e2e8;
  }

  .demo-app-sidebar-section {
    padding: 2em;
  }

  .demo-app-main {
    flex-grow: 1;
  }

  .fc {
    /* the calendar root */
    max-width: 100%;
    margin: 0 auto;
  }
  /* this file is included direcly from the webpack/postcss config */

  // :root {
  //   --fc-border-color: green;
  // }
</style>
