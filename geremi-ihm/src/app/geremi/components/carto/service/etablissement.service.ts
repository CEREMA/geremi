import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { FeatureCollection } from "geojson";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";

@Injectable({
  providedIn: 'root'
})
export class EtablissementService {
  private url = this.applicationConfigService.getEndpointFor("/etablissement");

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
  }

  findEtablissementInBox(annee: string, type: string): Observable<FeatureCollection> {
    const params = new HttpParams().set('annee', annee).set('type', type);
    return this.http.get<FeatureCollection>(this.url, { params });
  }

  findEtablissementsAnneeEtude(annee: number, idEtude: number, territoireSeul : boolean): Observable<FeatureCollection> {
    const params = new HttpParams().set('territoireSeul', territoireSeul);
    return this.http.get<FeatureCollection>(this.url+ '/' + annee + '/' + idEtude, {params});
  }

  findEtablissementDetailConfig(): Observable<Map<String, String>> {
    return this.http.get<Map<String, String>>(this.url + "/config/detail");
  }

  findEtablissementAffichageConfig(): Observable<Map<string, Array<string>>> {
    return this.http.get<Map<string, Array<string>>>(this.url + "/config/affichage");
  }

  findEtablissementTooltipConfig(): Observable<Array<string>> {
    return this.http.get<Array<string>>(this.url + "/config/tooltip");
  }

  getDistinctAnnees(): Observable<string[]> {
    return this.http.get<string[]>(`${this.url}/annees`);
  }

}
