
import { Zonage } from "./zonage.model";

/**
 * TODO à refaire
 */
export class Territoire{
    id_territoire : number;
    type : String;
    nom : String;
    description : String;
    liste_territoire : String;
    liste_id : Number[];
    type_zonage : String;
    id_etude : Number;
    zone: Zonage;
    valideEtape: boolean;

    constructor(){}
}
