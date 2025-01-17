import { Injectable } from "@angular/core";
import { TileLayer, tileLayer } from "leaflet";

@Injectable({
    providedIn: 'root'
})
export class BackgroundMapService {

    private osmTileLayer:TileLayer = tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      opacity: 0.8,
      maxZoom: 18,
      attribution: 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors'
    });

    private ignTileLayer:TileLayer = tileLayer('https://data.geopf.fr/wmts?layer=GEOGRAPHICALGRIDSYSTEMS.PLANIGNV2&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
      opacity: 0.5,
      maxZoom: 18,
        attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>'
    });

    private bgrmTileLayer:TileLayer = tileLayer.wms('https://geoservices.brgm.fr/geologie?', {
      layers: 'GEOLOGIE',
      maxZoom: 18,
      attribution: '<a href="https://www.brgm.fr/">©BRGM</a>'
    });

      private backgroundMap = new Map<string, TileLayer>([
        ["OSM", this.osmTileLayer],
        ["IGN", this.ignTileLayer],
        ["BRGM", this.bgrmTileLayer]
    ]);

    all():Array<TileLayer> {
        return Array.from(this.backgroundMap.values());
    }

    getTileLayer(type: string):any {
        return this.backgroundMap.get(type);
    }

}
