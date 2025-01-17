import { Component, Input, OnInit } from '@angular/core';
import { Scenario } from '../../model/scenario.model';
import { ScenarioGeneraliteService } from '../service/scenario-generalite.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'scenario-generalite',
  templateUrl: './scenario-generalite.component.html',
  styleUrls: ['./scenario-generalite.component.scss']
})
export class ScenarioGeneraliteComponent implements OnInit {
  @Input() scenario:Scenario;
  @Input() lectureSeule:boolean;

  listeDynamique:Array<String> = []

  formControlGeneralite:FormGroup;

  constructor(private scenarioGeneraliteService:ScenarioGeneraliteService,
    private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.listeDynamique = ['Basse','Centrale','Haute'];

    let nom = this.scenario.nom;
    let description = this.scenario.description;
    let dynamique = this.scenario.dynamique_demographique;

    if(dynamique === undefined){
      dynamique = 'Basse'
    }

    this.formControlGeneralite = this.formBuilder.group({
      nom: [{value:nom,disabled: this.isDisabled()}, [Validators.required, Validators.minLength(1)]],
      description: [{value:description,disabled: this.isDisabled()}],
      dynamique: [{value:dynamique,disabled: this.isDisabled()}]
    }, { validators: [this.validatorForm] });
  }

  ajoutScenario(){
    let scenario = new Scenario(this.scenario.id,this.scenario.etudeDTO);
    scenario.nom = this.formControlGeneralite.value.nom;
    scenario.description = this.formControlGeneralite.value.description;
    scenario.dynamique_demographique = this.formControlGeneralite.value.dynamique;

    this.scenarioGeneraliteService.ajoutScenario(scenario);
  }

  validatorForm(form: FormGroup) {
    if (form.value.nom != '' && form.value.nom != null) {
      return null;
    }
    return { validUrl: true };
  }

  isDisabled(){
    return this.scenario.if_retenu as boolean || this.lectureSeule;
  }

  notChanged() {
    return (this.formControlGeneralite.value.nom === this.scenario.nom
      && (this.formControlGeneralite.value.description === this.scenario.description ||
        ((this.formControlGeneralite.value.description == null || this.formControlGeneralite.value.description === '') && this.scenario.description == null))
      && this.formControlGeneralite.value.dynamique == this.scenario.dynamique_demographique)
  }
}
