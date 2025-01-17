import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Materiau } from './model/materiau.model';
import { TooltipOptions } from 'primeng/tooltip';
import { Subscription } from 'rxjs';
import { Etude } from '../model/etude.model';
import { AutresMateriauxService } from './service/autres-materiaux.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';
import {Etape} from "../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../shared/enums/etatEtape.enums";

@Component({
  selector: 'autres-materiaux',
  templateUrl: './autres-materiaux.component.html',
  styleUrls: ['./autres-materiaux.component.scss'],
  providers: [ArrayUtilsService]
})
export class AutresMateriauxComponent implements OnInit {

  @Output() actualStepEvent = new EventEmitter<Etape>();

  autresMateriaux:Materiau[] = [];

  ajoutMateriauDialog: boolean = false;
  nomDialog = 'Ajout Materiaux';
  autreMateriau: Materiau;
  formAutreMateriau:any;

  listeMateriauDisponible:Materiau[];
  listeOrigine:String[] = [];

  @Input() etude: Etude;
  @Input() actualStep: Etape;

  tooltipOptionsMessage : TooltipOptions = {tooltipStyleClass:'tooltip-import-message'};

  private autreMateriauEtudeSubscription: Subscription;
  private autreMateriauInitSubscription: Subscription;

  constructor(private autresMateriauxService:AutresMateriauxService,
    private formBuilder: FormBuilder,
    private arrayUtils: ArrayUtilsService) { }

  ngOnInit(): void {
    if(this.actualStep === Etape.MATERIAUX)
      this.initAutresMateriaux();

    this.formAutreMateriau = this.formBuilder.group({
      libelle: [''],
      origine: [''],
      tonnage: [''],
    },{validators: [this.formValidationAutreMateriau.bind(this)]});

    this.autreMateriauInitSubscription = this.autresMateriauxService.autresMateriauxInitSource$.subscribe(value => {
      this.initAutresMateriaux();
    });
  }

  initAutresMateriaux(){
    this.autresMateriauxService.getMateriauxDisponibles().subscribe({
      next: (value: Materiau[]) => {
        console.log("Materiaux disponibles récupérés");
        this.listeMateriauDisponible = value;
        for(let c of value){
          if(!this.listeOrigine.includes(c.origine)){
            this.listeOrigine.push(c.origine);
          }
        }
      },
      error: (error) => {
        console.log("Matériaux non récupérés");
      }
    });

    if(this.autreMateriauEtudeSubscription)
      this.autreMateriauEtudeSubscription.unsubscribe();
    this.autreMateriauEtudeSubscription = this.autresMateriauxService.getMateriauxEtude(this.etude.id).subscribe({
      next: (value: Materiau[]) => {
        this.autresMateriaux = value;
        console.log("Récupération des matériaux de l'étude.");
      },
      error: (error) => {
        console.log("Erreur lors de la récupération des matériaux de l'étude.");
      }
    });
  }

  clickSuppr(event: Materiau){
    this.autresMateriauxService.supprimerMateriau(event.id_materiau).subscribe({
      next: (value) => {
        console.log("Materiau supprimée.");
        this.autresMateriaux = this.arrayUtils.arrayRemoveIdMateriau(this.autresMateriaux,event);
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de la suppresion du Materiau.");
      }
    });
  }

  clickAjout(){
    this.nomDialog = 'Ajout matériaux';
    this.formAutreMateriau = this.formBuilder.group({
      libelle: [''],
      origine: [''],
      tonnage: [''],
    }, {validators: [this.formValidationAutreMateriau.bind(this)]});

    this.autreMateriau = new Materiau;

    this.ajoutMateriauDialog = true;
  }

  clickModification(event: Materiau){
    this.nomDialog = 'Modification matériaux';
    this.formAutreMateriau = this.formBuilder.group({
      libelle: [event.libelle],
      origine: [event.origine],
      tonnage: [event.tonnage],
    }, {validators: [this.formValidationAutreMateriau.bind(this)]});

    this.autreMateriau = event;
    this.ajoutMateriauDialog = true;
  }

  onChangeMateriau(event:any){
    let libelle = event.value;
    for(let m of this.listeMateriauDisponible){
      if(m.libelle === libelle){
        this.formAutreMateriau.get('origine').setValue(m.origine);
      }
    }
  }

  validerMateriau(){
    let nouveauMateriau = this.autreMateriau;
    nouveauMateriau.libelle = this.formAutreMateriau.value.libelle;
    nouveauMateriau.origine = this.formAutreMateriau.value.origine;
    nouveauMateriau.tonnage = this.formAutreMateriau.value.tonnage;
    nouveauMateriau.type = nouveauMateriau.origine === 'Naturel' ? 'Primaire' : 'Secondaire';
    nouveauMateriau.id_etude = this.etude.id;

    this.autresMateriauxService.ajoutMateriau(nouveauMateriau).subscribe({
      next: (value: any) => {
        console.log("Materiau ajouté.");
        nouveauMateriau.id_materiau = value.id_materiau as number;
        this.setAjoutModifAutreMateriau(nouveauMateriau);
        this.ajoutMateriauDialog = false;
        this.changeEtatEtape(EtatEtape.MODIFIE);
      },
      error: (value) => {
        console.log("Erreur lors de l'ajout du materiau.");
      },
    });
  }

  setAjoutModifAutreMateriau(newAutreMateriau:Materiau){
    // Trouver l'élément dans le tableau
    const index = this.autresMateriaux.findIndex(item => item.id_materiau === newAutreMateriau.id_materiau);
    // Mettre à jour l'élément avec les nouvelles valeurs
    if (index !== -1) {
      this.autresMateriaux[index] = {
        ...this.autresMateriaux[index],
        ...newAutreMateriau
      };
    }
    else {
      this.autresMateriaux.push(newAutreMateriau);
    }
  }

  annuler(){
    this.ajoutMateriauDialog = false;
  }

  formValidationAutreMateriau(form : FormGroup){
    if(form.value.libelle != '' &&
        form.value.origine != '' &&
        form.value.tonnage != '') {
          return null;
    }
    return {error : true};
  }

  tooltipValidation(){
    let tooltip = '';
    if((this.formAutreMateriau.value.libelle === '')){
      tooltip += '<br>- Libellé'
    }
    if((this.formAutreMateriau.value.origine === '')){
      tooltip += '<br>- Origine'
    }
    if((this.formAutreMateriau.value.tonnage === '')){
      tooltip += '<br>- Tonnage'
    }
    if(tooltip !== ''){
      return tooltip = 'Validation impossible : ' + tooltip;
    }
    return tooltip;
  }

  clearSubscription() {
    if (this.autreMateriauEtudeSubscription) {
      this.autreMateriauEtudeSubscription.unsubscribe();
    }
    if (this.autreMateriauInitSubscription) {
      this.autreMateriauInitSubscription.unsubscribe();
    }
  }

  ngOnDestroy(): void {
    this.clearSubscription();
  }

  changeEtatEtape(etatEtape: EtatEtape) {
    this.etude.etatEtapes[Etape.MATERIAUX] = etatEtape;
    this.actualStepEvent.emit(Etape.MATERIAUX);
  }

  isEtapeScenario(){
    return this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE
          || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE;
  }
}
