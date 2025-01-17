import { FeatureCollection } from "geojson";
import { Observable } from "rxjs";

export interface GeoService{
  findAll(): Observable<FeatureCollection>;
  findInBox(bounds: L.LatLngBounds, precision: number): Observable<FeatureCollection>;
}
 