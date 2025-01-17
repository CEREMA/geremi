import { Injectable } from "@angular/core";
import { Feature } from "geojson";
import L, { LatLng, LatLngExpression, Marker } from "leaflet";
import { StyleLayersUtilsService } from "./styleLayersUtils.service";
import { ScenarioMateriau } from "../../etude/scenario/scenario-materiau/model/scenario-materiau.model";
import { Scenario } from "../../etude/scenario/model/scenario.model";
import { GeometryUtilsService } from "./geometryUtils.service";

@Injectable()
export class CartoUtilsService {

  constructor(private styleLayersUtilsService: StyleLayersUtilsService, private geometryUtils:GeometryUtilsService) { }

  /**
   * Retourne le layerGeoJson d'une feature avec le style correspondant au type
   * @param feature Feature
   * @param type String, type du feature
   * @returns le layer
   */
  createGeoJson(feature: Feature, type: String) {
    return L.geoJSON(feature, {
      style: this.styleLayersUtilsService.getStyleLayer(type),
      pane: type.toString()
    })
  }

  createGeoJsonCustom(feature: Feature, type: String, typeStyle: String, annee:Number) {
    let style = this.styleLayersUtilsService.getStyleLayer(typeStyle);
    return L.geoJSON(feature,
      {
        pointToLayer: (feature, latlng) => {
          // Ici, on utilise les options de style retournées par getStyleLayer pour styliser le cercle
          if(!(annee >= feature.properties.annee_debut && annee <= feature.properties.annee_fin)){
            style.color = 'red';
          }

          // Retourner un cercle au lieu du marqueur par défaut
          return L.circleMarker(latlng, {
            color: style.color,
            fillColor: style.fillColor,
            fillOpacity: style.fillOpacity,
            radius: 6, // Définir le rayon du cercle
            weight: style.weight,
            pane: 'markerPane'
          });
        },
        onEachFeature(feature,layer:any){
          // annee_debut !== undefined pour vérifier si on ne se trouve pas dans une contrainte
          if(feature.properties.annee_debut !== undefined && !(annee >= feature.properties.annee_debut && annee <= feature.properties.annee_fin)){
            layer.setStyle({color : 'red'});
          }
        },
        style: style,
        pane: type.toString()
      })
  }

  createGeoJsonCustomCercle(feature: Feature, type: String, typeStyle: String) {
    return L.geoJSON(feature, {
      pointToLayer: (feature, latlng) => {
        // Ici, on utilise les options de style retournées par getStyleLayer pour styliser le cercle
        let style = this.styleLayersUtilsService.getStyleLayer(typeStyle);

        // Retourner un cercle au lieu du marqueur par défaut
        return L.circleMarker(latlng, {
          color: style.color,
          fillColor: style.fillColor,
          fillOpacity: style.fillOpacity,
          radius: 10, // Définir le rayon du cercle
          weight: style.weight,
          pane: 'markerPane'
        });
      },
      pane: type.toString()
    });
  }

  /**
   * Retourne le layerGeoJson d'une feature avec le style correspondant au type
   * @param feature Feature
   * @param type String, type du feature
   * @returns le layer
   */
  createGeoJsonOut(feature: Feature, type: String) {
    return L.geoJSON(feature, {
      style: this.styleLayersUtilsService.getStyleLayerOut(type),
      pane: 'zone_exterieur'
    })
  }

  /**
   * Retourne le layerGeoJson d'une feature avec le style correspondant au type
   * @param features Feature[], liste des features cliqués
   * @param feature Feature
   * @param type String, type du feature
   * @returns le layer
   */
  createGeoJsonClick(features: Feature[], feature: Feature, type: String) {
    return L.geoJSON(feature, {
      style: this.styleLayersUtilsService.getStyleClicked(features, feature, type),
      pane: type.toString()
    });
  }

  /**
   * Retourne un layerGroup comprenant les features avec le style du type de la feature
   * @param features Feature[]
   * @param type String, type des features
   * @returns le layerGroup
   */
  addGeoJsonLayers(features: Feature[], type: String) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      this.createGeoJson(feature, type)
        // Ajout du nom en Popup
        .bindPopup(feature.properties!.nom)
        // Ajout sur la map
        .addTo(layerGroup);
    }
    return layerGroup;
  }

  /**
   * Retourne un layerGroup comprenant les features avec le style du type de la feature
   * @param features Feature[]
   * @param type String, type des features
   * @returns le layerGroup
   */
  addGeoJsonContrainteLayers(features: Feature[], map: L.Map) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      let typeContrainte = 'contrainte' + feature.properties?.niveau.toString().toLowerCase();
      let typeStyle = typeContrainte;
      if (feature.properties?.afficher) {
        typeStyle += '-affiche'
      } else {
        typeStyle += '-cache'
      }
      this.createGeoJsonCustom(feature, typeContrainte, typeStyle, 0)
        // Ajout du nom en Popup
        .on('mouseover', (event: any) => {
          map.openPopup(
            L.popup({ closeButton: false, autoPan: false })
              .setLatLng(event.latlng)
              .setContent(feature.properties!.nom + ' - niveau : ' + feature.properties?.niveau)
              .openOn(map));
        })
        .on('mouseout', (event: any) => {
          map.closePopup();
        })
        // Ajout sur la map
        .addTo(layerGroup);
    }
    return layerGroup;
  }

  /**
    * Retourne un layerGroup comprenant les features avec le style du type de la feature
    * @param features Feature[]
    * @param type String, type des features
    * @returns le layerGroup
    */
  addGeoJsonChantierLayers(features: Feature[], map: L.Map, annee: Number) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      let typeChantier = 'chantier';
      let typeStyle = typeChantier;
      if (feature.properties?.afficher) {
        typeStyle += '-affiche'
      } else {
        typeStyle += '-cache'
      }
      this.createGeoJsonCustom(feature, typeChantier, typeStyle, annee)
        .on('mouseover', (event: any) => {
          map.openPopup(
            L.popup({ closeButton: false, autoPan: false })
              .setLatLng(event.latlng)
              .setContent(`${feature.properties!.nom} - ${feature.properties!.annee_debut}/${feature.properties!.annee_fin}<br>
              Besoin béton/préfabriqué : ${feature.properties!.beton_pref} kt <br>
              Besoin viabilité/autre: ${feature.properties!.viab_autre} kt <br>
              Total : ${feature.properties!.ton_tot} kt`)
              .openOn(map));
        })
        .on('mouseout', (event: any) => {
          map.closePopup();
        })
        // Ajout sur la map
        .addTo(layerGroup);
    }
    return layerGroup;
  }

  /**
    * Retourne un layerGroup comprenant les features avec le style du type de la feature
    * @param features Feature[]
    * @param type String, type des features
    * @returns le layerGroup
    */
  addGeoJsonInstallationStockageLayers(features: Feature[], map: L.Map) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      let typeInstallationStockage = 'stockage';
      let typeStyle = typeInstallationStockage;
      if (feature.properties?.afficher) {
        typeStyle += '-affiche';
      } else {
        typeStyle += '-cache';
      }
      this.createGeoJsonCustomCercle(feature, typeInstallationStockage, typeStyle)
        .on('mouseover', (event: any) => {
          map.openPopup(
            L.popup({ closeButton: false, autoPan: false })
              .setLatLng(event.latlng)
              .setContent(`${feature.properties!.nom_etab} - ${feature.properties?.code_etab ? ` ${feature.properties.code_etab} -` : ''} Fin : ${feature.properties!.annee_fin} -
              Stockage béton/préfabriqué : ${feature.properties!.beton_pref} kt -
              Stockage viabilité/autre: ${feature.properties!.viab_autre} kt -
              Total : ${feature.properties!.ton_tot} kt`)
              .openOn(map)
          );
        })
        .on('mouseout', (event: any) => {
          map.closePopup();
        })
        // Ajout sur la map
        .addTo(layerGroup);
    }
    return layerGroup;
  }

  /**
   * Retourne un layerGroup comprenant les features avec le style du type
   * de la feature qui n'est pas active selon la date Séléctionné
   *
   * @param features Feature[]
   * @param type String, type des features
   * @returns le layerGroup
   */
  addGeoJsonInstallationStockageNonActiveLayers(features: Feature[], map: L.Map) {
    let layerGroup = L.layerGroup();

    for (let feature of features) {
      let typeInstallationStockage = 'stockagenonactive';
      let typeStyle = typeInstallationStockage;
      typeStyle += '-affiche';

      let group = this.createGeoJsonCustomCercleNonActive(feature, typeInstallationStockage, typeStyle);
      group.on('mouseover', (event: any) => {
        map.openPopup(
          L.popup({ closeButton: false, autoPan: false })
            .setLatLng(event.latlng)
            .setContent(`${feature.properties!.nom_etab} - ${feature.properties?.code_etab ? ` ${feature.properties.code_etab}` : ''} - ${feature.properties!.annee_debut}/${feature.properties!.annee_fin}<br>
              Stockage béton/préfabriqué : ${feature.properties!.beton_pref} kt<br>
              Stockage viabilité/autre: ${feature.properties!.viab_autre} kt<br>
              Total : ${feature.properties!.ton_tot} kt`)
            .openOn(map)
        );
      })
      .on('mouseout', (event: any) => {
        map.closePopup();
      })
      .addTo(layerGroup);
    }

    return layerGroup;
  }

  createGeoJsonCustomCercleNonActive(feature: Feature, type: String, typeStyle: String) {
    return L.geoJSON(feature, {
        pointToLayer: (feature, latlng) => {
            let style = this.styleLayersUtilsService.getStyleLayer(typeStyle);

            let outerCircle = L.circleMarker(latlng, {
                color: style.color,
                fillColor: style.fillColor,
                fillOpacity: style.fillOpacity,
                radius: 16, // le plus grand rayon
                weight: style.weight,
            });

            let secondCircle = L.circleMarker(latlng, {
                color: '#FFFFFF', // couleur des "points" blancs
                fillColor: '#FFFFFF',
                fillOpacity: style.fillOpacity,
                radius: 12, // rayon intermédiaire
                weight: style.weight,
            });

            let middleCircle = L.circleMarker(latlng, {
                color: style.color,
                fillColor: style.fillColor,
                fillOpacity: style.fillOpacity,
                radius: 8, // rayon moyen
                weight: style.weight,
            });

            let innerCircle = L.circleMarker(latlng, {
                color: '#FFFFFF', // couleur des "points" blancs
                fillColor: '#FFFFFF',
                fillOpacity: style.fillOpacity,
                radius: 4, // plus petit rayon
                weight: style.weight,
            });

            let circleGroup = L.layerGroup([outerCircle, secondCircle, middleCircle, innerCircle]);

            return circleGroup;
        },
        pane: type.toString()
    });
}





  /**
   * Add layers on Map
   * @param map Map
   * @param layerGroup layerGroup à ajouté dans la map
   * @param data data
   * @param type type du layers
   * @returns le layergroup
   */
  addlayerOnMap(map: L.Map, layerGroup: L.LayerGroup, features: Feature[], type: String) {
    if (layerGroup) {
      layerGroup.removeFrom(map);
    }
    layerGroup = this.addGeoJsonLayers(features, type);
    layerGroup.addTo(map);

    return layerGroup;
  }

  addlayerOnMapInZone(map: L.Map, layerGroup: L.LayerGroup, features: Feature[], type: String) {
    if (layerGroup) {
      layerGroup.removeFrom(map);
    }
    layerGroup = this.addGeoJsonLayersIn(features, type);
    layerGroup.addTo(map);

    return layerGroup;
  }

  addlayerOnMapOutZone(map: L.Map, layerGroup: L.LayerGroup, features: Feature[], type: String) {
    if (layerGroup) {
      layerGroup.removeFrom(map);
    }
    layerGroup = this.addGeoJsonLayersOut(features, type);
    layerGroup.addTo(map);

    return layerGroup;
  }

  addGeoJsonLayersIn(features: Feature[], type: String) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      let exterieur = feature.properties?.exterieur === undefined ? false : feature.properties.exterieur;
      if (exterieur === false) {
        this.createGeoJson(feature, type)
          // Ajout du nom en Popup
          .bindPopup(feature.properties!.nom)
          // Ajout sur la map
          .addTo(layerGroup);
      }
    }
    return layerGroup;
  }

  addGeoJsonLayersOut(features: Feature[], type: String) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      let exterieur = feature.properties?.exterieur === undefined ? false : feature.properties.exterieur;
      if (exterieur === true) {
        this.createGeoJsonOut(feature, type)
          // Ajout sur la map
          .addTo(layerGroup);
      }
    }
    return layerGroup;
  }

  returnToolTipTerritoire(event: { resultats: any /*Map<Number,ResultatCalculDTO>*/, selectedYear: number } , layerTerritoire:any/*L.LayerGroup*/) {
    const { resultats, selectedYear } = event;
    // Créez un objet pour faciliter la recherche des données de production par codeZone

    // Filtrer les données de production en fonction de l'année sélectionnée
    const filteredResultats = resultats[selectedYear];

    if(layerTerritoire != null){
      let layer = layerTerritoire.getLayers()[0].getLayers()[0];
      const nomTerritoire = layer.feature.properties.nom;
      const resultatData = filteredResultats;
      if (resultatData && resultatData.productionTerritoireTotal != null) {
        layer.feature.properties.resultatTerritoireData = resultatData;
        let tooltipContent = `
          <div style="text-align: center; font-size: 1.2em;">
            <strong>Terrritoire : ${nomTerritoire}</strong>
          </div>
          <br>`
        if(layer.feature.properties.resultatTerritoireData.besoinTerritoireTotalChantier != null){
          tooltipContent = tooltipContent.concat(`<strong>Besoin chantiers :</strong> ${layer.feature.properties.resultatTerritoireData.besoinTerritoireTotalChantier?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.besoinTerritoirePrimaire != null){
          tooltipContent = tooltipContent.concat(`<strong>Besoin mat. primaires :</strong> ${layer.feature.properties.resultatTerritoireData.besoinTerritoirePrimaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.besoinTerritoireSecondaire != null){
          tooltipContent = tooltipContent.concat(`<strong>Besoin mat. secondaires :</strong> ${layer.feature.properties.resultatTerritoireData.besoinTerritoireSecondaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.besoinTerritoireTotal != null){
          tooltipContent = tooltipContent.concat(`<strong>Besoin total :</strong> ${layer.feature.properties.resultatTerritoireData.besoinTerritoireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireTotal != null){
          tooltipContent = tooltipContent.concat(`<strong>Prod. mat. primaires :</strong> ${layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireIntra != null){
          tooltipContent = tooltipContent.concat(`<strong>Prod. intra mat. primaires :</strong> ${layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireIntra?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireBrute != null){
          tooltipContent = tooltipContent.concat(`<strong>Prod. brute mat. primaires :</strong> ${layer.feature.properties.resultatTerritoireData.productionTerritoirePrimaireBrute?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.productionTerritoireSecondaireTotal != null){
          tooltipContent = tooltipContent.concat(`<strong>Prod. mat.  secondaires :</strong> ${layer.feature.properties.resultatTerritoireData.productionTerritoireSecondaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.productionTerritoireTotal != null){
          tooltipContent = tooltipContent.concat(`<strong>Prod. totale :</strong> ${layer.feature.properties.resultatTerritoireData.productionTerritoireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
        }
        if(layer.feature.properties.resultatTerritoireData.pourcentProductionTerritoireSecondaire != null){
          tooltipContent = tooltipContent.concat(`<strong>% prod. mat. secondaires :</strong> ${layer.feature.properties.resultatTerritoireData.pourcentProductionTerritoireSecondaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} %<br>`);
        }
        if(resultatData.besoinTerritoireTotal != null && resultatData.besoinTerritoireTotal !== 0){
          let tension = resultatData.productionTerritoireTotal / resultatData.besoinTerritoireTotal * 100;
          tooltipContent = tooltipContent.concat(`<strong>Ratio production/besoin :</strong> ${tension.toFixed(0)} %`);
        }

        let type = 'rouge';
        if((resultatData.productionTerritoireTotal / resultatData.besoinTerritoireTotal) < 1){
          type = 'rouge'
        }
        if((resultatData.productionTerritoireTotal / resultatData.besoinTerritoireTotal) >= 1){
          type = 'orange'
        }
        if(resultatData.besoinTerritoirePrimaire === 0 || (resultatData.productionTerritoireTotal / resultatData.besoinTerritoireTotal) > 1.2){
          type = 'vert'
        }
        let classColor = '';
        if(resultatData.productionTerritoireTotal != null && resultatData.besoinTerritoireTotal != null && resultatData.besoinTerritoireTotal !== 0){
          classColor = 'color-etiquette-'+type;
        }

        let centroid: LatLngExpression = new LatLng(
          this.geometryUtils.centroid(layer.feature).geometry.coordinates[1],
          this.geometryUtils.centroid(layer.feature).geometry.coordinates[0]);

        let tooltip = L.tooltip({
          permanent: true,
          opacity: 1,
          direction: "center",
          className:classColor
        })
        .setContent(tooltipContent)
        .setLatLng(centroid);

        return tooltip;
      } else {
        return null;
      }
    }
    return null;
  }

  getEtiquetteCalculResultatVentilation(event: { scenario: Scenario, selectedYear: number } , layerZonage:L.LayerGroup) {
    const { scenario, selectedYear } = event;
    // Créez un objet pour faciliter la recherche des données de production par codeZone

    // Filtrer les données de production en fonction de l'année sélectionnée
    const filteredResultats = scenario.resultat_calcul[selectedYear];

    let listeEtiquette: L.Tooltip[] = [];

    if(layerZonage != null){
      layerZonage.eachLayer((featureGroup: any) => {
        featureGroup.eachLayer((layer: any) => {

          const idZone = layer.feature.id;
          const nomZone = layer.feature.properties.nom;
          const resultatData = filteredResultats?.resultatZones[idZone];

          if (resultatData && (selectedYear as number) === parseInt(scenario.etudeDTO.anneeRef)){
            let tooltipContent = `
              <div style="text-align: center; font-size: 1.2em;">
                <strong>Zone : ${nomZone}</strong>
              </div>
              <br>`
            if(resultatData.productionZonePrimaireTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. mat. primaires :</strong> ${resultatData.productionZonePrimaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZoneSecondaireTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. mat.  secondaires :</strong> ${resultatData.productionZoneSecondaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZoneTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. totale :</strong> ${resultatData.productionZoneTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.pourcentageProductionZoneSecondaire != null){
              tooltipContent = tooltipContent.concat(`<strong>% prod. mat. secondaires :</strong> ${resultatData.pourcentageProductionZoneSecondaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} %<br>`);
            }
            let centroid: LatLngExpression = new LatLng(
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[1],
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[0]);

            listeEtiquette.push(
              L.tooltip({direction:"center",permanent:true})
              .setLatLng(centroid)
              .setContent(tooltipContent));
          }
        });
      });
    }

    return listeEtiquette;
  }

  getEtiquetteCalculResultatProjection(event: { resultatsCalculs: any /*Map<Number,ResultatCalculDTO>*/, selectedYear: number } , layerZonage:L.LayerGroup) {
    const { resultatsCalculs, selectedYear } = event;
    // Créez un objet pour faciliter la recherche des données de production par codeZone

    // Filtrer les données de production en fonction de l'année sélectionnée
    const filteredResultats = resultatsCalculs[selectedYear];

    let listeEtiquette:L.Tooltip[] = [];

    if(layerZonage != null){
      layerZonage.eachLayer((featureGroup: any) => {
        featureGroup.eachLayer((layer: any) => {

          const idZone = layer.feature.id;
          const nomZone = layer.feature.properties.nom;
          const resultatData = filteredResultats?.resultatZones[idZone];

          if (resultatData) {
            let tooltipContent = `
              <div style="text-align: center; font-size: 1.2em;">
                <strong>Zone : ${nomZone}</strong>
              </div>
              <br>`

            if(resultatData.besoinZoneTotalChantier != null){
              tooltipContent = tooltipContent.concat(`<strong>Besoin chantiers :</strong> ${resultatData.besoinZoneTotalChantier?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.besoinZonePrimaire != null){
              tooltipContent = tooltipContent.concat(`<strong>Besoin mat. primaires :</strong> ${resultatData.besoinZonePrimaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.besoinZoneSecondaire != null){
              tooltipContent = tooltipContent.concat(`<strong>Besoin mat. secondaires :</strong> ${resultatData.besoinZoneSecondaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.besoinZoneTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Besoin total :</strong> ${resultatData.besoinZoneTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZonePrimaireTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. mat. primaires :</strong> ${resultatData.productionZonePrimaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZonePrimaireIntra != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. intra mat. primaires :</strong> ${resultatData.productionZonePrimaireIntra?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZonePrimaireBrute != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. brute mat. primaires :</strong> ${resultatData.productionZonePrimaireBrute?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZoneSecondaireTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. mat. secondaires :</strong> ${resultatData.productionZoneSecondaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZoneTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Prod. totale :</strong> ${resultatData.productionZoneTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.pourcentageProductionZoneSecondaire != null){
              tooltipContent = tooltipContent.concat(`<strong>% prod. mat. secondaires :</strong> ${resultatData.pourcentageProductionZoneSecondaire?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} %<br>`);
            }

            if(resultatData.besoinZoneTotal != null && resultatData.besoinZoneTotal !== 0){
              let tension = ((resultatData.productionZoneTotal / resultatData.besoinZoneTotal) * 100);
              let classColor = 'rouge';
              if(tension >= 100){
                classColor = 'orange'
              }
              if(tension >= 120){
                classColor = 'vert'
              }
              tooltipContent = tooltipContent.concat(`<p class='color-font-${classColor}'><strong>Ratio production/besoin :</strong> ${tension.toFixed(0)} %</p>`);
              layer.setStyle({"fillColor": `var(--${classColor}-fill)`, "fillOpacity": 0.5});
            } else {
              layer.setStyle({"fillOpacity": 0});
            }

            let centroid: LatLngExpression = new LatLng(
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[1],
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[0]);

            listeEtiquette.push(L.tooltip({direction:"center",permanent:true})
            .setLatLng(centroid)
            .setContent(tooltipContent));
          }
        });
      });
    }
    return listeEtiquette;
  }

  getEtiquetteCalculResultatSuiviEtude(event: { resultatsCalculs: any /*Map<Number,ResultatCalculDTO>*/, selectedYear: number }, layerZonage:L.LayerGroup, scenario:Scenario) {
    const { resultatsCalculs, selectedYear } = event;
    // Créez un objet pour faciliter la recherche des données de production par codeZone

    // Filtrer les données de production en fonction de l'année sélectionnée
    const filteredResultats = resultatsCalculs[selectedYear];
    const anneeRef = parseInt(scenario.etudeDTO.anneeRef);
    let listeEtiquette:L.Tooltip[] = [];

    if(layerZonage != null){
      layerZonage.eachLayer((featureGroup: any) => {
        featureGroup.eachLayer((layer: any) => {

          const idZone = layer.feature.id;
          const nomZone = layer.feature.properties.nom;
          const resultatData = filteredResultats?.resultatZones[idZone];

          if (resultatData) {
            layer.feature.properties.resultatZoneData = resultatData;

            let tooltipContent = `
              <div style="text-align: center; font-size: 1.2em;">
                <strong>Zone : ${nomZone}</strong>
              </div>
              <br>`

            if(selectedYear != anneeRef && resultatData.productionZonePrimaireTotal != null){
              tooltipContent = tooltipContent.concat(`<strong>Données projetées :</strong><br>Prod. <strong>estimée</strong> mat. primaires : ${resultatData.productionZonePrimaireTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(selectedYear != anneeRef && resultatData.productionZonePrimaireBrute != null){
              tooltipContent = tooltipContent.concat(`Prod. brute <strong>estimée</strong> mat. primaires : ${resultatData.productionZonePrimaireBrute?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(selectedYear != anneeRef && resultatData.productionZoneTotal != null){
              tooltipContent = tooltipContent.concat(`Production totale <strong>estimée</strong> : ${resultatData.productionZoneTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(selectedYear != anneeRef && resultatData.besoinZoneTotal != null){
              tooltipContent = tooltipContent.concat(`Besoin total <strong>estimé</strong> : ${resultatData.besoinZoneTotal?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(selectedYear != anneeRef && resultatData.besoinZoneTotal != null){
              let tension = ((resultatData.productionZoneTotal / resultatData.besoinZoneTotal) * 100);
              let classColor = 'rouge';
              if(tension >= 100){
                classColor = 'orange'
              }
              if(tension >= 120){
                classColor = 'vert'
              }
              tooltipContent = tooltipContent.concat(`<p class='color-font-${classColor}'>Ratio production/besoin <strong>estimé</strong> : ${tension.toFixed(0)} %</p>`);
            }
            if(resultatData.productionZonePrimaireTotalReelle != null){
              tooltipContent = tooltipContent.concat('<strong>Données réelles :</strong><br>')
              tooltipContent = tooltipContent.concat(`Prod. <strong>réelle</strong> mat. primaires : ${resultatData.productionZonePrimaireTotalReelle?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.productionZonePrimaireBruteReelle != null){
              tooltipContent = tooltipContent.concat(`Prod. brute <strong>réelle</strong> mat. primaires : ${resultatData.productionZonePrimaireBruteReelle?.toLocaleString('fr-FR', {maximumFractionDigits: 0})} kt<br>`);
            }
            if(resultatData.besoinZoneTotal != null && resultatData.productionZonePrimaireTotalReelle != null){
              let tension = (((resultatData.productionZonePrimaireTotalReelle + resultatData.productionZoneSecondaireTotal) / resultatData.besoinZoneTotal) * 100);
              let classColor = 'rouge';
              if(tension >= 100){
                classColor = 'orange'
              }
              if(tension >= 120){
                classColor = 'vert'
              }
              tooltipContent = tooltipContent.concat(`<p class='color-font-${classColor}'>Ratio production/besoin <strong>ajusté</strong> : ${tension.toFixed(0)} %</p>`);
            }

            const tooltip = L.tooltip({direction:"center",permanent:true})
            .setLatLng((layer.getBounds().getCenter()))
            .setContent(tooltipContent);

            listeEtiquette.push(tooltip);
            // SI ProductionZoneTotal / BesoinZoneTotal < 100% ALORS couleur de la zone = rouge
            // SI 120% >= ProductionZoneTotal / BesoinZoneTotal >= 100% ALORS couleur de la zone = orange
            // SI ProductionZoneTotal / BesoinZoneTotal > 120% ALORS couleur de la zone = vert
            let type = '';
            if(((resultatData.productionZonePrimaireTotalReelle + resultatData.productionZoneSecondaireTotal) / resultatData.besoinZoneTotal) < 1){
              type = 'rouge'
            }
            if(((resultatData.productionZonePrimaireTotalReelle + resultatData.productionZoneSecondaireTotal) / resultatData.besoinZoneTotal) >= 1){
              type = 'orange'
            }
            if(resultatData.besoinZoneTotal === 0 || ((resultatData.productionZonePrimaireTotalReelle + resultatData.productionZoneSecondaireTotal) / resultatData.besoinZoneTotal) > 1.2){
              type = 'vert'
            }
            if(resultatData.productionZonePrimaireTotalReelle != null && resultatData.besoinZoneTotal != null && resultatData.besoinZoneTotal !== 0){
              layer.setStyle({
                "fillColor": `var(--${type}-fill)`,
                "fillOpacity": 0.5
              });
            } else {
              layer.setStyle({
                "fillOpacity": 0
              });
            }
          }
        });
      });
    }
    return listeEtiquette;
  }

  getEtiquetteRelationMateriau(event: { relationZone: ScenarioMateriau[]} , layerZonage:L.LayerGroup) {
    const { relationZone} = event;
    // Créez un objet pour faciliter la recherche des données de production par codeZone

    let listeIdZone : Number[] = [];

    for(let re of relationZone){
      if(re.id_zone !== undefined){
        listeIdZone.push(re.id_zone)
      }
    }
    let listeEtiquette:L.Tooltip[] = [];

    if(layerZonage){
      layerZonage.eachLayer((featureGroup: any) => {
        featureGroup.eachLayer((layer: any) => {
          if (relationZone.length > 0 && listeIdZone.findIndex(id => id === (layer.feature.id as Number)) !== -1) {

            let tooltipContent = `<div style="text-align: center; font-size: 0.8rem;">
                                    <strong>Zone : ${layer.feature.properties.nom}</strong>
                                  </div>
                                  <br>`;

            for(let rel of relationZone){
              if(rel.id_zone === (layer.feature.id as Number)){
                tooltipContent += '<li class="li-custom-properties">'+rel.libelle+' - Materiau '+rel.origine+' - '+rel.tonnage.toLocaleString('fr-FR', {minimumFractionDigits: 2})+' kt</li>'
              }
            }

            let centroid: LatLngExpression = new LatLng(
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[1],
              this.geometryUtils.centroid(layer.feature).geometry.coordinates[0]);

            const tooltip = L.tooltip({direction:"center",permanent:true})
            .setLatLng(centroid)
            .setContent(tooltipContent);

            listeEtiquette.push(tooltip);
          }
        });
      });
    }
    return listeEtiquette;
  }

  createAllPanesForMap(map: L.Map) {
    map.createPane('contraintefaible');
    map.getPane('contraintefaible')!.style.zIndex = '490';
    map.createPane('contraintemoyenne');
    map.getPane('contraintemoyenne')!.style.zIndex = '490';
    map.createPane('contrainteforte');
    map.getPane('contrainteforte')!.style.zIndex = '490';
    map.createPane('chantier');
    map.getPane('chantier')!.style.zIndex = '490';
    map.createPane('stockage');
    map.getPane('stockage')!.style.zIndex = '490';
    map.createPane('region');
    map.getPane('region')!.style.zIndex = '456';
    map.createPane('departement');
    map.getPane('departement')!.style.zIndex = '452';
    map.createPane('zoneemploi');
    map.getPane('zoneemploi')!.style.zIndex = '448';
    map.createPane('epci');
    map.getPane('epci')!.style.zIndex = '444';
    map.createPane('bassinvie');
    map.getPane('bassinvie')!.style.zIndex = '442';
    map.createPane('commune');
    map.getPane('commune')!.style.zIndex = '440';

    map.createPane('reseau_hydro');
    map.getPane('reseau_hydro')!.style.zIndex = '464';
    map.createPane('reseau_routier');
    map.getPane('reseau_routier')!.style.zIndex = '468';
    map.createPane('reseau_voies');
    map.getPane('reseau_voies')!.style.zIndex = '472';

    map.createPane('cadastre');
    map.getPane('cadastre')!.style.zIndex = '430';

    map.createPane('reserves_naturelles_regionales');
    map.getPane('reserves_naturelles_regionales')!.style.zIndex = '280';
    map.createPane('reserves_naturelles_nationales');
    map.getPane('reserves_naturelles_nationales')!.style.zIndex = '278';
    map.createPane('znieff1');
    map.getPane('znieff1')!.style.zIndex = '276';
    map.createPane('natura');
    map.getPane('natura')!.style.zIndex = '274';
    map.createPane('perimetre_naturel');
    map.getPane('perimetre_naturel')!.style.zIndex = '272';
    map.createPane('znieff2');
    map.getPane('znieff2')!.style.zIndex = '270';

    // Todo : vérifier celui qu'on garde
    map.createPane('perso');
    map.getPane('perso')!.style.zIndex = '470';
    map.createPane('autre');
    map.getPane('autre')!.style.zIndex = '470';

    map.createPane('zone_exterieur');
    map.getPane('zone_exterieur')!.style.zIndex = '472';
  }
}
