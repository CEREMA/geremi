import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Scenario } from '../../model/scenario.model';
import { ScenarioChantierService } from '../service/scenario-chantier.service';
import { LayersService } from 'src/app/geremi/components/carto/service/layers.service';
import { Subscription } from 'rxjs';
import { ScenarioService } from '../../service/scenario.service';
import { ChantiersService } from '../../../chantiers-envergure/service/chantiers-env.service';
import { Chantier } from '../../../chantiers-envergure/model/chantiers.model';
import {EtapeScenario} from "../../../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../../../shared/enums/etatEtape.enums";

@Component({
  selector: 'scenario-chantier',
  templateUrl: './scenario-chantier.component.html',
  styleUrls: ['./scenario-chantier.component.scss']
})
export class ScenarioChantierComponent implements OnInit, OnDestroy {
  @Input() scenario:Scenario;
  @Input() lectureSeule:boolean;
  @Input() scenarioCreationActiveStep:EtapeScenario;

  protected readonly EtapeScenario = EtapeScenario;

  chantiers: Chantier[] = [];
  selectedChantiers: Chantier[] = [];
  private chantierSubscription: Subscription;

  constructor(
    private scenarioChantierService:ScenarioChantierService,
    private chantierService: ChantiersService,
    private layersService: LayersService,
    private scenarioService: ScenarioService
  ) { }

  ngOnInit(): void {

    this.chantierSubscription = this.scenarioService.scenarioChantierSubject$.subscribe(() => {
      if (this.scenarioCreationActiveStep === EtapeScenario.CHANTIERS_SCENARIO) {
        this.displayEtudeChantierLayers();
      }
    });

    this.chantierService.getChantiersEtude(this.scenario.etudeDTO.id)
      .subscribe(chantiers => {
        this.chantiers = chantiers
        for(let chantier of this.scenario.scenario_chantiers){
          const index = this.chantiers.findIndex(c => c.id_chantier === chantier.id_chantier);
          if (index !== -1) {
            this.selectedChantiers.push(this.chantiers[index])
          }
        }

      });
  }

  ajoutChantierScenario(){
    this.scenarioChantierService.ajoutChantierScenario(this.scenario, this.selectedChantiers);
  }

  onChantierSelected(event: any, chantier: Chantier): void {
    if (event.target.checked) {
        this.selectedChantiers.push(chantier);
    } else {
        const index = this.selectedChantiers.findIndex(c => c.id_chantier === chantier.id_chantier);
        if (index !== -1) {
            this.selectedChantiers.splice(index, 1);
        }
    }

    this.layersService.displayEtudeChantiersExistantsLayers(this.selectedChantiers);
  }

  displayEtudeChantierLayers() {
    this.layersService.displayEtudeChantiersExistantsLayers(this.selectedChantiers);
  }

  isChecked(id:number){
    const index = this.selectedChantiers.findIndex(c => c.id_chantier === id);
    if (index !== -1) {
      return true;
    }
    return false;
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  notChanged(){

    if (this.scenario.scenario_chantiers?.length !== this.selectedChantiers?.length) {
      return false;
    }
    for(let chantier of this.scenario.scenario_chantiers){
      const indexSelected = this.selectedChantiers.findIndex(c => c.id_chantier === chantier.id_chantier as number);
      if (indexSelected === -1) {
        return false;
      }
    }
    return true;
  }

  ngOnDestroy(): void {
    if (this.chantierSubscription) {
      this.chantierSubscription.unsubscribe();
    }
  }

  isStepStatePassed(etape: EtapeScenario) {
    const s: any = this.scenario.etatEtapes;
    return s[etape] === EtatEtape.VALIDE.valueOf();
  }
}
