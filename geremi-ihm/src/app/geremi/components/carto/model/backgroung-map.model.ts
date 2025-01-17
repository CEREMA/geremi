import { TileLayer } from "leaflet";

export interface BackgroundMap {
    code:string;
    label:string;
    layer:TileLayer
}