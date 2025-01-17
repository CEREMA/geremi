import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Feature, FeatureCollection } from "geojson";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { GeoService } from "./geo-service.interface";

@Injectable({
    providedIn: 'root'
})
export class RegionService implements GeoService{

    private url = this.applicationConfigService.getEndpointFor("/region");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    findAll(): Observable<FeatureCollection> {
        return this.http.get<FeatureCollection>(this.url);

    }

    findInBox(bounds: L.LatLngBounds, precision:number): Observable<FeatureCollection> {

        // Todo : construire l'url avec les bounding box
        let params = new HttpParams()
            .set('bbox',
                bounds.getSouthWest().lng.toString() + ',' + // XMIN
                bounds.getSouthWest().lat.toString() + ',' + // YMIN
                bounds.getNorthEast().lng.toString() + ',' + // XMAX
                bounds.getNorthEast().lat.toString()         // YMAX
            )
            .set('precision', precision);

        return this.http.get<FeatureCollection>(this.url, { params })
    }

    findRegionFromId(idRegion:number, precision:number): Observable<Feature> {

        let params = new HttpParams()
            .set('idRegion',idRegion)
            .set('precision', precision);

        return this.http.get<Feature>(this.url+'/id', { params })
    }
  
}  