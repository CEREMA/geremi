export class ScenarioMateriau {
    id_rel_scenario_materiau:Number;
    id_scenario:Number;
    id_zone:Number;
    id_materiau:Number;
    libelle: String;
    origine: String;
    pourcent:number;
    tonnage:number;
}

export class ScenarioDepartement {
    id_rel_scenario_departement:Number;
    id_scenario:Number;
    id_departement:Number;
    repartition_departement:Number;
    date_maj:Date;
}