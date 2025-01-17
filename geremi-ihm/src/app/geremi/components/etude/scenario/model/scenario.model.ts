import { EtatEtape } from "src/app/shared/enums/etatEtape.enums";
import { Etude } from "../../model/etude.model";
import { ScenarioChantier } from "../scenario-chantier/model/scenario-chantier.model";
import { ScenarioInstallation } from "../scenario-installation/model/scenario-installation.model";
import { ScenarioContrainte } from "../scenario-contrainte/model/scenario-contrainte.model";
import { ZoneProductionDetails } from "../scenario-calcul/model/zoneProductionDetails.model";
import { ZoneDetails } from "../scenario-calcul/model/zoneDetails.model";
import { ScenarioMateriau } from "../scenario-materiau/model/scenario-materiau.model";
import { RelScenarioDepartement } from "../scenario-calcul/model/relScenarioDepartement.model";
import { ProductionZone } from "../scenario-calcul/model/productionZone.model";


export class Scenario {
    id: Number;
    nom: String;
    description: String;
    dynamique_demographique: String;
    tx_renouvellement_hc: Number;
    ponderation_surface_beton: Number;
    ponderation_surface_viabilite: Number;
    if_retenu: Boolean;
    date_maj: Date;
    scenario_contraintes: ScenarioContrainte[];
    scenario_chantiers: ScenarioChantier[];
    scenario_installations: ScenarioInstallation[];
    scenario_materiaux: ScenarioMateriau[];
    rel_scenario_departement: RelScenarioDepartement[];
    etatEtapes: any;
    etudeDTO: Etude;
    zone_production_details: ZoneProductionDetails[];
    zone_details: ZoneDetails[];
    
    production_zone: ProductionZone[];
    
    resultat_calcul: any;
    //Map<Integer, ResultatCalculDTO>; 

    constructor(id:Number, etude:Etude) {
        this.id = id;
        this.etudeDTO = etude;

        this.etatEtapes = {
            CREATION_SCENARIO : EtatEtape.NON_RENSEIGNE,
            CONTRAINTES_SCENARIO : EtatEtape.NON_RENSEIGNE,
            CHANTIERS_SCENARIO : EtatEtape.NON_RENSEIGNE,
            INSTALLATIONS_SCENARIO : EtatEtape.NON_RENSEIGNE,
            MATERIAUX_SCENARIO : EtatEtape.NON_RENSEIGNE
        }

        this.scenario_contraintes = [];
        this.scenario_chantiers = [];
        this.scenario_installations = [];
        this.scenario_materiaux = [];
        this.rel_scenario_departement = [];
    }
}
