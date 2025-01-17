import { Feature } from "geojson";

export class InstallationStockage {

    id   : number;
    nomEtab      : String;
    codeEtab     : String;
    description  : String;
    anneeDebut   : number;
    anneeFin     : number;
    betonPref    : number;
    viabAutre    : number;
    tonTot       : number;
    feature      : Feature;
    idSource     : number;
    libellePere  : String;
    idFrere      : Number;
    supprimable  : boolean = false;
    afficher     : boolean = false;
    disableArrow : boolean = false;

    constructor() { };
}