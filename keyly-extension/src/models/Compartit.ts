export type Compartit = {
    uuid: string;
    usuariCreador: {
        uuid: string;
        nom: string;
    },
    tipusEntitat: TipusEntitat;
    permisos: Permisos;
    carpeta?: CompartitCarpeta;
    item?: CompartitItem;
};

type CompartitCarpeta = {
    uuid: string,
    nom: string
    favorit: boolean
};

type CompartitItem = {
    uuid: string,
    titol: string,
    nomUsuari: string,
    url: string,
    favorit: boolean
    dinsDeCarpeta: boolean
};

export enum TipusEntitat {
    CARPETA = "CARPETA",
    ITEM = "ITEM"
};

enum Permisos {
    LECTURA,
    ESCRIPTURA,
    ADMINISTRADOR
};