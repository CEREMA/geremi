import { Injectable } from "@angular/core";
import { Observable, Subject, map } from "rxjs";
import { Scenario } from "../../model/scenario.model";
import { HttpClient } from "@angular/common/http";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { ProductionZone } from "../model/productionZone.model";
import { ZoneProductionDetails } from "../model/zoneProductionDetails.model";
import { Message } from "primeng/api";
import { DepartementDTO } from "../model/departementDTO.model";
import { RelScenarioDepartement } from "../model/relScenarioDepartement.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioCalculService {

    private scenarioCalculVentilationSource = new Subject<Scenario>();
    private scenarioCalculProjectionSource = new Subject<Scenario>();
    private scenarioCalculEtapeSource = new Subject<Number>();
    private displayScenarioCalculVentilationSource = new Subject<Scenario>();
    private displayScenarioCalculVentilationAnneeSource = new Subject<Number>();
    private displayScenarioCalculProjectionSource = new Subject<Scenario>();
    private displayScenarioCalculProjectionAnneeSource = new Subject<Number>();
    private displayScenarioCalculProjectionSuiviEtudeSource = new Subject<Scenario>();
    private displayScenarioCalculProjectionSuiviEtudeActualiserSource = new Subject();
    private zoneProductionDetailsSource = new Subject<ZoneProductionDetails[]>();
    private messageSubject = new Subject<Message[]>();


    scenarioCalculVentilationSource$ = this.scenarioCalculVentilationSource.asObservable();
    scenarioCalculProjectionSource$ = this.scenarioCalculProjectionSource.asObservable();
    scenarioCalculEtapeSource$ = this.scenarioCalculEtapeSource.asObservable();
    displayScenarioCalculVentilationSource$ = this.displayScenarioCalculVentilationSource.asObservable();
    displayScenarioCalculVentilationAnneeSource$ = this.displayScenarioCalculVentilationAnneeSource.asObservable();
    displayScenarioCalculProjectionSource$ = this.displayScenarioCalculProjectionSource.asObservable();
    displayScenarioCalculProjectionAnneeSource$ = this.displayScenarioCalculProjectionAnneeSource.asObservable();
    displayScenarioCalculProjectionSuiviEtudeSource$ = this.displayScenarioCalculProjectionSuiviEtudeSource.asObservable();
    displayScenarioCalculProjectionSuiviEtudeActualiserSource$ = this.displayScenarioCalculProjectionSuiviEtudeActualiserSource.asObservable();

    zoneProductionDetails$ = this.zoneProductionDetailsSource.asObservable();
    message$ = this.messageSubject.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    scenarioCalculVentilation(value:Scenario){
        return this.scenarioCalculVentilationSource.next(value);
    }

    scenarioCalculProjection(value:Scenario){
        return this.scenarioCalculProjectionSource.next(value);
    }

    displayScenarioCalculVentilationAnnee(value:Number){
        return this.displayScenarioCalculVentilationAnneeSource.next(value);
    }

    displayScenarioCalculVentilation(value:Scenario){
        return this.displayScenarioCalculVentilationSource.next(value);
    }

    displayScenarioCalculProjection(value:Scenario){
        return this.displayScenarioCalculProjectionSource.next(value);
    }

    displayScenarioCalculProjectionAnnee(value:Number){
        return this.displayScenarioCalculProjectionAnneeSource.next(value);
    }

    displayScenarioCalculProjectionSuiviEtude(value:Scenario){
        return this.displayScenarioCalculProjectionSuiviEtudeSource.next(value);
    }

    displayScenarioCalculProjectionSuiviEtudeActualiser(){
        return this.displayScenarioCalculProjectionSuiviEtudeActualiserSource.next(true);
    }

    calculProductionZone(scenario:Scenario,anneeRef:Number): Observable<Scenario>{
        return this.http.post<Scenario>(this.applicationConfigService.getEndpointFor("/calculsproductiondepartement/")+`${scenario.etudeDTO.id}/${scenario.id}/${anneeRef}`, scenario);
    }

    calculProjectionZone(scenario:Scenario): Observable<Scenario>{
        return this.http.post<Scenario>(this.applicationConfigService.getEndpointFor("/calculsprojectionzone"), scenario);
    }

    getZoneProductionDetails(zoneDetails: ZoneProductionDetails[]): void {
        return this.zoneProductionDetailsSource.next(zoneDetails);
    }

    setMessage(message: Message[]) {
        return this.messageSubject.next(message);
    }

    scenarioEtape(indexEtape:Number){
        return this.scenarioCalculEtapeSource.next(indexEtape);
    }

    getDepartementsPartielsEtude(idEtude:number): Observable<RelScenarioDepartement[]>{
        let response = this.http.get<RelScenarioDepartement[]>(this.applicationConfigService.getEndpointFor("/departement/partiel/"+idEtude)).pipe(
            map(rel => {
                let deps = [];
                for(let d of rel){
                    let prod = new RelScenarioDepartement();
                    prod.id_rel_scenario_departement = 0;
                    prod.id_departement = d.id_departement;
                    prod.nom = d.nom;
                    prod.repartition_departement_beton = d.repartition_departement_beton;
                    prod.repartition_departement_viab = d.repartition_departement_viab
                    deps.push(prod);
                }
                return deps;
            })
        );
        return response;
    }
}
