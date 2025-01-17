import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Zonage } from "../model/zonage.model";
import { FeatureCollection } from "geojson";

@Injectable({
    providedIn: 'root'
})
export class ZonageService {

    private url = this.applicationConfigService.getEndpointFor("/zone");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getZonageById(id: number): Observable<Zonage> {
        let params = new HttpParams().set('id', id.toString()) ;
        return this.http.get<Zonage>(this.url ,  {params});
    }

    getZonageFeatureByEtudeId(id: number,precision: number): Observable<FeatureCollection> {
        let params = new HttpParams().set('idEtude', id.toString()) ;
        params = params.set('precision',precision);
        return this.http.get<FeatureCollection>(this.url+'/feature' ,  {params});
    }

    getZonageByEtudeId(id: Number): Observable<Zonage[]> {
        let params = new HttpParams().set('idEtude', id.toString()) ;
        return this.http.get<Zonage[]>(this.url, {params});
    }

    getZonageByEtudeIdBeforeAdd(id: number,precision: number): Observable<FeatureCollection> {
        let params = new HttpParams().set('idEtude', id.toString()) ;
        params = params.set('precision',precision);
        return this.http.get<FeatureCollection>(this.url+'/before' ,  {params});
    }

    addZonage(zonage: Zonage): Observable<Zonage> {
        return this.http.post<Zonage>(this.url, zonage);
    }

    updateZonage(id: number, zonage: Zonage): Observable<Zonage> {    
        return this.http.put<Zonage>(this.url, zonage);
    }

    deleteZonage(id: number): Observable<void> {
        let params = new HttpParams().set('id', id) ;
    return this.http.delete<void>(this.url,  {params});
    }

    getZoneNamesByZoneIds(ids: Number[]): Observable<string[]> {
        let params = new HttpParams().set('ids', ids.join(','));
        return this.http.get<string[]>(this.url + '/names', {params});
    }    
    
}