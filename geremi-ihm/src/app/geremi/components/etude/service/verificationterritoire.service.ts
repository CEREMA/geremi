import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { TauxCourverture } from "../model/tauxcouverture.model";

@Injectable({
    providedIn: 'root'
})
export class VerificationTerritoireService {

    private url = this.applicationConfigService.getEndpointFor("/verification");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    findTauxTerritoireHorsFrance(idEtude: number): Observable<TauxCourverture> {
        let params = new HttpParams().set('idEtude', idEtude) ;
        return this.http.get<TauxCourverture>(`${this.url}/tauxhorsFrance`, {params});
    }

    findTauxTerritoireHorsRegion(idEtude: number): Observable<TauxCourverture> {
        let params = new HttpParams().set('idEtude', idEtude) ;
        return this.http.get<TauxCourverture>(`${this.url}/tauxhorsRegion`, {params});
    }
}
