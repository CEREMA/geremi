import { Injectable } from "@angular/core";
import { Feature } from "geojson";
import { Materiau } from "src/app/geremi/components/etude/autres-materiaux/model/materiau.model";
import { Chantier } from "src/app/geremi/components/etude/chantiers-envergure/model/chantiers.model";

@Injectable()
export class ArrayUtilsService {
    arrayRemoveFeature(array:Array<Feature> ,value: Feature): Feature[] {
        return array.filter(function (geeks) {
            return geeks.id != value.id;
        });
    }
    
    includeFeature(array:Array<Feature> ,value: Feature):boolean{
      for (const iterator of array) {
        if(iterator.id === value.id)
          return true;
      }
      return false;
    }

    arrayRemoveAny(array:any[] ,value: any): any[] {
        return array.filter(function (geeks) {
            return geeks != value;
        });
    }
    
    includeAny(array:any[] ,value: any):boolean{
      for (const iterator of array) {
        if(iterator === value)
          return true;
      }
      return false;
    }

    arrayRemoveId(array:any[] ,value: any): any[] {
      return array.filter(function (geeks) {
          return geeks.id != value.id;
      });
    }

    includeId(array:any[] ,value: any):boolean{
      for (const iterator of array) {
        if(iterator.id === value.id)
          return true;
      }
      return false;
    }

    addAny(array:any[] ,value: any): any[] {
      let tmp = [];
      array.forEach(e => tmp.push(e));
      tmp.push(value);
      return tmp;
    }

    arrayRemoveIdChantier(array:Chantier[] ,value: Chantier): Chantier[] {
      return array.filter(function (geeks) {
          return geeks.id_chantier != value.id_chantier;
      });
    }
    arrayRemoveIdMateriau(array:Materiau[] ,value: Materiau): Materiau[] {
      return array.filter(function (geeks) {
          return geeks.id_materiau != value.id_materiau;
      });
    }
}