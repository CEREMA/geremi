import { Feature } from "geojson";

export class Chantier{
    id_chantier: number;
    nom: string;
    description: string;
    annee_debut: number;
    annee_fin: number;
    beton_pref: number;
    viab_autre: number;
    ton_tot: number;
    id_source: number;
    id_frere: number;

    libelle_pere: String;

    afficher: boolean;
    supprimable: boolean;

    feature: Feature;
}