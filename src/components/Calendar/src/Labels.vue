<template>
  <CollapseContainer title="카테고리">
    <CheckboxGroup>
      <Row>
        <Col :span="24">
          <label class="cursor-pointer">
            <input class="h-4 w-4 accent-red-500 cursor-pointer" type="checkbox" checked />
            <span> 재택</span>
          </label>
        </Col>
        <Col :span="24">
          <label class="cursor-pointer">
            <input class="h-4 w-4 accent-yellow-500 cursor-pointer" type="checkbox" checked />
            <span> 휴가</span>
          </label>
        </Col>
        <Col :span="24">
          <label class="cursor-pointer">
            <input class="h-4 w-4 accent-green-500 cursor-pointer" type="checkbox" checked />
            <span> 연차</span>
          </label>
        </Col>
        <Col :span="24">
          <label class="cursor-pointer">
            <input class="h-4 w-4 accent-blue-500 cursor-pointer" type="checkbox" checked />
            <span> 반차</span>
          </label>
        </Col>
      </Row>
    </CheckboxGroup>
  </CollapseContainer>
</template>

<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { CheckboxGroup, Row, Col } from 'ant-design-vue';
  import { CollapseContainer } from '/@/components/Container/index';
  export default defineComponent({
    components: { CollapseContainer, CheckboxGroup, Row, Col },
    setup() {
      const text = ref(
        `A dog is a type of domesticated animal.Known for its loyalty and faithfulness,it can be found as a welcome guest in many households across the world.`,
      );
      const customStyle = ref(
        'background: #f7f7f7;border-radius: 4px;margin-bottom: 24px;border: 0;overflow: hidden',
      );
      return { text, customStyle };
    },
  });
</script>

<style scoped></style>
