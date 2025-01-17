import {Point} from "geojson";
import {Destination} from "./Destination.model";
import {TraitementDechet} from "./TraitementDechet.model";

export interface  Etablissement {
  id_etab: number;
  code_etab: string;
  annee: number;
  nom_etablissement: string;
  siret: string;
  region: string;
  departement: string;
  libelle_adresse: string;
  code_insee_commune: string;
  libelle_commune: string;
  code_ape: string;
  activite_principale: string;
  long: string;
  lat: string;
  volume_production: number;
  unite: string;
  type_produit: string;
  nb_employe: number;
  site_internet: string;
  the_geom: Point;
  id_entreprise: number;
  service_inspection: string;
  if_carriere: boolean;
  if_quota: boolean;
  origin_mat: string;
  max_production_annuelle_autorisee: number;
  moy_production_annuelle_autorisee: number;
  date_fin_autorisation: string;
  type_carriere: string;
  date_debut: string;
  date_fin: string;
  date_maj: string;
  code_postal_site: string;
  destinations : Destination;
  traitement_dechet : TraitementDechet;

  }
