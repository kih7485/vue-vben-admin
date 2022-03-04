import { withInstall } from '/@/utils';
import dateButtons from './src/DateButtons.vue';
import smallCalendar from './src/SmallCalendar.vue';

import labels from './src/Labels.vue';

export const DateButtons = withInstall(dateButtons);
export const Labels = withInstall(labels);
export const SmallCalendar = withInstall(smallCalendar);
