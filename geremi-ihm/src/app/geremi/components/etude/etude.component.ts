import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import {mergeMap, Subscription, switchMap} from 'rxjs';
import { Users } from 'src/app/shared/entities/users.model';
import { UsersService } from 'src/app/shared/service/users.service';
import { Etude } from './model/etude.model';
import { RelEtudeUserProcuration } from './model/reletudeuserprocuration.model';
import { EtudeService } from './service/etude.service';
import { RelEtudeUserProcurationService } from './service/reletudeuserprocuration.service';
import { UserService } from './service/user.service';
import { EtablissementService } from '../carto/service/etablissement.service';
import { AssociationZonageService } from './service/associationzonage.service';
import { Feature } from 'geojson';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';
import { Territoire } from './model/territoire.model';
import { TerritoireService } from './service/territoire.service';
import { Zonage } from './model/zonage.model';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ImportZonageService } from './service/importzonage.service';
import { ConfirmEventType, ConfirmationService, Message, MessageService } from 'primeng/api';
import { PopulationDTO } from './model/population.model';
import { PopulationService } from './service/population.service';
import { EtudesUtilisateur } from "./model/etudesUtilisateur.model";
import { LayersService } from '../carto/service/layers.service';
import { TooltipOptions } from 'primeng/tooltip';
import { ContrainteEnvironnetaleService } from './contraintes-environnementales/service/contrainte-env.service';
import { Etape } from "../../../shared/enums/etape.enums";
import { EtatEtape } from "../../../shared/enums/etatEtape.enums";
import { InstallationStockageService } from './installation-stockage/service/installation-stockage.service';
import { ChantiersService } from './chantiers-envergure/service/chantiers-env.service';
import { environment } from 'src/environments/environment';
import { AutresMateriauxService } from './autres-materiaux/service/autres-materiaux.service';
import { ScenarioService } from './scenario/service/scenario.service';
import { ChantiersEnvergureComponent } from "./chantiers-envergure/chantiers-env.component";
import { ContrainteEnvironnementaleComponent } from "./contraintes-environnementales/contrainte-env.component";
import { InstallationStockageComponent } from "./installation-stockage/installation-stockage.component";
import { AutresMateriauxComponent } from "./autres-materiaux/autres-materiaux.component";
import {Roles} from "../../../shared/enums/roles.enums";
import { OverlayService } from '../overlay/service/overlay.service';
import {FileUpload} from "primeng/fileupload";

@Component({
  selector: 'app-etude',
  templateUrl: './view/etude.component.html',
  styleUrls: ['./view/etude.component.scss'],
  providers: [ArrayUtilsService,ConfirmationService,MessageService]
})
export class EtudeComponent implements OnInit {
  @Output() openSideBarEvent = new EventEmitter<boolean>();
  @Output() layerTerritoireEvent = new EventEmitter<string>();
  @Output() layerZonageEvent = new EventEmitter<string>();
  @Output() displayZonagePersoEvent = new EventEmitter<number | undefined>();
  @Output() displayZonageEvent = new EventEmitter<Zonage>();
  @Output() displayZonageImportEvent = new EventEmitter<Zonage>();
  @Output() displayPopulationEvent = new EventEmitter<{ listPopulation: PopulationDTO[]; selectedYear: number}>();
  @Output() selectedYearEtudeEvent = new EventEmitter<number>();
  @Output() displayEtablissementEtudeEvent = new EventEmitter<number>();


  maxFileSize = environment.maxFileUplaodSize;

  @ViewChild(ContrainteEnvironnementaleComponent) contrainteEnvironnementaleComponent: ContrainteEnvironnementaleComponent;
  @ViewChild(ChantiersEnvergureComponent) chantiersEnvergureComponent: ChantiersEnvergureComponent;
  @ViewChild(InstallationStockageComponent) installationStockageComponent: InstallationStockageComponent;
  @ViewChild(AutresMateriauxComponent) autresMateriauxComponent: AutresMateriauxComponent;

  tooltipOptionsMessage : TooltipOptions = {tooltipStyleClass:'tooltip-import-message'};

  protected readonly Etape = Etape;
  protected readonly EtatEtape = EtatEtape;

  visibleAfficherEtudeSideBar: any = false;
  visibleAjoutEtudeSideBar: any = false;
  visibleAssocierZonagesEtudeSideBar: any = false;

  etudeSideBar: String = 'afficher';
  anneeRef: string[] = [];
  anneeFin: string[] = [];
  @Input() selectedYear: number;

  actualStepIndex: number = 0;
  actualStep: Etape = Etape.CREATION;

  activeIndexDonneesEtude: any;
  openDonneesEtude:boolean = false;
  openDonneesScenario:boolean = false;
  // Etape 1
  form1: any;

  isCheckboxDisabled: any = true;
  usersActif!: Users;
  user!: Users;
  users: Users[] = [];
  results: Users[];
  isDreal: boolean = false;
  showAutoComplete = false;
  userProc: Users[];

  etudes: EtudesUtilisateur;
  etude = new Etude();
  currentListEtude: Etude[];

  allSelected: boolean = true;
  procurations: Users[];

  delegateInput: any;

  // Etape 2
  form2: any;

  territoires: any[] = new Array();
  lastSelectedTerritoires: any;
  selectedTerritoires: any[];
  territoiresChoisisEtude: Feature[];

  detailTerritoires: string = '';
  zonages: any[] = new Array();
  selectedZonage: any;
  layerZonage: boolean = false;
  associationZonageSource: Subscription;

  messageTooltipBouton: String;
  message1: string;
  message2: string;
  messageErreurImport: Message[];
  messageInformationImport: Message[];

  importSubscription: Subscription;
  boutonImportDisable:boolean = false;
  boutonValideDisable:boolean = true;

  errorMessage: string | null = null;
  succesMessage: string | null = null;

  territoireChanged: boolean = false;

  // Etape 3

  listPopulation: PopulationDTO[];

  // Etape 4

  constructor(
    private etudeService: EtudeService,
    private layersService: LayersService,
    private usersService: UsersService,
    private userService: UserService,
    private etablissementService: EtablissementService,
    private territoireService: TerritoireService,
    private importZonageService: ImportZonageService,
    private relEtudeUserProcurationService: RelEtudeUserProcurationService,
    private populationService : PopulationService,
    private associationZonageService: AssociationZonageService,
    private contrainteService: ContrainteEnvironnetaleService,
    private installationStockageService: InstallationStockageService,
    private autresMateriauxService: AutresMateriauxService,
    private chantierService: ChantiersService,
    private arrayUtils: ArrayUtilsService,
    private messageService: MessageService,
    private confirmationService : ConfirmationService,
    private scenarioService : ScenarioService,
    private overlayService : OverlayService,
    private cdr: ChangeDetectorRef,
    private formBuilder: FormBuilder
  ) {
    this.procurations = [];
  }

  ngOnInit(): void {
    this.territoires = [
      { value: 'Région', code: 'region' },
      { value: 'Département', code: 'departement' },
      { value: 'Zone d\'emploi', code: 'zoneemploi' },
      { value: 'Bassin de Vie', code: 'bassinvie' },
      { value: 'EPCI', code: 'epci' },
      { value: 'Commune', code: 'commune' }
    ];

    this.zonages = [
      { value: 'Département', code: 'departement' },
      { value: 'Zone d\'emploi', code: 'zoneemploi' },
      { value: 'Bassin de Vie', code: 'bassinvie' },
      { value: 'EPCI', code: 'epci' },
      { value: 'Commune', code: 'commune' }
    ];

    this.message1 = `Si votre zonage comporte des limites administratives,
                    il est recommandé d'utiliser celles issues de ADMIN
                    EXPRESS COG CARTO 2022`;

    this.message2 = `<span class="pi pi-info-circle"></span>
                        Pour l'import, 1 des 3 options :
                  <li> .zip contenant(.cpg, .dbf, .prj, .shp, .shx)
                  <li> 5 fichiers .cpg, .dbf, .prj, .shp, .shx
                  <li> .gpkg`;

    this.messageTooltipBouton = `Le fichier de projection démographique doit être
                                au format ".ods/.xls" et doit contenir les données
                                population basse/centrale/haute pour chaque zone
                                et chaque année de l'étude. Le nom de la zone est
                                facultatif`;

    this.territoiresChoisisEtude = new Array();
    this.usersActif = this.usersService.currentUsers;

    this.isDreal = this.usersActif.libelle_profil === 'DREAL';
    this.etude.proprietaire = this.usersActif;

    this.form1 = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      description: ['', null],
      isEtudeSRC: [this.etude.ifSrc, null],
      anneeDeRef: ['', [Validators.required]],
      anneeFinEtu: ['', [Validators.required]],
      delegatedPpl: this.formBuilder.array([])
    });


    this.form2 = this.formBuilder.group({
      referentiel: ['Issudureferentiel', Validators.required],
      nomTer: ['', Validators.required],
      description: [''],
      issudureferentiel: this.formBuilder.group({
        territoires: [''],
        zonages: [{value:'',disabled: true}]
      }),
      personnalise: this.formBuilder.group({
        fileSelected: false
      })
    }, { validators: [this.validatorForm2] });

    this.associationZonageSource = this.associationZonageService.associationZonageSource$.subscribe(value => {
      Promise.resolve(null).then(() => {
        if (value) {
          if (this.territoiresChoisisEtude.length != 0 && this.arrayUtils.includeFeature(this.territoiresChoisisEtude, value)) {
            this.territoiresChoisisEtude = this.arrayUtils.arrayRemoveFeature(this.territoiresChoisisEtude, value);
          } else {
            this.territoiresChoisisEtude.push(value);
          }
          if(this.typeTerritoire.value['code'] === 'commune' && this.territoiresChoisisEtude.length >= 2){
            this.form2.get('issudureferentiel.zonages')?.enable();
          }
          else if(this.typeTerritoire.value['code'] === 'commune' && this.territoiresChoisisEtude.length <= 1){
            this.resetZonage();
          }
          else if(this.territoiresChoisisEtude.length == 0 && this.typeTerritoire.value['code'] != 'commune'){
            this.resetZonage();
          }
          else {
            this.form2.get('issudureferentiel.zonages')?.enable();
          }
          if(this.selectedZonage.value != ''){
            this.layerZonageEvent.emit('actualiser');
          }

          this.detailTerritoires = this.toString(this.territoiresChoisisEtude);
          this.cdr.detectChanges();
        } else {
          this.form2.get('issudureferentiel.zonages')?.disable();
          this.form2.get('issudureferentiel.zonages').value = [''];
        }
      });
    })

    this.scenarioService.scenarioCloseSource$.subscribe(value =>{
      this.closeScenario();
    })
    this.scenarioService.scenarioOpenSource$.subscribe(value =>{
      this.openDonneesScenario = true;
    })

    this.scenarioService.scenarioOpenAccordeonSource$.subscribe(value =>{
      this.openScenario();
    })

    this.getAnnees();
    this.getEtudes();

    this.scenarioService.scenarioLayerTerritoireSource$.subscribe(value => {
      this.setTerritoireEtude();
    })
  }

  validatorForm2(form: FormGroup) {
    if (form.value.referentiel == 'Issudureferentiel' && form.value.issudureferentiel.territoires != null && form.value.issudureferentiel.zonages != null) {
      return null;
    } else if (form.value.referentiel == 'personnalise' && form.value.personnalise.nomTerritoire != '' && form.value.personnalise.fileSelected) {
      return null;
    }
    return { validUrl: true };
  }

  toString(features: Feature[]): string {
    let toString = '';
    if (features.length === 0)
      return toString;
    let i = 0;
    for (const iterator of features) {
      if (i != 0)
        toString += ', ';
      i += 1;
      toString += iterator.properties!.nom
    }
    return toString;
  }

  modifAssociationZonage(){
    const territoireToSend: Territoire = new Territoire();

    territoireToSend.valideEtape = true;
    territoireToSend.nom = this.form2.get('nomTer').value;
    territoireToSend.description = this.form2.get('description').value;
    territoireToSend.id_etude = this.etude.id;

    this.territoireService.addTerritoire(territoireToSend).subscribe({
      next : (value: Territoire) => {
        this.etude.territoire.description = territoireToSend.description;
        this.etude.territoire.nom = territoireToSend.nom;
        this.computeValideStep();
        this.toStep(this.actualStep);
      },
      error: (err:Error) => {
        console.log("Erreur lors de l'ajout du Territoire");
      }
    });
  }

  addAssociationZonage(): void {
    const territoireToSend: Territoire = new Territoire();
    territoireToSend.type = this.typeTerritoire.value['code'];
    territoireToSend.liste_territoire = this.detailTerritoires;
    territoireToSend.type_zonage = this.selectedZonage.code;
    territoireToSend.nom = this.form2.get('nomTer').value;
    territoireToSend.description = this.form2.get('description').value;
    territoireToSend.id_etude = this.etude.id;

    let liste_id: Number[] = [];
    if(this.etude.ifSrc)
    {
      liste_id.push(this.etude.proprietaire.id_region);
    }
    else {
      this.territoiresChoisisEtude.forEach(element => {
        liste_id.push(element.id as Number);
      });
    }
    territoireToSend.liste_id = liste_id;

    const zonageToSend: Zonage = new Zonage();
    zonageToSend.type = this.selectedZonage.code;
    zonageToSend.nom = territoireToSend.nom;
    zonageToSend.description = territoireToSend.description;
    zonageToSend.id_etude = territoireToSend.id_etude;

    territoireToSend.zone = zonageToSend;

    this.territoireService.addTerritoire(territoireToSend).subscribe({
      next : (value: Territoire) => {
        this.etude.territoire = value;
        this.layersService.reset(true);
        this.displayZonageEvent.emit(territoireToSend.zone);
        this.etude.etatEtapes[Etape.ZONAGE] = EtatEtape.VALIDE;
        this.boutonValideDisable = true;
        this.computeValideStep();
        this.toStep(this.actualStep);
      },
      error: (err:Error) => {
        console.log("Erreur lors de l'ajout du Territoire");
      }
    });
  }

  addAssociationZonageImportValidation(): void{
    const territoireToSend: Territoire = new Territoire();
    territoireToSend.nom = this.form2.get('nomTer').value;
    territoireToSend.description = this.form2.get('description').value;
    territoireToSend.id_etude = this.etude.id;
    territoireToSend.type = 'perso';
    territoireToSend.type_zonage = 'perso';

    const zonageToSend: Zonage = new Zonage();
    zonageToSend.id_etude = territoireToSend.id_etude;
    zonageToSend.type = 'autre';

    this.territoireService.validationCreationTerritoire(territoireToSend).subscribe({
      next : (value: Territoire) => {
        this.etude.territoire = value;
        this.layersService.reset(true);
        this.displayZonageEvent.emit(zonageToSend);
        this.messageInformationImport = [];
        this.etude.etatEtapes[Etape.ZONAGE] = EtatEtape.VALIDE;
        this.boutonValideDisable = true;
        this.computeValideStep();
        this.toStep(this.actualStep);
      },
      error: (err:Error) => {
        console.log("Erreur lors de l'ajout du Territoire");
      }
    })
  }

  addEtude(): void {
    this.etudeService.addEtude(this.etude).subscribe({
      next: (etude: Etude) => {
        this.currentListEtude.push(etude);
        this.etude.id = etude.id;
        if (this.delegatedPpl.length > 0) {
          this.delegatePplControls(etude);
        }
        this.etude.etatEtapes[Etape.CREATION] = EtatEtape.VALIDE;
        this.computeValideStep();
        this.toStep(this.actualStep);
      },
      error: (err: Error) => console.log("Erreur lors de l'ajout de l'Etude")
    }
    );
  }

  updateEtude(): void {
    this.etudeService.updateEtude(this.etude.id,this.etude).subscribe({
      next: (etude: Etude) => {
        this.currentListEtude.pop();
        this.currentListEtude.push(etude);
        this.delegatePplControlsUpdate(etude);
        this.etude.etatEtapes[Etape.CREATION] = EtatEtape.VALIDE;
        this.computeValideStep();
        this.toStep(this.actualStep);
      },
      error: (err: Error) => console.log("Erreur lors de l'ajout de l'Etude")
    }
    );
  }

  addTracabiliteEtape(etape:Etape, etatEtape : EtatEtape): void {
    this.etudeService.addTracabiliteEtape(this.etude.id, etape, etatEtape).subscribe({
        next: (etatEtapes: any) => {
          this.etude.etatEtapes = etatEtapes;
          this.computeValideStep();
          if(this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE){
            this.openScenario();
          } else {
            this.toStep(this.actualStep);
          }
        },
        error: (err: Error) => console.log("Erreur lors de la validation de l'étape")
      }
    );
  }

  editEtude(selectedEtude: Etude, selectedliste: Etude[] ) : void {
    this.etudeService.getEtudeById(selectedEtude.id).subscribe(
      (etude: Etude) => {
        this.currentListEtude = selectedliste;
        this.etude = etude;
        if(this.etude.proprietaire.id_user === this.usersActif.id_user){
          this.etude.readOnly = false;
        }
        this.boutonValideDisable = true;
        this.isDreal = this.etude.ifSrc;
        this.form1.patchValue({
          "name": etude.nom,
          "description": etude.description,
          "isEtudeSRC": etude.ifSrc,
          "anneeDeRef": String(etude.anneeRef), //le cast est important sinon le binding ne marche pas
          "anneeFinEtu": String(etude.anneeFin) //le cast est important sinon le binding ne marche pas
        });

        // Mise à jour de l'année de ref
        this.majDateHandler(parseInt(this.etude.anneeRef));

        if (etude.mandataires && etude.mandataires.length > 0) {
          etude.mandataires.forEach( m => {
            this.addDelegation(m);
            if (m.id_user === this.usersActif.id_user && !['GEST','ADMIN'].includes(this.usersActif.libelle_profil)) {
              this.etude.readOnly = !m.if_droit_ecriture;
            }
          });
        }

        const e:any = etude.etatEtapes;

        if(e[Etape.ZONAGE] === EtatEtape.VALIDE || e[Etape.ZONAGE] === EtatEtape.IMPORTE) {
          this.layerZonage = true;
          this.setDonneeZone();

          this.form1.get('anneeDeRef')?.disable();
          this.form1.get('anneeFinEtu')?.disable();
        }
        if(this.isEtapeScenario() || this.isLectureSeule()){
          this.setDisabledForm(true);
        }

        this.computeValideStep();
        this.toStep(this.actualStep);
        this.toggleChangeSideBar('edit');
      }
    );
  }

  setDisabledForm(yes:boolean){
    if(yes){
      this.form1.get('name')?.disable();
      this.form1.get('description')?.disable();
      this.form1.get('anneeDeRef')?.disable();
      this.form1.get('anneeFinEtu')?.disable();
      this.form1.get('delegatedPpl')?.disable();

      this.form2.get('nomTer')?.disable();
      this.form2.get('description')?.disable();
      this.form2.get('referentiel')?.disable();
      this.form2.get('issudureferentiel.territoires')?.disable();
      this.form2.get('issudureferentiel.zonages')?.disable();
    } else {
      this.form1.get('name')?.enable();
      this.form1.get('description')?.enable();
      this.form1.get('anneeDeRef')?.enable();
      this.form1.get('anneeFinEtu')?.enable();
      this.form1.get('delegatedPpl')?.enable();

      this.form2.get('nomTer')?.enable();
      this.form2.get('description')?.enable();
      this.form2.get('referentiel')?.enable();
      this.form2.get('issudureferentiel.territoires')?.enable();
      this.form2.get('issudureferentiel.zonages')?.disable();
    }
  }

  majDateHandler(year:number){
    const { anneeDeRef, anneeFin } = this.getYears();
    if (anneeDeRef > this.selectedYear || anneeFin < this.selectedYear) {
      this.handleYearChanged(year);
    }
  }

  removeEtude(selectedEtude: Etude, selectedliste: Etude[]): void {
    this.confirmationService.confirm({
      message: 'Etes-vous sûr de supprimer cette Etude ?',
      header: 'Voulez-vous continuer ?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteEtude(selectedEtude)
      },
      key: 'deleteEtude'
    });
  }

  private delegatePplControls(etude:Etude){
    this.etude.mandataires = [];
    this.delegatedPpl.controls.forEach(control => {
      const procuration = control.value;
      this.userService.getUserByEmail(procuration.mail).pipe(
        mergeMap(user => {
          const relEtudeUserProcuration = new RelEtudeUserProcuration();
          relEtudeUserProcuration.etude = etude;
          relEtudeUserProcuration.user = user;
          relEtudeUserProcuration.ifDroitEcriture = procuration.isSelected;
          return this.relEtudeUserProcurationService.addRelEtudeUserProcuration(relEtudeUserProcuration);
        })
      ).subscribe(relEtudeUserProcuration => {
          relEtudeUserProcuration.user.if_droit_ecriture = relEtudeUserProcuration.ifDroitEcriture;
          this.etude.mandataires.push(relEtudeUserProcuration.user);
      })
    });
  }

  private delegatePplControlsUpdate(etude:Etude){
    let relations : RelEtudeUserProcuration[] = [];

    this.delegatedPpl.value.forEach((value: any) => {
      let relEtudeUserProcuration = new RelEtudeUserProcuration();
      relEtudeUserProcuration.etude = etude;
      relEtudeUserProcuration.user = value.user;
      relEtudeUserProcuration.ifDroitEcriture = value.isSelected;
      relations.push(relEtudeUserProcuration);
    });
    this.relEtudeUserProcurationService.updateRelEtudeUserProcuration(this.etude.id,relations).subscribe(
      (res: RelEtudeUserProcuration[]) => {
        this.etude.mandataires = res.map(
          rel => {
            let delegated: Users | undefined = this.delegatedPpl.value.find((u: any)  => u.user.id_user === rel.user.id_user);
            if (delegated != null) {
              rel.user.if_droit_ecriture = delegated.isSelected;
            }
            return rel.user;
          }
        );
      }
    );
  }

  getAnnees(): void {
    this.anneeFin = [];
    this.etablissementService.getDistinctAnnees().subscribe(annees => {
      const currentYear = new Date().getFullYear();
      this.anneeRef = annees;
      const startIndex = this.anneeRef.length > 0 ? Math.min(...this.anneeRef.map(Number)) : currentYear;
      const endIndex = this.anneeRef.length > 0 ? Math.max(...this.anneeRef.map(Number)) + 20 : currentYear;

      for (let i = startIndex; i <=  endIndex; i++) {
        this.anneeFin.push(i.toString());
      }
    });
  }


  getEtudes(): void {
    if (this.usersActif.libelle_profil !== Roles.Public) {
      this.etudeService.getAllEtudes().subscribe(
        (etudes: EtudesUtilisateur) => {
          this.etudes = etudes;
          this.etudes.etudesMandataireLectureSeule.forEach(e => e.readOnly = true);
        }
      );
    }
  }

  deleteEtude(etude: Etude): void {
    this.etudeService.deleteEtude(etude.id).subscribe(() => {
      this.getEtudes();
    });
  }

  publierEtude(etude: Etude){
    this.confirmationService.confirm({
      message: etude.ifPublic ? 'L\'etude '+etude.nom+' est déjà publiée, voulez vous la rendre privée ?' : 'Publiez l\'etude '+etude.nom+' ?',
      header: 'Voulez-vous continuer ?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.etudeService.publierEtude(etude).subscribe({
          next:(value:any) => {
            etude.ifPublic = !etude.ifPublic;
          }
        })
      },
      key: 'deleteEtude'
    });
  }

  getProcurations(): void {
    //this.procurationService.getAllProcurations().subscribe(procurations => this.procurations = procurations);
  }

  openCloseSideBar(open: boolean) {
    if (open) {
      this.etudeSideBar = 'afficher';
      this.visibleAfficherEtudeSideBar = true;
      this.visibleAjoutEtudeSideBar = false;
      this.resetDonnees();
      this.resetCreationEtude();
    } else {
      this.visibleAjoutEtudeSideBar = false;
      this.visibleAfficherEtudeSideBar = false;
      this.visibleAssocierZonagesEtudeSideBar = false;
    }
  }

  resetZonage() {
    this.form2.get('issudureferentiel.zonages')?.disable();
    this.form2.get('issudureferentiel.zonages').reset();
    this.selectedZonage = { value: '', code: '' };
    this.layerZonageEvent.emit('');
  }

  resetDonnees() {
    this.clearSubscription();
    this.clearZonageImportPerso();
    this.usersActif = this.usersService.currentUsers;
    this.getEtudes();
    this.getAnnees();
    this.etude = new Etude();
    this.isDreal = this.usersActif.libelle_profil === 'DREAL';
    this.etude.ifSrc = this.isDreal;
    this.etude.proprietaire = this.usersActif;
    this.allSelected = true;
    this.listPopulation = [];

    this.resetEtapeZonage('Issudureferentiel');
    this.boutonImportDisable = false;
    this.layerZonage = false;

    this.messageService.clear();
    this.errorMessage = "";
    this.succesMessage = "";

    this.activeIndexDonneesEtude = -1;
    this.openDonneesEtude = false;
    this.openDonneesScenario = false;

    this.setDisabledForm(false);

    this.form1.get('anneeDeRef')?.enable();
    this.form1.get('anneeFinEtu')?.enable();

    this.cdr.detectChanges();

    this.scenarioService.scenarioClear();
  }

  resetEtapeZonage(referentiel:String){
    this.territoiresChoisisEtude = new Array();
    this.lastSelectedTerritoires = undefined;
    this.detailTerritoires = '';

    this.form2.get('issudureferentiel.zonages')?.disable();
    this.selectedZonage = { value: '', code: '' };

    this.territoires = [
      { value: 'Région', code: 'region' },
      { value: 'Département', code: 'departement' },
      { value: 'Zone d\'emploi', code: 'zoneemploi' },
      { value: 'Bassin de Vie', code: 'bassinvie' },
      { value: 'EPCI', code: 'epci' },
      { value: 'Commune', code: 'commune' }
    ];

    this.zonages = [
      { value: 'Département', code: 'departement' },
      { value: 'Zone d\'emploi', code: 'zoneemploi' },
      { value: 'Bassin de Vie', code: 'bassinvie' },
      { value: 'EPCI', code: 'epci' },
      { value: 'Commune', code: 'commune' },
    ];
    this.form2.reset();
    this.referentiel = referentiel;
    this.messageInformationImport = [];
    this.messageErreurImport = [];
    this.associationZonageService.reset();
    this.layerZonage = false;

    this.form2.get('issudureferentiel.territoires')?.enable();
    this.form2.get('referentiel')?.enable();
  }

  resetEtapeZonageDREAL(referentiel:String){
    this.territoiresChoisisEtude = new Array();

    this.form2.reset();
    this.typeTerritoire = { value: 'Région', code: 'region' };
    this.territoires = [{ value: 'Région', code: 'region' }];
    this.detailTerritoires = this.etude.proprietaire.nom_region;
    this.form2.get('issudureferentiel.zonages')?.enable();

    this.selectedZonage = { value: '', code: '' };
    this.layerZonage = false;

    this.referentiel = referentiel;

    this.messageInformationImport = [];
    this.messageErreurImport = [];

    this.associationZonageService.resetDREAL();
    this.layersService.onRefRegionLayersChange(this.etude.proprietaire.id_region);
  }

  setDonneeZone(){
    if(this.etude.territoire != null) {
      this.form2.patchValue({
        "nomTer": this.etude.territoire.nom,
        "description": this.etude.territoire.description,
        "referentiel": this.etude.territoire.type === "perso" ? "personnalise" : "Issudureferentiel",
        "personnalise.fileSelected": false
      });

      if (this.etude.territoire.type !== "perso") {
        this.form2.get('issudureferentiel.territoires').setValue(this.territoires.filter(entry => entry.code === this.etude.territoire.type).pop());
        this.editListeZonage(this.etude.territoire.type.toString());
        this.form2.get('issudureferentiel.zonages').setValue(this.zonages.filter(entry => entry.code === this.etude.territoire.type_zonage).pop());
        this.detailTerritoires = this.etude.territoire.liste_territoire as string;
        this.selectedZonage = this.form2.get('issudureferentiel.zonages').value;
      }

      if(this.etude.ifSrc){
        this.typeTerritoire = { value: 'Région', code: 'region' };
        this.territoires = [{ value: 'Région', code: 'region' }];
        this.detailTerritoires = this.etude.proprietaire.nom_region;
        this.form2.get('issudureferentiel.zonages')?.enable();
      }
    }

    if(this.isEtapeProjectionPassed()){
      this.form2.get('referentiel')?.disable();
      this.form2.get('issudureferentiel.territoires')?.disable();
      this.form2.get('issudureferentiel.zonages')?.disable();
    }
  }

  openEtudeSideBar() {
    this.openCloseSideBar(true);
    this.cdr.detectChanges();
    this.openSideBarEvent.emit(true);
  }

  closeEtudeSideBar() {
    this.openCloseSideBar(false);
    this.cdr.detectChanges();
    this.openSideBarEvent.emit(false);
    this.resetCreationEtude();
    this.resetDonnees();
  }

  closeAjoutEtudeSideBar() {
    this.visibleAjoutEtudeSideBar = false;
    this.resetCreationEtude();
    this.resetDonnees();
  }

  openScenario(){
    this.activeIndexDonneesEtude = -1;
    this.actualStepIndex = -1;
    this.openDonneesEtude = false;
    this.openDonneesScenario = true;
    this.setTerritoireEtude();
  }

  closeScenario() {
    this.openDonneesScenario = false;
  }

  isEtapeScenario(){
    return this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE
          || this.etude.etatEtapes[Etape.MATERIAUX] === EtatEtape.VALIDE_VIDE;
  }

  onOpenDonneesEtude(event:any){
    if(event.index === 0){
      this.openDonneesEtude = true;
      this.scenarioService.scenarioCloseAccordeon();
      this.toStep(this.getEtapeFromIndex(this.actualStepIndex));
    }
  }

  getEtapeFromIndex(index:number){
    let i=0;
    for (const value of this.enumKeys(Etape)) {
      if (index === i) {
        return Etape[value];
      }
      i++;
    }
    return Etape.CREATION;
  }

  onCloseDonneesEtude(event:any){
    if(event.index === 0){
      this.openDonneesEtude = false;
    }
  }


  private isFileTypeValid(file: File, fileuploadform: FileUpload): boolean {
    let acceptableTypes = fileuploadform.accept?.split(',').map((type) => type.trim());
    for (let type of acceptableTypes!) {
      let acceptable = fileuploadform.isWildcard(type) ? fileuploadform.getTypeClass(file.type) === fileuploadform.getTypeClass(type) : file.type == type || fileuploadform.getFileExtension(file).toLowerCase() === type.toLowerCase();

      if (acceptable) {
        return true;
      }
    }

    return false;
  }

  validateInputFiles(event: any, fileuploadform: any) {
    this.overlayService.overlayOpen("Import en cours...");
    this.messageErreurImport = [];
    let files: File[] = Array.from(event.files)

    try {
      this.testFiles(fileuploadform, files);
    } catch(error: any) {
      console.log("Erreur lors du check de l'import");
      this.messageErreurImport = [{severity:'error', detail: error }];
      this.messageInformationImport = [];
      this.boutonImportDisable = false;
      setTimeout(() => {
        this.messageErreurImport = [];
      }, 5000);
      this.overlayService.overlayClose();
      fileuploadform.clear();
    }

  }
  myUploader(event: any, fileuploadform: any) {
      let files = event.files;
      fileuploadform.clear();
      this.uploadFiles(files);
  }

 private testFiles(fileuploadform: any, files: File[]) {

   // verification des fichiers coté client
   if (files.length > 5) {
     throw "Import impossible, nombre maximal de fichiers dépassé";
   }
   //verification de la taille max
   let invalidFiles: File[] = [];
   for (const file of files){
     if (file.size >= this.maxFileSize) {
       invalidFiles.push(file);
     }
   }
   if (invalidFiles.length > 0) {
     let errorMsg = 'Import impossible, les fichiers suivants dépassent la taille maximale autorisée (' + this.maxFileSize/1000000 + ' Mo)'
     for (const file of invalidFiles) {
       errorMsg = errorMsg + '<li>'+file.name+'</li>'
     }
     throw errorMsg;
   }
   //verification des extensions
   invalidFiles = [];
   for (const file of files){
     if (!this.isFileTypeValid(file, fileuploadform)) {
       invalidFiles.push(file);
     }
   }
   if (invalidFiles.length > 0) {
     let errorMsg = 'Import impossible, les extensions des fichiers suivants ne correspondent pas aux formats attendus :'
     for (const file of invalidFiles) {
       errorMsg = errorMsg + '<li>'+file.name+'</li>'
     }
     throw errorMsg;
   }
 }
  private uploadFiles(files: File[]): void{
    this.boutonImportDisable = true;
    this.messageErreurImport = [];
    this.layersService.reset(false);
    this.layerZonage = false;

    if(this.importSubscription){
      this.importSubscription.unsubscribe();
    }


      this.importSubscription = this.importZonageService.importZonageFiles(files,this.etude.id).subscribe(
        {
          next: (value: any) => {
          },
          error: (err) => {
            if(err.status && err.status === 500) {
              this.messageErreurImport = [{severity:'error', detail: err.error.errors[0].message }];
            } else {
              this.messageErreurImport = [{severity:'error', detail: err.error }];
            }
            this.messageInformationImport = [];
            this.boutonImportDisable = false;
            setTimeout(() => {
              this.messageErreurImport = [];
            }, 5000);
            this.overlayService.overlayClose();
          },
          complete: () => {
            const zonageToSend: Zonage = new Zonage();
            zonageToSend.type = 'autre';
            zonageToSend.id_etude = this.etude.id;
            this.displayZonageImportEvent.emit(zonageToSend);
            this.boutonImportDisable = true;
            this.layerZonage = true;
            this.importZonageService.importZonageCheck(this.etude.id).subscribe(
              {
                next: (value: any) => {
                  this.messageService.clear("informationImport");
                  if(value.tauxHorsCouvertureCalcule != 0) {
                    let territoire = undefined;
                    if(this.etude.ifSrc){
                      territoire = "de la région"
                    } else {
                      territoire = "du territoire français"
                    }
                    const message = "Le territoire importé sort des limites "+territoire+" ( taux erreur : "+this.truncateString(value.tauxHorsCouvertureCalcule.toString(),6)+"% ≤ "+value.tauxHorsCouvertureAccepte+"%)";
                    this.messageService.add({severity:'warn', detail: message, key: "informationImport"});
                  }
                  if(this.etude.ifSrc && value.tauxNonCouvertCalcule != 0) {
                    const message = "Le territoire importé ne couvre pas le territoire de la région ( taux erreur : "+this.truncateString(value.tauxNonCouvertCalcule.toString(),6)+"% ≤ "+value.tauxNonCouvertAccepte+"%)";
                    this.messageService.add({severity:'warn', detail: message, key: "informationImport"});
                  }
                  this.form2.get('personnalise.fileSelected').setValue(true);
                },
                error: (err) => {
                  console.log("Erreur lors du check de l'import");
                  this.messageErreurImport = [{severity:'error', detail: err.error }];
                  this.messageInformationImport = [];
                  this.boutonImportDisable = false;
                  setTimeout(() => {
                    this.messageErreurImport = [];
                  }, 5000);
                  this.overlayService.overlayClose();
                },
                complete: () => {
                  this.boutonImportDisable = false;
                  this.messageService.add({severity:'success', detail: "Zonage personnalisé importé avec succès", key: "informationImport"});
                  this.overlayService.overlayClose();
                  this.etude.etatEtapes[Etape.ZONAGE] = EtatEtape.IMPORTE;
                  this.computeValideStep();
                }
              }
            );
          }
        });


  }


  onChangeTerritoire(val: any) {
    this.territoiresChoisisEtude = new Array();
    this.detailTerritoires = '';
    this.selectedZonage = { value: '', code: '' };
    this.layerZonage = false;
    this.form2.get('issudureferentiel.zonages').reset();
    this.form2.get('issudureferentiel.zonages')?.disable();
    this.layerZonageEvent.emit('');

    this.layersService.reset(true);
    this.toggleLayer();

    if(this.isEtapeZoneValide()){
      this.territoireChanged = true;
    }
  }

  onChangeReferentiel(event: string) {
    if(this.importSubscription){
      this.importSubscription.unsubscribe();
      this.boutonImportDisable = false;
    }

    if(this.layerZonage === true){
      this.confirmChangeRef(event);
    } else {
      if(this.etude.ifSrc){
        this.resetEtapeZonageDREAL(event);
      } else {
        this.resetEtapeZonage(event);
      }
    }
  }

  confirmChangeRef(event: string){
    this.confirmationService.confirm({
      message: 'Il existe déjà un zonage d\'étude. Le changement de référentiel va supprimer celui en cours.',
      header: 'Voulez-vous continuer ?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        if(this.etude.ifSrc){
          this.resetEtapeZonageDREAL(event);
        } else {
          this.resetEtapeZonage(event);
        }
      },
      reject: (type:any) => {
          switch(type) {
              case ConfirmEventType.REJECT:
                if(this.form2.get('referentiel').value === 'personnalise'){
                  this.form2.get('referentiel').value = 'Issudureferentiel';
                  this.form2.get('referentiel').setValue('Issudureferentiel');
                } else {
                  this.form2.get('referentiel').value = 'personnalise';
                  this.form2.get('referentiel').setValue('personnalise');
                }
              break;
              case ConfirmEventType.CANCEL:
                if(this.form2.get('referentiel').value === 'personnalise'){
                  this.form2.get('referentiel').value = 'Issudureferentiel';
                  this.form2.get('referentiel').setValue('Issudureferentiel');
                } else {
                  this.form2.get('referentiel').value = 'personnalise';
                  this.form2.get('referentiel').setValue('personnalise');
                }
              break;
          }
      },
      key: 'changeReferentiel'
    });
  }

  confirmImport(event: any, fileuploadform: any){
    if (this.layerZonage) {
      this.confirmationService.confirm({
        message: 'Il existe déjà un zonage d\'étude. L\'import d\'un nouveau zonage va supprimer celui en cours.',
        header: 'Voulez-vous continuer ?',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          this.myUploader(event, fileuploadform);
        },
        reject: () => {
          this.overlayService.overlayClose();
          fileuploadform.clear();
        },
        key: 'resetImport'
      });
    } else {
      this.myUploader(event, fileuploadform);
    }
  }



  confirmProjection(event: any, fileuploadform: any){
    if((this.listPopulation != undefined && this.listPopulation.length != 0) ||
      (this.etude.populations != undefined && this.etude.populations.length != 0)){
      this.confirmationService.confirm({
        message: 'Il existe déjà une projection démographique. L\'import d\'une nouvelle projection va supprimer celle en cours.',
        header: 'Voulez-vous continuer ?',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          this.onUploadFile(event,fileuploadform);
        },
        reject: () => {
          fileuploadform.clear();
        },
        key: 'resetImport'
      });
    } else {
      this.onUploadFile(event,fileuploadform);
    }
  }

  toggleLayer() {
    if (this.lastSelectedTerritoires != undefined) {
      this.layerTerritoireEvent.emit(this.lastSelectedTerritoires.code);
    }
    this.layerTerritoireEvent.emit(this.typeTerritoire.value['code']);
    this.lastSelectedTerritoires = this.typeTerritoire.value;

    this.editListeZonage(this.typeTerritoire.value['code']);

    this.cdr.detectChanges();
  }

  editListeZonage(territoire: string) {
    if (territoire === 'region') {
      this.zonages = [
        { value: 'Département', code: 'departement' },
        { value: 'Zone d\'emploi', code: 'zoneemploi' },
        { value: 'Bassin de Vie', code: 'bassinvie' },
        { value: 'EPCI', code: 'epci' },
        { value: 'Commune', code: 'commune' },
      ]
    }
    if (territoire === 'departement') {
      this.zonages = [
        { value: 'Zone d\'emploi', code: 'zoneemploi' },
        { value: 'Bassin de Vie', code: 'bassinvie' },
        { value: 'EPCI', code: 'epci' },
        { value: 'Commune', code: 'commune' },
      ]
    }
    if (territoire === 'zoneemploi') {
      this.zonages = [
        { value: 'Bassin de Vie', code: 'bassinvie' },
        { value: 'EPCI', code: 'epci' },
        { value: 'Commune', code: 'commune' },
      ]
    }
    if (territoire === 'bassinvie' || territoire === 'epci' || territoire === 'commune') {
      this.zonages = [
        { value: 'Commune', code: 'commune' },
      ]
    }
  }

  toggleAjoutEtudeSidebarEnd() {
    this.visibleAfficherEtudeSideBar = false;
    this.visibleAjoutEtudeSideBar = false;
    this.visibleAssocierZonagesEtudeSideBar = false;
    this.resetDonnees();
    this.resetCreationEtude();
  }

  toggleLayerZonage(val: any) {
    if(this.etude.ifSrc){
      this.layersService.reset(true);
      this.layersService.onRefRegionLayersChange(this.etude.proprietaire.id_region);

      if(this.isEtapeZoneValide()){
        this.territoireChanged = true;
      }
    }

    this.layerZonageEvent.emit(val.code);
    this.selectedZonage = val;
    this.layerZonage = true;
  }

  toggleChangeSideBar(page: string) {
    if(page === 'ajout') {
      this.currentListEtude = this.etudes.etudesProprietaire;
      this.toStep(Etape.CREATION);
      this.activeIndexDonneesEtude = 0;
      this.openDonneesEtude = true;
      this.isDreal = this.usersActif.libelle_profil === 'DREAL';
    }

    if(page === 'edit' && !this.isEtapeScenario()){
      this.activeIndexDonneesEtude = 0;
      this.openDonneesEtude = true;
    }
    this.openDonneesScenario = this.isEtapeScenario();

    this.visibleAjoutEtudeSideBar = false;
    this.visibleAssocierZonagesEtudeSideBar = false;
    this.visibleAjoutEtudeSideBar = true;
    this.visibleAfficherEtudeSideBar = true;

    this.etudeSideBar = page;
  }

  addProcurations() {
    this.showAutoComplete = true;
    this.userService.getAllUsers().subscribe(users => {
        this.results = users
    });
  }

  search(event: any) {
    const query = event.query;
    this.userService.getUsersDelegation().subscribe(users => {
      this.results = users.filter(user => user.mail !== this.usersActif.mail)
          .filter(user => {
            return (
              user.mail.toLowerCase().indexOf(query.toLowerCase()) !== -1
            );
          });
      // Vérifie si un user est déjà présent
      this.results = this.results.filter(user => {
        for(let ppl of this.delegatedPpl.value){
          if(user.mail === ppl.mail){
            return false;
          }
        }
        return true;
      })
    });
  }

  addUser(event: any) {
    this.showAutoComplete = false;

    this.procurations.push(...this.userProc);
    this.userProc = [];
  }

  selectAll() {
    if (this.delegatedPpl.length > 0) {
      this.allSelected = !this.allSelected;

      this.delegatedPpl.controls.forEach(control => {
        control.patchValue({
          isSelected: this.allSelected
        });
      });
    }
  }

  deleteProcuration(row: any): void {
    const index = this.delegatedPpl.controls.findIndex((control: AbstractControl) => control.value === row.value);
    if (index !== -1) {
      this.delegatedPpl.removeAt(index);
    }
  }

  deleteAllProcurations() {
    if (this.delegatedPpl.length > 0) {
      this.delegatedPpl.clear();
    }
  }

  addDelegation(event: any) {
    let user = event as Users;
    const delegateForm = this.formBuilder.group({
      id: [user.id_user],
      isSelected: [user.if_droit_ecriture],
      mail: [user.mail],
      nom: [user.nom],
      prenom: [user.prenom],
      user: [user]
    });
    this.delegatedPpl.push(delegateForm);
    this.delegateInput = null;
  }

  addMultiplePopulations(): void {
    const populations: PopulationDTO[] = this.listPopulation;
    this.overlayService.overlayOpen('Ajout des populations en cours...');
    this.populationService.addPopulations(this.etude.id, populations).subscribe(
      {
        next: (response) => {
          console.log('Populations ajoutées avec succès :', response) ;
          this.etude.etatEtapes[Etape.POPULATION] = EtatEtape.VALIDE;
          this.overlayService.overlayClose();
          this.boutonValideDisable = true;
          this.computeValideStep();
          this.toStep(this.actualStep);
          },
        error: (error) => {
          console.error('Erreur lors de l\'ajout des populations :', error);
          this.overlayService.overlayClose();
        }
      }
    );

  }

  disableStep(step:Etape){
    return !this.isStepActive(step) || this.isEtapeScenario() || this.isLectureSeule()
  }

  validerEtapeZonageDisable(){
    if(this.isEtapeZoneValide()){
      if(this.form2.value.nomTer == null){
        return true;
      }

      if(this.layerZonage === false){
        return true;
      }

      if(this.layerZonage && this.territoireChanged){
        return false;
      }

      if(this.layerZonage){
        return this.form2.value.nomTer === this.etude.territoire.nom && (this.form2.value.description === this.etude.territoire.description
          || ((this.form2.value.description == null || this.form2.value.description === '') && this.etude.territoire.description == null))
          || this.form2.value.nomTer.length === 0;
      }
    }
    if(this.referentiel === 'Issudureferentiel'){
      return !this.form2.valid
    }
    return this.boutonImportDisable || !this.form2.valid;
  }

  validerEtapeGeneraliteDisable(){
    if(this.etude.etatEtapes[Etape.CREATION] === EtatEtape.VALIDE){
      if(this.form1.value.name == null){
        return false;
      }

      return (this.form1.value.name === this.etude.nom && (this.form1.value.description === this.etude.description
            || ((this.form1.value.description == null || this.form1.value.description === '') && this.etude.description == null))
          || this.form1.value.name.length === 0)

        && (this.form1.value.anneeDeRef == null || this.form1.value.anneeFinEtu == null
          || (this.form1.value.anneeDeRef == this.etude.anneeRef.toString() && this.form1.value.anneeFinEtu == this.etude.anneeFin.toString()))
         && !this.isDelegatedPplChanged();
    }

    return !this.form1.valid;
  }

  isDelegatedPplChanged() {
    if (this.etude.mandataires?.length == null && this.delegatedPpl.length === 0 ) {
      return false;
    }
    if (this.etude.mandataires?.length !== this.delegatedPpl.length ) {
      return true;
    }

    for (const c of this.delegatedPpl.value) {
      let user: Users | undefined = this.etude.mandataires.find(u => u.id_user === c.id);
      if ( user == null || c.isSelected !== user.if_droit_ecriture) {
        return true;
      }
    }

    return false;
  }

  setEtapeZone(){
    if(this.etude.etatEtapes[Etape.ZONAGE] === EtatEtape.VALIDE){
      this.setTerritoireEtude();
    }
    else if((this.etude.id != 0 && this.etude.ifSrc) || this.isDreal)
    {
      let id_region = (this.etude.id != undefined && this.etude.ifSrc) ? this.etude.proprietaire.id_region : this.usersActif.id_region;
      let nom_region = (this.etude.id != undefined && this.etude.ifSrc) ? this.etude.proprietaire.nom_region : this.usersActif.nom_region;

      this.layersService.onRefRegionLayersChange(id_region);

      this.typeTerritoire = { value: 'Région', code: 'region' };
      this.territoires = [{ value: 'Région', code: 'region' }];

      this.detailTerritoires = nom_region;
      this.form2.get('issudureferentiel.zonages')?.enable();
    }
  }

  getZonageEtude(){
    const zonageToSend: Zonage = new Zonage();
    zonageToSend.type = 'autre';
    zonageToSend.nom = this.etude.territoire.nom;
    zonageToSend.description = this.etude.territoire.description;
    zonageToSend.id_etude = this.etude.id;
    return zonageToSend;
  }

  setEtapeGeneralite(){
    if(this.actualStep === Etape.CREATION && !this.isEtapeScenario()){
      return;
    }
    if(this.etude.etatEtapes[Etape.CREATION] === EtatEtape.VALIDE){
      this.form1.patchValue({
        "name": this.etude.nom,
        "description": this.etude.description
      });
    }
    if(this.etude.etatEtapes[Etape.ZONAGE] === EtatEtape.VALIDE){
      this.form1.get('anneeDeRef')?.disable();
      this.form1.get('anneeFinEtu')?.disable();
    }
    if(this.isEtapeScenario()){
      this.form1.get('name')?.disable();
      this.form1.get('description')?.disable();
    }
    if(this.etude.territoire === null || this.etude.territoire === undefined){
      return;
    }
    this.setEtapeZone();
  }

  setEtapeProjection(){
    this.layersService.reset(true);
    this.layersService.displayEtudeTerritoireLayer(this.etude.territoire);
    this.layersService.onFlyToLayerEvent('EtudeTerritoire');
    this.displayEtablissementEtudeEvent.emit(this.etude.id);
    const zonageToSend: Zonage = this.getZonageEtude();
    zonageToSend.populations = this.etude.populations;
    this.displayZonageEvent.emit(zonageToSend);
  }

  setEtapeContrainte(){
    this.setTerritoireEtude();
    this.contrainteService.initContrainte();
  }

  setEtapeChantier(){
    this.setTerritoireEtude();
    this.chantierService.initChantier();
  }

  setEtapeInstallations(){
    this.setTerritoireEtude();
    this.installationStockageService.initInstallationStockage();
  }

  setEtapeMateriaux(){
    this.setTerritoireEtude();
    this.autresMateriauxService.initAutresMateriaux();
  }

  setTerritoireEtude(){
    this.layersService.reset(true);
    this.layersService.displayEtudeTerritoireLayer(this.etude.territoire);
    this.layersService.onFlyToLayerEvent('EtudeTerritoire');
    this.displayEtablissementEtudeEvent.emit(this.etude.id);
    this.displayZonageEvent.emit(this.getZonageEtude());
  }

  goStep(from: Etape) {
    switch (from) {
      case Etape.CREATION:
        this.etude.description = this.description.value;
        this.etude.nom = this.name.value;
        this.etude.anneeRef = this.anneeDeRef.value;
        this.etude.anneeFin = this.anneeFinEtu.value;
        this.procurations = this.delegatedPplAsArray;
        this.errorMessage = null;
        if(this.etude.id != 0){
          this.updateEtude();
        } else {
          this.addEtude();
        }
        break;
      case Etape.ZONAGE:
        if(this.isEtapeProjectionPassed()){
          this.modifAssociationZonage();
        }
        else if (this.form2.get('referentiel').value === 'Issudureferentiel') {
          this.addAssociationZonage();
        }
        else if (this.form2.get('referentiel').value === 'personnalise') {
          this.addAssociationZonageImportValidation();
        }
        break;
      case Etape.POPULATION:
        this.addMultiplePopulations()
        break;
      case Etape.CONTRAINTES:
        this.addTracabiliteEtape(Etape.CONTRAINTES, this.contrainteEnvironnementaleComponent?.contraintesEtude?.length > 0 ? EtatEtape.VALIDE :  EtatEtape.VALIDE_VIDE);
        break;
      case Etape.CHANTIERS:
        this.addTracabiliteEtape(Etape.CHANTIERS, this.chantiersEnvergureComponent?.chantiersEtude?.length > 0 ? EtatEtape.VALIDE :  EtatEtape.VALIDE_VIDE);
        break;
      case Etape.INSTALLATIONS:
        this.addTracabiliteEtape(Etape.INSTALLATIONS, this.installationStockageComponent?.installationStockagesEtude?.length > 0 ? EtatEtape.VALIDE :  EtatEtape.VALIDE_VIDE);
        break;
      case Etape.MATERIAUX:
        this.addTracabiliteEtape(Etape.MATERIAUX, this.autresMateriauxComponent?.autresMateriaux?.length > 0 ? EtatEtape.VALIDE :  EtatEtape.VALIDE_VIDE);
        break;
    }
  }

  toStep(to: Etape){
    switch (to) {
      case Etape.CREATION:
        this.setEtapeZone();
        break;
      case Etape.ZONAGE:
        this.setEtapeZone();
        break;
      case Etape.POPULATION:
        this.setEtapeProjection();
        break;
      case Etape.CONTRAINTES:
        this.setEtapeContrainte();
        break;
      case Etape.CHANTIERS:
        this.setEtapeChantier();
        break;
      case Etape.INSTALLATIONS:
        this.setEtapeInstallations();
        break;
      case Etape.MATERIAUX:
        this.setEtapeMateriaux();
        break;
    }
    this.cdr.detectChanges();
  }

  resetCreationEtude() {
    this.form1.reset();
    this.form2.reset();
    this.referentiel = 'Issudureferentiel';

    this.delegatedPpl.clear();
    this.actualStep = Etape.CREATION;
    this.actualStepIndex = 0;
    this.displayZonageEvent.emit(undefined);
    this.displayZonageImportEvent.emit(undefined);
    this.boutonValideDisable = true;
  }

  isEtapeZoneValide(){
    return this.etude.etatEtapes[Etape.ZONAGE] === EtatEtape.VALIDE;
  }

  isEtapeProjectionPassed(){
    return this.etude.etatEtapes[Etape.POPULATION] === EtatEtape.VALIDE;
  }

  isLectureSeule(){
    return this.etude.readOnly || this.etude.ifPublic;
  }

  telechargerODS() {
    this.etudeService.telechargerODS(this.etude.id).subscribe(data => {
      const blob = new Blob([data], { type: 'application/vnd.oasis.opendocument.text' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `modele_population.ods`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  telechargerZonesGpkg() {
    this.etudeService.telechargerZonesGpkg().subscribe(data => {
      const blob = new Blob([data], { type: 'application/geopackage+sqlite3' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `zones.gpkg`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }

  onUploadFile(event: any, fileuploadform: any) {
      // Récupération du fichier
      this.boutonImportDisable = true;
      this.errorMessage = null;
      if (event.currentFiles.length > 0) {
        const file: File = event.currentFiles[0];
        // Vérification si 'file' est défini
        if (file) {
          // Appel de la méthode uploadFile du service
          this.overlayService.overlayOpen("Import en cours...");
          this.boutonValideDisable = true;
          this.etudeService.uploadFile(this.etude.id, file)
            .subscribe({
              next: (response) => {
                const listPopulation: PopulationDTO[] = response as PopulationDTO[];
                this.displayPopulationEvent.emit({ listPopulation , selectedYear: this.selectedYear });
                this.listPopulation = listPopulation;
                this.etude.populations = listPopulation;
                this.etude.etatEtapes[Etape.POPULATION] = EtatEtape.IMPORTE;
                this.computeValideStep();
              },
              error: (error) => {
                this.succesMessage = "";
                this.errorMessage = error.error;
                this.boutonImportDisable = false;
                fileuploadform.clear();
                this.overlayService.overlayClose();
              },
              complete: () => {
                this.succesMessage = "Import finalisé";
                this.boutonImportDisable = false;
                fileuploadform.clear();
                this.boutonValideDisable = false;
                this.overlayService.overlayClose();
              }
            });
        }
      }
  }

  private getYears(): { anneeDeRef: number , anneeFin: number } {
    const anneeDeRefvalue = this.form1.get('anneeDeRef').value;
    const anneeDeRef = anneeDeRefvalue ? new Date(anneeDeRefvalue).getFullYear() : 2099;
    const anneeFinEtuvalue = this.form1.get('anneeFinEtu').value;
    const anneeFin = anneeFinEtuvalue ? new Date(anneeFinEtuvalue).getFullYear() : 2199;

    return { anneeDeRef, anneeFin };
  }

  private validateDates(): void {
    const { anneeDeRef, anneeFin } = this.getYears();

    // Si this.isDreal est true, sélectionnez l'année dans la dropdown
    if (this.isDreal && anneeFin == 2199) {
        const selectedYear = (anneeDeRef + 12).toString();
        this.form1.get('anneeFinEtu').patchValue(selectedYear);
    }

    if (anneeFin && anneeDeRef && anneeFin <= anneeDeRef) {
        this.form1.get('anneeFinEtu').setValue("");
    }
  }

  private validateDatesFinEtude(): void {
    const { anneeDeRef, anneeFin } = this.getYears();

    if (anneeFin && anneeDeRef && anneeFin <= anneeDeRef) {
      this.form1.get('anneeFinEtu').setValue("");
    }
    if (anneeFin && anneeFin < this.selectedYear) {
      this.selectedYear = anneeFin;
    }
  }

  handleYearChanged(year: number | string): void {
    if ( typeof year === 'string') {
      year = Number(year as string);
    }
    this.validateDates();
    this.selectedYear = year;
    this.selectedYearEtudeEvent.emit(year);
  }

  onEndDateSelected(): void {
      this.validateDatesFinEtude();
  }

  private truncateString(str: String, num:number) {
    if (str.length > num) {
      return str.slice(0, num);
    } else {
      return str;
    }
  }
  onCollapseChange(value: boolean) {
    this.visibleAssocierZonagesEtudeSideBar = !value;
  }

  clearSubscription(){
    this.contrainteService.reset();
    this.installationStockageService.reset();
  }

  onOpen(event:any){
    this.computeValideStep();
    this.clearSubscription();
    this.errorMessage = "";
    this.succesMessage = "";
    this.messageService.clear();

    switch (event.index) {
      case 0:
        this.setEtapeGeneralite();
        break;
      case 1:
        this.territoireChanged = false;
        if(this.actualStep === Etape.ZONAGE){
          break;
        }
        this.layersService.reset(true);
        if(this.etude.ifSrc || this.usersActif.libelle_profil === 'DREAL'){
          let id_region = (this.etude.id != undefined && this.etude.ifSrc) ? this.etude.proprietaire.id_region : this.usersActif.id_region;
          this.layersService.onRefRegionLayersChange(id_region);
        } else if(this.etude.territoire){
          this.setDonneeZone();
          this.layersService.displayEtudeTerritoireLayer(this.etude.territoire);
          this.layersService.onFlyToLayerEvent('EtudeTerritoire');
        }
        this.displayZonageEvent.emit(this.getZonageEtude());
        this.displayEtablissementEtudeEvent.emit(this.etude.id);
        break;
      case 2:
        if(this.actualStep === Etape.ZONAGE){
          break;
        }
        this.setEtapeProjection();
        break;
      case 3:
        this.setEtapeContrainte();
        break;
      case 4:
        this.setEtapeChantier();
        break;
      case 5:
        this.setEtapeInstallations();
        break;
      case 6:
        this.setEtapeMateriaux();
        break;
    }
  }

  computeValideStep() {
    const e : any  = this.etude.etatEtapes;
    let i=0;
    for (const value of this.enumKeys(Etape)) {
      if (e[Etape[value]] !== EtatEtape.VALIDE && e[Etape[value]] !== EtatEtape.VALIDE_VIDE) {
        this.actualStep = Etape[value];
        this.actualStepIndex= i;
        break;
      }
      i++;
    }
  }

  enumKeys<O extends object, K extends keyof O = keyof O>(obj: O): K[] {
    return Object.keys(obj).filter(k => Number.isNaN(+k)) as K[];
  }

  isStepStatePassedOrActive(etape: Etape) {
    if(this.isEtapeScenario()) return {'statePassed': true};
    const e : any = this.etude.etatEtapes;
    if (etape.valueOf() === this.actualStep.valueOf()) {
      return {'stateActive': true};
    }
    return {'statePassed': e[etape] === EtatEtape.VALIDE.valueOf() || e[etape] === EtatEtape.VALIDE_VIDE.valueOf()};

  }

  isStepStatePassed(etape: Etape) {
    if(this.isEtapeScenario()) return true;
    const e : any = this.etude.etatEtapes;
    return  e[etape] === EtatEtape.VALIDE.valueOf() || e[etape] === EtatEtape.VALIDE_VIDE.valueOf();

  }

  isStepDisabled(etape: Etape) {
    if (etape.valueOf() === this.actualStep.valueOf()) return false;
    for (const value in Etape) {
     if (value === Etape[this.actualStep]) return true;
     if (value === Etape[etape])  return false;
    }
    return true;
  }

  isStepActive(etape: Etape) {
    if(etape.valueOf() === this.actualStep.valueOf()) return true;
    return false;
  }

  updateActualStep(event: Etape) {
    this.actualStep = event;
  }

  ngOnDestroy(): void {
  }

  sortedListString(list:string[]){
    return list.sort((a:string,b:string)=> {return (a).localeCompare(b);});
  }

  clearZonageImportPerso(){
    if(this.etude.id !== 0 && this.etude.etatEtapes[Etape.ZONAGE] !== EtatEtape.VALIDE){
      this.territoireService.deleteTerritoire(this.etude.id).subscribe({
        next:(value:any) => console.log("Territoire supprimer")
      })
    }
  }

  // Getter and Setter
  get name() {
    return this.form1.get('name');
  }

  set name(val) {
    this.form1.get('name').setValue(val);
  }

  get description() {
    return this.form1.get('description');
  }

  set description(val) {
    this.form1.get('description').setValue(val);
  }

  get isEtudeSRC() {
    return this.form1.get('isEtudeSRC');
  }

  set isEtudeSRC(val) {
    this.form1.get('isEtudeSRC').setValue(val);
  }

  get anneeDeRef() {
    return this.form1.get('anneeDeRef');
  }

  get anneeFinEtu() {
    return this.form1.get('anneeFinEtu');
  }

  get anneeFinEtuValue(): number {
    const value = this.form1.get('anneeFinEtu').value;
    const anneeFin = value ? new Date(value).getFullYear() : 2099;
    return anneeFin;
  }

  get anneeDeRefValue(): number {
    const value = this.form1.get('anneeDeRef').value;
    const anneeDeRef = value ? new Date(value).getFullYear() : 2099;
    return anneeDeRef;
  }

  get delegatedPpl() {
    return this.form1.get('delegatedPpl') as FormArray;
  }

  get delegatedPplAsArray() {
    return this.form1.get('delegatedPpl');
  }

  get referentiel() {
    return this.form2.get('referentiel');
  }

  set referentiel(val) {
    this.form2.get('referentiel').setValue(val);
  }

  get typeTerritoire() {
    return this.form2.get('issudureferentiel.territoires');
  }

  set typeTerritoire(val) {
    this.form2.get('issudureferentiel.territoires').setValue(val);
  }

  rowspanTD(etude:Etude){
    if(!['GEST','ADMIN'].includes(this.usersActif.libelle_profil)){
      return false;
    }
    if(etude.readOnly || etude.ifPublic){
      return false;
    }
    return true;
  }
}

