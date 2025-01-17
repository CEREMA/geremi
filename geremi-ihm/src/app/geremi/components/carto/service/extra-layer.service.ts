import { Injectable } from "@angular/core";
import { TileLayer, tileLayer } from "leaflet";
import { ExtraLayer } from "../model/extra-layer.model";

@Injectable({
    providedIn: 'root'
})
export class ExtraLayerService {

    private extraLayerArray:Array<ExtraLayer> =  new Array<ExtraLayer>(


        {
            code:'CADASTRE',
            label:'Parcelles cadastrales',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=CADASTRALPARCELS.PARCELLAIRE_EXPRESS&style=PCI%20vecteur&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image%2Fpng&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'cadastre'
            })
        },
        {
            code:'ROUTE',
            label:'Routes',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=TRANSPORTNETWORKS.ROADS&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'reseau_routier'
            })
        },
        {
            code:'VOIESFERREES',
            label:'Voies ferrées',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=TRANSPORTNETWORKS.RAILWAYS&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'reseau_voies'
            })
        },
        {
            code:'HYDROGRAPHY',
            label:'Réseau hydrographique',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=HYDROGRAPHY.HYDROGRAPHY&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'reseau_hydro'
            })
        },
        {
            code:'ZNIEFFI',
            label:'ZNIEFF Type I',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.ZNIEFF1&style=PROTECTEDAREAS.ZNIEFF1&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'znieff1'
            })
        },
        {
            code:'ZNIEFFII',
            label:'ZNIEFF Type II',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.ZNIEFF2&style=PROTECTEDAREAS.ZNIEFF2&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'znieff2'
            })
        },
        {
            code:'PROTECTEDAREAS',
            label:'Périmètre de protection de Réserve Naturelle',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.MNHN.RN.PERIMETER&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'perimetre_naturel'
            })
        },
        {
            code:'PROTECTEDSITES',
            label:'Réserves naturelles Régionales',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDSITES.MNHN.RESERVES-REGIONALES&style=PROTECTEDSITES.MNHN.RESERVES-REGIONALES&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'reserves_naturelles_regionales'
            })
        },
        {
            code:'PROTECTEDAREAS.RN',
            label:'Réserves naturelles nationales',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.RN&style=PROTECTEDAREAS.RN&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'reserves_naturelles_nationales'
            })
        },
        {
            code:'PROTECTEDAREAS.ZPS',
            label:'Site NATURA 2000 ZPS',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.ZPS&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'natura'
            })
        },
        {
            code:'PROTECTEDAREAS.SIC',
            label:'Site NATURA 2000 SIC',
            layer: tileLayer('https://data.geopf.fr/wmts?layer=PROTECTEDAREAS.SIC&style=normal&tilematrixset=PM&Service=WMTS&Request=GetTile&Version=1.0.0&Format=image/png&TileMatrix={z}&TileCol={x}&TileRow={y}', {
                maxZoom: 18,
                attribution: '<a href="https://www.geoportail.gouv.fr/">©IGN</a>',
                pane: 'natura'
            })
        }

    );

    all():Array<ExtraLayer>{
        return this.extraLayerArray;
    }

    getLayer(type: string): TileLayer{
        return this.extraLayerArray.filter(l => l.code === type)[0].layer
    }
}
