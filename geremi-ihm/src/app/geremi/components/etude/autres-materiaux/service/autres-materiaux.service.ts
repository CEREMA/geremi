import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Materiau } from "../model/materiau.model";

@Injectable({
    providedIn: 'root'
})
export class AutresMateriauxService {
    private autresMateriauxResetSource = new Subject();
    private autresMateriauxInitSource = new Subject();

    private url = this.applicationConfigService.getEndpointFor("/materiau");

    autresMateriauxResetSource$ = this.autresMateriauxResetSource.asObservable();
    autresMateriauxInitSource$ = this.autresMateriauxInitSource.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    getMateriauxDisponibles(): Observable<Materiau[]> {
        return this.http.get<Materiau[]>(this.url+'/liste');
    }

    getMateriauxEtude(idEtude:number): Observable<Materiau[]> {
        return this.http.get<Materiau[]>(this.url+'/'+idEtude);
    }

    ajoutMateriau(materiau:Materiau): Observable<Object> {
        return this.http.post<Object>(this.url+'/add',materiau);
    }

    supprimerMateriau(idMateriau:number): Observable<Object> {
        return this.http.delete<Object>(this.url+'/supprimer/'+idMateriau);
    }

    initAutresMateriaux(){
        this.autresMateriauxInitSource.next(true);
    }

    reset() {
        this.autresMateriauxResetSource.next(false);
    }
}
