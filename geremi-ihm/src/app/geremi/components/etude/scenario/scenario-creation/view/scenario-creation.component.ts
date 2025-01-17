import {ChangeDetectorRef, Component, Input, OnInit, ViewChild} from "@angular/core";
import { Etude } from "../../../model/etude.model";
import { ScenarioGeneraliteService } from "../../scenario-generalite/service/scenario-generalite.service";
import { ScenarioService } from "../../service/scenario.service";
import { Scenario } from "../../model/scenario.model";
import { ScenarioCreationService } from "../service/scenario-creation.service";
import { Etape, EtapeScenario } from "src/app/shared/enums/etape.enums";
import { EtatEtape } from "src/app/shared/enums/etatEtape.enums";
import { Subscription } from "rxjs";
import { ScenarioContrainteService } from "../../scenario-contrainte/service/scenario-contrainte.service";
import { ScenarioChantierService } from "../../scenario-chantier/service/scenario-chantier.service";
import { ScenarioInstallationService } from "../../scenario-installation/service/scenario-installation.service";
import { ScenarioMateriauService } from "../../scenario-materiau/service/scenario-materiau.service";
import { ScenarioCalculService } from "../../scenario-calcul/service/scenario-calcul.service";
import { OverlayService } from "src/app/geremi/components/overlay/service/overlay.service";
import { ZonageService } from "../../../service/zonage.service";
import { ZoneProductionDetails } from "../../scenario-calcul/model/zoneProductionDetails.model";
import { Zonage } from "../../../model/zonage.model";
import { ConfirmationService } from "primeng/api";
import {ScenarioContrainteComponent} from "../../scenario-contrainte/view/scenario-contrainte.component";

@Component({
  selector: 'scenario-creation',
  templateUrl: './scenario-creation.component.html',
  styleUrls: ['./scenario-creation.component.scss']
})
export class ScenarioCreationComponent implements OnInit {
  protected readonly Etape = Etape;
  protected readonly EtapeScenario = EtapeScenario;
  protected readonly EtatEtape = EtatEtape;

  // Scénario
  @Input() scenarioEnCours: any;
  @Input() etude: Etude;
  @Input() anneeRef: any;
  @Input() listeZoneTerritoire: Zonage[];

  @ViewChild('scenarioContrainte') scenarioContrainte: ScenarioContrainteComponent

  activeIndexScenario: any;
  activeIndexStepCalcul: any;
  openAccordeonCalcul: any;

  actualStep: EtapeScenario = EtapeScenario.CREATION_SCENARIO;
  activeStep: EtapeScenario = EtapeScenario.CREATION_SCENARIO;

  zoneDetails: ZoneProductionDetails[];
  openCalculScenario: boolean = false;

  private scenarioGenAjoutSub: Subscription;
  private scenarioConAjoutSub: Subscription;
  private scenarioChanAjoutSub: Subscription;
  private scenarioInstAjoutSub: Subscription;
  private scenarioMatAjoutSub: Subscription;
  private scenarioHypVentAjoutSub: Subscription;
  private scenarioHypVentDisplaySub: Subscription;
  private scenarioHypProjAjoutSub: Subscription;
  private scenarioCreOpenSub: Subscription;
  private scenarioEtiquetteSub: Subscription;
  private scenarioEtapeSub: Subscription;

  constructor(private scenarioGeneraliteService: ScenarioGeneraliteService,
    private scenarioContrainteService: ScenarioContrainteService,
    private scenarioChantierService: ScenarioChantierService,
    private scenarioInstallationService: ScenarioInstallationService,
    private scenarioMateriauService: ScenarioMateriauService,
    private scenarioCalculService: ScenarioCalculService,
    private scenarioService: ScenarioService,
    private scenarioCreationService: ScenarioCreationService,
    private confirmationService: ConfirmationService,
    private overlayService: OverlayService,
    private zonageService: ZonageService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.scenarioGenAjoutSub = this.scenarioGeneraliteService.scenarioAjoutSource$.subscribe(value => {
      if (value.id !== 0) {
        this.modifScenario(value);
      } else {
        this.ajoutScenario(value);
      }
    })

    this.scenarioConAjoutSub = this.scenarioContrainteService.scenarioContrainteSource$.subscribe(value => {
      this.validationEtapeContrainteScenario(value);
    })

    this.scenarioChanAjoutSub = this.scenarioChantierService.scenarioChantierSource$.subscribe(value => {
      this.validationEtapeChantierScenario(value);
    })

    this.scenarioInstAjoutSub = this.scenarioInstallationService.scenarioInstallationSource$.subscribe(value => {
      this.validationEtapeInstallationScenario(value);
    })

    this.scenarioMatAjoutSub = this.scenarioMateriauService.scenarioMateriauSource$.subscribe(value => {
      this.validationEtapeMateriauScenario(value);
    })

    this.scenarioHypVentAjoutSub = this.scenarioCalculService.scenarioCalculVentilationSource$.subscribe(value => {
      this.validationEtapeHypotheseVentilationScenario(value);
    })

    this.scenarioHypVentDisplaySub = this.scenarioCalculService.displayScenarioCalculVentilationAnneeSource$.subscribe(value => {
      this.displayHypotheseVentilation(value);
    })

    this.scenarioHypVentDisplaySub = this.scenarioCalculService.displayScenarioCalculProjectionAnneeSource$.subscribe(value => {
      this.displayHypotheseProjection(value);
    })

    this.scenarioHypProjAjoutSub = this.scenarioCalculService.scenarioCalculProjectionSource$.subscribe(value => {
      this.validationEtapeHypotheseProjectionScenario(value);
    })

    this.scenarioCreOpenSub = this.scenarioCreationService.scenarioCreationOpenSource$.subscribe(value => {
      this.toggleCreerScenario(value);
    })

    this.scenarioService.scenarioClearSource$.subscribe(() => {
      this.closeScenario();
    })

    this.scenarioEtiquetteSub = this.scenarioMateriauService.scenarioMateriauDisplayEtiquetteMoveEndSource$.subscribe(() => {
      if(this.activeStep === EtapeScenario.MATERIAUX_SCENARIO){
        this.scenarioService.scenarioMateriau();
      }
    });

    this.scenarioEtapeSub = this.scenarioCalculService.scenarioCalculEtapeSource$.subscribe(value => {
      this.activeStep = value === 0 ? EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO : EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO;
      this.setEtapeCalculs();
    })

    this.toggleCreerScenario(this.scenarioEnCours);
  }

  toggleCreerScenario(value: Scenario) {
    this.scenarioEnCours = value;
    if(this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] !== EtatEtape.VALIDE){
      this.computeValideStep();
    }
    else {
      this.actualStep = EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO;
      this.activeStep = EtapeScenario.CREATION_SCENARIO;
      this.cdr.detectChanges()
    }
  }

  toggleCloseScenarioEnCours() {
    this.scenarioEnCours = undefined;
    this.scenarioCreationService.scenarioCreationClose();
    this.setLayerTerritoire();
  }

  ajoutScenario(scenario: Scenario) {
    this.scenarioService.addScenario(scenario).subscribe({
      next: (value: Scenario) => {
        scenario.id = value.id;
        this.scenarioEnCours = scenario;
        this.scenarioEnCours.etatEtapes[EtapeScenario.CREATION_SCENARIO] = EtatEtape.VALIDE;
        this.computeValideStep();
      },
      error: (err: Error) => {
        console.log("Erreur lors de l'ajout d'un scénario")
      },
    });
  }

  modifScenario(scenario: Scenario) {
    if(this.scenarioEnCours.dynamique_demographique !== scenario.dynamique_demographique
      && this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] === EtatEtape.VALIDE){
      this.confirmationService.confirm({
          message: `En modifiant les données, les résultats des calculs seront supprimés`,
          header: 'Voulez-vous continuer ?',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.callServiceGeneralite(scenario,true);
          },
          key: 'confirmRetenu'
      });
    } else {
      this.callServiceGeneralite(scenario,false);
    }
  }

  toStep(index: EtapeScenario) {
    switch (index) {
      case EtapeScenario.CREATION_SCENARIO:
        this.setEtapeGeneralite();
        break;
      case EtapeScenario.CONTRAINTES_SCENARIO:
        this.setEtapeContrainte();
        break;
      case EtapeScenario.CHANTIERS_SCENARIO:
        this.setEtapeChantier();
        break;
      case EtapeScenario.INSTALLATIONS_SCENARIO:
        this.setEtapeInstallations();
        break;
      case EtapeScenario.MATERIAUX_SCENARIO:
        this.setEtapeMateriaux();
        break;
      case EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO:
        this.setEtapeCalculs();
        break;
      case EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO:
        this.setEtapeCalculs();
        break;
    }
  }

  onOpen(event: any) {
    const etudeEtapes: any = this.etude.etatEtapes;
    let etapesScenarios = this.enumKeys(EtapeScenario);

    if (etudeEtapes[Etape.CHANTIERS] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.CHANTIERS_SCENARIO;
      })
    }

    if (etudeEtapes[Etape.INSTALLATIONS] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.INSTALLATIONS_SCENARIO;
      })
    }
    if (etudeEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.MATERIAUX_SCENARIO;
      })
    }

    switch (etapesScenarios[event.index]) {
      case EtapeScenario.CREATION_SCENARIO:
        this.setEtapeGeneralite();
        break;
      case EtapeScenario.CONTRAINTES_SCENARIO:
        this.setEtapeContrainte();
        break;
      case EtapeScenario.CHANTIERS_SCENARIO:
        this.setEtapeChantier();
        break;
      case EtapeScenario.INSTALLATIONS_SCENARIO:
        this.setEtapeInstallations();
        break;
      case EtapeScenario.MATERIAUX_SCENARIO:
        this.setEtapeMateriaux();
        break;
      case EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO:
        this.setEtapeCalculs();
        break;
      case EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO:
        this.setEtapeCalculs();
        break;
    }
  }

  setEtapeGeneralite() {
    this.activeStep = EtapeScenario.CREATION_SCENARIO;
    this.cdr.detectChanges()
    this.closeEtapeCalculs();
    this.setLayerTerritoire();
  }

  setEtapeContrainte() {
    this.activeStep = EtapeScenario.CONTRAINTES_SCENARIO;
    this.cdr.detectChanges()
    this.closeEtapeCalculs();
    this.setLayerTerritoire();
    this.setLayerScenarioContrainte();
  }

  setEtapeChantier() {
    this.activeStep = EtapeScenario.CHANTIERS_SCENARIO;
    this.cdr.detectChanges()
    this.closeEtapeCalculs();
    this.setLayerTerritoire();
    this.setLayerScenarioChantier();
  }

  setEtapeInstallations() {
    this.activeStep = EtapeScenario.INSTALLATIONS_SCENARIO;
    this.cdr.detectChanges()
    this.closeEtapeCalculs();
    this.setLayerTerritoire();
    this.setLayerScenarioInstallationStockage();
  }

  setEtapeMateriaux() {
    this.activeStep = EtapeScenario.MATERIAUX_SCENARIO;
    this.cdr.detectChanges()
    this.closeEtapeCalculs();
    this.setLayerTerritoire();
    this.setLayerScenarioMateriaux();
  }

  setEtapeCalculs() {
    this.openEtapeCalculs();
    this.cdr.detectChanges()
  }

  closeEtapeCalculs() {
    this.openCalculScenario = false;
    this.openAccordeonCalcul = -1;
  }

  openEtapeCalculs() {
    this.openCalculScenario = true;
    this.activeIndexScenario = -1;
    this.openAccordeonCalcul = 0;

    if (this.activeStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO || this.actualStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO) {
      this.activeIndexStepCalcul = 0;
      this.setLayerTerritoire();
    }
    else if (this.activeStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO || this.actualStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO) {
      this.activeIndexStepCalcul = 1;
      this.setLayerTerritoire();
      this.getZoneNames();
    }

    if(this.activeStep !== EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO && this.activeStep !== EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO){
      this.activeStep = this.actualStep;
    }

    // Petite délai pour afficher les étiquettes
    setTimeout(() => {
      if(this.activeStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO && this.scenarioEnCours.resultat_calcul != null){
        this.scenarioCalculService.displayScenarioCalculVentilation(this.scenarioEnCours);
      }
    }, 500);
    // Petite délai pour afficher les étiquettes
    setTimeout(() => {
      if(this.activeStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO && this.scenarioEnCours.resultat_calcul != null){
        this.scenarioCalculService.displayScenarioCalculProjection(this.scenarioEnCours);
      }
    }, 500);
  }

   validationEtapeContrainteScenario(scenario:Scenario){
    if(scenario.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] === EtatEtape.VALIDE){
      this.confirmationService.confirm({
          message: `En modifiant les données, les résultats des calculs seront supprimés`,
          header: 'Voulez-vous continuer ?',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.callServiceContrainte(scenario);
          },
          key: 'confirmRetenu'
      });
    } else {
      this.callServiceContrainte(scenario);
    }
   }

   callServiceGeneralite(scenario: Scenario, modifRes:boolean){
    this.scenarioEnCours.nom = scenario.nom;
    this.scenarioEnCours.description = scenario.description;
    this.scenarioEnCours.dynamique_demographique = scenario.dynamique_demographique;

    this.scenarioService.updateScenario(this.scenarioEnCours).subscribe({
      next: (value: Scenario) => {
        this.scenarioEnCours.etatEtapes[EtapeScenario.CREATION_SCENARIO] = EtatEtape.VALIDE;
        if(modifRes){
          this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
          this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        }
        this.scenarioEnCours.resultat_calcul = {};

        this.computeValideStep();
      },
      error: (err: Error) => {
        console.log("Erreur lors de la modification d'un scénario")
      },
    });
   }

   callServiceContrainte(scenario: Scenario){
    this.scenarioService.ajoutContrainteScenario(scenario).subscribe({
      next:() => {
       this.scenarioEnCours = scenario;
       this.scenarioEnCours.etatEtapes[EtapeScenario.CONTRAINTES_SCENARIO] = EtatEtape.VALIDE;
       this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
       this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
       this.scenarioEnCours.resultat_calcul = {};

       this.computeValideStep();
       console.log("Contrainte(s) ajouter au Scénario");
      },
      error:() => {
       console.log("Erreur lors de l'ajout des contraintes au scénario")
      },
    });
   }

  validationEtapeChantierScenario(scenario: Scenario) {
    if(scenario.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] === EtatEtape.VALIDE){
      this.confirmationService.confirm({
          message: `En modifiant les données, les résultats des calculs seront supprimés`,
          header: 'Voulez-vous continuer ?',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.callServiceChantier(scenario);
          },
          key: 'confirmRetenu'
      });
    } else {
      this.callServiceChantier(scenario);
    }
  }

  callServiceChantier(scenario:Scenario){
    this.scenarioService.ajoutChantierScenario(scenario).subscribe({
      next: () => {
        this.scenarioEnCours = scenario;
        this.scenarioEnCours.etatEtapes[EtapeScenario.CHANTIERS_SCENARIO] = EtatEtape.VALIDE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.resultat_calcul = {};

        this.computeValideStep();
        console.log("Chantier(s) ajouter au Scénario");
      },
      error: () => {
        console.log("Erreur lors de l'ajout du chantier au scénario")
      },
    });
  }

  validationEtapeInstallationScenario(scenario: Scenario) {
    if(scenario.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] === EtatEtape.VALIDE){
      this.confirmationService.confirm({
          message: `En modifiant les données, les résultats des calculs seront supprimés`,
          header: 'Voulez-vous continuer ?',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.callServiceInstallation(scenario);
          },
          key: 'confirmRetenu'
      });
    } else {
      this.callServiceInstallation(scenario);
    }
  }

  callServiceInstallation(scenario:Scenario){
    this.scenarioService.ajoutInstallationScenario(scenario).subscribe({
      next: () => {
        this.scenarioEnCours = scenario;
        this.scenarioEnCours.etatEtapes[EtapeScenario.INSTALLATIONS_SCENARIO] = EtatEtape.VALIDE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.resultat_calcul = {};

        this.computeValideStep();
        console.log("Installation(s) ajouter au Scénario");
      },
      error: () => {
        console.log("Erreur lors de l'ajout de l'Installation au scénario")
      },
    });
  }

  validationEtapeMateriauScenario(scenario: Scenario) {
    if(scenario.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] === EtatEtape.VALIDE){
      this.confirmationService.confirm({
          message: `En modifiant les données, les résultats des calculs seront supprimés`,
          header: 'Voulez-vous continuer ?',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.callServiceMateriau(scenario);
          },
          key: 'confirmRetenu'
      });
    } else {
      this.callServiceMateriau(scenario);
    }
  }

  callServiceMateriau(scenario:Scenario){
    this.scenarioService.ajoutMateriauScenario(scenario).subscribe({
      next: () => {
        this.scenarioEnCours = scenario;
        this.scenarioEnCours.etatEtapes[EtapeScenario.MATERIAUX_SCENARIO] = EtatEtape.VALIDE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.scenarioEnCours.resultat_calcul = {};

        this.computeValideStep();
        console.log("Materiau(x) ajouter au Scénario");
      },
      error: () => {
        console.log("Erreur lors de l'ajout du matériau au scénario")
      },
    });
  }

  validationEtapeHypotheseVentilationScenario(scenario: Scenario) {
    this.overlayService.overlayOpen("Calcul de la ventilation en cours...");
    this.scenarioCalculService.calculProductionZone(this.scenarioEnCours, parseInt(this.etude.anneeRef)).subscribe({
      next: (value: Scenario) => {
        this.scenarioEnCours.resultat_calcul = value.resultat_calcul;
        this.scenarioCalculService.displayScenarioCalculVentilation(this.scenarioEnCours);
        this.getZoneNames();
        this.overlayService.overlayClose();
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO] = EtatEtape.VALIDE;
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.NON_RENSEIGNE;
        this.actualStep = EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO;
      },
      error: (err) => {
        this.overlayService.overlayClose();
      },
    })
  }

  validationEtapeHypotheseProjectionScenario(scenario: Scenario) {
    this.overlayService.overlayOpen("Calcul de la projection en cours...");
    this.scenarioCalculService.calculProjectionZone(scenario).subscribe({
      next: (value: Scenario) => {
        this.overlayService.overlayClose();
        this.scenarioEnCours = value;
        this.scenarioCalculService.displayScenarioCalculProjection(value);
        console.log("Resultat Calcul Projection :")
        this.scenarioEnCours.etatEtapes[EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO] = EtatEtape.VALIDE;
        this.actualStep = EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO;
        this.scenarioCalculService.setMessage([{
          severity: 'success',
          detail: 'Les calculs du scénario sont terminés. Les résultats sont affichés sur la carte.'
        }]);

       },
      error: (err) => {
        // Gérer les erreurs ici, comme la fermeture de l'overlay et l'affichage d'un message d'erreur.
        this.overlayService.overlayClose();
        console.error(err);
      },
    })
  }


  displayHypotheseVentilation(anneeRef: Number) {
    if (this.activeStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO && this.scenarioEnCours?.resultat_calcul != null)
      this.scenarioCalculService.displayScenarioCalculVentilation(this.scenarioEnCours);
  }

  displayHypotheseProjection(anneeRef: Number) {
    if (this.activeStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO && this.scenarioEnCours?.resultat_calcul != null)
      this.scenarioCalculService.displayScenarioCalculProjection(this.scenarioEnCours);
  }

  isScenarioEnCours() {
    return this.scenarioEnCours !== undefined;
  }

  closeScenario() {
    this.scenarioEnCours = undefined;
    this.scenarioService.scenarioClose();
  }

  isEtapeEtudeVide(etape: Etape) {
    return this.etude.etatEtapes[etape] === EtatEtape.VALIDE_VIDE;
  }

  computeValideStep() {
    const etudeEtapes: any = this.etude.etatEtapes;
    const scenarioEtapes: any = this.scenarioEnCours.etatEtapes;
    let etapesScenarios = this.enumKeys(EtapeScenario);


    if (etudeEtapes[Etape.CHANTIERS] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.CHANTIERS_SCENARIO;
      })
    }

    if (etudeEtapes[Etape.INSTALLATIONS] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.INSTALLATIONS_SCENARIO;
      })
    }
    if (etudeEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE) {
      etapesScenarios = etapesScenarios.filter(e => {
        return e !== EtapeScenario.MATERIAUX_SCENARIO;
      })
    }

    let j = 0;
    for (const value of etapesScenarios) {
      if (scenarioEtapes[EtapeScenario[value]] !== EtatEtape.VALIDE) {
        this.actualStep = EtapeScenario[value];
        this.activeStep = EtapeScenario[value];
        this.activeIndexScenario = j;
        if (this.actualStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO) {
          this.activeIndexStepCalcul = 0;
        }
        if (this.actualStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO) {
          this.activeIndexStepCalcul = 1;
        }
        this.toStep(this.activeStep);
        break;
      }
      j++;
    }
    this.cdr.detectChanges()
  }

  nomScenario(scenario:Scenario){
    if(scenario.if_retenu){
      return scenario.nom + ' - retenu';
    }
    return scenario.nom;
  }

  enumKeys<O extends object, K extends keyof O = keyof O>(obj: O): K[] {
    return Object.keys(obj).filter(k => Number.isNaN(+k)) as K[];
  }

  isStepStatePassedOrActive(etape: EtapeScenario) {
    const s: any = this.scenarioEnCours.etatEtapes;
    if (etape.valueOf() === this.actualStep.valueOf()) {
      return { 'stateActive': true };
    }
    return { 'statePassed': s[etape] === EtatEtape.VALIDE.valueOf() || s[etape] === EtatEtape.VALIDE_VIDE.valueOf() };
  }

  isStepStateActive(etape: EtapeScenario) {
    return etape.valueOf() === this.actualStep.valueOf();
  }

  isStepStatePassedOrActiveCalcul() {
    const s: any = this.scenarioEnCours.etatEtapes;
    if (this.actualStep === EtapeScenario.HYPOTHESE_PROJECTION_SCENARIO || this.actualStep === EtapeScenario.HYPOTHESE_VENTILATION_SCENARIO) {
      return { 'stateActive': true };
    }
    return { 'statePassed': s[this.actualStep] === EtatEtape.VALIDE.valueOf() };
  }

  isStepDisabled(etape: EtapeScenario) {
    if (etape.valueOf() === this.actualStep.valueOf()) return false;
    for (const value in EtapeScenario) {
      if (value === EtapeScenario[this.actualStep]) return true;
      if (value === EtapeScenario[etape]) return false;
    }
    return true;
  }

  setLayerTerritoire() {
    this.scenarioService.setScenarioLayerTerritoire();
  }

  setLayerScenarioContrainte() {
    this.scenarioService.scenarioContrainte();
  }

  setLayerScenarioChantier() {
    this.scenarioService.scenarioChantier();
  }

  setLayerScenarioInstallationStockage() {
    this.scenarioService.scenarioInstallationStockage();
  }

  setLayerScenarioMateriaux() {
    this.scenarioService.scenarioMateriau();
  }

  getZoneNames() {
    this.zoneDetails = [];

    if(this.scenarioEnCours.resultat_calcul != null){
      this.zonageService.getZonageByEtudeId(this.etude.id).subscribe({
        next: (zoneNames: Zonage[]) => {
          this.zoneDetails = zoneNames.map((zonage) => {
            let productionZone = this.scenarioEnCours.resultat_calcul[this.etude.anneeRef].resultatZones[zonage.id_zone as number];
            return {
              id: productionZone.idZone,
              name: zonage.nom.toString(),
              tons: productionZone.productionZoneSecondaireTotal,
              pourcentage1: productionZone.pourcentageProductionZoneSecondaire,
              pourcentage2: this.scenarioEnCours.zone_production_details?.find((z:ZoneProductionDetails) => z.id === productionZone.idZone)?.pourcentage2
            };
          });

          this.scenarioCalculService.getZoneProductionDetails(this.zoneDetails);
        },
        error: (err) => {
          console.error('Erreur lors de la récupération des noms de zones : ', err);
        },
      });
    }
  }

  isLectureSeule(){
    return this.etude.readOnly || this.etude.ifPublic;
  }

  ngOnDestroy(): void {
    if (this.scenarioCreOpenSub)
      this.scenarioCreOpenSub.unsubscribe();
    if (this.scenarioGenAjoutSub)
      this.scenarioGenAjoutSub.unsubscribe();
    if (this.scenarioChanAjoutSub)
      this.scenarioChanAjoutSub.unsubscribe();
    if (this.scenarioConAjoutSub)
      this.scenarioConAjoutSub.unsubscribe();
    if (this.scenarioInstAjoutSub)
      this.scenarioInstAjoutSub.unsubscribe();
    if (this.scenarioMatAjoutSub)
      this.scenarioMatAjoutSub.unsubscribe();
    if (this.scenarioHypVentAjoutSub)
      this.scenarioHypVentAjoutSub.unsubscribe();
    if (this.scenarioHypVentDisplaySub)
      this.scenarioHypVentDisplaySub.unsubscribe();
    if (this.scenarioHypProjAjoutSub)
      this.scenarioHypProjAjoutSub.unsubscribe();
    if (this.scenarioEtiquetteSub)
      this.scenarioEtiquetteSub.unsubscribe();
    if (this.scenarioEtapeSub)
      this.scenarioEtapeSub.unsubscribe();
  }
}
