import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { Territoire } from "../../etude/model/territoire.model";
import { ContrainteEnvironnementale } from "../../etude/contraintes-environnementales/model/contrainteEnv.model";
import { Chantier } from "../../etude/chantiers-envergure/model/chantiers.model";
import { InstallationStockage } from "../../etude/installation-stockage/model/installation-stockage.model";
import {FeatureCollection} from "geojson";
import { Etude } from "../../etude/model/etude.model";

@Injectable({
    providedIn: 'root'
})
export class LayersService {
    private layersSource = new Subject<String>();
    private layersEtudeSource = new Subject<String>();
    private extraLayersSource = new Subject<String>();
    private extraLayersEtudeSource = new Subject<String>();
    private backgroundLayersSource = new Subject<String>();
    private layerRefRegionSource = new Subject<number>();
    private layersResetSource = new Subject<boolean>();
    private layerFlyToSource = new Subject<String>();
    private layerEtudeTerritoireSource = new Subject<Territoire>();
    private layerEtudeContraintesSource = new Subject<ContrainteEnvironnementale[]>();
    private layerEtudeContraintesExistantesSource = new Subject<ContrainteEnvironnementale[]>();

    private layerEtudeInstallationStockagesSource = new Subject<InstallationStockage[]>();
    private layerEtudeInstallationStockagesExistantesSource = new Subject<InstallationStockage[]>();
    private layerEtudeInstallationStockagesNonActiveExistantesSource = new Subject<InstallationStockage[]>();

    private layerEtudeChantiersSource = new Subject<Chantier[]>();
    private layerEtudeChantiersExistantsSource = new Subject<Chantier[]>();

    private layerEtablissementsEtudeSource = new Subject<FeatureCollection>();

    private displayEtudeConsultationSource = new Subject<Etude[]>();
    private clearEtudeConsultationSource = new Subject();

    layersSource$ = this.layersSource.asObservable();
    layersEtudeSource$ = this.layersEtudeSource.asObservable();
    extraLayersSource$ = this.extraLayersSource.asObservable();
    extraLayersEtudeSource$ = this.extraLayersEtudeSource.asObservable();
    backgroundLayersSource$ = this.backgroundLayersSource.asObservable();
    layerRefRegionSource$ = this.layerRefRegionSource.asObservable();
    layersResetSource$ = this.layersResetSource.asObservable();
    layerFlyToSource$ = this.layerFlyToSource.asObservable();
    layerEtudeTerritoireSource$ = this.layerEtudeTerritoireSource.asObservable();
    layerEtudeContraintesSource$ = this.layerEtudeContraintesSource.asObservable();
    layerEtudeContraintesExistantesSource$ = this.layerEtudeContraintesExistantesSource.asObservable();
    layerEtudeInstallationStockagesSource$ = this.layerEtudeInstallationStockagesSource.asObservable();
    layerEtudeInstallationStockagesExistantesSource$ = this.layerEtudeInstallationStockagesExistantesSource.asObservable();
    layerEtudeInstallationStockagesNonActiveExistantesSource$  = this.layerEtudeInstallationStockagesNonActiveExistantesSource.asObservable();
    layerEtudeChantiersSource$ = this.layerEtudeChantiersSource.asObservable();
    layerEtudeChantiersExistantsSource$ = this.layerEtudeChantiersExistantsSource.asObservable();

    layerEtablissementsEtudeSource$ = this.layerEtablissementsEtudeSource.asObservable();

    displayEtudeConsultationSource$ = this.displayEtudeConsultationSource.asObservable();
    clearEtudeConsultationSource$= this.clearEtudeConsultationSource.asObservable();

    onLayersChange(event: String) {
        this.layersSource.next(event);
    }

    onLayersEtudeChange(event: String) {
        this.layersEtudeSource.next(event);
    }

    onExtraLayersChange(event: String) {
        this.extraLayersSource.next(event);
    }

    onExtraLayersEtudeChange(event: String) {
        this.extraLayersEtudeSource.next(event);
    }

    onBackgroundLayersChange(event: String) {
        this.backgroundLayersSource.next(event);
    }

    onRefRegionLayersChange(event: number) {
        this.layerRefRegionSource.next(event);
    }

    onFlyToLayerEvent(event: String) {
        this.layerFlyToSource.next(event);
    }

    displayEtudeTerritoireLayer(event: Territoire) {
        this.layerEtudeTerritoireSource.next(event);
    }

    displayEtablissementsEtude(event: FeatureCollection) {
      this.layerEtablissementsEtudeSource.next(event);
    }

    displayEtudeContrainteLayers(event: ContrainteEnvironnementale[]) {
        this.layerEtudeContraintesSource.next(event);
    }

    displayEtudeContrainteExistantesLayers(event: ContrainteEnvironnementale[]) {
        this.layerEtudeContraintesExistantesSource.next(event);
    }

    displayEtudeInstallationStockageExistantesLayers(event: InstallationStockage[]) {
        this.layerEtudeInstallationStockagesExistantesSource.next(event);
    }

    displayEtudeInstallationStockageLayers(event: InstallationStockage[]) {
        this.layerEtudeInstallationStockagesSource.next(event);
    }

    displayEtudeInstallationStockageNonActiveExistantesLayers(event: InstallationStockage[]) {
        this.layerEtudeInstallationStockagesNonActiveExistantesSource.next(event);
    }

    displayEtudeChantiersLayers(event: Chantier[]) {
        this.layerEtudeChantiersSource.next(event);
    }

    displayEtudeChantiersExistantsLayers(event: Chantier[]) {
        this.layerEtudeChantiersExistantsSource.next(event);
    }

    displayEtudeConsultation(etudes:Etude[]){
        this.displayEtudeConsultationSource.next(etudes);
    }

    clearEtudeConsultation(){
        this.clearEtudeConsultationSource.next(true);
    }

    reset(event: boolean) {
        this.layersResetSource.next(event);
    }
}
