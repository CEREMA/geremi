import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { FeatureCollection } from "geojson";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";

@Injectable({
    providedIn: 'root'
})
export class SelectionZonageService {
    private url = this.applicationConfigService.getEndpointFor("/selectionzonage");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    findZonageInTerritoire(zonage:String, territoire:String, liste_id:String[] , precision:number): Observable<FeatureCollection> {

        let liste_id_to_send = '';
        let i = 0;
        for (const iterator of liste_id) {
          if(i != 0)
            liste_id_to_send += ', ';
          i += 1;
          liste_id_to_send += iterator; 
        }

        let params = new HttpParams()
            .set('zonage',zonage.toString())
            .set('territoire',territoire.toString())
            .set('liste_id', liste_id_to_send)
            .set('precision',precision);

        return this.http.get<FeatureCollection>(this.url, { params });
    }
}