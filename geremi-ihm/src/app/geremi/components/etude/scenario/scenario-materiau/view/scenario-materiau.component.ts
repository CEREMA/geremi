import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { Scenario } from '../../model/scenario.model';
import { ScenarioMateriauService } from '../service/scenario-materiau.service';
import { Materiau } from '../../../autres-materiaux/model/materiau.model';
import { AutresMateriauxService } from '../../../autres-materiaux/service/autres-materiaux.service';
import { Subscription } from 'rxjs';
import { ScenarioService } from '../../service/scenario.service';
import { Zonage } from '../../../model/zonage.model';
import { ScenarioMateriau } from '../model/scenario-materiau.model';
import {EtapeScenario} from "../../../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../../../shared/enums/etatEtape.enums";

@Component({
  selector: 'scenario-materiau',
  templateUrl: './scenario-materiau.component.html',
  styleUrls: ['./scenario-materiau.component.scss']
})
export class ScenarioMateriauComponent implements OnInit {
  @Input() scenario:Scenario;
  @Input() listeZoneTerritoire: Zonage[] = [];
  @Input() lectureSeule:boolean;
  @Input() scenarioCreationActiveStep:EtapeScenario;

  protected readonly EtapeScenario = EtapeScenario;

  materiaux: Materiau[] = [];
  materiauxInit: Materiau[] = [];
  selectedMateriaux: Materiau[] = [];

  materiauxSubscription: Subscription;
  displayMateriauxSubscription: Subscription;

  constructor(private scenarioMateriauService:ScenarioMateriauService,
    private scenarioService:ScenarioService,
    private materiauService:AutresMateriauxService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {

    this.displayMateriauxSubscription = this.scenarioService.scenarioMateriauSubject$.subscribe(() => {
      if (this.scenarioCreationActiveStep === EtapeScenario.MATERIAUX_SCENARIO) {
        this.displayEtudeMateriauxEtiquette();
      }
    });

    this.materiauxSubscription = this.materiauService.getMateriauxEtude(this.scenario.etudeDTO.id).subscribe(materiaux => {
      this.materiaux = materiaux;
      let selectedMateriaux: Materiau[] = [];
      for(let mat of this.scenario.scenario_materiaux){
        const index = this.materiaux.findIndex(m => m.id_materiau === mat.id_materiau as number);
        if (index !== -1) {
          let tmp = this.materiaux[index];
          if(tmp.scenario_materiau === undefined){
            tmp.scenario_materiau = [];
          }

          tmp.scenario_materiau.push(mat);

          const indexSelected = selectedMateriaux.findIndex(m => m.id_materiau === mat.id_materiau as number);
          if(indexSelected !== -1) {
            selectedMateriaux.splice(indexSelected, 1);
          }
          selectedMateriaux.push(this.materiaux[index]);
        }
      }
      this.materiauxInit = JSON.parse(JSON.stringify(this.materiaux));
      this.selectedMateriaux = selectedMateriaux;
    });

  }

  ajoutMateriauScenario(){
    // Envoyer l'ensembles des informations de la table Materiau
    this.scenarioMateriauService.ajoutMateriauScenario(this.scenario, this.selectedMateriaux);
    this.materiauxInit = JSON.parse(JSON.stringify(this.materiaux));
  }

  onMateriauSelected(event: any, materiau: Materiau): void {
    if (event.target.checked) {
      const id = this.scenario.scenario_materiaux.findIndex(c => c.id_materiau === materiau.id_materiau)
      if (id !== -1) {
        materiau.scenario_materiau = [];
        for(let sc of this.scenario.scenario_materiaux){
          if(sc.id_materiau === materiau.id_materiau){
            materiau.scenario_materiau.push(sc);
          }
        }
      } else {
        let sce_mat = new ScenarioMateriau();
        sce_mat.id_materiau = materiau.id_materiau;
        sce_mat.id_scenario = this.scenario.id;
        sce_mat.libelle = materiau.libelle;
        sce_mat.origine = materiau.origine;

        materiau.scenario_materiau = [];
        materiau.scenario_materiau.push(sce_mat);
      }
      this.selectedMateriaux.push(materiau);
    } else {
      const index = this.selectedMateriaux.findIndex(c => c.id_materiau === materiau.id_materiau);
      if (index !== -1) {
        this.selectedMateriaux.splice(index, 1);
      }
    }
    this.displayEtudeMateriauxEtiquette();
  }

  addEtiquette(materiaux:Materiau[]){
    let relationZone:ScenarioMateriau[] = [];
    for(let mat of materiaux){
      for(let sce_mat of mat.scenario_materiau){
        sce_mat.libelle = mat.libelle;
        sce_mat.origine = mat.origine;
        if(!(sce_mat.id_zone == null || sce_mat.pourcent == null)){
          relationZone.push(sce_mat);
        }
      }
    }

    this.scenarioMateriauService.displayEtiquetteOnZone(relationZone);
  }

  onChangeZone(event:any, relation:ScenarioMateriau){
    if(!(relation.pourcent ===  undefined || relation.pourcent ===  null)){
      this.displayEtudeMateriauxEtiquette();
    }
  }

  onChangeVentilation(relation:ScenarioMateriau, event:any){
    const index = this.materiaux.findIndex(m => m.id_materiau === relation.id_materiau);
    let val = event.value;
    if(val > 100){
      val = 100;
    }
    let mat = this.materiaux[index];
    relation.tonnage = (val as number) / 100 * (mat.tonnage as number);
    relation.pourcent = val;
    this.addEtiquette(this.selectedMateriaux);
  }

  ajouterZone(id_materiau:Number){
    const index = this.selectedMateriaux.findIndex(m => m.id_materiau === id_materiau);
    if (index !== -1) {
      let sce_mat = new ScenarioMateriau();
      sce_mat.id_materiau = id_materiau;
      sce_mat.id_scenario = this.scenario.id;
      this.selectedMateriaux[index].scenario_materiau.push(sce_mat);
    }
  }

  isChecked(id_materiau:number){
    const index = this.selectedMateriaux.findIndex(m => m.id_materiau === id_materiau);
    if (index !== -1) {
      return true;
    }
    return false;
  }

  clickSuppr(relations:ScenarioMateriau[],index:number){
    relations.splice(index,1);
    this.displayEtudeMateriauxEtiquette();
  }

  filterZone(relations:ScenarioMateriau[],relation:ScenarioMateriau){
    let listeZoneFilter:Zonage[] = [];
    if(this.listeZoneTerritoire === undefined){
      return listeZoneFilter;
    }
    for(let l of this.listeZoneTerritoire){
      listeZoneFilter.push(l);
    }

    for(let rel of relations){
      if(rel.id_materiau === relation.id_materiau){
        const index = listeZoneFilter.findIndex(z => z.id_zone === rel.id_zone);
        if(index !== -1 && relation.id_zone !== listeZoneFilter[index].id_zone){
          listeZoneFilter.splice(index,1);
        }
      }
    }
    return listeZoneFilter;
  }

  displayEtudeMateriauxEtiquette() {
    if(this.selectedMateriaux !== undefined)
      this.addEtiquette(this.selectedMateriaux);
  }

  verificationAjout(){
    let map = new Map();

    for(let materiau of this.selectedMateriaux){
      map.set(materiau.id_materiau,0)
      for(let m of materiau.scenario_materiau){
        let tmp = map.get(materiau.id_materiau);
        if(m.pourcent == null || m.id_zone == null){
          return false;
        }
        map.set(materiau.id_materiau,tmp+m.pourcent)
      }
    }

    for(let pourcent of map.values()){
      if(pourcent !== 100){
        return false;
      }
    }
    return true;
  }

  tooltipValideAjout(){
    let map = new Map();

    for(let materiau of this.selectedMateriaux){
      map.set(materiau.id_materiau,0)
      for(let m of materiau.scenario_materiau){
        let tmp = map.get(materiau.id_materiau);
        if(m.pourcent == null || m.id_zone == null){
          return 'Validation impossible : des données sont manquantes';
        }
        map.set(materiau.id_materiau,tmp+m.pourcent)
      }
    }

    for(let pourcent of map.values()){
      if(pourcent === 0 || pourcent > 100){
        return 'Validation impossible : la somme des ventilations de chaque matériau sélectionné est supérieur à 100%';
      }
    }
    return '';
  }

  ajoutZoneShow(id_materiau:Number){
    let map = new Map();

    for(let materiau of this.selectedMateriaux){
      map.set(materiau.id_materiau,0)
      for(let m of materiau.scenario_materiau){
        let tmp = map.get(materiau.id_materiau);
        map.set(materiau.id_materiau,tmp+m.pourcent)
      }
    }
    if(map.get(id_materiau) === 100){
      return false;
    }
    return true;
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  notChanged(){

    const selectedMatInit = this.materiauxInit.filter(m => m.scenario_materiau != null && m.scenario_materiau.length > 0);
    // check si on a autant de materiaux de cochés
    if (this.selectedMateriaux?.length !== selectedMatInit?.length) {
      return false;
    }

    // check si les mêmes materiaux sont cochés
    for(let materiau of this.selectedMateriaux) {
      const indexSelected = selectedMatInit.findIndex(c => c.id_materiau === materiau.id_materiau as number);
      if (indexSelected === -1) {
        return false;
      }
    }

    //check si les mêmes zones et pourcentage sont affectés
    for(let materiau of this.selectedMateriaux) {
      const current = this.materiaux.find(m => m.id_materiau === materiau.id_materiau as number);
      let init =  selectedMatInit.find(m => m.id_materiau === materiau.id_materiau as number);
      //pour le materiau considéré on compare la taille de la liste des zones
      if (current?.scenario_materiau?.length !== init?.scenario_materiau?.length) {
        return false;
      }
      if (init?.scenario_materiau != null) {
        for (let zone of init?.scenario_materiau) {
          const idx = current?.scenario_materiau?.findIndex(z => z.id_zone === zone.id_zone && z.pourcent === zone.pourcent);
          if (idx == null || idx === -1) {
            return false;
          }
        }
      }
    }
    return true;
  }

  ngOnDestroy(): void {
    if (this.materiauxSubscription) {
      this.materiauxSubscription.unsubscribe();
    }
    if (this.displayMateriauxSubscription) {
      this.displayMateriauxSubscription.unsubscribe();
    }
  }

  isStepStatePassed(etape: EtapeScenario) {
    const s: any = this.scenario.etatEtapes;
    return s[etape] === EtatEtape.VALIDE.valueOf();
  }


}
