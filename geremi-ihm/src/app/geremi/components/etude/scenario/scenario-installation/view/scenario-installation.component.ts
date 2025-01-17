import { Component, Input, OnInit } from '@angular/core';
import { Scenario } from '../../model/scenario.model';
import { ScenarioInstallationService } from '../service/scenario-installation.service';
import { InstallationStockage } from '../../../installation-stockage/model/installation-stockage.model';
import { InstallationStockageService } from '../../../installation-stockage/service/installation-stockage.service';
import { LayersService } from 'src/app/geremi/components/carto/service/layers.service';
import { Subscription } from 'rxjs';
import { ScenarioService } from '../../service/scenario.service';
import {EtapeScenario} from "../../../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../../../shared/enums/etatEtape.enums";

@Component({
  selector: 'scenario-installation',
  templateUrl: './scenario-installation.component.html',
  styleUrls: ['./scenario-installation.component.scss']
})
export class ScenarioInstallationComponent implements OnInit {
  @Input() scenario:Scenario;
  @Input() lectureSeule:boolean;
  @Input() scenarioCreationActiveStep:EtapeScenario;

  protected readonly EtapeScenario = EtapeScenario;

  installationStockages: InstallationStockage[] = [];
  selectedInstallations: InstallationStockage[] = [];
  private installationSubscription: Subscription;

  constructor(private scenarioInstallationService:ScenarioInstallationService,
    private installationStockageService: InstallationStockageService,
    private layersService: LayersService,
    private scenarioService: ScenarioService) { }

  ngOnInit(): void {

    this.installationSubscription = this.scenarioService.scenarioInstallationStockageSubject$.subscribe(() => {
      if (this.scenarioCreationActiveStep === EtapeScenario.INSTALLATIONS_SCENARIO) {
        this.displayEtudeInstallationStockageLayers();
      }
    });

    this.installationStockageService.getInstallationStockageEtude(this.scenario.etudeDTO.id)
      .subscribe(installations => {
        this.installationStockages = installations

        for(let installation of this.scenario.scenario_installations){
          const index = this.installationStockages.findIndex(inst => inst.id === installation.id_installation);
          if (index !== -1) {
            this.selectedInstallations.push(this.installationStockages[index])
          }
        }

      });
  }

  ajoutInstallationScenario(){
    this.scenarioInstallationService.ajoutInstallationScenario(this.scenario, this.selectedInstallations);
  }

  onInstallationSelected(event: any, installation: InstallationStockage): void {
    if (event.target.checked) {
        // Si la case est cochée, ajoutez l'installation à la liste des installations sélectionnées
        this.selectedInstallations.push(installation);
    } else {
        // Si la case n'est pas cochée, retirez l'installation de la liste
        const index = this.selectedInstallations.findIndex(i => i.id === installation.id);
        if (index !== -1) {
            this.selectedInstallations.splice(index, 1);
        }
    }

    // Appelez displayEtudeInstallationStockageLayers avec la liste mise à jour des installations sélectionnées
    this.layersService.displayEtudeInstallationStockageLayers(this.selectedInstallations);
  }

  displayEtudeInstallationStockageLayers() {
    this.layersService.displayEtudeInstallationStockageLayers(this.selectedInstallations);
  }

  isChecked(id:number){
    const index = this.selectedInstallations.findIndex(i => i.id === id);
    if (index !== -1) {
      return true;
    }
    return false;
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  notChanged(){

    if (this.scenario.scenario_installations?.length !== this.selectedInstallations?.length) {
      return false;
    }
    for(let installation of this.scenario.scenario_installations){
      const indexSelected = this.selectedInstallations.findIndex(i => i.id === installation.id_installation as number);
      if (indexSelected === -1) {
        return false;
      }
    }
    return true;
  }

  ngOnDestroy(): void {
    if (this.installationSubscription) {
      this.installationSubscription.unsubscribe();
    }
  }

  isStepStatePassed(etape: EtapeScenario) {
    const s: any = this.scenario.etatEtapes;
    return s[etape] === EtatEtape.VALIDE.valueOf();
  }
}
