import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { Scenario } from "../../model/scenario.model";
import { Materiau } from "../../../autres-materiaux/model/materiau.model";
import { ScenarioMateriau } from "../model/scenario-materiau.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioMateriauService {

    private scenarioMateriauSource = new Subject<Scenario>();
    private scenarioMateriauDisplayEtiquetteSource = new Subject<ScenarioMateriau[]>();
    private scenarioMateriauDisplayEtiquetteMoveEndSource = new Subject();

    scenarioMateriauSource$ = this.scenarioMateriauSource.asObservable();
    scenarioMateriauDisplayEtiquetteSource$ = this.scenarioMateriauDisplayEtiquetteSource.asObservable();
    scenarioMateriauDisplayEtiquetteMoveEndSource$ = this.scenarioMateriauDisplayEtiquetteMoveEndSource.asObservable();
    
    constructor() { }

    ajoutMateriauScenario(scenario:Scenario, materiaux:Materiau[]){
        scenario.scenario_materiaux = [];
        for(let m of materiaux){
            for(let s of m.scenario_materiau){
                if(s.id_rel_scenario_materiau === undefined)
                    s.id_rel_scenario_materiau = 0;
                scenario.scenario_materiaux.push(s);
            }      
        }
        return this.scenarioMateriauSource.next(scenario);
    }

    displayEtiquetteOnZone(relationsMateriaux:ScenarioMateriau[]){
        this.scenarioMateriauDisplayEtiquetteSource.next(relationsMateriaux);
    }

    displayEtiquetteOnZoneAfterMapMove(){
        this.scenarioMateriauDisplayEtiquetteMoveEndSource.next(true);
    }
}