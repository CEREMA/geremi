import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Scenario } from "../../model/scenario.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioCreationService {
    private scenarioCreationCloseSource = new Subject();
    private scenarioCreationOpenSource = new Subject<Scenario>();

    private url = this.applicationConfigService.getEndpointFor("/scenario");

    scenarioCreationCloseSource$ = this.scenarioCreationCloseSource.asObservable();
    scenarioCreationOpenSource$ = this.scenarioCreationOpenSource.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    scenarioCreationClose(){
        return this.scenarioCreationCloseSource.next(true);
    }

    scenarioCreationOpen(value:Scenario){
        return this.scenarioCreationOpenSource.next(value);
    }
}