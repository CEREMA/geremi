import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Scenario } from "../../model/scenario.model";
 
import { ScenarioChantier } from "../model/scenario-chantier.model";
import { Chantier } from "../../../chantiers-envergure/model/chantiers.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioChantierService {
    private scenarioChantierSource = new Subject<Scenario>();
    scenarioChantierSource$ = this.scenarioChantierSource.asObservable();
     

    constructor() { }

    ajoutChantierScenario(scenario: Scenario, chantiers: Chantier[]){
        let scenarioChantierList: ScenarioChantier[] = [];
        
        chantiers.forEach(chantier => {
            let scenarioChantier = new ScenarioChantier();
            scenarioChantier.id_rel_scenario_chantier = 0;
            scenarioChantier.id_scenario = scenario.id;
            scenarioChantier.id_chantier = chantier.id_chantier;
            scenarioChantierList.push(scenarioChantier);
        });
        scenario.scenario_chantiers = scenarioChantierList;
        this.scenarioChantierSource.next(scenario);        
    }
}
