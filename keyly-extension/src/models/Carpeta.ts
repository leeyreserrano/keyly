import type { Item } from "./Item";

export type Carpeta = {
  uuid: string;
  nom: string;
  favorit: boolean;
  dataCreacio: string;
  items: Item[];
};