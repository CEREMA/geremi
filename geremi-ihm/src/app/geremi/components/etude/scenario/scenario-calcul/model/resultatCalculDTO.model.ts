export class ResultatCalculDTO {
  idEtude:Number;
  idScenario:Number;
  annee:Number;
  
  besoinTerritoireTotalChantier:Number;
  besoinTerritoirePrimaire:Number;
  besoinTerritoireSecondaire:Number;
  besoinTerritoireTotal:Number;

  productionTerritoirePrimaireTotal:Number;
  productionTerritoirePrimaireIntra:Number;
  productionTerritoirePrimaireBrute:Number;
  productionTerritoireSecondaireTotal:Number;
  productionTerritoireTotal:Number;

  pourcentProductionTerritoireSecondaire:Number;

  resultatZones:any;
}