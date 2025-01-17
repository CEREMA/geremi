import {Users} from "../../../../shared/entities/users.model";
import {Territoire} from "./territoire.model";
import {PopulationDTO} from "./population.model";
import {Etape} from "../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../shared/enums/etatEtape.enums";

export class Etude {
  id: number;
  nom: string;
  description: string;
  dateCreation: Date;
  ifSrc: boolean = false;
  anneeRef: string;
  anneeFin: string;
  ifPublic: boolean;
  ifImporte: boolean;
  readOnly : boolean = false;
  scenarioRetenu: boolean = false;

  proprietaire: Users;

  mandataires: Users[];

  territoire: Territoire;

  populations: PopulationDTO[];

  etatEtapes: any;

  constructor() {
    this.id = 0;
    this.nom = '';
    this.description = '';
    this.dateCreation = new Date(Date.now());
    this.ifSrc = false;
    this.ifPublic = false;
    this.ifImporte = false;
    this.scenarioRetenu = false;
    this.anneeRef = '';
    this.anneeFin = '';
    this.etatEtapes = {};
    for (const value of  this.enumKeys(Etape)) {
      this.etatEtapes[value] = EtatEtape.NON_RENSEIGNE;
    }
  }

  enumKeys<O extends object, K extends keyof O = keyof O>(obj: O): K[] {
    return Object.keys(obj).filter(k => Number.isNaN(+k)) as K[];
  }


}
