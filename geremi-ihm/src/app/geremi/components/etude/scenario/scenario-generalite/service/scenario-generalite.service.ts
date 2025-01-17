import { Injectable } from '@angular/core'; 
import { Scenario } from '../../model/scenario.model';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScenarioGeneraliteService {

    private scenarioAjoutSource = new Subject<Scenario>();

    scenarioAjoutSource$ = this.scenarioAjoutSource.asObservable();

    ajoutScenario(scenario:Scenario){
        return this.scenarioAjoutSource.next(scenario);
    }
}
