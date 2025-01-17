import { Component, Input, OnInit } from '@angular/core';
import { Scenario } from '../../model/scenario.model';
import { ScenarioContrainteService } from '../service/scenario-contrainte.service';
import { ContrainteEnvironnementale } from '../../../contraintes-environnementales/model/contrainteEnv.model';
import { ContrainteEnvironnetaleService } from '../../../contraintes-environnementales/service/contrainte-env.service';
import { LayersService } from 'src/app/geremi/components/carto/service/layers.service';
import { ScenarioService } from '../../service/scenario.service';
import { Subscription } from 'rxjs';
import {EtapeScenario} from "../../../../../../shared/enums/etape.enums";

@Component({
  selector: 'scenario-contrainte',
  templateUrl: './scenario-contrainte.component.html',
  styleUrls: ['./scenario-contrainte.component.scss']
})
export class ScenarioContrainteComponent implements OnInit {
  @Input() scenario:Scenario;
  @Input() lectureSeule:boolean;
  @Input() scenarioCreationActiveStep:EtapeScenario;
  contraintes: ContrainteEnvironnementale[] = [];
  selectedContrainte: ContrainteEnvironnementale[] = [];
  tauxHCInitial: Number;
  valide: boolean = false;
  private contrainteSubscription: Subscription;

  constructor(private scenarioContrainteService:ScenarioContrainteService,
    private contrainteService: ContrainteEnvironnetaleService,
    private layersService: LayersService,
    private scenarioService: ScenarioService) { }

  ngOnInit(): void {
    let horsContrainte = new ContrainteEnvironnementale();
    horsContrainte.nom = 'Hors Contrainte';
    this.contraintes = [];
    this.contraintes.push(horsContrainte);
    this.tauxHCInitial = this.scenario?.tx_renouvellement_hc;

    this.contrainteSubscription = this.scenarioService.scenarioContrainteSubject$.subscribe(() => {
      if (this.scenarioCreationActiveStep === EtapeScenario.CONTRAINTES_SCENARIO) {
        this.displayEtudeContraintesLayers();
      }
    });

    this.contrainteService.getContrainteEtude(this.scenario.etudeDTO.id).subscribe( value => {
      let valueSorted = value.sort((a,b) => this.compareNiveau(a,b));
      for(let c of valueSorted){
        this.contraintes.push(c);
      }

      for(let cont of this.scenario.scenario_contraintes){
        const index = this.contraintes.findIndex(c => c.id === cont.id_contrainte as number);
        if (index !== -1) {
          let contrainte = this.contraintes[index];
          contrainte.tx_renouvellement_contrainte = cont.tx_renouvellement_contrainte;
          this.selectedContrainte.push(contrainte)
        }
      }
      this.scenarioService.scenarioContrainte();
    })
    this.scenarioContrainteService.scenarioContrainteActualiserTauxSource$.subscribe(() => {
      this.displayTxRenouvellement();
    })

    this.scenarioContrainteService.scenarioContrainteClearTauxSource$.subscribe(() => {
      for(let c of this.contraintes) {
        c.tx_renouvellement_contrainte = undefined;
      }
      this.displayTxRenouvellement();
    })

    this.verificationDonnees();
  }

  ajoutContrainteScenario(){
    // Envoyer l'ensembles des informations de la table Contrainte
    this.scenarioContrainteService.ajoutContrainteScenario(this.scenario, this.selectedContrainte);
    this.tauxHCInitial = this.scenario.tx_renouvellement_hc;
  }

  displayEtudeContraintesLayers() {
    this.layersService.displayEtudeContrainteExistantesLayers(this.selectedContrainte)
  }

  onContrainteSelected(event:any, contrainte:ContrainteEnvironnementale){
    if (event.target.checked) {
      this.selectedContrainte.push(contrainte);
    } else {
      const index = this.selectedContrainte.findIndex(c => c.id === contrainte.id);
      if (index !== -1) {
          this.selectedContrainte.splice(index, 1);
      }
      const ind = this.contraintes.findIndex(c => c.id === contrainte.id);
      this.contraintes[ind].tx_renouvellement_contrainte = undefined;
    }
    this.layersService.displayEtudeContrainteExistantesLayers(this.selectedContrainte)
    this.displayTxRenouvellement();
    this.verificationDonnees();
  }

  isChecked(idContrainte:number){
    const index = this.selectedContrainte.findIndex(c => c.id === idContrainte);
    if (index !== -1) {
      return true;
    }
    return false;
  }

  onChangeTxHorsContrainte(event:any){
    this.scenario.tx_renouvellement_hc = event.value;

    this.displayTxRenouvellement();
    this.verificationDonnees();
  }

  onChangeTxContrainte(event:any, contrainte:ContrainteEnvironnementale){
    for(let c of this.selectedContrainte) {
      if(c.id === contrainte.id){
        if(event.value == null){
          c.tx_renouvellement_contrainte = undefined;
        } else {
          c.tx_renouvellement_contrainte = event.value;
        }
      }
    }
    this.displayTxRenouvellement();
    this.verificationDonnees();
  }

  verificationDonnees(){
    this.valide = this.donneesValide();
  }

  displayTxRenouvellement(){
    let horsContrainte = new ContrainteEnvironnementale();
    horsContrainte.id = 0;
    horsContrainte.tx_renouvellement_contrainte = this.scenario.tx_renouvellement_hc;

    let tmpliste: ContrainteEnvironnementale[] = [];
    tmpliste.push(horsContrainte);
    for(let c of this.selectedContrainte){
      tmpliste.push(c);
    }

    setTimeout(() => {
      this.scenarioContrainteService.onChangeTauxRenouvellement(tmpliste);
    }, 800);
  }

  donneesValide(){
    let txFaibleMin;
    let txMoyenneMin;
    let txForteMin;

    let txFaibleMax;
    let txMoyenneMax;
    let txForteMax;

    if(this.scenario.tx_renouvellement_hc === undefined || this.scenario.tx_renouvellement_hc === null){
      return false;
    }
    for(let c of this.selectedContrainte) {
      if(c.tx_renouvellement_contrainte === undefined || c.tx_renouvellement_contrainte === null){
        return false;
      }
      if(c.niveau === 'Faible'){
        if(txFaibleMin === undefined){
          txFaibleMin = c.tx_renouvellement_contrainte;
          txFaibleMax = c.tx_renouvellement_contrainte;
        }
        if(txFaibleMin > c.tx_renouvellement_contrainte){
          txFaibleMin = c.tx_renouvellement_contrainte;
        }
        if(txFaibleMax < c.tx_renouvellement_contrainte){
          txFaibleMax = c.tx_renouvellement_contrainte;
        }
      } else if(c.niveau === 'Moyenne'){
        if(txMoyenneMin === undefined){
          txMoyenneMin = c.tx_renouvellement_contrainte;
          txMoyenneMax = c.tx_renouvellement_contrainte;
        }
        if(txMoyenneMin > c.tx_renouvellement_contrainte){
          txMoyenneMin = c.tx_renouvellement_contrainte;
        }
        if(txMoyenneMax < c.tx_renouvellement_contrainte){
          txMoyenneMax = c.tx_renouvellement_contrainte;
        }
      } else if(c.niveau === 'Forte'){
        if(txForteMin === undefined){
          txForteMin = c.tx_renouvellement_contrainte;
          txForteMax = c.tx_renouvellement_contrainte;
        }
        if(txForteMin > c.tx_renouvellement_contrainte){
          txForteMin = c.tx_renouvellement_contrainte;
        }
        if(txForteMax < c.tx_renouvellement_contrainte){
          txForteMax = c.tx_renouvellement_contrainte;
        }
      }
    }

    // Tx Faible Max
    if(txFaibleMax !== undefined && this.scenario.tx_renouvellement_hc < (txFaibleMax as Number)){
      return false;
    }
    // Tx Moyenne Max
    if(txMoyenneMax !== undefined && txFaibleMin !== undefined && (txFaibleMin as Number) < (txMoyenneMax as Number)){
      return false;
    }
    if(txMoyenneMax !== undefined && this.scenario.tx_renouvellement_hc < (txMoyenneMax as Number)){
      return false;
    }
    // Tx Forte Max
    if(txForteMax !== undefined && txFaibleMin !== undefined && (txFaibleMin as Number) < (txForteMax as Number)){
      return false;
    }
    if(txForteMax !== undefined && txMoyenneMin !== undefined && (txMoyenneMin as Number) < (txForteMax as Number)){
      return false;
    }
    if(txForteMax !== undefined && this.scenario.tx_renouvellement_hc < (txForteMax as Number)){
      return false;
    }

    return true;
  }

  contrainteNotSelected(contrainte:ContrainteEnvironnementale){
    const index = this.selectedContrainte.findIndex(c => c.id === contrainte.id);
    if (index !== -1) {
      return false;
    }
    return true;
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  notChanged(){

    if (this.tauxHCInitial !== this.scenario.tx_renouvellement_hc) {
      return false;
    }
    if (this.scenario.scenario_contraintes?.length !== this.selectedContrainte?.length) {
      return false;
    }
    for(let cont of this.scenario.scenario_contraintes){
      const indexSelected = this.selectedContrainte.findIndex(c => c.id === cont.id_contrainte as number);
      if (indexSelected !== -1) {
        let contrainte = this.selectedContrainte[indexSelected];
        if (contrainte.tx_renouvellement_contrainte !== cont.tx_renouvellement_contrainte) {
          return false;
        }
      } else {
        return false;
      }
    }
    return true;
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

  ngOnDestroy(): void {
    if (this.contrainteSubscription) {
      this.contrainteSubscription.unsubscribe();
    }
  }
}
