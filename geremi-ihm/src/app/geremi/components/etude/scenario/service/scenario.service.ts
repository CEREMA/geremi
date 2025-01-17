import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Scenario } from "../model/scenario.model";

@Injectable({
    providedIn: 'root'
})
export class ScenarioService {
    private scenarioClearSource = new Subject();
    private scenarioCloseSource = new Subject();
    private scenarioOpenSource = new Subject();
    private scenarioOpenAccordeonSource = new Subject();
    private scenarioCloseAccordeonSource = new Subject();
    private scenarioLayerTerritoireSource = new Subject();
    private scenarioContrainteSubject = new Subject();
    private scenarioChantierSubject = new Subject();
    private scenarioInstallationStockageSubject = new Subject();
    private scenarioMateriauSubject = new Subject();
    private scenarioRetenuSubject = new Subject();

    private url = this.applicationConfigService.getEndpointFor("/scenario");

    scenarioClearSource$ = this.scenarioClearSource.asObservable();
    scenarioCloseSource$ = this.scenarioCloseSource.asObservable();
    scenarioOpenSource$ = this.scenarioOpenSource.asObservable();
    scenarioOpenAccordeonSource$ = this.scenarioOpenAccordeonSource.asObservable();
    scenarioCloseAccordeonSource$ = this.scenarioCloseAccordeonSource.asObservable();
    scenarioLayerTerritoireSource$ = this.scenarioLayerTerritoireSource.asObservable();
    scenarioContrainteSubject$ = this.scenarioContrainteSubject.asObservable();
    scenarioChantierSubject$ = this.scenarioChantierSubject.asObservable();
    scenarioInstallationStockageSubject$ = this.scenarioInstallationStockageSubject.asObservable();
    scenarioMateriauSubject$ = this.scenarioMateriauSubject.asObservable();
    scenarioRetenuSubject$ = this.scenarioRetenuSubject.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getListScenarioEtude(idEtude:Number): Observable<Scenario[]>{
        return this.http.get<Scenario[]>(this.url+'/liste/'+idEtude);
    }

    getScenarioById(idScenario:Number): Observable<Scenario>{
        return this.http.get<Scenario>(this.url+'/'+idScenario);
    }

    getScenarioValideByIdEtude(idEtude:Number): Observable<Scenario>{
        return this.http.get<Scenario>(this.url+'/suivi/'+idEtude);
    }

    exportScenarioValideByIdEtude(idEtude:Number): Observable<Blob>{
        return this.http.get(this.url+'/suivi/export/'+idEtude,{ responseType: 'blob' });
    }

    addScenario(scenario:Scenario): Observable<Scenario>{
        return this.http.post<Scenario>(this.url+'/ajouter',scenario);
    }

    updateScenario(scenario:Scenario): Observable<Scenario>{
        return this.http.put<Scenario>(this.url+'/modification',scenario);
    }

    deleteScenario(idScenario:Number): Observable<Object>{
        return this.http.delete<Object>(this.url+'/supprimer/'+idScenario);
    }

    ajoutContrainteScenario(scenario: Scenario): Observable<Scenario> {
        return this.http.post<Scenario>(this.url+'/ajoutContraintes', scenario);
    }

    ajoutInstallationScenario(scenario: Scenario): Observable<Scenario> {
        return this.http.post<Scenario>(this.url+'/ajoutScenarioInstallations', scenario);
    }

    ajoutChantierScenario(scenario: Scenario): Observable<Scenario> {
        return this.http.post<Scenario>(this.url+'/ajoutChantiers', scenario);
    }

    ajoutMateriauScenario(scenario: Scenario): Observable<Scenario> {
        return this.http.post<Scenario>(this.url+'/ajoutMateriaux', scenario);
    }

    setScenarioRetenu(scenario: Scenario): Observable<Scenario> {
        return this.http.post<Scenario>(this.url+'/retenu', scenario);
    }

    scenarioClear(){
        return this.scenarioClearSource.next(true);
    }

    scenarioClose(){
        return this.scenarioCloseSource.next(true);
    }

    scenarioOpen(){
        return this.scenarioOpenSource.next(true);
    }

    scenarioOpenAccordeon(){
        return this.scenarioOpenAccordeonSource.next(true);
    }

    scenarioCloseAccordeon(){
        return this.scenarioCloseAccordeonSource.next(true);
    }

    setScenarioLayerTerritoire(){
        return this.scenarioLayerTerritoireSource.next(true);
    }

    scenarioContrainte(){
        return this.scenarioContrainteSubject.next(true);
    }

    scenarioChantier(){
        return this.scenarioChantierSubject.next(true);
    }

    scenarioInstallationStockage(){
        return this.scenarioInstallationStockageSubject.next(true);
    }

    scenarioMateriau(){
        return this.scenarioMateriauSubject.next(true);
    }

}
