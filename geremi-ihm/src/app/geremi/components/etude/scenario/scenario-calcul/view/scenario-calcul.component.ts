import {Component, OnInit, Input, ChangeDetectorRef} from "@angular/core";
import { Scenario } from "../../model/scenario.model";
import { EtapeScenario } from "src/app/shared/enums/etape.enums";
import { EtatEtape } from "src/app/shared/enums/etatEtape.enums";
import { ScenarioCalculService } from "../service/scenario-calcul.service";
import { ZoneProductionDetails } from "../model/zoneProductionDetails.model";
import { Message } from "primeng/api";
import { ProductionDepartement } from "../model/productionDepartement.model";
import { RelScenarioDepartement } from "../model/relScenarioDepartement.model";


@Component({
  selector: 'scenario-calcul',
  templateUrl: './scenario-calcul.component.html',
  styleUrls: ['./scenario-calcul.component.scss']
})
export class ScenarioCalculComponent implements OnInit {
  protected readonly EtapeScenario = EtapeScenario;
  protected readonly EtatEtape = EtatEtape;
  @Input() scenario: Scenario;
  @Input() actualStep: EtapeScenario;
  @Input() activeIndexStepCalcul: any;
  @Input() anneeRef: any;
  @Input() lectureSeule:boolean;
  zoneProductionDetails: ZoneProductionDetails[] = [];

  departementsPartielsTer: RelScenarioDepartement[] = [];
  valueSurfaceBeton: number = 50;
  valueSurfaceViab: number = 50;

  messageInformationImport : Message [];

  constructor(private cdr: ChangeDetectorRef, private scenarioCalculService: ScenarioCalculService) {
    this.scenarioCalculService.message$.subscribe(message => {
      this.messageInformationImport = message;
      setTimeout(() => {
        this.messageInformationImport = [];
      }, 3000);
    });
  }

  ngOnInit(): void {
    this.scenarioCalculService.zoneProductionDetails$.subscribe((zoneProductionDetails: ZoneProductionDetails[]) => {
      this.zoneProductionDetails = JSON.parse(JSON.stringify(zoneProductionDetails));
      if (this.scenario.zone_production_details == null) {
        this.scenario.zone_production_details = zoneProductionDetails;
      }
    });

    this.scenarioCalculService.getDepartementsPartielsEtude(this.scenario.etudeDTO.id).subscribe( value => {
      for(let d of value){
        d.id_etude = this.scenario.etudeDTO.id;
        d.id_scenario = this.scenario.id;
        for(let rel of this.scenario.rel_scenario_departement){
          if(rel.id_departement === d.id_departement){
            d.id_rel_scenario_departement = rel.id_rel_scenario_departement
            d.repartition_departement_beton = rel.repartition_departement_beton
            d.repartition_departement_viab = rel.repartition_departement_viab
          }
        }
      }
      this.departementsPartielsTer = value;
    });

    if(this.scenario.ponderation_surface_beton != null){
      this.valueSurfaceBeton = this.scenario.ponderation_surface_beton as number;
      this.valueSurfaceViab = this.scenario.ponderation_surface_viabilite as number;
    }
  }

  calculVentilation() {
    this.scenario.ponderation_surface_beton = this.valueSurfaceBeton;
    this.scenario.ponderation_surface_viabilite = this.valueSurfaceViab;
    this.scenario.rel_scenario_departement = JSON.parse(JSON.stringify(this.departementsPartielsTer));

    this.scenarioCalculService.scenarioCalculVentilation(this.scenario);
  }

  calculProjection() {
    for (let zpd of this.zoneProductionDetails) {
      if (zpd.pourcentage2 != null) {
        zpd.pourcentage2 = parseInt(zpd.pourcentage2.toString());
      }
    }
    this.scenario.zone_production_details = JSON.parse(JSON.stringify(this.zoneProductionDetails));
    this.scenarioCalculService.scenarioCalculProjection(this.scenario);
  }

  isStepDisabled(etape: EtapeScenario) {
    if (etape === this.actualStep) return false;
    return true;
  }

  isStepStatePassedOrActive(etape: EtapeScenario) {
    const s: any = this.scenario.etatEtapes;
    if(EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO === this.actualStep && s[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] === EtatEtape.VALIDE){
      return { 'statePassed': s[etape] === EtatEtape.VALIDE };
    }
    if (etape === this.actualStep) {
      return { 'stateActive': true };
    }
    return { 'statePassed': s[etape] === EtatEtape.VALIDE };
  }

  numberOnlyEvent(event: any,zpd:ZoneProductionDetails): boolean {
    const charCode: number = event.which ? event.which : event.keyCode;
    if ([8, 46, 37, 39].includes(charCode)) {
      return true;
    }
    const inputValue: string = event.key;
    if (inputValue === '') {
      return false;
    }
    const numValue: number = Number(inputValue);
    if (isNaN(numValue)) {
      return false;
    }
    return true;
  }

  verifValideVentilation(): boolean {
    return this.departementsPartielsTer.every(dep => dep.repartition_departement_beton !== undefined && dep.repartition_departement_viab !== null
      && dep.repartition_departement_beton !== undefined && dep.repartition_departement_viab !== null);
  }

  areAllFieldsFilled(): boolean {
    return this.zoneProductionDetails.every(detail => detail.pourcentage2 != null && !isNaN(parseInt(detail.pourcentage2.toString())) && Number(detail.pourcentage2) !== 100);
  }

  onOpen(event: any) {
    switch (event.index) {
      case 0:
        this.callEtapeVentilation(event.index);
        break;
      case 1:
        this.callEtapeProjection(event.index);
        break;
    }
  }

  callEtapeVentilation(indexEtape:Number){
    this.scenarioCalculService.scenarioEtape(indexEtape)
  }

  callEtapeProjection(indexEtape:Number){
    this.scenarioCalculService.scenarioEtape(indexEtape)
  }

  onChangeBeton(event:any, scedep:RelScenarioDepartement){
    scedep.repartition_departement_beton = event.value;
  }

  onChangeViab(event:any, scedep:RelScenarioDepartement){
    scedep.repartition_departement_viab = event.value;
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  ventilationNotChanged(){
    if (this.scenario.ponderation_surface_beton !== this.valueSurfaceBeton || this.scenario.ponderation_surface_viabilite !== this.valueSurfaceViab) {
      return false;
    }
    for(let rel of this.scenario.rel_scenario_departement){
      const deptTer = this.departementsPartielsTer.find(d => d.id_departement === rel.id_departement);
      if (rel.repartition_departement_beton !== deptTer?.repartition_departement_beton || rel.repartition_departement_viab !== deptTer?.repartition_departement_viab) {
        return false;
      }
    }
    return true;
  }

  projectionNotChanged(){
    if (this.scenario.zone_production_details != null) {
      for (let zpd of this.scenario.zone_production_details) {
        const zoneProd = this.zoneProductionDetails.find(z => z.id === zpd.id);
        if (zpd.pourcentage2 !== Number(zoneProd?.pourcentage2)) {
          return false;
        }
      }
    }
    return true;
  }
  isStepStatePassed(etape: EtapeScenario) {
    const s: any = this.scenario.etatEtapes;
    return s[etape] === EtatEtape.VALIDE.valueOf();
  }
}
