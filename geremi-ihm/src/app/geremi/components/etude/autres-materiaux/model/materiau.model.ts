import { ScenarioMateriau } from "../../scenario/scenario-materiau/model/scenario-materiau.model";

export class Materiau {
    id_materiau: number;
    libelle: String;
    type: String;
    origine: String;
    tonnage: number;
    id_etude: Number;

    scenario_materiau:ScenarioMateriau[];
}