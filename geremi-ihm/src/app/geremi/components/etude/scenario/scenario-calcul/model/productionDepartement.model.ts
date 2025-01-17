import { ProductionZone } from "./productionZone.model";

export class ProductionDepartement {
    idEtude:Number;
	idScenario:Number;
	idDepartement:Number;
	nom:String;
	anneeRef:Number;
	productionDepartementBeton:Number;
	productionDepartementViab:Number;

	listProductionZoneDto:ProductionZone[];
}