import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Chantier } from './model/chantiers.model';
import { Message, MessageService } from 'primeng/api';
import { ChantiersService } from './service/chantiers-env.service';
import { LayersService } from '../../carto/service/layers.service';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';
import { Etude } from '../model/etude.model';
import { TooltipOptions } from 'primeng/tooltip';
import { Subscription } from 'rxjs';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Etape} from "../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../shared/enums/etatEtape.enums";
import { DateService } from '../../carto/service/date.service';
import { OverlayService } from '../../overlay/service/overlay.service';
import {environment} from "../../../../../environments/environment";
import {FileUpload} from "primeng/fileupload";

@Component({
  selector: 'chantiers-env',
  templateUrl: './chantiers-env.component.html',
  styleUrls: ['./chantiers-env.component.scss']
})
export class ChantiersEnvergureComponent implements OnInit {
  @Output() actualStepEvent = new EventEmitter<Etape>();

  chantiersExistants : Chantier[] = [];
  chantiersEtude : Chantier[] = [];

  modificationChantierDialog: boolean = false;
  messageToolTipBouton: String;
  messageToolTipDownload: String;
  layerAfficher: boolean = true;

  @Input() etude: Etude;
  @Input() actualStep: Etape;

  chantier:Chantier;
  formChantier:any;

  tooltipOptionsMessage : TooltipOptions = {tooltipStyleClass:'tooltip-import-message'};

  maxFileSize = environment.maxFileUplaodSize;

  private chantierEtudeSubscription: Subscription;
  private chantierExistantesSubscription: Subscription;
  private chantierInitSubscription: Subscription;
  private chantierResetSubscription: Subscription;

  constructor(
    private chantiersService : ChantiersService,
    private layersService : LayersService,
    private formBuilder: FormBuilder,
    private arrayUtils : ArrayUtilsService,
    private messageService : MessageService,
    private overlayService : OverlayService,
    private dateService: DateService){}

  ngOnInit(): void {
    if(this.actualStep === Etape.CHANTIERS)
      this.initChantiers();

    this.formChantier = this.formBuilder.group({
      nom: new FormControl({value:'',disabled: true}, [Validators.required, Validators.minLength(1)]),
      description: new FormControl(''),
      annee_debut: new FormControl('', [Validators.required, Validators.maxLength(4), Validators.minLength(4)]),
      annee_fin: new FormControl('', [Validators.required, Validators.maxLength(4), Validators.minLength(4)]),
      beton_pref: new FormControl('', [Validators.min(1), Validators.pattern('\\d*')]),
      viab_autre: new FormControl('', [Validators.min(1), Validators.pattern('\\d*')]),
      ton_tot: new FormControl('', [Validators.required, Validators.min(1), Validators.pattern('\\d+')]),
    });

    this.formChantier.get("beton_pref")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formChantier.get("viab_autre")?.value);
    });

    this.formChantier.get("viab_autre")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formChantier.get("beton_pref")?.value);
    });

    this.chantierInitSubscription = this.chantiersService.chantierInitSource$.subscribe(value => {
      this.layerAfficher = true;
      this.initChantiers();
    });

    this.chantierResetSubscription = this.chantiersService.chantierResetSource$.subscribe(value => {
      this.layerAfficher = false;
      this.chantiersEtude = [];
      this.chantiersExistants = [];
      this.clearSubscription();
    });

    this.messageToolTipBouton = `<span class="pi pi-info-circle"></span>
                        Pour l'import, 1 des 3 options :
                  <li> .zip contenant(.cpg, .dbf, .prj, .shp, .shx)
                  <li> 5 fichiers .cpg, .dbf, .prj, .shp, .shx
                  <li> .gpkg`;

    this.messageToolTipDownload = `<span class="pi pi-info-circle"></span>
                        Besoin des chantiers à exprimer en kt `;

    this.dateService.selectedYear$.subscribe(() => {
      this.changeClassAllChantier();
    });
  }

  initChantiers(){
    console.log("init chantier")
    if(this.chantierExistantesSubscription){
      this.chantierExistantesSubscription.unsubscribe();
    }
    this.chantierExistantesSubscription = this.chantiersService.getChantiers(this.etude.territoire.id_territoire, this.etude.id).subscribe({
      next: (value: Chantier[]) => {
        this.chantiersExistants = value;
        this.layersService.displayEtudeChantiersExistantsLayers(value);
        console.log("Récupération des Chantiers existants.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des Chantiers existants.");
      }
    });

    if(this.chantierEtudeSubscription){
      this.chantierEtudeSubscription.unsubscribe();
    }
    this.chantierEtudeSubscription = this.chantiersService.getChantiersEtude(this.etude.id).subscribe({
      next: (value: Chantier[]) => {
        this.chantiersEtude = value;
        this.layersService.displayEtudeChantiersLayers(value);
        console.log("Récupération des Chantiers de l'étude.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des Chantiers de l'étude.");
      }
    });
  }

  clickEye(event: any){
    for(let c in this.chantiersEtude){
      if(this.chantiersEtude[c].id_chantier === event.id_chantier){
        this.chantiersEtude[c].afficher = !this.chantiersEtude[c].afficher;
        this.chantiersService.changeClassAffichage(this.chantiersEtude[c]);
        break;
      }
    }
    for(let c in this.chantiersExistants){
      if(this.chantiersExistants[c].id_chantier === event.id_chantier){
        this.chantiersExistants[c].afficher = !this.chantiersExistants[c].afficher;
        this.chantiersService.changeClassAffichage(this.chantiersExistants[c]);
        break;
      }
    }
  }

  clickArrowUp(event: any){
    this.chantiersService.deleteChantier(event.id_chantier).subscribe({
      next: (value) => {
        console.log("Suppression du Chantier");
        event.id_chantier = value;
        this.chantiersEtude = this.arrayUtils.arrayRemoveIdChantier(this.chantiersEtude,event);
        event.afficher = false;

        if(event.id_frere !== null){
          let index = this.chantiersExistants.findIndex(item => item.id_chantier === event.id_frere);
          if(index === -1){
            this.chantiersService.getChantierEnvergureById(event.id_frere).subscribe({
              next: (value) => {
                // On set à l'ancien id pour trouver dans la liste
                value.feature.id = event.feature.id;
                this.chantiersService.changeFeatureChantier(value);
                // On set la nouvelle id
                value.feature.id = value.id_chantier;
                this.chantiersExistants.push(value);
                this.chantiersService.changeClassAffichage(value);
              }
            });
          }
        }

        else if(event.id_source !== null){
          let index = this.chantiersExistants.findIndex(item => item.id_chantier === event.id_source);
          if(index === -1){
            this.chantiersService.getChantierEnvergureById(event.id_source).subscribe({
              next: (value) => {
                // On set à l'ancien id pour trouver dans la liste
                value.feature.id = event.feature.id;
                this.chantiersService.changeFeatureChantier(value);
                // On set la nouvelle id
                value.feature.id = value.id_chantier;
                this.chantiersExistants.push(value);
                this.chantiersService.changeClassAffichage(value);
              }
            });
          }
        }
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error(err) {
        console.log("Erreur lors de la suppression du Chantier")
      },
    });
  }

  clickArrowDown(event: any){
    this.chantiersService.duplicateChantier(event.id_chantier,this.etude.id).subscribe({
      next:(value) => {
        console.log("Ajout du Chantier");
        this.chantiersExistants = this.arrayUtils.arrayRemoveIdChantier(this.chantiersExistants,event);
        if(event.id_source === null || event.id_source === undefined){
          event.id_source = event.id_chantier;
          event.libelle_pere = event.nom;
        } else {
          event.id_frere = event.id_chantier;
          event.libelle_pere = event.libelle_pere;
        }

        // On modifie les attributs
        event.id_chantier = value;
        event.afficher = true;
        event.supprimable = false;
        // On change l'opacité
        this.chantiersService.changeClassAffichage(event);
        this.chantiersService.changeFeatureChantier(event);
        event.feature.id = event.id_chantier;

        this.chantiersEtude.push(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error(err) {
          console.log("Erreur lors de l'ajout du Chantier")
      },
    });
  }

  clickSuppr(event: Chantier){
    this.chantiersService.deleteChantier(event.id_chantier).subscribe({
      next: (value) => {
        console.log("Chantier supprimée.");
        this.chantiersEtude = this.arrayUtils.arrayRemoveIdChantier(this.chantiersEtude,event);
        this.chantiersService.removeFromMap(event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de la suppresion du Chantier.");
      }
    });
  }

  clickModification(event: Chantier){
    this.formChantier = this.formBuilder.group({
      nom: new FormControl({value:event.nom,disabled: true}, [Validators.required, Validators.minLength(1)]),
      description: new FormControl(event.description),
      annee_debut: new FormControl(event.annee_debut, [Validators.required, Validators.maxLength(4), Validators.minLength(4)]),
      annee_fin: new FormControl(event.annee_fin, [Validators.required, Validators.maxLength(4), Validators.minLength(4)]),
      beton_pref: new FormControl(event.beton_pref, [Validators.min(1), Validators.pattern('\\d*')]),
      viab_autre: new FormControl(event.viab_autre, [Validators.min(1), Validators.pattern('\\d*')]),
      ton_tot: new FormControl(event.ton_tot, [Validators.required, Validators.min(1), Validators.pattern('\\d+')]),
    });


    this.formChantier.get("beton_pref")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formChantier.get("viab_autre")?.value);
    });

    this.formChantier.get("viab_autre")?.valueChanges.subscribe((val:string) => {
      this.onBetonViabChange(val, this.formChantier.get("beton_pref")?.value);
    });


    this.chantier = event;

    this.modificationChantierDialog = true;
  }

  onBetonViabChange(val : string, other : string) {
    const valInt = val === null || val === '' ? 0 : parseInt(val);
    const otherInt = other === null  || other === '' ? 0 : parseInt(other);
    if ((!Number.isNaN(valInt) && !Number.isNaN(otherInt) && val !== '') || (!Number.isNaN(otherInt) && !(other === '' || other == null)) ) {
      this.formChantier.get("ton_tot")?.setValue(valInt + otherInt);
      this.formChantier.get("ton_tot")?.markAsDirty()
      this.formChantier.get("ton_tot")?.markAsTouched()
    }
  }


  nomDisable(){
    if(this.chantier === undefined || this.formChantier === undefined){
      return;
    }
    let beton_form = this.formChantier.value.beton_pref === '' ? null : this.formChantier.value.beton_pref;
    let viab_form = this.formChantier.value.viab_autre === '' ? null : this.formChantier.value.viab_autre;
    if(!(parseInt(this.formChantier.value.annee_debut) === this.chantier.annee_debut &&
        parseInt(this.formChantier.value.annee_fin) === this.chantier.annee_fin &&
        (((beton_form == null && beton_form === this.chantier.beton_pref)
        || parseInt(this.formChantier.value.beton_pref) === this.chantier.beton_pref) &&
        ((viab_form == null && viab_form === this.chantier.viab_autre)
        || parseInt(this.formChantier.value.viab_autre) === this.chantier.viab_autre)) &&
        parseInt(this.formChantier.value.ton_tot) === this.chantier.ton_tot)) {
        this.formChantier.get('nom')?.enable();
    } else {
      this.formChantier.get('nom').setValue(this.chantier.nom);
      this.formChantier.get('nom')?.disable();
    }
  }

  // Vérifie le chantier en cours de modification
  chantierIdentique(){
    if(this.chantier === undefined || this.formChantier === undefined){
      return false;
    }

    let beton_form = this.formChantier.value.beton_pref === '' ? null : this.formChantier.value.beton_pref;
    let viab_form = this.formChantier.value.viab_autre === '' ? null : this.formChantier.value.viab_autre;

    return parseInt(this.formChantier.value.annee_debut) === this.chantier.annee_debut &&
            parseInt(this.formChantier.value.annee_fin) === this.chantier.annee_fin &&

            (((beton_form == null && beton_form === this.chantier.beton_pref)
            || parseInt(this.formChantier.value.beton_pref) === this.chantier.beton_pref) &&

            ((viab_form == null && viab_form === this.chantier.viab_autre)
            || parseInt(this.formChantier.value.viab_autre) === this.chantier.viab_autre)) &&

            parseInt(this.formChantier.value.ton_tot) === this.chantier.ton_tot &&
            this.formChantier.value.description === this.chantier.description
  }

  tooltipValidationModif(){
    this.nomDisable();

    if(this.chantierIdentique()){
      return 'Validation impossible : ' + '<br>- Chantier non modifé';
    }

    if(!(parseInt(this.formChantier.value.annee_debut) <= parseInt(this.formChantier.value.annee_fin))){
      return 'Validation impossible : ' + '<br>- Année début > Année de fin';
    }
    if(!(parseInt(this.formChantier.value.annee_debut) <= parseInt(this.etude.anneeFin) && parseInt(this.formChantier.value.annee_fin) >= parseInt(this.etude.anneeRef))){
      return 'Validation impossible : ' + '<br>- Période du chantier en dehors de la période de l\'étude ( '+ this.etude.anneeRef+'- '+this.etude.anneeFin + ' )';
    }

    let beton_pref = this.formChantier.value.beton_pref;
    let viab_autre = this.formChantier.value.viab_autre;
    let ton_tot = this.formChantier.value.ton_tot;
    beton_pref = beton_pref === null || beton_pref === '' ? 0 : parseInt(beton_pref);
    viab_autre = viab_autre === null || viab_autre === '' ? 0 : parseInt(viab_autre);
    ton_tot = ton_tot === null ? 0 : parseInt(ton_tot);

    if ((this.formChantier.value.beton_pref === '' || this.formChantier.value.beton_pref == null )
          && (this.formChantier.value.viab_autre === '' || this.formChantier.value.viab_autre == null )
          && (this.formChantier.value.ton_tot === '' || this.formChantier.value.ton_tot == null )
      ) {
      return 'Validation impossible : ' + '<br>- Besoins en matériaux non renseignés';
    }
    if( (ton_tot !== beton_pref + viab_autre) && (beton_pref + viab_autre !== 0) ){
      return 'Validation impossible : ' + '<br>- Somme des besoins en matériaux différente du total des besoins en matériaux';
    }

    if (!this.formChantier.get("nom")?.valid) {
      return 'Validation impossible : ' + '<br>- Nom du chantier non renseigné';
    }

    if (!this.formChantier.get("annee_debut")?.valid) {
      return 'Validation impossible : ' + '<br>- Année de début non valide';
    }

    if (!this.formChantier.get("annee_fin")?.valid) {
      return 'Validation impossible : ' + '<br>- Année de fin non valide';
    }

    if (!this.formChantier.get("beton_pref")?.valid) {
      return 'Validation impossible : ' + '<br>- Le besoin en béton/préfabriqué, si renseigné, doit être un entier > 0';
    }

    if (!this.formChantier.get("viab_autre")?.valid) {
      return 'Validation impossible : ' + '<br>- Le besoin en viabilité/autre, si renseigné, doit être un entier > 0';
    }

    if (!this.formChantier.get("ton_tot")?.valid) {
      return 'Validation impossible : ' + '<br>- Le besoin total doit être un entier > 0';
    }

    return '';
  }

  calculTotal(){
    let beton_pref = this.formChantier.value.beton_pref;
    let viab_autre = this.formChantier.value.viab_autre;

    beton_pref = beton_pref === null ? 0 : parseInt(beton_pref);
    viab_autre = viab_autre === null ? 0 : parseInt(viab_autre);
    let ton_tot = beton_pref + viab_autre;

    this.formChantier.value.ton_tot = ton_tot;
  }

  validerModif(){
    let nouveauChantier = this.chantier;
    nouveauChantier.nom = this.formChantier.value.nom;
    nouveauChantier.description = this.formChantier.value.description;
    nouveauChantier.annee_debut = this.formChantier.value.annee_debut;
    nouveauChantier.annee_fin = this.formChantier.value.annee_fin;
    nouveauChantier.description = this.formChantier.value.description;
    nouveauChantier.beton_pref = this.formChantier.value.beton_pref;
    nouveauChantier.viab_autre = this.formChantier.value.viab_autre;
    nouveauChantier.ton_tot = this.formChantier.value.ton_tot;

    this.chantiersService.modificationChantier(nouveauChantier).subscribe({
      next: (value) => {
        console.log("Chantier modifié.");
        this.setModifChantier(nouveauChantier);
        this.modificationChantierDialog = false;
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de la modification du Chantier.");
      },
    });
  }

  setModifChantier(newChantier:Chantier){
    // Trouver l'élément dans le tableau
    const index = this.chantiersEtude.findIndex(item => item.id_chantier === newChantier.id_chantier);
    // Mettre à jour l'élément avec les nouvelles valeurs
    if (index !== -1) {
      this.chantiersEtude[index] = {
        ...this.chantiersEtude[index],
        ...newChantier
      };
    }
    this.chantiersService.changeFeatureChantier(newChantier);
  }

  annulerModif(){
    this.modificationChantierDialog = false;
  }

  validateInputFiles(event: any, fileuploadform: any) {
    this.overlayService.overlayOpen("Import en cours...");
    this.messageService.clear('chantier');
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

  uploadChantierEtude(event: any, fileuploadform: any) {
    let files = event.files;
    fileuploadform.clear();
    this.uploadFiles(files);
  }

  uploadFiles(files: File[]) {

    this.chantiersService.importChantierFiles(files, this.etude.id).subscribe({
      next: (value) => {
        this.messageService.clear();
        if (Array.isArray(value)) {
          for (const message of value) {
            this.messageService.add({ severity: 'warn', detail: message })
          }
        }
      },
      error: (error) => {
        this.showError(error.error);
        this.overlayService.overlayClose();
      },
      complete: () => {
        this.changeEtatEtape(EtatEtape.IMPORTE);
        this.initChantiers();
        this.showSuccess();
        this.overlayService.overlayClose();
      }
    });
  }

  showError(message: string) {
    this.messageService.clear();
    this.messageService.add({ severity: 'error', detail: message, key:'chantier' });
    setTimeout(() => {
      this.messageService.clear('chantier');
    }, 5000);
  }

  showSuccess() {
    this.messageService.add({ severity: 'success', detail: "Chantier(s) importé(s) avec succès.", key:'chantier' });
    setTimeout(() => {
      this.messageService.clear('chantier');
    }, 5000);
  }

  telechargerModele() {
    console.log("Téléchargement du modèle.");
    this.chantiersService.telechargerChantierZip().subscribe(data => {
      const blob = new Blob([data], { type: 'application/geopackage+sqlite3' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `chantier.zip`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  tooltipChantierOrigine(chantier:Chantier){
    if(chantier.libelle_pere != null){
      return 'Ce chantier est dérivé du chantier '+ chantier.libelle_pere;
    }
    return '';
  }

  getChantierOrigine(chantier:Chantier){
    if(chantier.id_source === null || chantier.id_source === undefined){
      return null;
    }
    for(let c of this.chantiersEtude){
      if(c.id_chantier === chantier.id_source && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    for(let c of this.chantiersEtude){
      if(c.feature.id === chantier.id_source && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    for(let c of this.chantiersExistants){
      if(c.id_chantier === chantier.id_source && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    return null;
  }

  getChantierFrere(chantier:Chantier){
    if(chantier.id_frere === null || chantier.id_frere === undefined){
      return null;
    }
    for(let c of this.chantiersEtude){
      if(c.id_chantier === chantier.id_frere && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    for(let c of this.chantiersEtude){
      if(c.feature.id === chantier.id_frere && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    for(let c of this.chantiersExistants){
      if(c.id_chantier === chantier.id_frere && c.id_chantier != chantier.id_chantier){
        return c;
      }
    }
    return null;
  }

  classAffichage(chantier:Chantier){
    if(chantier.libelle_pere != null){
      return 'td-nom-gras-ital';
    }
    else
      return 'td-nom-class';
  }

  changeClassAllChantier() {
    for(let c of this.chantiersEtude){
      this.chantiersService.changeClassAffichage(c);
    }
    for(let c of this.chantiersExistants){
      this.chantiersService.changeClassAffichage(c);
    }
  }

  ajoutImpossible(chantier:Chantier){
    if(chantier.id_source === null || chantier.id_source === undefined){
      for(let c of this.chantiersEtude){
        if(c.id_source === chantier.id_chantier){
          return true;
        }
      }
      return false;
    }
    for(let c of this.chantiersEtude){
      if(c.id_chantier !== chantier.id_chantier && c.id_source === chantier.id_source){
        return true;
      }
      if(c.id_source === chantier.id_chantier){
        return false;
      }
    }
    return false;
  }

  clearSubscription() {
    if (this.chantierEtudeSubscription) {
      this.chantierEtudeSubscription.unsubscribe();
    }
    if (this.chantierExistantesSubscription) {
      this.chantierExistantesSubscription.unsubscribe();
    }
  }

  ngOnDestroy(): void {
    this.clearSubscription();

    if(this.chantierInitSubscription)
      this.chantierInitSubscription.unsubscribe();
    if(this.chantierResetSubscription)
      this.chantierResetSubscription.unsubscribe();
  }

  changeEtatEtape(etatEtape: EtatEtape) {
    this.etude.etatEtapes[Etape.CHANTIERS] = etatEtape;
    this.actualStepEvent.emit(Etape.CHANTIERS);
  }

  isEtapeScenario(){
    return this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE
          || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE;
  }

  isLectureSeule(){
    return this.etude.readOnly;
  }
}
