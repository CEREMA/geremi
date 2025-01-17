import { Injectable } from "@angular/core";
import {Feature, FeatureCollection, Point} from "geojson";
import L, {LatLng} from "leaflet";
import { EtablissementService } from "./etablissement.service";
import { PanneauLateralComponent } from "../../panneau-lateral/view/panneaulateral.component";
import { ScenarioContrainteService } from "../../etude/scenario/scenario-contrainte/service/scenario-contrainte.service";

@Injectable({
    providedIn: 'root'
})
export class MarkerUtilsService {

	private tooltipConfig: Array<string>;
	private tooltipLibelleConfig: any;

	private markerClicked: any;

	constructor(private etablissementService : EtablissementService, private scenarioContrainteService:ScenarioContrainteService){
		this.etablissementService.findEtablissementTooltipConfig().subscribe(config => {
			this.setTooltipConfig(config)
		})

		this.etablissementService.findEtablissementDetailConfig().subscribe(config => {
			this.setTooltipLibelleConfig(config)
		})
	}

	createLayerGroupMarker(markers: FeatureCollection, map: L.Map, panneauLateralComponent: PanneauLateralComponent, selectedYear: number, afficherTx:boolean):  any {
		let layerGroup = L.layerGroup();
		for (let entity of markers.features.filter(f=> map.getBounds().contains(new LatLng((f.geometry as Point).coordinates[1],(f.geometry as Point).coordinates[0])))) {
		  L.geoJSON(entity, {
			pointToLayer: (feature, latLng) => {
				return this.getDefaultMarker(feature, map.getZoom(), latLng, selectedYear)
					.on('click', (event) => {
					  // Vérifie s'il y un ancien bouton cliqué
					  if (this.markerClicked != undefined) {
						this.markerClicked.setIcon(this.getDefaultIcon(this.markerClicked.feature, map.getZoom(),selectedYear));
					  }
					  this.markerClicked = event.target;
					  event.target.setIcon(this.getClickedIcon(event.target.feature, map.getZoom(),selectedYear));
					  panneauLateralComponent.visiblePanneauLateral = true;
					  panneauLateralComponent.feature = event.target.feature;
					  panneauLateralComponent.carriere = event.sourceTarget;
					  panneauLateralComponent.forceRerender();
					});
			},
		  })

		  // Ajout dans le layer
		  .addTo(layerGroup);
		}

		if(afficherTx){
			layerGroup.eachLayer((featureGroup: any) => {
				featureGroup.eachLayer((layer: any) => {
					this.scenarioContrainteService.scenarioContrainteTauxRenouvellementSource$.subscribe(contraintes => {
						for(let value of contraintes){
							if(value.id === 0){
								if(value.tx_renouvellement_contrainte === null || value.tx_renouvellement_contrainte === undefined){
									layer.feature.properties.tx_renouvellement = undefined;
								}
								else {
									layer.feature.properties.tx_renouvellement = value.tx_renouvellement_contrainte;
								}
							}
							if(layer.feature.properties.contrainte_environnementale !== undefined && layer.feature.properties.contrainte_environnementale.includes(value.id)){
								if(value.tx_renouvellement_contrainte === null || value.tx_renouvellement_contrainte === undefined){
									layer.feature.properties.tx_renouvellement = undefined;
								}
								else if(layer.feature.properties.tx_renouvellement !== undefined && layer.feature.properties.tx_renouvellement > value.tx_renouvellement_contrainte){
									layer.feature.properties.tx_renouvellement = value.tx_renouvellement_contrainte;
								}
								else if(layer.feature.properties.tx_renouvellement === undefined){
									layer.feature.properties.tx_renouvellement = value.tx_renouvellement_contrainte;
								}
							}
							this.hideIcon(layer,selectedYear);
						}
						layer.bindTooltip(this.getContentToolTip(layer.feature))
					})
				})
			})
		}
		return layerGroup;
	}

	hideIcon(layer:any,selectedYear:number){
		if(layer._icon !== null){
			if(layer.feature.properties.tx_renouvellement === 0 && layer.feature.properties!.date_fin_autorisation != null){
				const dateStr = layer.feature.properties!.date_fin_autorisation;
				const [ day,month, year] = dateStr.split('/');
				const dateFinAuth = new Date(+year, +month - 1, +day);
				if (dateFinAuth < new Date(selectedYear, 0, 1)) {
					layer._icon.style.display = 'none';
				}
			} else {
				layer._icon.style.display = 'block';
			}
		}
	}

	setTooltipConfig(config: Array<string>){
		this.tooltipConfig = config;
	}

	setTooltipLibelleConfig(config: any){
		this.tooltipLibelleConfig = config;
	}

	getScaleMarker(zoom: number) {
        let number = 0.2
        if(zoom > 15){
            number = 1
        } else if(zoom > 13){
            number = 0.9
        } else if(zoom > 11){
            number = 0.7
        } else if(zoom > 9){
            number = 0.6
        } else if(zoom > 8){
            number = 0.5
        } else if(zoom > 6){
            number = 0.3
        }
        return number;
    };

  computeIconShape(feature:Feature) {
    if (feature.properties!.origin_mat === "Matériaux naturels") {
      return `<polygon points="15,0 30,30 0,30" style="fill:{mapIconColor};" />`
    } else if (feature.properties!.origin_mat === "Matériaux recyclés") {
      return `<circle cx="15" cy="15" r="15" style="fill:{mapIconColor};" />`
    } else {
      return `<polygon points="0,0 30,0 30,30 0,30" style="fill:{mapIconColor};" />`
    }
  }

  computeIconColor(feature:Feature, clicked:boolean, selectedYear: number) {
    let baseColor: string;
    let gradientMin: string = "500";
    let gradientMax: string = "800";
    if (feature.properties!.origin_mat === 'Matériaux naturels') {
      baseColor = 'purple';
    } else if (feature.properties!.origin_mat === 'Matériaux recyclés') {
      baseColor = 'green';
    } else {
      baseColor = 'blue';
    }
    //override en gris si la date de fin authorisation est dépassée
    if(feature.properties!.date_fin_autorisation != null) {
      const dateStr = feature.properties!.date_fin_autorisation;
      const [ day,month, year] = dateStr.split('/');

      const dateFinAuth = new Date(+year, +month - 1, +day);
      if (dateFinAuth < new Date(selectedYear, 0, 1)) {
        baseColor = 'bluegray';
        gradientMin = "300";
        gradientMax = "600";
      }


    }
    return "var(--".concat( baseColor , "-" , clicked?gradientMax:gradientMin , ")");
  }

	getIconSettings(feature:Feature, zoomLevel:number, selectedYear: number){
		if(zoomLevel >= 12){
			return {
				mapIconUrl: `<div class="div-marker">
							<svg height="30" width="30" style="scale:{scaleZoom};">`
                +this.computeIconShape(feature) +
             `</svg>
							<p class="marker-etiquette">` + feature.properties!.nom_etablissement + '<p> </div>',
				mapIconColor: this.computeIconColor(feature, false, selectedYear),
				scaleZoom: this.getScaleMarker(zoomLevel),
				style:'scale:{scaleZoom};'
			};
		}
		return {
			mapIconUrl: `<svg height="30" width="30" style="scale:{scaleZoom};">`
							+ this.computeIconShape(feature) +
						`</svg>`,
			mapIconColor: this.computeIconColor(feature, false, selectedYear),
			scaleZoom: this.getScaleMarker(zoomLevel),
			style:'scale:{scaleZoom};'
		};
	}

	getIconClickedSettings(feature:Feature, zoomLevel:number, selectedYear: number){
		if(zoomLevel >= 12){
			return {
				mapIconUrl: `<div class="div-marker">
							<svg height="30" width="30" style="scale:{scaleZoom};">`
                +this.computeIconShape(feature) +
             `</svg>
							<p class="marker-etiquette">` + feature.properties!.nom_etablissement + '<p> </div>',
				mapIconColor: this.computeIconColor(feature, true, selectedYear),
				scaleZoom: this.getScaleMarker(zoomLevel),
				style:'scale:{scaleZoom};'
			};
		}
		return {
			mapIconUrl: `<svg height="30" width="30" style="scale:{scaleZoom};">`
                    +this.computeIconShape(feature) +
						      `</svg>`,
			mapIconColor: this.computeIconColor(feature, true, selectedYear),
			scaleZoom: this.getScaleMarker(zoomLevel),
			style:'scale:{scaleZoom};'
		};
	}

	getDefaultIcon(feature:Feature, zoomLevel:number, selectedYear: number){
		return new L.DivIcon({
			iconAnchor  : [12, 14],
 			iconSize    : [0, 0],
			className: "leaflet-data-marker",
			html: L.Util.template(this.getIconSettings(feature, zoomLevel, selectedYear).mapIconUrl, this.getIconSettings(feature, zoomLevel, selectedYear)),
		})
	}

	getClickedIcon(feature:Feature, zoomLevel:number, selectedYear: number){
		return new L.DivIcon({
			iconAnchor  : [12, 14],
 			iconSize    : [0, 0],
			className: "leaflet-data-marker",
			html: L.Util.template(this.getIconClickedSettings(feature, zoomLevel, selectedYear).mapIconUrl, this.getIconClickedSettings(feature, zoomLevel, selectedYear)),
		})
	}

	private getDefaultMarker(feature: Feature, zoomLevel:number, latLng: L.LatLng, selectedYear: number): L.Marker {
		let marker = L.marker(latLng,{
			icon: this.getDefaultIcon(feature, zoomLevel, selectedYear),
		})
		// Ajout du nom en Popup
		.bindTooltip(this.getToolTip(feature,latLng),{className:"marker-tooltip"});

		// Verifie qu'un marker n'a pas été cliqué avant
		if(this.markerClicked != undefined && this.markerClicked.feature?.id === feature.id && this.markerClicked.feature?.properties!['origin_mat']! === feature.properties!['origin_mat']!){
			marker.setIcon(this.getClickedIcon(feature, zoomLevel, selectedYear));
			this.markerClicked = marker;
		}

		return marker;
	}

	private getToolTip(feature: Feature, latLng: L.LatLng): L.Tooltip{
		return L.tooltip()
			.setLatLng(latLng)
			.setContent(this.getContentToolTip(feature));
	}

    private getContentToolTip(feature: Feature) : L.Content {
        let content = '<div class="tooltip-marker">';

        for (let prop of this.tooltipConfig){
			let value = feature.properties![prop];
			if(value == null) {
				value = '<i>Non renseigné</i>'
			}
      		let label = '';
      		if (this.tooltipLibelleConfig[prop] != null && this.tooltipLibelleConfig[prop]!['label'] != null) {
      		  	label = this.tooltipLibelleConfig[prop]!['label'];
      		}
			if((prop === 'tx_renouvellement' && feature.properties![prop] !== undefined)){
				content += '<li class="li-custom-properties"><p>' + label + ' ' + value + ' %</p></li>'
			}
			else if(feature.properties![prop] !== undefined){
				content += '<li class="li-custom-properties" *ngIf="feature.properties![prop] != undefined"><p>' + label + ' ' + value + '</p></li>'
			}

        }

        content += '</div>';
        return content;
    }

	resetMarker(){
		this.markerClicked = undefined;
	}

  getSelectedMarker() {
    return this.markerClicked?.feature
  }
}
