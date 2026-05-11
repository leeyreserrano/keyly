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
    },
    fallbackLng: "ca",
    lng: "ca",
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;