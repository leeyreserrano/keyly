import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

import caAuth from "./locales/ca/auth.json";
import caFolder from "./locales/ca/folder.json";
import caItem from "./locales/ca/item.json";
import caShared from "./locales/ca/shared.json";
import caCommon from "./locales/ca/common.json";
import caHome from "./locales/ca/home.json";
import caChoose from "./locales/ca/choose.json";
import caStats from "./locales/ca/stats.json";
import caConfig from "./locales/ca/config.json";
import caSidebar from "./locales/ca/sidebar.json";
import caToolbar from "./locales/ca/toolbar.json";
import caCard from "./locales/ca/card.json";
import caShare from "./locales/ca/share.json";

import enAuth from "./locales/en/auth.json";
import enFolder from "./locales/en/folder.json";
import enItem from "./locales/en/item.json";
import enShared from "./locales/en/shared.json";
import enCommon from "./locales/en/common.json";
import enHome from "./locales/en/home.json";
import enChoose from "./locales/en/choose.json";
import enStats from "./locales/en/stats.json";
import enConfig from "./locales/en/config.json";
import enSidebar from "./locales/en/sidebar.json";
import enToolbar from "./locales/en/toolbar.json";
import enCard from "./locales/en/card.json";
import enShare from "./locales/en/share.json";

import esAuth from "./locales/es/auth.json";
import esFolder from "./locales/es/folder.json";
import esItem from "./locales/es/item.json";
import esShared from "./locales/es/shared.json";
import esCommon from "./locales/es/common.json";
import esHome from "./locales/es/home.json";
import esChoose from "./locales/es/choose.json";
import esStats from "./locales/es/stats.json";
import esConfig from "./locales/es/config.json";
import esSidebar from "./locales/es/sidebar.json";
import esToolbar from "./locales/es/toolbar.json";
import esCard from "./locales/es/card.json";
import esShare from "./locales/es/share.json";

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      ca: {
        auth: caAuth,
        folder: caFolder,
        item: caItem,
        shared: caShared,
        common: caCommon,
        home: caHome,
        choose: caChoose,
        stats: caStats,
        config: caConfig,
        sidebar: caSidebar,
        toolbar: caToolbar,
        card: caCard,
        share: caShare,
      },
      en: {
        auth: enAuth,
        folder: enFolder,
        item: enItem,
        shared: enShared,
        common: enCommon,
        home: enHome,
        choose: enChoose,
        stats: enStats,
        config: enConfig,
        sidebar: enSidebar,
        toolbar: enToolbar,
        card: enCard,
        share: enShare,
      },
      es: {
        auth: esAuth,
        folder: esFolder,
        item: esItem,
        shared: esShared,
        common: esCommon,
        home: esHome,
        choose: esChoose,
        stats: esStats,
        config: esConfig,
        sidebar: esSidebar,
        toolbar: esToolbar,
        card: esCard,
        share: esShare,
      },
    },
    fallbackLng: "ca",
    lng: "ca",
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;