
export interface  TraitementDechet {
  id_traitement_dechet: number;
  id_etablissement: number;
  annee: number;
  code_dechet: string;
  libelle_dechet: string;
  if_statut_sortie_dechet: boolean;
  departement_origine: string;
  pays_origine: string;
  quantite_admise: number;
  quantite_traitee: number;
  code_ope: string;
  libelle_ope: string;
  numero_notification: string;
  date_maj: Date;
}
