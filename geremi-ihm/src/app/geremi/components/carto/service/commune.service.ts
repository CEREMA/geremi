import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { FeatureCollection } from "geojson";
import { Observable } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { GeoService } from "./geo-service.interface";

@Injectable({
    providedIn: 'root'
})
export class CommuneService implements GeoService{

    private url = this.applicationConfigService.getEndpointFor("/commune");

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
}