import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service"; 
import { Scenario } from "../../model/scenario.model";
import { InstallationStockage } from "../../../installation-stockage/model/installation-stockage.model";
import { ScenarioInstallation } from "../model/scenario-installation.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioInstallationService {
    private scenarioInstallationSource = new Subject<Scenario>();
    scenarioInstallationSource$ = this.scenarioInstallationSource.asObservable();

    private url = this.applicationConfigService.getEndpointFor("/scenarioInstallation");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    ajoutInstallationScenario(scenario: Scenario, installations: InstallationStockage[]){        
        
        let scenarioInstallationList: ScenarioInstallation[] = [];
        
        installations.forEach(installation => {
            let scenarioInstallation = new ScenarioInstallation();
            scenarioInstallation.id_rel_scenario_installation = 0;
            scenarioInstallation.id_scenario = scenario.id;
            scenarioInstallation.id_installation = installation.id;
            scenarioInstallationList.push(scenarioInstallation);
        });
        scenario.scenario_installations = scenarioInstallationList;
        this.scenarioInstallationSource.next(scenario);        
    }
    
}
