import {FeatureCollection} from "geojson";
import {PopulationDTO} from "./population.model";

export class Zonage{
    id_zone : Number;
    type : String;
    nom : String;
    code: String;
    description : String;
    id_territoire : Number;
    features : FeatureCollection;
    populations : PopulationDTO[];
    id_etude : Number;

    constructor(){}
}
