import { Feature } from "geojson";

export class ContrainteEnvironnementale {
    id: number;
    nom: String;
    descrip: String;
    niveau: String;
    feature: Feature;

    idSource: any = null;

    supprimable: boolean = false;
    afficher: boolean = false;

    tx_renouvellement_contrainte: any;
    constructor(){};
}
