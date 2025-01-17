import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Users} from "src/app/shared/entities/users.model";
import {UsersService} from "src/app/shared/service/users.service";
import {EtudeService} from "../../etude/service/etude.service";
import {EtudesUtilisateur} from "../../etude/model/etudesUtilisateur.model";
import {Etude} from "../../etude/model/etude.model";
import {Roles} from "src/app/shared/enums/roles.enums";
import {LayersService} from "../../carto/service/layers.service";
import {ScenarioService} from "../../etude/scenario/service/scenario.service";
import {Scenario} from "../../etude/scenario/model/scenario.model";
import {Zonage} from "../../etude/model/zonage.model";
import {ScenarioCalculService} from "../../etude/scenario/scenario-calcul/service/scenario-calcul.service";
import {ContrainteEnvironnementale} from "../../etude/contraintes-environnementales/model/contrainteEnv.model";
import {ScenarioContrainteService} from "../../etude/scenario/scenario-contrainte/service/scenario-contrainte.service";
import {EtablissementService} from "../../carto/service/etablissement.service";
import {OverlayService} from "../../overlay/service/overlay.service";

@Component({
    selector: 'app-suivi-etude',
    templateUrl: './suivi-etude.component.html',
    styleUrls: ['./suivi-etude.component.scss'],
    providers: []
})
export class SuiviEtudeComponent implements OnInit {
  @Output() openSideBarEvent = new EventEmitter<boolean>();
  @Output() displayEtablissementEtudeEvent = new EventEmitter<number>();
  @Output() displayZonageEvent = new EventEmitter<Zonage>();
  @Output() selectedYearEtudeEvent = new EventEmitter<number>();

  @Input() selectedYear:number;

  visibleAfficherSuiviEtudeSideBar: any = false;

  usersActif!: Users;

  etudes: EtudesUtilisateur;
  etude = new Etude();
  currentListEtude: Etude[];
  scenario: Scenario;

  anneeMin: number;
  anneeMax: number;

  constructor(
    private etudeService: EtudeService,
    private etablissementService: EtablissementService,
    private usersService: UsersService,
    private layersService: LayersService,
    private scenarioCalculService: ScenarioCalculService,
    private scenarioContrainteService: ScenarioContrainteService,
    private overlayService: OverlayService,
    private scenarioService: ScenarioService
  ) {}

  ngOnInit(): void {
      this.usersActif = this.usersService.currentUsers;
      this.getSuiviEtudes();

      this.scenarioCalculService.displayScenarioCalculProjectionSuiviEtudeActualiserSource$.subscribe(() => {
        if(this.etude.id !== 0){
          this.displayHypotheseProjection();
        }
      });

      this.scenarioContrainteService.scenarioContrainteActualiserSuiviEtudeTauxSource$.subscribe(() => {
        if(this.etude.id !== 0){
          this.displayTxRenouvellement();
        }
      });

      this.etablissementService.getDistinctAnnees().subscribe(annees => {
        this.anneeMin = Math.min(...annees.map(Number));
        this.anneeMax = Math.max(...annees.map(Number));
      });
  }

  getSuiviEtudes(): void {
    if (this.usersActif.libelle_profil !== Roles.Public) {
      this.etudeService.getAllEtudesSuivies().subscribe(
        (etudes: EtudesUtilisateur) => {
          this.etudes = etudes;
          this.etudes.etudesMandataireLectureSeule.forEach(e => e.readOnly = true);
        }
      );
    }
  }

  showEtudeLayers(etude:Etude){
    console.log("Show Etude Layer")

    this.overlayService.overlayOpen("Calcul en cours ...");
    this.etudeService.getEtudeById(etude.id).subscribe({
      next:(value:Etude) => {
        this.etude = value;
        this.majDateHandler(parseInt(value.anneeRef));
        this.scenarioService.getScenarioValideByIdEtude(etude.id).subscribe({
          next:(value:Scenario) => {
            console.log(value);

            this.scenario = value;
            this.setTerritoireEtude();
            setTimeout(() => {
             this.displayHypotheseProjection();
            }, 500);
          },
          error:() => this.overlayService.overlayClose(),
          complete:() => this.overlayService.overlayClose()
        })
      },
      error:() => this.overlayService.overlayClose()
    })
  }

  uploadEtude(etude:Etude){
    console.log("Upload Etude")

    this.scenarioService.exportScenarioValideByIdEtude(etude.id).subscribe(data => {
      const blob = new Blob([data], { type: 'text/csv;charset=utf-8' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `SuiviGlobal_${etude.nom}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  openEtudeSuiviSideBar() {
    this.visibleAfficherSuiviEtudeSideBar = true;
    this.openSideBarEvent.emit(true);
    this.getSuiviEtudes();
    this.etude = new Etude();
  }

  closeEtudeSuiviSideBar() {
    this.openSideBarEvent.emit(false);
    this.layersService.reset(true);
    this.etude = new Etude();
  }

  isRetenu(etude: Etude){
    return this.etude.id === etude.id;
  }

  // AFFICHAGE LAYERS
  setTerritoireEtude(){
    this.layersService.reset(true);
    this.layersService.displayEtudeTerritoireLayer(this.etude.territoire);
    this.layersService.onFlyToLayerEvent('EtudeTerritoire');
    this.displayEtablissementEtudeEvent.emit(this.etude.id);
    this.displayZonageEvent.emit(this.getZonageEtude());
  }

  getZonageEtude(){
    const zonageToSend: Zonage = new Zonage();
    zonageToSend.type = 'autre';
    zonageToSend.nom = this.etude.territoire.nom;
    zonageToSend.description = this.etude.territoire.description;
    zonageToSend.id_etude = this.etude.id;
    return zonageToSend;
  }

  // AFFICHAGE ETIQUETTE
  displayHypotheseProjection() {
    if (this.scenario?.resultat_calcul != null)
      this.scenarioCalculService.displayScenarioCalculProjectionSuiviEtude(this.scenario);
  }

  // AFFICHAGE TAUX RENOUVELLEMENT
  displayTxRenouvellement(){
    let horsContrainte = new ContrainteEnvironnementale();
    horsContrainte.id = 0;
    horsContrainte.tx_renouvellement_contrainte = this.scenario.tx_renouvellement_hc;

    let tmpliste: ContrainteEnvironnementale[] = [];
    tmpliste.push(horsContrainte);
    for(let c of this.scenario.scenario_contraintes){
      let cont = new ContrainteEnvironnementale;
      cont.id = c.id_contrainte as number;
      cont.tx_renouvellement_contrainte = c.tx_renouvellement_contrainte;
      tmpliste.push(cont);
    }

    setTimeout(() => {
      this.scenarioContrainteService.onChangeTauxRenouvellement(tmpliste);
    }, 700);
  }

  // HANDLE YEAR EVENT
  handleYearChanged(year: number): void {
    if (year > this.anneeMax) {year = this.anneeMax;}
    if (year < parseInt(this.etude.anneeRef)) {year = parseInt(this.etude.anneeRef);}
    this.selectedYear = year;
    this.selectedYearEtudeEvent.emit(year);
  }

  majDateHandler(year:number){
    if(parseInt(this.etude.anneeRef) > this.selectedYear || parseInt(this.etude.anneeFin) < this.selectedYear) {
      this.handleYearChanged(year);
    }
  }

  get anneeFinEtuValue(): number {
    const value = parseInt(this.etude.anneeFin);
    const anneeFin = value ? value : 2099;
    if(this.anneeMax){
      return Math.min(...[anneeFin,this.anneeMax]);
    }
    return anneeFin;
  }

  get anneeDeRefValue(): number {
    const value = parseInt(this.etude.anneeRef);
    return value ? value : 2099;
  }
}
