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
};

type CompartitItem = {
    uuid: string,
    titol: string,
    dinsDeCarpeta: boolean
};

enum TipusEntitat {
    CARPETA,
    ITEM
};

enum Permisos {
    LECTURA,
    ESCRIPTURA,
    ADMINISTRADOR
};