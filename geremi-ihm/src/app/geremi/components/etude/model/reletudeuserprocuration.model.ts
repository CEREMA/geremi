import { Etude } from "./etude.model";
import {Users} from "../../../../shared/entities/users.model";

export class RelEtudeUserProcuration {
  id: number = 0;
  etude: Etude;
  user: Users ;
  ifDroitEcriture: boolean = false;

  constructor() {
  }

}
