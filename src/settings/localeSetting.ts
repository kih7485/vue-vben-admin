import type { DropMenu } from '../components/Dropdown';
import type { LocaleSetting, LocaleType } from '/#/config';

export const LOCALE: { [key: string]: LocaleType } = {
  KR_KO: 'ko',
  ZH_CN: 'zh_CN',
  EN_US: 'en',
};

export const localeSetting: LocaleSetting = {
  showPicker: true,
  // Locale
  locale: LOCALE.KR_KO,
  // Default locale
  fallback: LOCALE.KR_KO,
  // available Locales
  availableLocales: [LOCALE.KR_KO, LOCALE.EN_US],
};

// locale list
export const localeList: DropMenu[] = [
  {
    text: '한국어',
    event: LOCALE.KR_KO,
  },
  {
    text: 'English',
    event: LOCALE.EN_US,
  },
];
