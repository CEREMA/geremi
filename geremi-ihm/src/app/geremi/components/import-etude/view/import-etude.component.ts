import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { AbstractControl, FormArray, FormBuilder, Validators } from "@angular/forms";
import { Users } from "src/app/shared/entities/users.model";
import { PopulationDTO } from "../../etude/model/population.model";
import { Zonage } from "../../etude/model/zonage.model";
import { EtablissementService } from "../../carto/service/etablissement.service";
import { UserService } from "../../etude/service/user.service";
import { UsersService } from "src/app/shared/service/users.service";
import { Message } from "primeng/api";
import { ImportEtudeService } from "../service/import-etude.service";
import { Etude } from "../../etude/model/etude.model";
import { switchMap } from "rxjs";
import { RelEtudeUserProcuration } from "../../etude/model/reletudeuserprocuration.model";
import { RelEtudeUserProcurationService } from "../../etude/service/reletudeuserprocuration.service";
import { OverlayService } from "../../overlay/service/overlay.service";
import { ImportEtudeDTO } from "../model/import-etude.model";
import { Scenario } from "../../etude/scenario/model/scenario.model";
import { Territoire } from "../../etude/model/territoire.model";
import {environment} from "../../../../../environments/environment";
import {TooltipOptions} from "primeng/tooltip";

@Component({
    selector: 'import-etude',
    templateUrl: './import-etude.component.html',
    styleUrls: ['./import-etude.component.scss']
})
export class ImportEtudeComponent implements OnInit {

  @Output() openSideBarEvent = new EventEmitter<boolean>();
  @Output() layerTerritoireEvent = new EventEmitter<string>();
  @Output() layerZonageEvent = new EventEmitter<string>();
  @Output() displayZonagePersoEvent = new EventEmitter<number | undefined>();
  @Output() displayZonageEvent = new EventEmitter<Zonage>();
  @Output() displayZonageImportEvent = new EventEmitter<Zonage>();
  @Output() displayPopulationEvent = new EventEmitter<{ listPopulation: PopulationDTO[]; selectedYear: number}>();
  @Output() selectedYearEtudeEvent = new EventEmitter<number>();
  @Output() displayEtablissementEtudeEvent = new EventEmitter<number>();
  @Input() selectedYear: number;
  etude = new Etude();
  form1: any;
  anneeRef: string[] = [];
  anneeFin: string[] = [];
  delegateInput: any;
  proprietaireInput: any;
  proprietaire: Users;
  results: Users[];
  proprietaireResults: Users[];
  visibleAfficherImportEtudeSideBar: any = false;
  visibleAfficherEtudeSideBar: any = false;
  visibleAjoutEtudeSideBar: any = false;
  visibleAssocierZonagesEtudeSideBar: any = false;
  usersActif!: Users;
  messageToolTipDownload: String;
  tooltipOptionsMessage : TooltipOptions = {tooltipStyleClass:'tooltip-import-message'};
  messageErreurImport: Message[];
  messageInformationImport: Message[];
  messageInformationImportTemp: Message[];
  allSelected: boolean = true;
  private filesToImport: any;

  maxFileSize = environment.maxFileUplaodSize;

  constructor(
    private relEtudeUserProcurationService: RelEtudeUserProcurationService,
    private formBuilder: FormBuilder,
    private etablissementService: EtablissementService,
    private userService: UserService,
    private usersService: UsersService,
    private overlayService: OverlayService,
    private importEtudeService: ImportEtudeService) {
  }

  ngOnInit(): void {
    this.form1 = this.formBuilder.group({
        name: ['', [Validators.required, Validators.minLength(1)]],
        description: ['', null],
        isSRC: [false, null],
        anneeDeRef: ['', [Validators.required]],
        anneeFinEtu: ['', [Validators.required]],
        nomterritoire: ['', [Validators.required]],
        descterritoire: ['', null],
        nomscenario: ['', [Validators.required]],
        descscenario: ['', null],
        proprietaire: ['', [Validators.required]],
        delegatedPpl: this.formBuilder.array([])
    });
    this.usersActif = this.usersService.currentUsers;
    this.getAnnees();
    this.messageToolTipDownload = `<span class="pi pi-info-circle"></span>
                        Tous les résultats sont à exprimer en kt `;

  }

  openCloseSideBar(open : boolean){
    this.visibleAfficherImportEtudeSideBar = open;
  }

  closeImportEtudeSideBar() {
    this.visibleAfficherImportEtudeSideBar = false;
  }

  sortedListString(list: string[]) {
    return list.sort((a: string, b: string) => { return (a).localeCompare(b); });
  }

  handleYearChanged(year: number): void {
    this.validateDates();
    this.selectedYear = year;
    this.selectedYearEtudeEvent.emit(year);
  }

  onEndDateSelected(): void {
    this.validateDatesFinEtude();
  }

  private searchUsers(query: string, callback: (results: any[]) => void): void {
    this.userService.getUsersDelegation().subscribe(users => {
      let filteredUsers = users.filter(user => user.mail.toLowerCase().indexOf(query.toLowerCase()) !== -1);
      filteredUsers = filteredUsers.filter(user => {
        for(let ppl of this.delegatedPpl.value){
          if(user.mail === ppl.mail){
            return false;
          }
        }
        return true;
      });
      callback(filteredUsers);
    });
  }

  search(event: any) {
    this.searchUsers(event.query, (filteredUsers) => {
      this.results = filteredUsers;
    });
  }

  searchProprietaire(event: any) {
    this.searchUsers(event.query, (filteredUsers) => {
      this.proprietaireResults = filteredUsers;
    });
  }

  onSelectUser(event: Users) {
    this.proprietaireInput = event.nom + " " + event.prenom;
    this.proprietaire = event;
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

  importEtude(event: any, fileuploadform: any) {
    this.filesToImport = event.files;
    this.messageErreurImport = [];
    fileuploadform.clear();
    this.etude.nom = this.form1.get('name').value;
    this.etude.description = this.form1.get('description').value;
    this.etude.ifSrc = this.form1.get('isSRC').value;
    this.etude.anneeRef = this.form1.get('anneeDeRef').value;
    this.etude.anneeFin = this.form1.get('anneeFinEtu').value;
    this.etude.proprietaire = this.proprietaire;
    if (event.files.length === 0) {
      this.showError("Aucun fichier sélectionné.");
      return;
    }

    this.etude.mandataires = [];
    this.delegatedPpl.controls.forEach(control => {
      let procuration = control.value.user
      procuration.if_droit_ecriture = control.value.isSelected;
      this.etude.mandataires.push(procuration)
      console.log(procuration)
    });

    let territoire = new Territoire();
    territoire.nom = this.form1.get('nomterritoire').value;
    territoire.description = this.form1.get('descterritoire').value;
    this.etude.territoire = territoire;

    let scenario = new Scenario(0, this.etude);
    scenario.nom = this.form1.get('nomscenario').value;
    scenario.description = this.form1.get('descscenario').value;
    scenario.etudeDTO = this.etude;

    let importEtudeDTO : ImportEtudeDTO = new ImportEtudeDTO();
    importEtudeDTO.etude = this.etude;
    importEtudeDTO.scenario = scenario;

    this.overlayService.overlayOpen("Import en cours ...");
    this.importEtudeService.importEtudeFiles(event.files, importEtudeDTO).subscribe({
      next : (response:any) => {
        this.messageInformationImportTemp = [];
        if (Array.isArray(response)) {
          for (const message of response) {
            this.messageInformationImportTemp = [{ severity: 'info', detail: message }];
          }
        }
      },
      error : (error) => {
        this.showError(error.error);
        this.overlayService.overlayClose();
      },
      complete : () => {
        this.showSuccess();
        this.overlayService.overlayClose();
      }
  });
  }

  showError(message: string) {
    this.messageInformationImport = [];
    this.messageErreurImport = [{ severity: 'error', detail: message }];
    setTimeout(() => {
      this.messageErreurImport = [];
    }, 5000);
  }

  showSuccess() {
    if (this.messageInformationImportTemp && this.messageInformationImportTemp.length > 0) {
      setTimeout(() => {
        this.messageInformationImport = [{ severity: 'success', detail: "Etude importée avec succès." }];
        setTimeout(() => {
          this.messageInformationImport = [];
        }, 5000);
      }, 5000);
    } else {
      this.messageInformationImport = [{ severity: 'success', detail: "Etude importée avec succès." }];
      setTimeout(() => {
        this.messageInformationImport = [];
      }, 5000);
    }
  }

  addDelegation(event: any) {
    let user = event as Users;
    const delegateForm = this.formBuilder.group({
      id: [user.id_user],
      isSelected: [user.isSelected?user.isSelected:false],
      mail: [user.mail],
      nom: [user.nom],
      prenom: [user.prenom],
      user: [user]
    });
    this.delegatedPpl.push(delegateForm);
    this.delegateInput = null;
  }

  get delegatedPpl() {
    return this.form1.get('delegatedPpl') as FormArray;
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

  private validateDates(): void {
    const { anneeDeRef, anneeFin } = this.getYears();
    // Si this.isDreal est true, sélectionnez l'année dans la dropdown
    if (this.form1.get('isSRC').value && anneeDeRef) {
        const selectedYear = (anneeDeRef + 12).toString();
        this.form1.get('anneeFinEtu').patchValue(selectedYear);
    }
    if (anneeFin && anneeDeRef && anneeFin <= anneeDeRef) {
        this.form1.get('anneeFinEtu').setValue("");
    }
  }

  private getYears(): { anneeDeRef: number , anneeFin: number } {
    const anneeDeRefvalue = this.form1.get('anneeDeRef').value;
    const anneeDeRef = anneeDeRefvalue ? new Date(anneeDeRefvalue).getFullYear() : 2099;
    const anneeFinEtuvalue = this.form1.get('anneeFinEtu').value;
    const anneeFin = anneeFinEtuvalue ? new Date(anneeFinEtuvalue).getFullYear() : 2099;
    return { anneeDeRef, anneeFin };
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

  telechargerModele() {
    console.log("Téléchargement du modèle.");
    this.importEtudeService.telechargerImportEtudeGpkg().subscribe(data => {
      const blob = new Blob([data], { type: 'application/geopackage+sqlite3' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `import_etude.gpkg`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  }
}
