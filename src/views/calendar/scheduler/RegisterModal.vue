<template>
  <BasicModal
    v-bind="$attrs"
    @register="register"
    title="일정 등록"
    @visible-change="handleVisibleChange"
    @ok="handleSubmit"
    width="700px"
  >
    <div class="pt-3px pr-3px">
      <BasicForm @register="registerForm" :model="model">
        <template #dateButton>
          <DateButtons />
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>
<script lang="ts">
  import { defineComponent, ref, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { DateButtons } from '/@/components/Calendar';
  import dayjs from 'dayjs';

  // export const calendarProps = {
  //   isDetail: { type: Boolean },
  //   title: { type: String, default: '' },
  //   loadingText: { type: String },
  //   showDetailBack: { type: Boolean, default: true },
  //   visible: { type: Boolean },
  //   loading: { type: Boolean },
  //   maskClosable: { type: Boolean, default: true },
  //   getContainer: {
  //     type: [Object, String] as PropType<any>,
  //   },
  //   closeFunc: {
  //     type: [Function, Object] as PropType<any>,
  //     default: null,
  //   },
  // };
  export default defineComponent({
    components: { BasicModal, BasicForm, DateButtons },
    props: { calendarProps: {} },
    setup(props, { emit }) {
      const { createMessage } = useMessage();
      const modelRef = ref({});
      const isDateShow = ref(false);
      let mockId = 100;

      const schemas: FormSchema[] = [
        {
          field: 'title',
          component: 'Input',
          label: '제목',
          required: true,
          colProps: {
            span: 24,
          },
          defaultValue: '',
        },
        {
          field: 'category',
          component: 'Select',
          label: '구분',
          required: true,
          componentProps: {
            options: [
              {
                label: '선택',
                value: '',
              },
              {
                label: '재택',
                value: 'home',
              },
              {
                label: '휴가',
                value: 'vacation',
              },
              {
                label: '연차',
                value: 'annual',
              },
              {
                label: '반차',
                value: 'semiAnnual',
              },
            ],
            onChange: (e: any) => {
              return e === 'home' ? (isDateShow.value = true) : (isDateShow.value = false);
            },
          },
        },
        {
          field: 'range',
          component: 'Input',
          label: '일정',
          required: true,
          slot: 'dateButton',
          colProps: {
            span: 24,
          },
          ifShow: ({ values }) => {
            return values.category === 'home';
          },
        },
        {
          field: 'fieldTime',
          component: 'RangePicker',
          label: '일정',
          required: true,
          colProps: {
            span: 16,
          },
          ifShow: ({ values }) => {
            return values.category !== 'home';
          },
        },
        {
          field: 'time',
          component: 'Select',
          label: '',
          required: true,
          ifShow: ({ values }) => {
            return values.category === 'semiAnnual';
          },
          colProps: {
            span: 8,
          },
          componentProps: {
            options: [
              {
                label: '선택',
                value: '',
              },
              {
                label: '오전',
                value: 'home',
              },
              {
                label: '오후',
                value: 'vacation',
              },
            ],
            // onChange: (e: any) => {},
          },
        },

        {
          field: 'contents',
          component: 'InputTextArea',
          label: '내용',
          required: true,
          colProps: {
            span: 24,
          },
        },
      ];
      const [
        registerForm,
        {
          validateFields,
          // getFieldsValue,
          // resetFields,
          // setFieldsValue,
          // setProps
        },
      ] = useForm({
        labelWidth: 120,
        schemas,
        showActionButtonGroup: false,
        actionColOptions: {
          span: 24,
        },
      });
      async function validateForm() {
        try {
          const res = await validateFields();
          console.log('passing', res);

          emit('addEvents', {
            id: (mockId++).toString(),
            title: res.title,
            start: res.fieldTime[0].$d,
            end: res.fieldTime[1].$d,
            backgroundColor: getCategoryColor(res.category),
          });
          await createMessage.success('저장완료되었습니다.:' + JSON.stringify(res));
        } catch (error) {
          console.log('not passing', error);
          createMessage.error('저장에 실패하였습니다. :' + error);
        }
      }
      const [register] = useModalInner((data) => {
        data && onDataReceive(data);
      });

      const getCategoryColor = (category) => {
        switch (category) {
          case 'home':
            return 'rgb(239, 68, 68)';
            break;
          case 'vacation':
            return 'rgb(245, 158, 11)';
            break;
          case 'annual':
            return 'rgb(16, 185, 129)';
            break;
          default:
            return 'rgb(59, 130, 246)';
            break;
        }
      };
      function onDataReceive(data) {
        console.log('Data Received ', data);

        // 方式1;
        // setFieldsValue({
        //   field2: data.data,
        //   field1: data.info,
        // });

        // // 方式2
        modelRef.value = {
          fieldTime: [dayjs(data.startStr).format(), dayjs(data.endStr).format()],
        };
      }

      function handleVisibleChange(v) {
        console.log(props.calendarProps, 'Zzz');
        v && props.calendarProps && nextTick(() => onDataReceive(props.calendarProps));
      }

      return {
        register,
        schemas,
        registerForm,
        model: modelRef,
        isDateShow,
        handleVisibleChange,
        handleSubmit: () => {
          validateForm();
          // alert(111);
        },
      };
    },
  });
</script>

<style lang="less">
  .ant-picker {
    width: 100%;
  }
</style>
