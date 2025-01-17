import { Component, Input, OnInit } from '@angular/core';
import { Scenario } from '../model/scenario.model';
import { Etude } from '../../model/etude.model';
import { ScenarioService } from '../service/scenario.service';
import { ScenarioCreationService } from '../scenario-creation/service/scenario-creation.service';
import { Subscription } from 'rxjs';
import { ConfirmationService } from 'primeng/api';
import { EtapeScenario } from 'src/app/shared/enums/etape.enums';
import { EtatEtape } from 'src/app/shared/enums/etatEtape.enums';
import { ScenarioContrainteService } from '../scenario-contrainte/service/scenario-contrainte.service';
import {ZonageService} from "../../service/zonage.service";
import {Zonage} from "../../model/zonage.model";

@Component({
  selector: 'scenario',
  templateUrl: './scenario.component.html',
  styleUrls: ['./scenario.component.scss']
})
export class ScenarioComponent implements OnInit {
    // Scénario
    scenarios: Scenario[] = [];
    scenarioEnCours: any;

    @Input() etude: Etude;
    @Input() anneeRef: any;

    activeScenario:any;

    infoEtude:boolean = false;
    infoScenario:boolean = false;

    listeZoneTerritoire: Zonage[];

    // Subscription :
    scenarioCreationSub: Subscription;
    scenarioCreationOpenSub: Subscription;
    scenarioCreationCloseSub: Subscription;

    constructor(private confirmationService : ConfirmationService,
        private scenarioService:ScenarioService,
        private scenarioCreationService:ScenarioCreationService,
        private scenarioContrainteService: ScenarioContrainteService,
        private zonageService: ZonageService) { }

    ngOnInit(): void {
        this.scenarioCreationCloseSub = this.scenarioCreationService.scenarioCreationCloseSource$.subscribe(value => {
            this.toggleCloseScenarioEnCours();
        })

        this.scenarioCreationOpenSub = this.scenarioService.scenarioOpenSource$.subscribe(value => {
            this.infoScenario = true;
        })

        this.scenarioCreationSub = this.scenarioService.scenarioCloseAccordeonSource$.subscribe(value => {
            this.activeScenario = -1;
            this.infoScenario = false;
        })

        this.zonageService.getZonageByEtudeId(this.etude.id).subscribe( value => {
          this.listeZoneTerritoire = value;
        });
        this.initListScenario();
        this.activeScenario = 0;
    }

    toggleCreerScenario(){
        let scenario = new Scenario(0, this.etude);
        this.scenarioEnCours = scenario;

        this.infoScenario = true;

        this.scenarioCreationService.scenarioCreationOpen(scenario);

        this.scenarioService.scenarioOpen();
    }

    toggleCloseScenarioEnCours(){
        this.scenarioEnCours = undefined;
        this.scenarios = [];
        this.scenarioContrainteService.clearTauxRenouvellement();
        this.scenarioService.scenarioClose();

        this.infoScenario = false;

        this.initListScenario();
    }

    supprimerScenario(scenario:Scenario){
        this.scenarioService.deleteScenario(scenario.id).subscribe({
            next:(value) => {
                this.scenarios = this.scenarios.filter(function (element) {
                    return element.id != scenario.id;
                });
                console.log("Scénario supprimer");
            },
            error:(err:Error) => {
                console.log("Erreur lors de la suppresion du Scénario")
            },
        });

    }

    modifScenario(scenario:Scenario){
        this.scenarioEnCours = scenario;
        this.infoScenario = true;
        this.scenarioService.scenarioOpen();
        this.scenarioCreationService.scenarioCreationOpen(scenario);
    }

    isScenarioEnCours(){
        return this.scenarioEnCours !== undefined;
    }

    onOpenAccordion(event:any){
        this.scenarioService.scenarioOpenAccordeon();
        if(this.scenarioEnCours){
            this.infoScenario = true;
        }
    }

    onCloseAccordion(event:any){
        this.infoScenario = false;
    }

    initListScenario(){
        this.scenarioService.getListScenarioEtude(this.etude.id).subscribe({
            next:(value:Scenario[]) => {
                if(value === null){
                    this.scenarios = [];
                } else {
                    this.scenarios = value;
                }
                console.log("Récupération des Scénarios réussi");
            },
            error:(err:Error) => {
                console.log("Erreur lors de la récupération des Scénarios");
            }
        });
    }

    scenarioRetenu(scenario:Scenario){
        let retenu = scenario.if_retenu;
        scenario.if_retenu = !retenu;
        this.scenarioService.setScenarioRetenu(scenario).subscribe({
            next:(value:Scenario) => {
                for(let sc of this.scenarios){
                    if(sc.if_retenu){
                        sc.if_retenu = false;
                    }
                }
                scenario.if_retenu = value.if_retenu;
                console.log("Scénario retenu")
            },
            error:(err:Error) => {
                scenario.if_retenu = retenu;
                console.log("Echec lors de la validation du Scénario")
            }
        })
    }

    confirmationRetenu(scenario:Scenario): void {
        if(scenario.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] === EtatEtape.VALIDE){
            let message = scenario.if_retenu ? `Ne plus retenir ce Scénario ?`
                : `Voulez vous retenir ce scénario ?<br>
                Un seul scénario peut être retenu.`;

            this.confirmationService.confirm({
                message: message,
                header: 'Voulez-vous continuer ?',
                icon: 'pi pi-exclamation-triangle',
                accept: () => {
                  this.scenarioRetenu(scenario)
                },
                key: 'confirmRetenu'
            });
        }
    }

    readOnly(scenario:Scenario){
        return scenario.if_retenu || this.etude.readOnly || this.etude.ifPublic || this.etude.ifImporte;
    }

    retenuPossible(scenario:Scenario){
        return !this.etude.readOnly && !this.etude.ifPublic && !this.etude.ifImporte && scenario.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] === EtatEtape.VALIDE;
    }

    toolTipRetenu(scenario:Scenario){
        if(this.etude.readOnly || this.etude.ifPublic || this.etude.ifImporte)
            return 'Modification impossible en lecture seule';
        if(!this.retenuPossible(scenario)){
            return 'Validation impossible sans résultat de projection';
        }
        return scenario.if_retenu ? 'Scénario retenu' : 'Retenir le Scénario';
    }

    ngOnDestroy(): void {
        if(this.scenarioCreationCloseSub)
            this.scenarioCreationCloseSub.unsubscribe();
        if(this.scenarioCreationOpenSub)
            this.scenarioCreationOpenSub.unsubscribe();
        if(this.scenarioCreationSub)
            this.scenarioCreationSub.unsubscribe();
    }
}
