import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { Scenario } from "../../model/scenario.model";
import { ContrainteEnvironnementale } from "../../../contraintes-environnementales/model/contrainteEnv.model";
import { ScenarioContrainte } from "../model/scenario-contrainte.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioContrainteService {
    private scenarioContrainteSource = new Subject<Scenario>();
    private scenarioContrainteTauxRenouvellementSource = new Subject<ContrainteEnvironnementale[]>();
    private scenarioContrainteActualiserTauxSource = new Subject();
    private scenarioContrainteClearTauxSource = new Subject();
    private scenarioContrainteActualiserSuiviEtudeTauxSource = new Subject();
    scenarioContrainteSource$ = this.scenarioContrainteSource.asObservable();
    scenarioContrainteTauxRenouvellementSource$ = this.scenarioContrainteTauxRenouvellementSource.asObservable();
    scenarioContrainteActualiserTauxSource$ = this.scenarioContrainteActualiserTauxSource.asObservable();
    scenarioContrainteClearTauxSource$ = this.scenarioContrainteClearTauxSource.asObservable();
    scenarioContrainteActualiserSuiviEtudeTauxSource$ = this.scenarioContrainteActualiserSuiviEtudeTauxSource.asObservable();

    constructor() { }

    ajoutContrainteScenario(scenario:Scenario, contraintes:ContrainteEnvironnementale[]){
        let scenarioContrainteList: ScenarioContrainte[] = [];

        contraintes.forEach(contrainte => {
            let scenarioContrainte = new ScenarioContrainte();
            scenarioContrainte.id_rel_scenario_contrainte = 0;
            scenarioContrainte.id_scenario = scenario.id;
            scenarioContrainte.id_contrainte = contrainte.id;
            scenarioContrainte.tx_renouvellement_contrainte = contrainte.tx_renouvellement_contrainte;
            scenarioContrainteList.push(scenarioContrainte);
        });
        scenario.scenario_contraintes = scenarioContrainteList;

        return this.scenarioContrainteSource.next(scenario);
    }

    onChangeTauxRenouvellement(contrainte: ContrainteEnvironnementale[]){
        return this.scenarioContrainteTauxRenouvellementSource.next(contrainte);
    }

    actualiserTauxRenouvellement(){
        return this.scenarioContrainteActualiserTauxSource.next(true);
    }

    clearTauxRenouvellement(){
        return this.scenarioContrainteClearTauxSource.next(true);
    }

    actualiserSuiviEtudeTauxRenouvellement(){
        return this.scenarioContrainteActualiserSuiviEtudeTauxSource.next(true);
    }
}
