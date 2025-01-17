import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstallationStockage } from './model/installation-stockage.model';
import { InstallationStockageService } from './service/installation-stockage.service';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';
import { ConfirmationService, Message } from 'primeng/api';
import { TooltipOptions } from 'primeng/tooltip';
import { Etude } from '../model/etude.model';
import { LayersService } from '../../carto/service/layers.service';
import { Subscription, startWith, map, Observable } from 'rxjs';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import { InstallationStockageDTO } from './model/installation-stockage-dto.model';
import { EtatEtape } from "../../../../shared/enums/etatEtape.enums";
import { Etape } from "../../../../shared/enums/etape.enums";
import { DateService } from '../../carto/service/date.service';
import { OverlayService } from '../../overlay/service/overlay.service';
import {environment} from "../../../../../environments/environment";
import {FileUpload} from "primeng/fileupload";

@Component({
  selector: 'installation-stockage',
  templateUrl: './installation-stockage.component.html',
  styleUrls: ['./installation-stockage.component.scss'],
  providers: [ArrayUtilsService, ConfirmationService]
})

export class InstallationStockageComponent implements OnInit {
  @Output() actualStepEvent = new EventEmitter<Etape>();
  installationStockagesExistantes: InstallationStockage[] = [];
  installationStockagesEtude: InstallationStockage[] = [];
  installationStockage: InstallationStockage;
  modificationInstallationStockageDialog: boolean = false;
  isAnyFieldModified = false;
  messageToolTipBouton: String;
  messageToolTipDownload: String;
  messageInformationImportTemp: Message[];
  messageInformationImport: Message[];
  messageErreurImport: Message[];
  layerAfficher: boolean = true;
  @Input() etude: Etude;
  @Input() selectedYearFromLegend: Observable<number>;
  @Input() actualStep: Etape;
  formInstallationStockage: FormGroup;

  maxFileSize = environment.maxFileUplaodSize;

  tooltipOptionsMessage: TooltipOptions = { tooltipStyleClass: 'tooltip-import-message' };
  private installationStockageEtudeSubscription: Subscription;
  private installationStockageExistantesSubscription: Subscription;
  private selectedYearFromLegendsubscription: Subscription;
  private installationStockageInitSubscription: Subscription;
  private installationStockageResetSubscription: Subscription;

  constructor(
    private installationStockageService: InstallationStockageService,
    private layersService: LayersService,
    private formBuilder: FormBuilder,
    private arrayUtils: ArrayUtilsService,
    private overlayService: OverlayService,
    private dateService: DateService) { }

  ngOnInit(): void {
    if(this.actualStep === Etape.INSTALLATIONS)
      this.initInstallationStockagesEnv();

    this.installationStockageInitSubscription = this.installationStockageService.installationStockageInitSource$.subscribe(value => {
      this.layerAfficher = true;
      this.initInstallationStockagesEnv();
    });

    this.installationStockageResetSubscription = this.installationStockageService.installationStockageResetSource$.subscribe(value => {
      this.layerAfficher = false;
      this.installationStockagesEtude = [];
      this.installationStockagesExistantes = [];
    });

    this.messageToolTipBouton = `<span class="pi pi-info-circle"></span>
                                    Pour l'import, 1 des 3 options :
                                    <li> .zip contenant(.cpg, .dbf, .prj, .shp, .shx)
                                    <li> 5 fichiers .cpg, .dbf, .prj, .shp, .shx
                                    <li> .gpkg`;

    this.messageToolTipDownload = `<span class="pi pi-info-circle"></span>
                        Données de stockage à exprimer en kt `;

    this.formInstallationStockage = this.formBuilder.group({
      nom_etab: new FormControl({ value: '', disabled: true }, [Validators.required, Validators.minLength(1)]),
      description: new FormControl('', null),
      annee_fin: new FormControl('', [Validators.required,Validators.maxLength(4), Validators.minLength(4)]),
      beton_pref: new FormControl('', [Validators.min(1), Validators.pattern('\\d*')]),
      viab_autre: new FormControl('', [Validators.min(1), Validators.pattern('\\d*')]),
      ton_tot: new FormControl('', [Validators.required, Validators.min(1), Validators.pattern('\\d+')]),
    });

    this.formInstallationStockage.get("beton_pref")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formInstallationStockage.get("viab_autre")?.value);
    });

    this.formInstallationStockage.get("viab_autre")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formInstallationStockage.get("beton_pref")?.value);
    });

    this.selectedYearFromLegendsubscription = this.dateService.selectedYear$.subscribe(year => {
     this.updateInactiveInstallations(year);
    });
  }

  updateInactiveInstallations(year: number) {
    // Identifiez les installations de stockage qui ne sont pas actives pour l'année sélectionnée
    const inactiveInstallationsEtude = this.installationStockagesEtude.filter(installation =>
        installation.anneeDebut > year || installation.anneeFin < year
    );
    const inactiveInstallationsExistantes = this.installationStockagesExistantes.filter(installation =>
        installation.anneeDebut > year || installation.anneeFin < year
    );
    // Fusionnez les deux listes en une seule
    const inactiveInstallations = inactiveInstallationsEtude.concat(inactiveInstallationsExistantes);
    this.layersService.displayEtudeInstallationStockageNonActiveExistantesLayers(inactiveInstallations);
}

  nomDisable() {
    if (this.formInstallationStockage === undefined || this.installationStockage === undefined) {
      return;
    }
    this.formInstallationStockage.get('nom_etab')?.disable();
    if (!(parseInt(this.formInstallationStockage.value.annee_fin) === this.installationStockage.anneeFin &&
      parseInt(this.formInstallationStockage.value.beton_pref) === this.installationStockage.betonPref &&
      parseInt(this.formInstallationStockage.value.viab_autre) === this.installationStockage.viabAutre &&
      parseInt(this.formInstallationStockage.value.ton_tot) === this.installationStockage.tonTot)) {
      this.formInstallationStockage.get('nom_etab')?.enable();
    } else {
      this.formInstallationStockage.get('nom_etab')?.disable();
    }
  }

  installationStockageIdentique() {
    if (this.formInstallationStockage === undefined || this.installationStockage === undefined) {
      return;
    }
    let beton_form = this.formInstallationStockage.value.beton_pref === '' ? null : this.formInstallationStockage.value.beton_pref;
    let viab_form = this.formInstallationStockage.value.viab_autre === '' ? null : this.formInstallationStockage.value.viab_autre;

    return parseInt(this.formInstallationStockage.value.annee_fin) === this.installationStockage.anneeFin &&

            (((beton_form == null && beton_form === this.installationStockage.betonPref)
            || parseInt(this.formInstallationStockage.value.beton_pref) === this.installationStockage.betonPref) &&

            ((viab_form == null && viab_form === this.installationStockage.viabAutre)
            || parseInt(this.formInstallationStockage.value.viab_autre) === this.installationStockage.viabAutre)) &&

            parseInt(this.formInstallationStockage.value.ton_tot) === this.installationStockage.tonTot &&
            this.formInstallationStockage.value.description === this.installationStockage.description
  }

  tooltipValidationModif() {
    if (this.installationStockageIdentique()) {
      return 'Validation impossible : ' + '<br>- Installation de stockage non modifiée';
    }

    if (parseInt(this.formInstallationStockage.value.annee_fin) < parseInt(this.etude.anneeRef)) {
      return 'Validation impossible : ' + '<br>- Période de l\'installation stockage en dehors de la période de l\'étude ( ' + this.etude.anneeRef + '- ' + this.etude.anneeFin + ')';
    }

    let beton_pref = this.formInstallationStockage.value.beton_pref;
    let viab_autre = this.formInstallationStockage.value.viab_autre;
    let ton_tot = this.formInstallationStockage.value.ton_tot;

    beton_pref = beton_pref === null || beton_pref === '' ? 0 : parseInt(beton_pref);
    viab_autre = viab_autre === null || viab_autre === '' ? 0 : parseInt(viab_autre);
    ton_tot = ton_tot === null ? 0 : parseInt(ton_tot);


    if ((this.formInstallationStockage.value.beton_pref === '' || this.formInstallationStockage.value.beton_pref == null )
      && (this.formInstallationStockage.value.viab_autre === '' || this.formInstallationStockage.value.viab_autre == null )
      && (this.formInstallationStockage.value.ton_tot === '' || this.formInstallationStockage.value.ton_tot == null )
    ) {
      return 'Validation impossible : ' + '<br>- Stockages de matériaux non renseignés';
    }
    if( (ton_tot !== beton_pref + viab_autre) && (beton_pref + viab_autre !== 0) ){
      return 'Validation impossible : ' + '<br>- Somme des stockages de matériaux différente du stockage total ';
    }

    if (!this.formInstallationStockage.get("nom_etab")?.valid) {
      return 'Validation impossible : ' + '<br>- Nom de l\'établissement non renseigné';
    }

    if (!this.formInstallationStockage.get("annee_fin")?.valid) {
      return 'Validation impossible : ' + '<br>- Année de fin non valide';
    }

    if (!this.formInstallationStockage.get("beton_pref")?.valid) {
      return 'Validation impossible : ' + '<br>- Le stockage béton/préfabriqué, si renseigné, doit être un entier > 0';
    }

    if (!this.formInstallationStockage.get("viab_autre")?.valid) {
      return 'Validation impossible : ' + '<br>- Le stockage viabilité/autre, si renseigné, doit être un entier > 0';
    }

    if (!this.formInstallationStockage.get("ton_tot")?.valid) {
      return 'Validation impossible : ' + '<br>- Le stockage total doit être un entier > 0';
    }

    return '';
  }

  clickModification(event: InstallationStockage) {
    console.log("Modification Installation Stockage");
    this.installationStockage = event;
    this.formInstallationStockage = this.formBuilder.group({
      installationStockageId:  new FormControl(event.id, null),
      nom_etab:  new FormControl(event.nomEtab, [Validators.required, Validators.minLength(1)]),
      description:  new FormControl(event.description, null),
      annee_fin:  new FormControl(event.anneeFin, [Validators.required,Validators.maxLength(4), Validators.minLength(4)]),
      beton_pref:  new FormControl(event.betonPref,[Validators.min(1), Validators.pattern('\\d*')]),
      viab_autre:  new FormControl(event.viabAutre,[Validators.min(1), Validators.pattern('\\d*')]),
      ton_tot:  new FormControl(event.tonTot, [Validators.required, Validators.min(1), Validators.pattern('\\d+')]),
    });

    this.formInstallationStockage.get("beton_pref")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formInstallationStockage.get("viab_autre")?.value);
    });

    this.formInstallationStockage.get("viab_autre")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formInstallationStockage.get("beton_pref")?.value);
    });

    this.modificationInstallationStockageDialog = true;
    this.formInstallationStockage.get('nom_etab')?.disable();
  }

  onBetonViabChange(val : string, other : string) {
    const valInt = val === null || val === '' ? 0 : parseInt(val);
    const otherInt = other === null  || other === '' ? 0 : parseInt(other);
    if ((!Number.isNaN(valInt) && !Number.isNaN(otherInt) && val !== '') || (!Number.isNaN(otherInt) && !(other === '' || other == null)) ) {
      this.formInstallationStockage.get("ton_tot")?.setValue(valInt + otherInt);
      this.formInstallationStockage.get("ton_tot")?.markAsDirty()
      this.formInstallationStockage.get("ton_tot")?.markAsTouched()
    }
  }

  initInstallationStockagesEnv() {
    if(this.installationStockageExistantesSubscription)
      this.installationStockageExistantesSubscription.unsubscribe()

    this.installationStockageExistantesSubscription = this.installationStockageService.getInstallationStockage(this.etude.territoire.id_territoire, this.etude.id).subscribe({
      next: (value: InstallationStockage[]) => {
        this.installationStockagesExistantes = value;
        this.layersService.displayEtudeInstallationStockageExistantesLayers(value);
        console.log("Récupération des installations stockage existantes.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des installations stockage existantes.");
      }
    });

    if(this.installationStockageEtudeSubscription)
      this.installationStockageEtudeSubscription.unsubscribe()

    this.installationStockageEtudeSubscription = this.installationStockageService.getInstallationStockageEtude(this.etude.id).subscribe({
      next: (value: InstallationStockage[]) => {
        this.installationStockagesEtude = value;
        this.layersService.displayEtudeInstallationStockageLayers(value);
        console.log("Récupération des installations stockage de l'étude.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des installations stockage de l'étude.");
      }
    });
  }

  clickEye(event: any) {
    for (let c in this.installationStockagesEtude) {
      if (this.installationStockagesEtude[c].id === event.id) {
        this.installationStockagesEtude[c].afficher = !this.installationStockagesEtude[c].afficher;
        this.installationStockageService.changeOpacity(this.installationStockagesEtude[c]);
        break;
      }
    }
    for (let c in this.installationStockagesExistantes) {
      if (this.installationStockagesExistantes[c].id === event.id) {
        this.installationStockagesExistantes[c].afficher = !this.installationStockagesExistantes[c].afficher;
        this.installationStockageService.changeOpacity(this.installationStockagesExistantes[c]);
        break;
      }
    }
  }

  clickArrowUp(event: any) {
    this.installationStockageService.deleteInstallationStockage(event.id).subscribe({
      next: (value) => {
        console.log("Suppression de l'installation stockage.");
        event.id = value;
        this.installationStockagesEtude = this.arrayUtils.arrayRemoveId(this.installationStockagesEtude, event);
        event.afficher = false;

        if(event.idFrere !== null){
          let index = this.installationStockagesExistantes.findIndex(item => item.id === event.idFrere);
          if(index === -1){
            this.installationStockageService.getInstallationStockageById(event.idFrere).subscribe({
              next: (value) => {
                // On set à l'ancien id pour trouver dans la liste
                value.feature.id = event.feature.id;
                this.installationStockageService.changeFeatureInstallationStockage(this.mapInstallationStockageToDTO(value));
                // On set la nouvelle id
                value.feature.id = value.id;
                this.installationStockagesExistantes.push(value);
                this.installationStockageService.changeOpacity(value);
              },
              error(err) {
                console.log("Erreur lors de la suppression de l'installation de stockage.")
              }
            });
          }
        }

        else if(event.idSource !== null){
          let index = this.installationStockagesExistantes.findIndex(item => item.id === event.idSource);
          if(index === -1){
            this.installationStockageService.getInstallationStockageById(event.idSource).subscribe({
              next: (value) => {
                // On set à l'ancien id pour trouver dans la liste
                value.feature.id = event.feature.id;
                this.installationStockageService.changeFeatureInstallationStockage(this.mapInstallationStockageToDTO(value));
                // On set la nouvelle id
                value.feature.id = value.id;
                this.installationStockagesExistantes.push(value);
                this.installationStockageService.changeOpacity(value);
              },
              error(err) {
                console.log("Erreur lors de la suppression de l'installation de stockage.")
              }
            });
          }
        }
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error(err) {
        console.log("Erreur lors de la suppression de l'installation de stockage.")
      },
    });
  }

  clickArrowDown(event: any) {
    // Si la flèche est désactivée, ne faites rien et retournez
    if (event.disableArrow) {
      return;
    }
    // Vérifie si l'installation peut être ajoutée à la liste "Installations de l'étude"
    const canBeAdded = this.installationStockagesEtude.every(installation => {
      const isSameSource = installation.idSource && installation.idSource === event.feature.id;
      const isChildOfSameSource = !installation.idSource && installation.id === event.feature.id;
      return !(isSameSource || isChildOfSameSource);
    });

    if (!canBeAdded) {
      console.log("L'installation ne peut pas être ajoutée car une installation similaire existe déjà dans la liste 'Installations de l'étude'.");
      return;
    }

    this.installationStockageService.duplicateInstallationStockage(event.id, this.etude.id).subscribe({
      next: (value) => {
        console.log("Ajout de l'installation stockage.");

       // Supprime l'installation de la liste "Installations Existantes"
       this.installationStockagesExistantes = this.arrayUtils.arrayRemoveId(this.installationStockagesExistantes, event);

        if(event.idSource === null || event.idSource === undefined){
          event.idSource = event.id;
          event.libellePere = event.nomEtab;
        } else {
          event.idFrere = event.id;
          event.libellePere = event.libellePere;
        }

        // Met à jour les attributs de l'installation
        event.id = value;
        // Vérifie si l'id_source de l'installation de stockage source est vide ou non,
        event.afficher = true;
        event.supprimable = false;
        // Modifie l'opacité de l'installation
        this.installationStockageService.changeOpacity(event);
        this.installationStockageService.changeFeatureInstallationStockage(this.mapInstallationStockageToDTO(event));
        event.feature.id = event.id;
        // Ajoute l'installation à la liste "Installations de l'étude"
        this.installationStockagesEtude.push(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error(err) {
        console.log("Erreur lors de l'ajout de l'installation stockage.");
      },
    });
  }

  clickSuppr(event: any) {
    this.installationStockageService.deleteInstallationStockage(event.id).subscribe({
      next: (value) => {
        console.log("Installation stockage supprimée.");
        this.installationStockagesEtude = this.arrayUtils.arrayRemoveId(this.installationStockagesEtude, event);
        this.installationStockageService.removeFromMap(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de la suppresion de l'Installation stockage.");
      }
    });
  }

  validateInputFiles(event: any, fileuploadform: any) {
    this.overlayService.overlayOpen("Import en cours...");
    this.messageErreurImport = [];
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

  uploadInstallationStockageEtude(event: any, fileuploadform: any) {
    let files = event.files;
    fileuploadform.clear();
    this.uploadFiles(files);
  }

  uploadFiles(files: File[]) {
    this.overlayService.overlayOpen("Import en cours...");
    this.installationStockageService.importInstallationStockageFiles(files, this.etude.id).subscribe({
      next: (response) => {
        this.messageInformationImportTemp = [];
        if (Array.isArray(response)) {
          for (const message of response) {
            this.messageInformationImportTemp = [{severity: 'info', detail: message}];
          }
        }
      },
      error: (error) => {
        this.showError(error.error);
        this.overlayService.overlayClose();
      },
      complete: () => {
        this.showSuccess();
        this.initInstallationStockagesEnv();
        this.changeEtatEtape(EtatEtape.IMPORTE);
        this.overlayService.overlayClose();
      }
    });


  }

  showError(message: string) {
    this.messageInformationImport = [];
    this.messageErreurImport = [{ severity: 'error', detail: message }];
    setTimeout(() => {
      this.messageErreurImport = [];
    }, 5000);
  }

  showSuccess() {
    if (this.messageInformationImportTemp && this.messageInformationImportTemp.length > 0) {
      setTimeout(() => {
        this.messageInformationImport = [{ severity: 'success', detail: "Installation(s) Stockage importée(s) avec succès." }];
        setTimeout(() => {
          this.messageInformationImport = [];
        }, 5000);
      }, 5000);
    } else {
      this.messageInformationImport = [{ severity: 'success', detail: "Installation(s) Stockage importée(s) avec succès." }];
      setTimeout(() => {
        this.messageInformationImport = [];
      }, 5000);
    }
  }


  telechargerModele() {
    console.log("Téléchargement du modèle.");
    this.installationStockageService.telechargerInstallationStockageGpkg().subscribe(data => {
      const blob = new Blob([data], { type: 'application/geopackage+sqlite3' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `stockage.gpkg`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  clearSubscription() {
    if (this.installationStockageExistantesSubscription) {
      this.installationStockageExistantesSubscription.unsubscribe();
    }
    if (this.installationStockageEtudeSubscription) {
      this.installationStockageEtudeSubscription.unsubscribe();
    }
    if (this.selectedYearFromLegendsubscription ) {
      this.selectedYearFromLegendsubscription.unsubscribe();
    }
    if (this.installationStockageResetSubscription) {
      this.installationStockageResetSubscription.unsubscribe();
    }
    if (this.installationStockageInitSubscription ) {
      this.installationStockageInitSubscription.unsubscribe();
    }
  }

  ajoutImpossible(installationStockage:InstallationStockage){
    if(installationStockage.idSource === null || installationStockage.idSource === undefined){
      for(let i of this.installationStockagesEtude){
        if(i.idSource === installationStockage.id){
          return true;
        }
      }
      return false;
    }
    for(let i of this.installationStockagesEtude){
      if(i.id !== installationStockage.id && i.idSource === installationStockage.idSource){
        return true;
      }
      if(i.idSource === installationStockage.id){
        return false;
      }
    }
    return false;
  }

  ngOnDestroy(): void {
    this.clearSubscription();
  }

  valider() {
    if (this.formInstallationStockage.valid) {
      const installationStockageDTO: InstallationStockageDTO = {
        nomEtab: this.formInstallationStockage.get('nom_etab')?.value,
        description: this.formInstallationStockage.get('description')?.value,
        anneeDebut: parseInt(this.etude.anneeRef) as number,
        anneeFin: this.formInstallationStockage.get('annee_fin')?.value as number,
        betonPref: this.formInstallationStockage.get('beton_pref')?.value as number,
        viabAutre: this.formInstallationStockage.get('viab_autre')?.value as number,
        tonTot: this.formInstallationStockage.get('ton_tot')?.value as number,
        idFeature: this.installationStockage.feature.id as number,
        idStockage: this.installationStockage.id as number
      };

      const installationStockageId = this.formInstallationStockage.get('installationStockageId')?.value;

      this.installationStockageService.updateDataInstallationStockage(installationStockageId, installationStockageDTO).subscribe({
        next: response => {
          console.log('Mise à jour réussie');
          // Trouver l'élément dans le tableau
          const index = this.installationStockagesEtude.findIndex(item => item.id === installationStockageId);
          // Mettre à jour l'élément avec les nouvelles valeurs
          if (index !== -1) {
            this.installationStockagesEtude[index] = {
              ...this.installationStockagesEtude[index],
              ...installationStockageDTO
            };
            this.changeEtatEtape(EtatEtape.MODIFIE);
            this.installationStockageService.changeFeatureInstallationStockage(installationStockageDTO);
          }
          this.formInstallationStockage.reset();
        },
        error: error => {
          console.error('Erreur lors de la mise à jour', error);

        }
      });
    } else {
      console.log('Formulaire non valide');
    }
    this.modificationInstallationStockageDialog = false;
  }

  annuler() {
    this.formInstallationStockage.reset();
    this.modificationInstallationStockageDialog = false;
  }

  isIdSourceNotEmpty(idSource: any): boolean {
    return idSource !== null && idSource !== undefined && idSource !== '';
  }


  changeEtatEtape(etatEtape: EtatEtape) {
    this.etude.etatEtapes[Etape.INSTALLATIONS] = etatEtape;
    this.actualStepEvent.emit(Etape.INSTALLATIONS);
  }

  mapInstallationStockageToDTO(installationStockage: InstallationStockage) {
    let dto: InstallationStockageDTO = new InstallationStockageDTO();
    dto.nomEtab = installationStockage.nomEtab as string;
    dto.description = installationStockage.description as string;
    dto.anneeDebut = installationStockage.anneeDebut as number;
    dto.anneeFin = installationStockage.anneeFin as number;
    dto.betonPref = installationStockage.betonPref as number;
    dto.viabAutre = installationStockage.viabAutre as number;
    dto.tonTot = installationStockage.tonTot as number;
    dto.idStockage = installationStockage.id as number;
    dto.idFeature = installationStockage.feature.id as number;
    return dto;
  }

  tooltipInstallationOrigine(installationStockage:InstallationStockage){
    if(installationStockage.libellePere != null || installationStockage.libellePere != undefined){
      return 'Cette installation est dérivée de '+ installationStockage.libellePere;
    }
    return '';
  }

  getInstallationStockageOrigine(installationStockage:InstallationStockage){
    if(installationStockage.idSource === null || installationStockage.idSource === undefined){
      return null;
    }
    let idSourceNum = Number(installationStockage.idSource);
    let idNum = Number(installationStockage.id);

    for(let c of this.installationStockagesEtude){
      let cIdNum = Number(c.id);
      if(cIdNum === idSourceNum && cIdNum != idNum){
        return c;
      }
    }

    for(let c of this.installationStockagesExistantes){
      let cIdNum = Number(c.id);
      if(cIdNum === idSourceNum && cIdNum != idNum){
        return c;
      }
    }
    return null;
  }

  isEtapeScenario(){
    return this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE
          || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE;
  }

  isLectureSeule(){
    return this.etude.readOnly;
  }
}





