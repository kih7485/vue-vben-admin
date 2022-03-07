<template>
  <Teleport to="body">
    <transition name="zoom-fade" mode="out-in">
      <div :class="getClass" @click.stop v-if="visible">
        <div :class="`${prefixCls}-content`" v-click-outside="handleClose">
          <div :class="`${prefixCls}-input__wrapper`">
            <div class="flex relative mx-auto w-full">
              <button type="submit" class="absolute right-2 top-3 mr-4">
                <svg
                  class="text-black h-6 w-6 fill-current"
                  version="1.1"
                  id="Capa_1"
                  x="0px"
                  y="0px"
                  viewBox="0 0 56.966 56.966"
                  style="enable-background: new 0 0 56.966 56.966"
                  xml:space="preserve"
                  width="512px"
                  height="512px"
                >
                  <path
                    d="M55.146,51.887L41.588,37.786c3.486-4.144,5.396-9.358,5.396-14.786c0-12.682-10.318-23-23-23s-23,10.318-23,23  s10.318,23,23,23c4.761,0,9.298-1.436,13.177-4.162l13.661,14.208c0.571,0.593,1.339,0.92,2.162,0.92  c0.779,0,1.518-0.297,2.079-0.837C56.255,54.982,56.293,53.08,55.146,51.887z M23.984,6c9.374,0,17,7.626,17,17s-7.626,17-17,17  s-17-7.626-17-17S14.61,6,23.984,6z"
                  />
                </svg>
              </button>
              <input
                class="border-2 border-primary bg-red transition h-12 px-5 pr-16 rounded-md focus:outline-none w-full text-black text-lg"
                type="search"
                name="search"
                placeholder="검색할 메뉴명을 입력하세요."
                ref="inputRef"
                allow-clear
                @input="handleSearch"
              />
            </div>
          </div>

          <div :class="`${prefixCls}-not-data`" v-show="getIsNotData">
            {{ t('component.app.searchNotData') }}
          </div>

          <ul :class="`${prefixCls}-list`" v-show="!getIsNotData" ref="scrollWrap">
            <li
              :ref="setRefs(index)"
              v-for="(item, index) in searchResult"
              :key="item.path"
              :data-index="index"
              @mouseenter="handleMouseenter"
              @click="handleEnter"
              :class="[
                `${prefixCls}-list__item`,
                {
                  [`${prefixCls}-list__item--active`]: activeIndex === index,
                },
              ]"
            >
              <div :class="`${prefixCls}-list__item-icon`">
                <Icon :icon="item.icon || 'mdi:form-select'" :size="20" />
              </div>
              <div :class="`${prefixCls}-list__item-text`">
                {{ item.name }}
              </div>
              <div :class="`${prefixCls}-list__item-enter`">
                <Icon icon="ant-design:enter-outlined" :size="20" />
              </div>
            </li>
          </ul>
          <AppSearchFooter />
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script lang="ts" setup>
  import { computed, unref, ref, watch, nextTick } from 'vue';
  import AppSearchFooter from './AppSearchFooter.vue';
  import Icon from '/@/components/Icon';
  // @ts-ignore
  import vClickOutside from '/@/directives/clickOutside';
  import { useDesign } from '/@/hooks/web/useDesign';
  import { useRefs } from '/@/hooks/core/useRefs';
  import { useMenuSearch } from './useMenuSearch';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { useAppInject } from '/@/hooks/web/useAppInject';

  const props = defineProps({
    visible: { type: Boolean },
  });

  const emit = defineEmits(['close']);

  const scrollWrap = ref(null);
  const inputRef = ref<Nullable<HTMLElement>>(null);

  const { t } = useI18n();
  const { prefixCls } = useDesign('app-search-modal');
  const [refs, setRefs] = useRefs();
  const { getIsMobile } = useAppInject();

  const { handleSearch, searchResult, keyword, activeIndex, handleEnter, handleMouseenter } =
    useMenuSearch(refs, scrollWrap, emit);

  const getIsNotData = computed(() => !keyword || unref(searchResult).length === 0);

  const getClass = computed(() => {
    return [
      prefixCls,
      {
        [`${prefixCls}--mobile`]: unref(getIsMobile),
      },
    ];
  });

  watch(
    () => props.visible,
    (visible: boolean) => {
      visible &&
        nextTick(() => {
          unref(inputRef)?.focus();
        });
    },
  );

  function handleClose() {
    searchResult.value = [];
    emit('close');
  }
</script>
<style lang="less" scoped>
  @prefix-cls: ~'@{namespace}-app-search-modal';
  @footer-prefix-cls: ~'@{namespace}-app-search-footer';
  .@{prefix-cls} {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 800;
    display: flex;
    width: 100%;
    height: 100%;
    padding-top: 50px;
    background-color: rgb(0 0 0 / 25%);
    justify-content: center;

    &--mobile {
      padding: 0;

      > div {
        width: 100%;
      }

      .@{prefix-cls}-input {
        width: calc(100% - 38px);
      }

      .@{prefix-cls}-cancel {
        display: inline-block;
      }

      .@{prefix-cls}-content {
        width: 100%;
        height: 100%;
        border-radius: 0;
      }

      .@{footer-prefix-cls} {
        display: none;
      }

      .@{prefix-cls}-list {
        height: calc(100% - 80px);
        max-height: unset;

        &__item {
          &-enter {
            opacity: 0% !important;
          }
        }
      }
    }

    &-content {
      position: relative;
      width: 632px;
      margin: 0 auto auto;
      background-color: @component-background;
      border-radius: 16px;
      box-shadow: 0 25px 50px -12px rgb(0 0 0 / 25%);
      flex-direction: column;
    }

    &-input__wrapper {
      display: flex;
      padding: 14px 14px 0;
      justify-content: space-between;
      align-items: center;
    }

    &-input {
      width: 100%;
      height: 48px;
      font-size: 1.5em;
      color: #1c1e21;
      border-radius: 6px;

      span[role='img'] {
        color: #999;
      }
    }

    &-cancel {
      display: none;
      font-size: 1em;
      color: #666;
    }

    &-not-data {
      display: flex;
      width: 100%;
      height: 100px;
      font-size: 0.9;
      color: rgb(150 159 175);
      align-items: center;
      justify-content: center;
    }

    &-list {
      max-height: 472px;
      padding: 0 14px;
      padding-bottom: 20px;
      margin: 0 auto;
      margin-top: 14px;
      overflow: auto;

      &__item {
        position: relative;
        display: flex;
        width: 100%;
        height: 56px;
        padding-bottom: 4px;
        padding-left: 14px;
        margin-top: 8px;
        font-size: 14px;
        color: @text-color-base;
        cursor: pointer;
        background-color: @component-background;
        border-radius: 4px;
        box-shadow: 0 1px 3px 0 #d4d9e1;
        align-items: center;

        > div:first-child,
        > div:last-child {
          display: flex;
          align-items: center;
        }

        &--active {
          color: #fff;
          background-color: @primary-color;

          .@{prefix-cls}-list__item-enter {
            opacity: 100%;
          }
        }

        &-icon {
          width: 30px;
        }

        &-text {
          flex: 1;
        }

        &-enter {
          width: 30px;
          opacity: 0%;
        }
      }
    }
  }
</style>
