import { Injectable } from "@angular/core";
import { Feature } from "geojson";
import { ArrayUtilsService } from "src/app/shared/service/arrayUtils.service";

@Injectable()
export class StyleLayersUtilsService {
    constructor(private arrayUtils:ArrayUtilsService){}

    getCSSVariableAsNumber = (variableName: string): number => {
        const variableValue = getComputedStyle(document.documentElement).getPropertyValue(variableName);
        return parseFloat(variableValue);
    };

    /**
     * Retourne le Style correspondant au type du layer
     * @param type type du layer
     * @returns map du style
     */
    getStyleLayer(type:String){
        const opacity = this.getCSSVariableAsNumber(`--${type}-opacity`);
        const weight = this.getCSSVariableAsNumber(`--${type}-weight`);
        const fillOpacity = this.getCSSVariableAsNumber(`--${type}-fill-opacity`);

        return {
            "color": `var(--${type}-color)`,
            "opacity": opacity,
            "weight": weight,
            "fillColor": `var(--${type}-fill)`,
            "fillOpacity": fillOpacity
        }
    }

    /**
     * Retourne le Style correspondant au type du layer exterieur
     * @param type type du layer
     * @returns map du style
     */
    getStyleLayerOut(type:String){
        const opacity = this.getCSSVariableAsNumber(`--${type}-opacity`);
        const weight = this.getCSSVariableAsNumber(`--${type}-weight`);
        const fillOpacity = this.getCSSVariableAsNumber(`--${type}-fill-opacity`);

        return {
            "color": `black`,
            "opacity": opacity,
            "weight": weight,
            "fillColor": `black`,
            "fillOpacity": 0.5
        }
    }

    /**
     * Retourne le Style correspondant au type du layer si cliqué ou non
     * @param features liste des features cliqués
     * @param feature feature à ajouter
     * @param type type du layer
     * @returns retourne le style correspondant
     */
    getStyleClicked(features:Feature[], feature: Feature, type:String){
        if(this.arrayUtils.includeFeature(features,feature)){
          return {
            "color": `var(--${type}-color)`,
            "opacity": 1,
            "weight": 1,
            "fillColor": `var(--${type}-fill)`,
            "fillOpacity": 0.5,
          }
        }

        return this.getStyleLayer(type);
    }
}