import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Territoire } from "../model/territoire.model";
import { Feature } from "geojson";

@Injectable({
    providedIn: 'root'
})
export class TerritoireService {

    private url = this.applicationConfigService.getEndpointFor("/territoire");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getTerritoireById(id: number): Observable<Territoire> {
        let params = new HttpParams().set('id', id.toString()) ;
        return this.http.get<Territoire>(this.url ,  {params});
    }

    getTerritoireByIdWithPrecision(id: number,precision:number): Observable<Feature> {
        let params = new HttpParams().set('id', id.toString()).set('precision',precision.toString()) ;
        return this.http.get<Feature>(this.url+"/find" ,  {params});
    }

    addTerritoire(territoire: Territoire): Observable<Territoire> {
        console.log("Territoire Send");
        return this.http.post<Territoire>(this.url, territoire);
    }

    validationCreationTerritoire(territoire: Territoire): Observable<Territoire> {
        console.log("Territoire Validation");
        return this.http.post<Territoire>(this.url+"/validation", territoire);
    }

    updateTerritoire(id: number, territoire: Territoire): Observable<Territoire> {
        return this.http.put<Territoire>(this.url, territoire);
    }

    deleteTerritoire(idEtude: number): Observable<void> {
        return this.http.delete<void>(this.url+'/delete/'+idEtude);
    }
}
