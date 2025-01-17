import { TileLayer } from "leaflet";

export interface ExtraLayer {
    code:string;
    label:string;
    layer:TileLayer
}