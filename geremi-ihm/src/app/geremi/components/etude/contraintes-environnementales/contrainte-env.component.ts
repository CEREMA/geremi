import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { ContrainteEnvironnementale } from './model/contrainteEnv.model';
import { ContrainteEnvironnetaleService } from './service/contrainte-env.service';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';
import { MessageService } from 'primeng/api';
import { TooltipOptions } from 'primeng/tooltip';
import { Etude } from '../model/etude.model';
import { LayersService } from '../../carto/service/layers.service';
import { Subscription } from 'rxjs';
import {Etape} from "../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../shared/enums/etatEtape.enums";
import { OverlayService } from '../../overlay/service/overlay.service';
import {FileUpload} from "primeng/fileupload";
import {environment} from "../../../../../environments/environment";

@Component({
  selector: 'contrainte-env',
  templateUrl: './contrainte-env.component.html',
  styleUrls: ['./contrainte-env.component.scss'],
  providers: [ArrayUtilsService]
})

export class ContrainteEnvironnementaleComponent implements OnInit {
  @Output() actualStepEvent = new EventEmitter<Etape>();

  contraintesExistantes: ContrainteEnvironnementale[] = [];
  contraintesEtude: ContrainteEnvironnementale[] = [];

  messageToolTipBouton: String;

  layerAfficher: boolean = true;

  @Input() etude: Etude;
  @Input() actualStep: Etape;

  maxFileSize = environment.maxFileUplaodSize;

  tooltipOptionsMessage: TooltipOptions = { tooltipStyleClass: 'tooltip-import-message' };

  private contrainteEtudeSubscription: Subscription;
  private contrainteExistantesSubscription: Subscription;
  private contrainteInitSubscription: Subscription;
  private contrainteResetSubscription: Subscription;

  constructor(
    private contraintesService: ContrainteEnvironnetaleService,
    private layersService: LayersService,
    private arrayUtils: ArrayUtilsService,
    private overlayService: OverlayService,
    private messageService: MessageService) { }

  ngOnInit(): void {
    if(this.actualStep === Etape.CONTRAINTES)
      this.initContraintesEnv();

    this.contrainteInitSubscription = this.contraintesService.contrainteEnvironnetaleInitContrainteSource$.subscribe(value => {
      this.layerAfficher = true;
      this.initContraintesEnv();
    });

    this.contrainteResetSubscription = this.contraintesService.contrainteEnvironnetaleResetSource$.subscribe(value => {
      this.layerAfficher = false;
      this.contraintesEtude = [];
      this.contraintesExistantes = [];
      this.clearSubscription();
    });

    this.messageToolTipBouton = `<span class="pi pi-info-circle"></span>
                        Pour l'import, 1 des 3 options :
                  <li> .zip contenant(.cpg, .dbf, .prj, .shp, .shx)
                  <li> 5 fichiers .cpg, .dbf, .prj, .shp, .shx
                  <li> .gpkg`;
  }

  initContraintesEnv() {
    if(this.contrainteExistantesSubscription){
      this.contrainteExistantesSubscription.unsubscribe();
    }
    this.contrainteExistantesSubscription = this.contraintesService.getContrainteEnvironnementale(this.etude.territoire.id_territoire).subscribe({
      next: (value: ContrainteEnvironnementale[]) => {
        this.contraintesExistantes = this.sort(value);
        this.layersService.displayEtudeContrainteExistantesLayers(value);
        console.log("Récupération des contraintes existantes.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des contraintes existantes.");
      }
    });

    if(this.contrainteEtudeSubscription){
      this.contrainteEtudeSubscription.unsubscribe();
    }
    this.contrainteEtudeSubscription = this.contraintesService.getContrainteEtude(this.etude.id).subscribe({
      next: (value: ContrainteEnvironnementale[]) => {
        this.contraintesEtude = this.sort(value);
        this.layersService.displayEtudeContrainteLayers(value);
        console.log("Récupération des contraintes de l'étude.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des contraintes de l'étude.");
      }
    });
  }

  clickEye(event: any) {
    for (let c in this.contraintesEtude) {
      if (this.contraintesEtude[c].id === event.id) {
        this.contraintesEtude[c].afficher = !this.contraintesEtude[c].afficher;
        this.contraintesService.changeOpacity(this.contraintesEtude[c]);
        break;
      }
    }
    for (let c in this.contraintesExistantes) {
      if (this.contraintesExistantes[c].id === event.id) {
        this.contraintesExistantes[c].afficher = !this.contraintesExistantes[c].afficher;
        this.contraintesService.changeOpacity(this.contraintesExistantes[c]);
        break;
      }
    }
  }
  clickArrowUp(event: any) {
    this.contraintesService.deleteContrainte(event.id).subscribe({
      next: (value) => {
        console.log("Suppression de la contrainte.")
        event.id = value;
        this.contraintesEtude = this.arrayUtils.arrayRemoveId(this.contraintesEtude, event);
        event.afficher = false;
        this.contraintesService.changeOpacity(event);
        this.contraintesExistantes.push(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
        this.contraintesExistantes = this.sort(this.contraintesExistantes);
      },
      error(err) {
        console.log("Erreur lors de la suppression de la contrainte.")
      },
    });
  }
  clickArrowDown(event: any) {
    this.contraintesService.updateContrainte(event.id, this.etude.id).subscribe({
      next: (value) => {
        console.log("Ajout de la contrainte.")
        this.contraintesExistantes = this.arrayUtils.arrayRemoveId(this.contraintesExistantes, event);
        event.id = value;
        event.afficher = true;
        this.contraintesService.changeOpacity(event);
        this.contraintesEtude.push(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
        this.contraintesEtude = this.sort(this.contraintesEtude);
      },
      error(err) {
        console.log("Erreur lors de l'ajout de la contrainte.")
      },
    });
  }
  clickSuppr(event: any) {
    this.contraintesService.deleteContrainte(event.id).subscribe({
      next: (value) => {
        console.log("Contrainte supprimée.");
        this.contraintesEtude = this.arrayUtils.arrayRemoveId(this.contraintesEtude, event);
        this.contraintesService.removeFromMap(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de la suppresion de la contrainte.");
      }
    });
  }

  validateInputFiles(event: any, fileuploadform: any) {
    this.overlayService.overlayOpen("Import en cours...");
    this.messageService.clear('contrainte');
    let files: File[] = Array.from(event.files)

    try {
      this.testFiles(fileuploadform, files);
    } catch(error: any) {
      console.log("Erreur lors du check de l'import");
      this.showError(error);
      this.overlayService.overlayClose();
      fileuploadform.clear();
      console.log(event);
    }

  }

  private testFiles(fileuploadform: any, files: File[]) {

    // verification des fichiers coté client
    if (files.length > 5) {
      throw "Import impossible, nombre maximal de fichiers dépassé";
    }
    //verification de la taille max
    let invalidFiles: File[] = [];
    for (const file of files){
      if (file.size >= this.maxFileSize) {
        invalidFiles.push(file);
      }
    }
    if (invalidFiles.length > 0) {
      let errorMsg = 'Import impossible, les fichiers suivants dépassent la taille maximale autorisée (' + this.maxFileSize/1000000 + ' Mo)'
      for (const file of invalidFiles) {
        errorMsg = errorMsg + '<li>'+file.name+'</li>'
      }
      throw errorMsg;
    }
    //verification des extensions
    invalidFiles = [];
    for (const file of files){
      if (!this.isFileTypeValid(file, fileuploadform)) {
        invalidFiles.push(file);
      }
    }
    if (invalidFiles.length > 0) {
      let errorMsg = 'Import impossible, les extensions des fichiers suivants ne correspondent pas aux formats attendus :'
      for (const file of invalidFiles) {
        errorMsg = errorMsg + '<li>'+file.name+'</li>'
      }
      throw errorMsg;
    }
  }

  private isFileTypeValid(file: File, fileuploadform: FileUpload): boolean {
    let acceptableTypes = fileuploadform.accept?.split(',').map((type) => type.trim());
    for (let type of acceptableTypes!) {
      let acceptable = fileuploadform.isWildcard(type) ? fileuploadform.getTypeClass(file.type) === fileuploadform.getTypeClass(type) : file.type == type || fileuploadform.getFileExtension(file).toLowerCase() === type.toLowerCase();

      if (acceptable) {
        return true;
      }
    }

    return false;
  }

  uploadContrainteEtude(event: any, fileuploadform: any) {
    let files = event.files;
    fileuploadform.clear();
    this.uploadFiles(files);
  }

  uploadFiles(files: File[]) {

      this.contraintesService.importContrainteEnvironnementaleFiles(files, this.etude.id).subscribe({
        next: (value) => {
          this.messageService.clear();
          if (Array.isArray(value)) {
            for (const message of value) {
              this.messageService.add({ severity: 'warn', detail: message });
            }
          }
        },
        error: (error) => {
          this.showError(error.error);
          this.overlayService.overlayClose();
        },
        complete: () => {
          this.initContraintesEnv();
          this.showSuccess();
          this.changeEtatEtape(EtatEtape.IMPORTE);
          this.overlayService.overlayClose();
        }
      });

  }

  showError(message: string) {
    this.messageService.clear();
    this.messageService.add({ severity: 'error', detail: message, key:'contrainte' });
    setTimeout(() => {
      this.messageService.clear();
    }, 5000);
  }

  showSuccess() {
    this.messageService.add({ severity: 'success', detail: "Contrainte(s) importée(s) avec succès.", key:'contrainte' });
    setTimeout(() => {
      this.messageService.clear('contrainte');
    }, 5000);
  }

  telechargerModele() {
    console.log("Téléchargement du modèle.");
    this.contraintesService.telechargerContrainteGpkg().subscribe(data => {
      const blob = new Blob([data], { type: 'application/geopackage+sqlite3' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `contrainte.gpkg`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  clearSubscription() {
    if (this.contrainteExistantesSubscription) {
      this.contrainteExistantesSubscription.unsubscribe();
    }
    if (this.contrainteEtudeSubscription) {
      this.contrainteEtudeSubscription.unsubscribe();
    }
  }

  changeEtatEtape(etatEtape: EtatEtape) {
    this.etude.etatEtapes[Etape.CONTRAINTES] = etatEtape;
    this.actualStepEvent.emit(Etape.CONTRAINTES);
  }

  isEtapeScenario(){
    return this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE
          || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE;
  }

  compareNiveau(a:ContrainteEnvironnementale, b:ContrainteEnvironnementale) {
    const map = new Map([
      ['Faible',1],
      ['Moyenne',2],
      ['Forte',3]
    ])
    let ca = map.get(a.niveau.toString());
    let cb = map.get(b.niveau.toString());
    if(ca == null || cb == null){
      return 0;
    }
    return ca - cb;
  }

  sort(list:ContrainteEnvironnementale[]){
    return list.sort((a,b) => this.compareNiveau(a,b));
  }

  isLectureSeule(){
    return this.etude.readOnly;
  }

  ngOnDestroy(): void {
    this.clearSubscription();

    if(this.contrainteInitSubscription)
      this.contrainteInitSubscription.unsubscribe();
    if(this.contrainteResetSubscription)
      this.contrainteResetSubscription.unsubscribe();
  }
}
