import { ChangeDetectorRef, Component, Input, Output, EventEmitter } from "@angular/core";
import { Feature } from "geojson";
import { EtablissementService } from "../../carto/service/etablissement.service";
import {Etablissement} from "../../config-carto/model/Etablissement.model";

@Component({
    selector: 'app-panneau-lateral',
    templateUrl: './panneaulateral.component.html',
    styleUrls: ['./panneaulateral.component.scss']
  })
  export class PanneauLateralComponent {
    @Input() feature: Feature;
    @Input() visiblePanneauLateral: any = false;

    @Output() focusEvent = new EventEmitter();
    // Carriere
    carriere: any;
    // Configurations des affichages
    detailPrincipal: Array<string>;
    detailTraitementDechet: Array<string>;
    detailActivite: Array<string>;
    detailDestination: Array<string>;

    // Configurations des libellés
    detailConfig: any;

    constructor(private cdr: ChangeDetectorRef,
                private etablissementService: EtablissementService){}

    ngOnInit(){
      // Recherche des libellés
      this.etablissementService.findEtablissementDetailConfig().subscribe((data) => {
        this.detailConfig = data;
      });
      // Recherche des configurations d'affichages
      this.etablissementService.findEtablissementAffichageConfig().subscribe((data) => {
        this.setAffichageConfig(data);
      });
    }

    ngAfterViewInit(): void {

    }


    displayAcordeonTab(baseObject: any, propList : String[]) :boolean {
      for (let prop of  propList) {
        if(this.computePropertyValue(baseObject, prop) != null) {
          return true;
        }
      }
      return false;
    }

    computePropertyValue(baseObject: any, field: any) {

      if (baseObject == null) {
        return null;
      }
      let str = undefined;
      if(Object.prototype.toString.call(baseObject) === '[object Array]') {
        let tab: any[] = baseObject.map((o: any) =>  this.aggregateValues(o, field)).filter((element: any) => element);
        return tab.length > 0 ? tab : null;
      } else {
        str = this.aggregateValues(baseObject, field);
      }
      if (str != null) {
        return [str];
      }
      return str;
    }

    aggregateValues(baseObject: any, field: any) {
      if (this.detailConfig![field]! != null && this.detailConfig![field]!['attrList'] != null && this.detailConfig![field]!['attrListSeparator'] != null) {
        let elemArray:any[] = this.detailConfig![field]!['attrList'].map((f: any) => this.aggregateValues(baseObject!,f)).filter((element: any) => element);
        if(elemArray.length > 0) {
          let strConcat: string = this.concatFieldList('', elemArray[0],this.detailConfig![field]!['attrListSeparator']);
          for (let i = 1; i <  elemArray.length; i++) {

            strConcat = this.concatFieldList(strConcat, elemArray[i],this.detailConfig![field]!['attrListSeparator']);
          }
          return strConcat === '' ? undefined : strConcat;
        } else {
          return undefined;
        }

      } else {
        return this.readFieldValue(baseObject!,field);
      }
    }

    concatFieldList(strConcat: string, value:string, separator:string) {
      if (value.length > 2 && value.charAt(0) === '\"' && value.charAt(value.length-1) === '\"') {
        if (strConcat === '') {
          return '' // cas particulier, on ne concatène jamais une constante avec une chaine vide
        }
        if (separator !== '') {
          return strConcat.concat(' ', separator ,' ', value.substring(1, value.length - 1));
        } else {
          return strConcat.concat(' ', value.substring(1, value.length - 1));
        }
      } else {
        if (strConcat === '') {
          return value.toString()
        }
        if (separator !== '') {
          return strConcat.concat(' ', separator ,' ', value.toString());
        } else {
          return strConcat.concat(' ', value.toString());
        }
      }
    }

    readFieldValue(baseObject: any,field: any) {
      if (field.length > 2 && field.charAt(0) === '\"' && field.charAt(field.length-1) === '\"') {
        return field;
      }
      if (baseObject[field]! == null) {
        return undefined;
      }
      //conversion en chaine de caractère, pour éviter que la valeur 0 (en integer) ne soit filtrée
      return `${baseObject[field]!}`;
    }

    setAffichageConfig(data:any){
      this.detailPrincipal = data.principal!;
      this.detailTraitementDechet = data.traitementDechet!;
      this.detailActivite = data.activite!;
      this.detailDestination = data.destination!;
    }

    focusOnMarker(){
      this.focusEvent.emit();
    }

    closePanneauLateral(){
      this.visiblePanneauLateral = false;
      this.forceRerender();
    }

    forceRerender(){
      this.cdr.detectChanges();
    }
  }
