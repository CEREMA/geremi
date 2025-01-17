import { Component, OnInit, OnDestroy, Input, ViewChild } from "@angular/core";
import L, { LayerGroup, TileLayer } from "leaflet";
import { latLng } from "leaflet";
import { EtablissementService } from "../service/etablissement.service";
import { MarkerUtilsService } from "../service/markerUtils.service";
import { RegionService } from "../service/region.service";
import { DepartementService } from "../service/departement.service";
import { CommuneService } from "../service/commune.service";
import { BassinvieService } from "../service/bassinvie.service";
import { ZoneemploiService } from "../service/zoneemploi.service";
import { EpciService } from "../service/epci.service";
import { forkJoin, Subscription} from "rxjs";
import { BackgroundMapService } from "../service/background-map.service";
import { ExtraLayerService } from "../service/extra-layer.service";
import { PanneauLateralComponent } from "../../panneau-lateral/view/panneaulateral.component";
import { MenuService } from "src/app/layout/app.menu.service";
import { ConfigCartoComponent } from "../../config-carto/view/config.component";
import { EtudeComponent } from "../../etude/etude.component";
import {Feature, FeatureCollection} from "geojson";
import { AssociationZonageService } from "../../etude/service/associationzonage.service";
import { StyleLayersUtilsService } from "../service/styleLayersUtils.service";
import { CartoUtilsService } from "../service/cartoUtils.service";
import { ArrayUtilsService } from "src/app/shared/service/arrayUtils.service";
import { SelectionZonageService } from "../service/selectionzonage.service";
import { GeometryReducePrecisionService } from "../service/geometry-reduce-precision.service";
import { Router } from "@angular/router";
import { ZonageService } from "../../etude/service/zonage.service";
import { Zonage } from "../../etude/model/zonage.model";
import { ZoomChangeService } from "../../config-carto/service/zoom-change.service";
import { PopulationDTO } from "../../etude/model/population.model";
import { MessageService } from "primeng/api";
import { ToastService } from "../service/toast.service";
import { DateService } from "../service/date.service";
import { LayersService } from "../service/layers.service";
import { Territoire } from "../../etude/model/territoire.model";
import { TerritoireService } from "../../etude/service/territoire.service";
import { ContrainteEnvironnementale } from "../../etude/contraintes-environnementales/model/contrainteEnv.model";
import { ContrainteEnvironnetaleService } from "../../etude/contraintes-environnementales/service/contrainte-env.service";
import { Chantier } from "../../etude/chantiers-envergure/model/chantiers.model";
import { ChantiersService } from "../../etude/chantiers-envergure/service/chantiers-env.service";
import { InstallationStockage } from "../../etude/installation-stockage/model/installation-stockage.model";
import { InstallationStockageService } from "../../etude/installation-stockage/service/installation-stockage.service";
import { InstallationStockageDTO } from "../../etude/installation-stockage/model/installation-stockage-dto.model";
import { ScenarioCalculService } from "../../etude/scenario/scenario-calcul/service/scenario-calcul.service";
import { ScenarioMateriauService } from "../../etude/scenario/scenario-materiau/service/scenario-materiau.service";
import { ScenarioContrainteService } from "../../etude/scenario/scenario-contrainte/service/scenario-contrainte.service";
import { SuiviEtudeComponent } from "../../etude-suivi/view/suivi-etude.component";
import { OverlayService } from "../../overlay/service/overlay.service";
import { ImportEtudeComponent } from "../../import-etude/view/import-etude.component";
import { Etude } from "../../etude/model/etude.model";
import { ScenarioService } from "../../etude/scenario/service/scenario.service";


@Component({
  selector: 'app-carto',
  templateUrl: './carto.component.html',
  styleUrls: ['./carto.component.scss'],
  providers: [StyleLayersUtilsService,
    CartoUtilsService,
    ArrayUtilsService,
    GeometryReducePrecisionService,
    MessageService
  ]
})
export class CartoComponent implements OnInit, OnDestroy {
  // Map
  @Input() mapId: string = 'map';
  private map: L.Map;

  // Child Component
  @ViewChild(PanneauLateralComponent)
  private panneauLateralComponent: PanneauLateralComponent;
  @ViewChild(ConfigCartoComponent)
  private configCartoComponent: ConfigCartoComponent;
  @ViewChild(EtudeComponent)
  private etudeComponent: EtudeComponent;
  @ViewChild(SuiviEtudeComponent)
  private suiviEtudeComponent: SuiviEtudeComponent;
  @ViewChild(ImportEtudeComponent)
  private importEtudeComponent: ImportEtudeComponent;

  // Subscription
  private menuSourceSubscription: Subscription;
  private associationZonageResetSubscription: Subscription;
  private toastSourceSubscription: Subscription;
  private firstSubscription: Subscription;
  private secondSubscription: Subscription;
  private layersServiceSubscription: Subscription;
  private layersEtudeServiceSubscription: Subscription;
  private layersRefRegionServiceSubscription: Subscription;
  private extraLayersServiceSubscription: Subscription;
  private extraLayersEtudeServiceSubscription: Subscription;
  private backgroundLayersServiceSubscription: Subscription;
  private zonageFromEtudeSubscription: Subscription;
  private subscription: Subscription;

  private zonageInTerritoireSubscription: Subscription;
  private territoireLayerSubscription: Subscription;
  private zonageImportBeforeAddSubscription: Subscription;
  // Cartographie Subscription
  private mapSub: any;
  // Contraintes
  private layerEtudeContraintesSubscription: Subscription;
  private layerEtudeContraintesExistantesSubscription: Subscription;
  // Chantiers
  private layerEtudeChantiersSubscription: Subscription;
  private layerEtudeChantiersExistantsSubscription: Subscription;

  private layerEtudeInstallationStockagesSubscription: Subscription;
  private layerEtudeInstallationStockagesExistantesSubscription: Subscription;
  private layerEtudeInstallationStockagesWithNonActiveSubscription: Subscription;
  private layerEtablissementsEtudeSubscription: Subscription;

  // Layer for cluster
  layer: any;
  // Layers
  private layerMarkersNaturel: any;
  private layerMarkersRecycle: any;
  private layerMarkersNaturelRecycle: any;
  private layerRegion: any;
  private layerDepartement: any;
  private layerCommune: any;
  private layerBassinvie: any;
  private layerZoneemploi: any;
  private layerEpci: any;
  // Territoire
  private layerTerritoire: any;
  private layerUserRegion: any;
  // Zonage
  private layerZonageIn: any;
  private layerZonageOut: any;
  private layerZonageImportIn: any;
  private layerZonageImportOut: any;
  private layerZonageFromEtude: any;
  // Territoire Etude
  private layerEtudeTerritoire: any;
  // Contrainte Etude
  private layerEtudeContraintes: any;
  private layerEtudeContraintesExistantes: any;
  // Chantier Etude
  private layerEtudeChantiers: any;
  private layerEtudeChantiersExistants: any;
  // Etablissements Etude
  private layerEtablissementsEtude: any;

  // Installation Stockage Etude
  private layerEtudeInstallationStockages: any;
  private layerEtudeInstallationStockagesExistantes: any;
  private layerEtudeInstallationStockagesWithNonActive: any;

  // Consutlation Etude Cartographique
  private layerEtudeConsultation: Map<number,L.LayerGroup> = new Map();
  private etudeConsulte: Etude[] = [];
  private toolTipEtudeConsulte: Map<number,L.Tooltip[]> = new Map();
  private etudeConsulteSubscription: Map<number,Subscription> = new Map();
  private layerEtudeEtablissementCarto: Map<number,L.LayerGroup> = new Map();

  private featuresTerritoires: any[] = new Array();
  typeTerritoire: string;
  typeLayerTerritoire: any;

  private zonageFromEtude:Zonage;
  typeZonage: any;
  precisionZonage: any;
  idRegionEtude: any;
  focusOnRegion: boolean = false;

  // Active layers
  private activeLayer: Array<String> = new Array();
  private activeBackground: TileLayer;
  private activeExtraLayer: Array<String> = new Array();

  // Variable
  displayConfig: string = '';
  displayImportEtude: string = 'displayNone';
  displayEtude: string = 'displayNone';
  displaySuiviEtude: string = 'displayNone';
  options: any;
  private zoomLevel: number;

  toolTipTerritoire:any = null;
  toolTipZones:L.Tooltip[] = [];

  // Etablissement choisis
  selectedYear: number;
  listPopulation:PopulationDTO[];

  constructor(private regionService: RegionService,
    private departementService: DepartementService,
    private etablissementService: EtablissementService,
    private markersUtilsService: MarkerUtilsService,
    private communeService: CommuneService,
    private bassinvieService: BassinvieService,
    private zoneemploiService: ZoneemploiService,
    private epciService: EpciService,
    private backgroundService: BackgroundMapService,
    private extraLayerService: ExtraLayerService,
    private menuService: MenuService,
    private associerzonageservice: AssociationZonageService,
    private selectionZonageService: SelectionZonageService,
    private zonageService: ZonageService,
    private territoireService: TerritoireService,
    private scenarioService: ScenarioService,
    private contrainteService: ContrainteEnvironnetaleService,
    private chantiersService: ChantiersService,
    private installationStockageService: InstallationStockageService,
    private scenarioCalculService: ScenarioCalculService,
    private scenarioMateriauService: ScenarioMateriauService,
    private scenarioContrainteService: ScenarioContrainteService,
    // Utils
    private layersService: LayersService,
    private toastService: ToastService,
    private messageService: MessageService,
    private styleLayersUtils: StyleLayersUtilsService,
    private arrayUtils: ArrayUtilsService,
    private cartoUtils: CartoUtilsService,
    private geometryReducePrecisionService: GeometryReducePrecisionService,
    private zoomChangeService: ZoomChangeService,
    private overlayService: OverlayService,
    private dateService : DateService,
    private _router: Router) {

    this._router.routeReuseStrategy.shouldReuseRoute = function () {
      return true;
    }

    this.zoomLevel = 6;
  }

  ngOnInit() {
    this.mapSub = new Map();
    this.activeBackground = this.backgroundService.getTileLayer('OSM');
    this.options = {
      layers: [this.activeBackground],
      zoom: this.zoomLevel,
      center: latLng(45.764043, 4.835659),
      zoomControl: false,
      minZoom: 5,
      zoomSnap: -0.5,
      zoomDelta: -0.5
    };
    this.activeLayer = new Array();

    this.menuSourceSubscription = this.menuService.menuSource$.subscribe(value => {
      Promise.resolve(null).then(() => {
        if (value) {
          this.changeDisplay(value.key)
        }
      });
    })

    this.associationZonageResetSubscription = this.associerzonageservice.associationZonageResetSource$.subscribe(value => {
      Promise.resolve(null).then(() => {
        if (value === true) {
          this.removeLayersForEtudeDREAL();
          this.stopAssociationZonageSubscription();
        }
        else {
          this.removeLayersForEtude();
          this.stopAssociationZonageSubscription();
        }
      });
    })

    this.toastSourceSubscription = this.toastService.toastSource$.subscribe(value => {
      Promise.resolve(null).then(() => {
        if (value) {
          this.messageService.add(value);
        }
      });
    })

    this.subscription = this.dateService.selectedYear$.subscribe(year => {
      this.selectedYear = year;
    });
    this.layersService.layersResetSource$.subscribe(value => {
      this.clearLayers(value);
    });
    this.layersServiceSubscription = this.layersService.layersSource$.subscribe(type => {
      this.toggleLayer(type);
    });
    this.layersEtudeServiceSubscription = this.layersService.layersEtudeSource$.subscribe(type => {
      this.toggleLayer(type);
    });
    this.extraLayersServiceSubscription = this.layersService.extraLayersSource$.subscribe(type => {
      this.onExtraLayerChange(type);
    });
    this.extraLayersEtudeServiceSubscription = this.layersService.extraLayersEtudeSource$.subscribe(type => {
      this.onExtraLayerChange(type);
    });
    this.backgroundLayersServiceSubscription = this.layersService.backgroundLayersSource$.subscribe(type => {
      this.onBackgroundChange(type);
    });
    this.layersRefRegionServiceSubscription = this.layersService.layerRefRegionSource$.subscribe(id => {
      this.toggleUserLayer(id);
    });
    this.layersService.layerFlyToSource$.subscribe(value => {
      this.flyToLayer(value);
    });
    this.layersService.layerEtudeTerritoireSource$.subscribe(value => {
      this.displayTerritoireFromEtude(value);
    });
    this.layerEtudeContraintesSubscription = this.layersService.layerEtudeContraintesSource$.subscribe(value => {
      this.displayEtudeContraintes(value);
    });
    this.layerEtudeContraintesExistantesSubscription = this.layersService.layerEtudeContraintesExistantesSource$.subscribe(value => {
      this.displayEtudeContraintesExistantes(value);
    });
    this.layerEtudeChantiersSubscription = this.layersService.layerEtudeChantiersSource$.subscribe(value => {
      this.displayEtudeChantiers(value);
    });
    this.layerEtudeChantiersExistantsSubscription = this.layersService.layerEtudeChantiersExistantsSource$.subscribe(value => {
      this.displayEtudeChantiersExistants(value);
    });
    this.layerEtudeInstallationStockagesSubscription = this.layersService.layerEtudeInstallationStockagesSource$.subscribe(value => {
      this.displayEtudeInstallationStockages(value);
    });
    this.layerEtudeInstallationStockagesExistantesSubscription = this.layersService.layerEtudeInstallationStockagesExistantesSource$.subscribe(value => {
      this.displayEtudeInstallationStockagesExistantes(value);
    });
    this.layerEtudeInstallationStockagesWithNonActiveSubscription = this.layersService.layerEtudeInstallationStockagesNonActiveExistantesSource$.subscribe(value => {
      this.displayEtudeInstallationStockagesNonActiveExistantes(value);
    });
    this.layerEtablissementsEtudeSubscription = this.layersService.layerEtablissementsEtudeSource$.subscribe(value => {
      if(this.displayEtude !== 'displayNone'){
        this.displayEtablissementsEtude(this.etudeComponent.etude?.id, this.etudeComponent.openDonneesScenario);
      }
      else if(this.displaySuiviEtude !== 'displayNone') {
        this.displayEtablissementsEtude(this.suiviEtudeComponent.etude?.id, true);
      }
    });

    this.scenarioCalculService.displayScenarioCalculVentilationSource$.subscribe(value => {
      // Gestion ToolTip Zones
      const tooltipZones = this.cartoUtils.getEtiquetteCalculResultatVentilation({scenario:value,selectedYear:this.selectedYear},this.layerZonageFromEtude);
      this.handleToolTipZones(tooltipZones);
    });
    this.scenarioCalculService.displayScenarioCalculProjectionSource$.subscribe(value => {
      // Gestion ToolTip Zones
      const tooltipZones = this.cartoUtils.getEtiquetteCalculResultatProjection({resultatsCalculs:value.resultat_calcul,selectedYear:this.selectedYear},this.layerZonageFromEtude);
      this.handleToolTipZones(tooltipZones);

      // Gestion de la toolTip Territoire
      const tooltip:any = this.cartoUtils.returnToolTipTerritoire({resultats:value.resultat_calcul,selectedYear:this.selectedYear},this.layerEtudeTerritoire);
      this.handleToolTipTerritoire(tooltip);
    });
    this.scenarioCalculService.displayScenarioCalculProjectionSuiviEtudeSource$.subscribe(value => {
      // Gestion ToolTip Zones
      const tooltipZones = this.cartoUtils.getEtiquetteCalculResultatSuiviEtude({resultatsCalculs:value.resultat_calcul,selectedYear:this.selectedYear},this.layerZonageFromEtude,value);
      this.handleToolTipZones(tooltipZones);
    });
    this.scenarioMateriauService.scenarioMateriauDisplayEtiquetteSource$.subscribe(value => {
      const tooltipZones = this.cartoUtils.getEtiquetteRelationMateriau({relationZone:value},this.layerZonageFromEtude);
      // Gestion ToolTip Zones
      this.handleToolTipZones(tooltipZones);
    });

    // Consultation Etude Cartographie
    this.layerEtudeConsultation = new Map();
    this.layersService.displayEtudeConsultationSource$.subscribe(value => {
      this.etudeConsulte = value;

      this.displayEtudeConsulte(value);
    })
  }

  handleToolTipEtudeConsulte(idEtude:number,tooltip:L.Tooltip[]){
    if(this.toolTipEtudeConsulte.has(idEtude)){
      if(this.toolTipEtudeConsulte.get(idEtude)?.length !== 0){
        this.toolTipEtudeConsulte.get(idEtude)?.forEach((t:any) => this.map.closeTooltip(t))
      }
    }
    if (tooltip.length > 0) {
      tooltip.forEach(t => this.map.openTooltip(t))
      this.toolTipEtudeConsulte.set(idEtude,tooltip);
    } else {
      this.toolTipEtudeConsulte.delete(idEtude);
    }
  }

  handleToolTipZones(tooltipZones:L.Tooltip[]){
    if(tooltipZones.length === 0){
      if(this.toolTipZones.length !== 0){
        this.toolTipZones.forEach(t => this.map.closeTooltip(t))
      }
      this.toolTipZones = [];
    } else {
      if(this.toolTipZones.length !== 0){
        this.toolTipZones.forEach(t => this.map.closeTooltip(t))
      }
      tooltipZones.forEach(t => this.map.openTooltip(t))
      this.toolTipZones = tooltipZones;
    }
  }

  handleToolTipTerritoire(toolTipTerritoire:L.Tooltip){
    if(toolTipTerritoire == null){
      if(this.toolTipTerritoire != null){
        this.map.closeTooltip(this.toolTipTerritoire);
      }
      this.toolTipTerritoire = null;
    } else {
      if(this.toolTipTerritoire != null){
        this.map.closeTooltip(this.toolTipTerritoire);
        this.toolTipTerritoire = null;
      }
      this.map.openTooltip(toolTipTerritoire);
      this.toolTipTerritoire = toolTipTerritoire;
    }
  }

  onBackgroundChange(type: String) {
    this.map.removeLayer(this.activeBackground);
    this.activeBackground = this.backgroundService.getTileLayer(type.toString());
    this.activeBackground?.addTo(this.map)
  }

  onExtraLayerChange(type: String) {
    const tileLayerRequested: TileLayer = this.extraLayerService.getLayer(type.toString());
    if (this.activeExtraLayer.includes(type)) {
      this.activeExtraLayer = this.arrayUtils.arrayRemoveAny(this.activeExtraLayer, type);
      this.map.removeLayer(tileLayerRequested);
    } else {
      tileLayerRequested.addTo(this.map)
      this.activeExtraLayer.push(type)
    }
  }

  onMapReady(map: L.Map) {
    this.map = map;
    this.cartoUtils.createAllPanesForMap(this.map);
    L.control.scale({metric: true, imperial: false, position: 'bottomright'}).addTo(this.map);
  }

  onMoveEnd() {
    if (this.map) {
      this.zoomChangeService.onZoomChange(this.map.getZoom());
      if (this.displayEtude === 'displayNone' && this.displaySuiviEtude === 'displayNone') {
        this.activeLayersForCarto();
      }

      if (this.layerTerritoire && this.typeTerritoire && this.typeTerritoire != '') {
        this.callServiceTerritoire(this.typeTerritoire.toString());
      }

      if (this.layerZonageIn && this.typeZonage && this.typeZonage != '' && this.geometryReducePrecisionService.getCoeff(this.typeZonage, this.map.getZoom()) != this.precisionZonage) {
        this.callServiceAssociationZonage(this.typeZonage.toString());
      }

      if(this.layerZonageFromEtude) {
        this.displayZonageFromEtude(this.zonageFromEtude)
      }

      if(this.layerZonageImportIn || this.layerZonageImportOut) {
        this.displayZonageFromImport(this.zonageFromEtude)
      }

      if (this.layerEtablissementsEtude) {
        this.callEtablissementService('etablissementsEtude');
      }

      if(this.layerUserRegion){
        this.toggleUserLayer(this.idRegionEtude);
      }
      this.zoomLevel = this.map.getZoom();
    }
  }

  callService(type: String) {
    if(this.mapSub.has(type)){
      this.mapSub.get(type).unsubscribe();
      this.mapSub.delete(type);
    }

    let subscription = this.getService(type).findInBox(this.map.getBounds(), this.geometryReducePrecisionService.getCoeff(type.toString(), this.map.getZoom())).subscribe(
      (data) => {
        if(this.displayEtude === 'displayNone')
          this.addlayerOnMap(data, type);
      }
    );
    this.mapSub.set(type, subscription)
  }

  selectedYearConfigEvent(selectedYear: number) {
    this.selectedYear = selectedYear;
    this.markersUtilsService.resetMarker();
    this.panneauLateralComponent.visiblePanneauLateral = false;
    this.panneauLateralComponent.carriere = undefined;
    this.panneauLateralComponent.forceRerender();
    ['EtablissementNaturel','EtablissementRecycle','EtablissementNaturelRecycle', 'etablissementsEtude'].forEach(l => {if (this.activeLayer.includes(l)) this.callEtablissementService(l);})

    if(this.etudeConsulte.length > 0){
      this.displayEtudeConsulte(this.etudeConsulte);
    }
  }

  callEtablissementService(type: string) {
    if('etablissementsEtude' === type) {
      if(this.displaySuiviEtude === ''){
        this.displayEtablissementsEtude(this.suiviEtudeComponent.etude?.id, true);
      } else {
        this.displayEtablissementsEtude(this.etudeComponent.etude?.id, this.etudeComponent.openDonneesScenario);
      }
    } else {
      if (!this.selectedYear) {
        this.etablissementService.getDistinctAnnees()
          .subscribe((annees) => {
            if (annees.length > 0) {
              const annee = annees[0];
              this.loadEtablissementsData(annee, type);
            }
          });
      } else {
        this.loadEtablissementsData(this.selectedYear.toString(), type);
      }
    }
  }

  displayEtablissementsEtudeEvent(idEtude: number){
    this.displayEtablissementsEtude(idEtude, this.etudeComponent.openDonneesScenario);
  }

  displayEtablissementsEtude(idEtude: number, afficherTx: boolean){
    if (idEtude !== 0) {
      if (!this.activeLayer.includes('etablissementsEtude')) {
        this.activeLayer.push('etablissementsEtude');
      }

      this.layerEtablissementsEtudeSubscription = this.etablissementService.findEtablissementsAnneeEtude(this.selectedYear, idEtude, false).subscribe({
        next: (value: FeatureCollection) => {
          if (this.activeLayer.includes('etablissementsEtude')) {
            this.addEtablissementsOnMap(value, 'etablissementsEtude', afficherTx);
          }
        },
        error: (err: Error) => {
          console.log("Erreur lors de la récupération des établissements liés à l'étude");
        }
      });
    }
  }

  loadEtablissementsData(annee: string, type: string) {
    this.etablissementService.findEtablissementInBox(annee, type)
      .subscribe((filteredData) => {
        if(this.activeLayer.includes(type)){
          this.addEtablissementsOnMap(filteredData, type, false);
        }
      });
  }

  displayEtudeConsulte(etudes:Etude[]){
    this.etudeConsulte = etudes;

    if(this.layerEtudeConsultation != null){
      this.layerEtudeConsultation.forEach((r:L.Layer,k:any) => r.removeFrom(this.map));
    }
    this.layerEtudeConsultation = new Map();

    if(this.layerEtudeEtablissementCarto != null){
      this.layerEtudeEtablissementCarto.forEach((r:L.LayerGroup<any>,k:any) => r.removeFrom(this.map));
    }
    this.layerEtudeEtablissementCarto = new Map();

    for(let etude of this.toolTipEtudeConsulte.keys()){
        this.handleToolTipEtudeConsulte(etude,[]);
    }

    for(let etude of etudes) {

      if(this.etudeConsulteSubscription.has(etude.id)){
        this.etudeConsulteSubscription.get(etude.id)?.unsubscribe();
        this.etudeConsulteSubscription.delete(etude.id);
      }
      const etudeConsulteObservable = forkJoin({
        zonage: this.zonageService.getZonageFeatureByEtudeId(etude.id, this.geometryReducePrecisionService.getCoeff('autre', this.map.getZoom())),
        scenario: this.scenarioService.getScenarioValideByIdEtude(etude.id),
        etabs: this.etablissementService.findEtablissementsAnneeEtude(this.selectedYear, etude.id, true)
      })

      this.etudeConsulteSubscription.set(etude.id,
        etudeConsulteObservable.subscribe(({zonage, scenario, etabs}) => {
          if(this.layerEtudeConsultation.has(etude.id) ) {
            this.layerEtudeConsultation.get(etude.id)?.removeFrom(this.map);
          }
          const etudeLayerGroup: LayerGroup = this.cartoUtils.addGeoJsonLayers(zonage.features,"autre").addTo(this.map);
          this.layerEtudeConsultation.set(etude.id, etudeLayerGroup);

          this.handleToolTipEtudeConsulte(
            etude.id,
            this.cartoUtils.getEtiquetteCalculResultatSuiviEtude({resultatsCalculs:scenario.resultat_calcul,selectedYear:this.selectedYear},etudeLayerGroup,scenario));

          this.addEtablissementsEtudeConsulteMap(etabs,etude.id);
        })
      );
    }

  }

  addEtablissementsEtudeConsulteMap(data:any,idEtude:number){
    let layerEtab = this.markersUtilsService.createLayerGroupMarker(data, this.map, this.panneauLateralComponent, this.selectedYear, true);
    this.map.addLayer(layerEtab);

    this.layerEtudeEtablissementCarto.set(idEtude,layerEtab);
  }

  removeLayersOnMap() {
    // Remove previous layers
    this.map.eachLayer((layer: L.Layer) => {
      if (layer instanceof L.GeoJSON) {
        this.map.removeLayer(layer);
      }
    });
  }

  addEtablissementsOnMap(data: any, type: string, afficherTx:boolean) {
    // Remove previous layers
    if ('EtablissementNaturel' === type) {
      if (this.layerMarkersNaturel) this.layerMarkersNaturel.removeFrom(this.map);
      this.layerMarkersNaturel = this.markersUtilsService.createLayerGroupMarker(data, this.map, this.panneauLateralComponent, this.selectedYear, afficherTx);
      this.map.addLayer(this.layerMarkersNaturel);
    } else if ('EtablissementRecycle' === type) {
      if (this.layerMarkersRecycle) this.layerMarkersRecycle.removeFrom(this.map);
      this.layerMarkersRecycle = this.markersUtilsService.createLayerGroupMarker(data, this.map, this.panneauLateralComponent, this.selectedYear, afficherTx);
      this.map.addLayer(this.layerMarkersRecycle);
    } else if ('EtablissementNaturelRecycle' === type) {
      if (this.layerMarkersNaturelRecycle) this.layerMarkersNaturelRecycle.removeFrom(this.map);
      this.layerMarkersNaturelRecycle = this.markersUtilsService.createLayerGroupMarker(data, this.map, this.panneauLateralComponent, this.selectedYear, afficherTx);
      this.map.addLayer(this.layerMarkersNaturelRecycle);
    } else if ('etablissementsEtude' === type) {
      if (this.layerEtablissementsEtude) this.layerEtablissementsEtude.removeFrom(this.map);
      this.layerEtablissementsEtude = this.markersUtilsService.createLayerGroupMarker(data, this.map, this.panneauLateralComponent, this.selectedYear, afficherTx);
      this.map.addLayer(this.layerEtablissementsEtude);
    }
  }

  addlayerOnMap(data: any, type: String) {
    switch (type) {
      case "region":
        this.layerRegion = this.cartoUtils.addlayerOnMap(this.map, this.layerRegion, data.features, type);
        break;
      case "departement":
        this.layerDepartement = this.cartoUtils.addlayerOnMap(this.map, this.layerDepartement, data.features, type);
        break;
      case "commune":
        this.layerCommune = this.cartoUtils.addlayerOnMap(this.map, this.layerCommune, data.features, type);
        break;
      case "bassinvie":
        this.layerBassinvie = this.cartoUtils.addlayerOnMap(this.map, this.layerBassinvie, data.features, type);
        break;
      case "zoneemploi":
        this.layerZoneemploi = this.cartoUtils.addlayerOnMap(this.map, this.layerZoneemploi, data.features, type);
        break;
      case "epci":
        this.layerEpci = this.cartoUtils.addlayerOnMap(this.map, this.layerEpci, data.features, type);
        break;
      default:
        //
        break;
    }
  }

  private getService(type: String):
    RegionService | DepartementService | CommuneService | BassinvieService | ZoneemploiService | EpciService {

    switch (type) {
      case 'region':
        return this.regionService;
      case 'departement':
        return this.departementService;
      case 'commune':
        return this.communeService;
      case 'bassinvie':
        return this.bassinvieService;
      case 'zoneemploi':
        return this.zoneemploiService;
      case 'epci':
        return this.epciService;
      default:
        throw new Error(`Service inconnu : ${type}`);
    }
  }

  callServiceTerritoire(territoire: string) {
    if(this.territoireLayerSubscription)
      this.territoireLayerSubscription.unsubscribe();

    this.territoireLayerSubscription =
      this.getService(territoire).findInBox(this.map.getBounds(), this.geometryReducePrecisionService.getCoeff(territoire, this.map.getZoom())).subscribe(
        (data) => {
          this.addLayerTerritoire(data, territoire);
        }
      );
  }

  callServiceAssociationZonage(zonage: string) {
    let liste_id_to_send = new Array<String>();
    this.featuresTerritoires.forEach(f => liste_id_to_send.push(f.id));
    let precision = this.geometryReducePrecisionService.getCoeff(zonage, this.map.getZoom());
    this.precisionZonage = precision;

    if(this.zonageInTerritoireSubscription){
      this.zonageInTerritoireSubscription.unsubscribe();
    }
    this.overlayService.overlayOpen("Zonage en cours de calcul...");
    this.zonageInTerritoireSubscription =
      this.selectionZonageService.findZonageInTerritoire(zonage, this.typeTerritoire, liste_id_to_send, precision).subscribe({
        next:(data) => {
          this.addLayerAssociationZonage(data, zonage);
        },
        error:() => this.overlayService.overlayClose(),
        complete:() => this.overlayService.overlayClose()
      });
  }

  displayZonageFromImport(zonage:Zonage){
    if(zonage){
      this.zonageImportBeforeAddSubscription = this.zonageService.getZonageByEtudeIdBeforeAdd(zonage.id_etude as number,this.geometryReducePrecisionService.getCoeff(zonage.type  as string, this.map.getZoom()))
        .subscribe((data) => {
          this.zonageFromEtude = zonage;
          this.addLayerAssociationZonageImport(data, zonage.type);
      });
    }
  }

  displayZonageFromEtude(zonage:Zonage){
    if(zonage){
      if(!zonage.features) {
        this.zonageFromEtudeSubscription = this.zonageService.getZonageFeatureByEtudeId(zonage.id_etude as number, this.geometryReducePrecisionService.getCoeff(zonage.type as string, this.map.getZoom())).subscribe((data) => {
          this.layerZonageFromEtude = this.cartoUtils.addlayerOnMap(this.map, this.layerZonageFromEtude, data.features, zonage.type);
          this.zonageFromEtude = zonage;
          if (zonage.populations) {
            this.onUploadFile({listPopulation: zonage.populations, selectedYear: this.selectedYear});
          }
          if(this.displayEtude !== 'displayNone'){
            this.affichageScenario();
          }
          if(this.displaySuiviEtude !== 'displayNone'){
            this.affichageSuiviEtude();
          }
        })
      } else {
        this.layerZonageFromEtude = this.cartoUtils.addlayerOnMap(this.map, this.layerZonageFromEtude, zonage.features.features, zonage.type);
        this.zonageFromEtude = zonage;
        if (this.listPopulation) {
          this.onUploadFile({ listPopulation: this.listPopulation, selectedYear: this.selectedYear });
        }
        if(this.displayEtude !== 'displayNone'){
          this.affichageScenario();
        }
        if(this.displaySuiviEtude !== 'displayNone'){
          this.affichageSuiviEtude();
        }
      };
    } else {
      if(this.zonageFromEtudeSubscription){
        this.zonageFromEtudeSubscription.unsubscribe();
      }
    }
  }

  affichageScenario(){
    this.scenarioCalculService.displayScenarioCalculVentilationAnnee(this.selectedYear);
    this.scenarioCalculService.displayScenarioCalculProjectionAnnee(this.selectedYear);
    this.scenarioMateriauService.displayEtiquetteOnZoneAfterMapMove();
    this.scenarioContrainteService.actualiserTauxRenouvellement();
  }

  affichageSuiviEtude(){
    this.scenarioCalculService.displayScenarioCalculProjectionSuiviEtudeActualiser();
    this.scenarioContrainteService.actualiserSuiviEtudeTauxRenouvellement();
  }

  displayTerritoireFromEtude(territoire:Territoire){
    if(this.layerEtudeTerritoire){
      this.layerEtudeTerritoire.addTo(this.map);
    }
    else{
      this.territoireLayerSubscription = this.territoireService.getTerritoireByIdWithPrecision(territoire.id_territoire,0).subscribe({
        next: (value) => {
          this.layerEtudeTerritoire = this.cartoUtils.addGeoJsonLayers([value],"autre");
          this.layerEtudeTerritoire.addTo(this.map);
          this.flyToLayer("EtudeTerritoire");
        },
      })
    }
  }

  changeOpacityLayerContrainte(layer:any , contrainte:ContrainteEnvironnementale){
    if(layer){
      if(layer.getLayers()[0].feature.id === contrainte.feature.id){
        let typeContrainte = 'contrainte'+contrainte.niveau.toLowerCase();
        if(contrainte.afficher){
          typeContrainte += '-affiche';
        } else {
          typeContrainte += '-cache';
        }
        layer.setStyle(this.styleLayersUtils.getStyleLayer(typeContrainte));
      }
    }
  }

  customRemoveLayerForFeature(layer:any , feature:Feature){
    if(layer){
      if(layer.getLayers()[0].feature.id === feature.id){
        layer.remove();
      }
    }
  }

  changeFeatureChantier(layer:any , chantier:Chantier){
    if(layer){
      if(layer.getLayers()[0].feature.id === chantier.feature.id){
        layer.getLayers()[0].feature.properties!.nom = chantier.nom;
        layer.getLayers()[0].feature.properties!.annee_debut = chantier.annee_debut;
        layer.getLayers()[0].feature.properties!.annee_fin = chantier.annee_fin;
        layer.getLayers()[0].feature.properties!.beton_pref = chantier.beton_pref;
        layer.getLayers()[0].feature.properties!.viab_autre = chantier.viab_autre;
        layer.getLayers()[0].feature.properties!.ton_tot = chantier.ton_tot;
        layer.getLayers()[0].feature.properties!.id_source = chantier.id_source;
        layer.getLayers()[0].feature.properties!.id_frere = chantier.id_frere;
        layer.getLayers()[0].feature.id = chantier.id_chantier;
      }
    }
  }

  changeClassLayerChantier(layer:any , chantier:Chantier){
    if(layer){
      if(layer.getLayers()[0].feature.id === chantier.feature.id){
        let typeChantier = 'chantier';
        if(chantier.afficher){
          typeChantier += '-affiche';
        } else {
          typeChantier += '-cache';
        }
        let style = this.styleLayersUtils.getStyleLayer(typeChantier);
        layer.setStyle({
          opacity : style.opacity,
          fillOpacity : style.fillOpacity
        });
        if(!(this.selectedYear >= chantier.annee_debut && this.selectedYear <= chantier.annee_fin)){
          layer.setStyle({
            color : 'red'
          });
        } else {
          layer.setStyle({
            color : style.color
          });
        }
      }
    }
  }

  changeOpacityLayerInstallationStockage(layer:any , installationStockage:InstallationStockage){
    if(layer){
      if(layer.getLayers()[0].feature.id === installationStockage.feature.id){
        let typeInstallationStockage = 'stockage';
        if(installationStockage.afficher){
          typeInstallationStockage += '-affiche';
        } else {
          typeInstallationStockage += '-cache';
        }
        layer.setStyle(this.styleLayersUtils.getStyleLayer(typeInstallationStockage));
      }
    }
  }

  changeOpacityLayerInstallationStockageNonActive(layer:any , installationStockage:InstallationStockage){
    if(layer){
      if(layer.getLayers()[0].feature.id === installationStockage.feature.id){
        let typeInstallationStockage = 'stockagenonactive-affiche';
        layer.setStyle(this.styleLayersUtils.getStyleLayer(typeInstallationStockage));
      }
    }
  }

  changeFeatureInstallationStockage(layer:any , installationStockage:InstallationStockageDTO){
    if(layer){
      if(layer.getLayers()[0].feature.id === installationStockage.idFeature){
        layer.getLayers()[0].feature.properties!.nom_etab = installationStockage.nomEtab;
        layer.getLayers()[0].feature.properties!.annee_debut = installationStockage.anneeDebut;
        layer.getLayers()[0].feature.properties!.annee_fin = installationStockage.anneeFin;
        layer.getLayers()[0].feature.properties!.beton_pref = installationStockage.betonPref;
        layer.getLayers()[0].feature.properties!.viab_autre = installationStockage.viabAutre;
        layer.getLayers()[0].feature.properties!.ton_tot = installationStockage.tonTot;

        layer.getLayers()[0].feature.id = installationStockage.idStockage;
      }
    }
  }

  displayEtudeContraintes(contraintes:ContrainteEnvironnementale[]){
    let features: Feature[] = [];
    for(let c of contraintes){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }
    if(this.layerEtudeContraintes)
      this.layerEtudeContraintes.removeFrom(this.map);
    this.layerEtudeContraintes = this.createLayerContraintes(features).addTo(this.map);
  }

  displayEtudeContraintesExistantes(contraintes:ContrainteEnvironnementale[]){
    let features: Feature[] = [];
    for(let c of contraintes){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }
    if(this.layerEtudeContraintesExistantes)
      this.layerEtudeContraintesExistantes.removeFrom(this.map);
    this.layerEtudeContraintesExistantes = this.createLayerContraintes(features).addTo(this.map);
  }

  createLayerContraintes(features:Feature[]) {
    return this.cartoUtils.addGeoJsonContrainteLayers(features, this.map).
      eachLayer((layer:any) => {
        this.contrainteService.contrainteEnvironnetaleChangeOpacitySource$.subscribe(contrainte => {
          this.changeOpacityLayerContrainte(layer,contrainte);
        });
        this.contrainteService.contrainteEnvironnetaleRemoveSource$.subscribe(contrainte => {
          this.customRemoveLayerForFeature(layer,contrainte.feature);
        });
      })
  }

  displayEtudeChantiers(chantiers:Chantier[]){
    let features: Feature[] = [];
    for(let c of chantiers){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }
    if(this.layerEtudeChantiers)
      this.layerEtudeChantiers.removeFrom(this.map);
    this.layerEtudeChantiers = this.createLayerChantiers(features).addTo(this.map);
  }

  displayEtudeChantiersExistants(chantiers:Chantier[]){
    let features: Feature[] = [];
    for(let c of chantiers){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }

    if(this.layerEtudeChantiersExistants)
      this.layerEtudeChantiersExistants.removeFrom(this.map);
    this.layerEtudeChantiersExistants = this.createLayerChantiers(features).addTo(this.map);
  }

  createLayerChantiers(features:Feature[]) {
    return this.cartoUtils.addGeoJsonChantierLayers(features,this.map,this.selectedYear).
      eachLayer((layer:any) => {
        this.chantiersService.chantierChangeClassAffichageSource$.subscribe(chantier => {
          this.changeClassLayerChantier(layer,chantier);
        });
        this.chantiersService.chantierRemoveSource$.subscribe(chantier => {
          this.customRemoveLayerForFeature(layer,chantier.feature);
        });
        this.chantiersService.chantierChangeFeatureSource$.subscribe(chantier => {
          this.changeFeatureChantier(layer,chantier);
        });
      })
  }

  displayEtudeInstallationStockages(installationStockages:InstallationStockage[]){
    let features: Feature[] = [];
    for(let c of installationStockages){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }
    if(this.layerEtudeInstallationStockages)
      this.layerEtudeInstallationStockages.removeFrom(this.map);
    this.layerEtudeInstallationStockages = this.createLayerInstallationStockages(features).addTo(this.map);
    // Permet de mettre à jour les layers
    this.dateService.changeSelectedYear(this.selectedYear);
  }

  displayEtudeInstallationStockagesExistantes(installationStockages:InstallationStockage[]){
    let features: Feature[] = [];
    for(let c of installationStockages){
      let feat = c.feature;
      feat.properties!['afficher'] = c.afficher;
      features.push(feat);
    }
    if(this.layerEtudeInstallationStockagesExistantes)
      this.layerEtudeInstallationStockagesExistantes.removeFrom(this.map);
    this.layerEtudeInstallationStockagesExistantes = this.createLayerInstallationStockages(features).addTo(this.map);
    // Permet de mettre à jour les layers
    this.dateService.changeSelectedYear(this.selectedYear);
  }

  createLayerInstallationStockages(features:Feature[]) {
    return this.cartoUtils.addGeoJsonInstallationStockageLayers(features, this.map).
      eachLayer((layer:any) => {
        this.installationStockageService.installationStockageChangeOpacitySource$.subscribe(installationStockage => {
          this.changeOpacityLayerInstallationStockage(layer,installationStockage);
        });
        this.installationStockageService.installationStockageRemoveSource$.subscribe(installationStockage => {
          this.customRemoveLayerForFeature(layer,installationStockage.feature);
        });
        this.installationStockageService.installationStockageChangeFeatureSource$.subscribe(installationStockage => {
          this.changeFeatureInstallationStockage(layer,installationStockage);
        });
      })
  }

  displayEtudeInstallationStockagesNonActiveExistantes(installationStockages:InstallationStockage[]){
    let features: Feature[] = [];
    for(let c of installationStockages){
      let feat = c.feature;
      features.push(feat);
    }
    if(this.layerEtudeInstallationStockagesWithNonActive)
      this.layerEtudeInstallationStockagesWithNonActive.removeFrom(this.map);
    this.layerEtudeInstallationStockagesWithNonActive = this.createLayerInstallationStockagesNonActive(features).addTo(this.map);
  }

  createLayerInstallationStockagesNonActive(features:Feature[]) {
    return this.cartoUtils.addGeoJsonInstallationStockageNonActiveLayers(features, this.map).
      eachLayer((layer:any) => {
        // Assumez que layer est une instance de LayerGroup, vous pouvez itérer sur chaque layer dans le groupe
        layer.eachLayer((subLayer: any) => {
          this.installationStockageService.installationStockageNonActiveChangeOpacitySource$.subscribe(installationStockage => {
            this.changeOpacityLayerInstallationStockageNonActive(subLayer,installationStockage);
          });
          this.installationStockageService.installationStockageNonActiveRemoveSource$.subscribe(installationStockage => {
            this.customRemoveLayerForFeature(subLayer,installationStockage.feature);
          });
          this.installationStockageService.installationStockageChangeFeatureSource$.subscribe(installationStockage => {
            this.changeFeatureInstallationStockage(layer,installationStockage);
          });
        });
      })
  }

  addLayerTerritoire(data: any, type: string) {
    if (this.layerTerritoire)
      this.layerTerritoire.removeFrom(this.map);
    this.onChangeLayerTerritoire(type);
    this.layerTerritoire = this.addGeoJsonLayersClick(data.features, type);
    this.layerTerritoire.addTo(this.map);
  }

  addLayerAssociationZonage(data: any, type: string) {
    this.layerZonageIn = this.cartoUtils.addlayerOnMapInZone(this.map, this.layerZonageIn, data.features, type);
    this.layerZonageOut = this.cartoUtils.addlayerOnMapOutZone(this.map, this.layerZonageOut, data.features, type);

    this.layerZonageOut.eachLayer((layer:any) => {
      layer.on('mouseover', (event: any) => {
        this.map.openPopup(
          L.popup({closeButton:false,autoPan:false})
          .setLatLng(event.latlng)
          .setContent("Le zonage sélectionné sort du territoire, seule la partie incluse dans le territoire sera prise en compte dans l'étude en cours.")
          .openOn(this.map));
      });

      layer.on('mouseout', (event: any) => {
        this.map.closePopup();
      });
    })
  }

  addLayerAssociationZonageImport(data: any, type: String) {
    this.layerZonageImportIn = this.cartoUtils.addlayerOnMapInZone(this.map, this.layerZonageImportIn, data.features, type);
    this.layerZonageImportOut = this.cartoUtils.addlayerOnMapOutZone(this.map, this.layerZonageImportOut, data.features, type);

    this.layerZonageImportOut.eachLayer((layer:any) => {
      layer.on('mouseover', (event: any) => {
        this.map.openPopup(
          L.popup({closeButton:false,autoPan:false})
          .setLatLng(event.latlng)
          .setContent("Le zonage sélectionné sort du territoire, seule la partie incluse dans le territoire sera prise en compte dans l'étude en cours.")
          .openOn(this.map));
      });

      layer.on('mouseout', (event: any) => {
        this.map.closePopup();
      });
    })
  }

  toggleLayerZonage(zone: any) {
    if(zone === 'actualiser'){
      this.actualiserZonage();
      return;
    }
    if ((zone || zone === '') && this.layerZonageIn) {
      this.layerZonageIn.removeFrom(this.map);
      this.layerZonageOut.removeFrom(this.map);
    }
    if (zone !== '' && zone !== this.typeZonage)
      this.callServiceAssociationZonage(zone)
    this.typeZonage = zone;
  }

  actualiserZonage(){
    if (this.layerZonageIn && this.typeZonage) {
      this.layerZonageIn.removeFrom(this.map);
      this.layerZonageOut.removeFrom(this.map);
      this.callServiceAssociationZonage(this.typeZonage);
    }
  }

  selectedYearEtudeEvent(year : number){
    this.selectedYear = year;
    if (this.listPopulation && this.etudeComponent.actualStepIndex === 2) {
      this.onUploadFile({ listPopulation: this.listPopulation, selectedYear: this.selectedYear });
    }

    if(this.displayEtude !== 'displayNone'){
      this.scenarioCalculService.displayScenarioCalculVentilationAnnee(this.selectedYear);
      this.scenarioCalculService.displayScenarioCalculProjectionAnnee(this.selectedYear);
    }
    if(this.displaySuiviEtude !== 'displayNone'){
      this.scenarioCalculService.displayScenarioCalculProjectionSuiviEtudeActualiser();
    }

    if (this.activeLayer.includes('etablissementsEtude')) {
      this.markersUtilsService.resetMarker();
      this.panneauLateralComponent.visiblePanneauLateral = false;
      this.panneauLateralComponent.carriere = undefined;
      this.panneauLateralComponent.forceRerender();
      this.callEtablissementService('etablissementsEtude');
    }

    ['EtablissementNaturel','EtablissementRecycle','EtablissementNaturelRecycle'].forEach(l =>
      { let etab = false
        if (this.activeLayer.includes(l) && this.displayEtude === 'displayNone') {
          etab = true
          this.callEtablissementService(l);
        }
        if (etab) {
          this.panneauLateralComponent.visiblePanneauLateral = false;
          this.panneauLateralComponent.forceRerender();
        }
      }
    );
  }

  onUploadFile(event: { listPopulation: PopulationDTO[], selectedYear: number }) {
    const { listPopulation, selectedYear } = event;
    // Créez un objet pour faciliter la recherche des données de population par codeZone
    this.listPopulation = listPopulation;
    this.zonageFromEtude.populations = listPopulation;
    // Filtrer les données de population en fonction de l'année sélectionnée
    const filteredPopulation = listPopulation.filter(
      (population) =>  population.annee == selectedYear
    );
    const populationByCodeZone: { [codeZone: string]: PopulationDTO } = {};
    for (const population of filteredPopulation) {
      populationByCodeZone[population.codeZone] = population;
    }

    this.layerZonageFromEtude.eachLayer((featureGroup: any) => {
      featureGroup.eachLayer((layer: any) => {
        const codeZone = layer.feature.properties.code;
        const nomZone = layer.feature.properties.nom;
        const populationData = populationByCodeZone[codeZone];
        if(populationData != undefined)
          layer.setStyle({
            fillColor: 'var(--blue-200)',
            fillOpacity: 0.6
          })
        const handleMouseOver = (event: any) => {
          let popupContent = '';

          if (populationData) {
            popupContent = `
              <div style="text-align: center; font-size: 1.2em;">
                <strong>Nom zone : ${nomZone} Code zone : (${codeZone})</strong>
              </div>
              <br>
              <strong>Population Basse :</strong> ${populationData.populationBasse}<br>
              <strong>Population Centrale :</strong> ${populationData.populationCentrale}<br>
              <strong>Population Haute :</strong> ${populationData.populationHaute}
            `;

          } else {
            popupContent = `
              <div style="text-align: center; font-size: 1.2em;">
                <strong>Nom zone : ${nomZone} Code zone : (${codeZone})</strong>
              </div>
              <br>
              <strong>Aucune donnée de population disponible pour l'année ${selectedYear}</strong>
            `;
          }

          const popup = L.popup()
            .setLatLng(event.latlng)
            .setContent(popupContent)
            .openOn(this.map);
        };

        const handleMouseOut = (event: any) => {
          this.map.closePopup();
        };

        if (layer instanceof L.Polygon ) {
          // Si la couche est un polygone ou un multipolygone, attachez directement les gestionnaires d'événements
          layer.on('mouseover', handleMouseOver);
          layer.on('mouseout', handleMouseOut);
        } else {
          // Sinon, parcourez les sous-couches (polygones) et attachez les gestionnaires d'événements
          layer.eachLayer((polygon: L.Layer) => {
            polygon.on('mouseover', handleMouseOver);
            polygon.on('mouseout', handleMouseOut);
          });
        }
      });
    });
  }

  toggleLayerTerritoire(territoire: any) {
    if (territoire === '' && this.layerTerritoire) {
      this.layerTerritoire.removeFrom(this.map);
    }
    if (territoire != '' && territoire != this.typeTerritoire){
      this.stopAssociationZonageSubscription();
      this.callServiceTerritoire(territoire)
    }
    else if (territoire != '' && this.layerTerritoire)
      this.layerTerritoire.addTo(this.map)
    this.typeTerritoire = territoire;
  }

  toggleLayer(type: String) {
    if (this.activeLayer.includes(type)) {
      this.activeLayer = this.arrayUtils.arrayRemoveAny(this.activeLayer, type);
      if ('epci' === type) {
        this.layerEpci.removeFrom(this.map);
      }
      if ('zoneemploi' === type) this.layerZoneemploi.removeFrom(this.map);
      if ('bassinvie' === type) {
        this.layerBassinvie.removeFrom(this.map);
      }
      if ('commune' === type) {
        this.layerCommune.removeFrom(this.map);
      }
      if ('departement' === type) this.layerDepartement.removeFrom(this.map);
      if ('region' === type) this.layerRegion.removeFrom(this.map);
      if ('EtablissementNaturel' === type || 'EtablissementRecycle' === type || 'EtablissementNaturelRecycle' === type){
        if('EtablissementNaturel' === type)
          this.layerMarkersNaturel.removeFrom(this.map);
        if('EtablissementRecycle' === type)
          this.layerMarkersRecycle.removeFrom(this.map);
        if('EtablissementNaturelRecycle' === type)
          this.layerMarkersNaturelRecycle.removeFrom(this.map);
        let clicked: Feature = this.markersUtilsService.getSelectedMarker();
        if ((clicked?.properties?.origin_mat === "Matériaux naturels" && 'EtablissementNaturel' === type)
          ||(clicked?.properties?.origin_mat === "Matériaux recyclés" && 'EtablissementRecycle' === type)
          ||(clicked?.properties?.origin_mat === "Matériaux naturels et recyclés" && 'EtablissementNaturelRecycle' === type)) {
          this.markersUtilsService.resetMarker()
          this.panneauLateralComponent.visiblePanneauLateral = false;
        }
      }
    } else {
      this.activeLayer.push(type);
      if ('EtablissementNaturel' === type || 'EtablissementRecycle' === type || 'EtablissementNaturelRecycle' === type){
        this.callEtablissementService(type.toString());
      } else {
        this.callService(type);
      }
    }
  }

  toggleUserLayer(id : number){
    this.idRegionEtude = id;
    if(this.layerUserRegion){
      this.layerUserRegion.removeFrom(this.map);
    }
    this.regionService.findRegionFromId(id,this.geometryReducePrecisionService.getCoeff('region'.toString(), this.map.getZoom())).subscribe({
      next: (data:any) => {
        this.typeTerritoire = 'region';
        this.layerUserRegion = this.cartoUtils.addlayerOnMap(this.map,this.layerUserRegion,[data],'region');
        this.featuresTerritoires = [data];

        if(!this.focusOnRegion){
          this.map.flyTo(this.layerUserRegion.getLayers()[0].getLayers()[0].getBounds().getCenter(),8);
          this.focusOnRegion = true;
        }
      },
      error: (err:Error) => console.log("Erreur appel region"),
    })
  }

  removeTileLayer(layer: String) {
    const tileLayer = L.tileLayer(layer.valueOf());
    tileLayer.removeFrom(this.map);
  }

  addTileLayer(layer: String) {
    const tileLayer = L.tileLayer(layer.valueOf());
    tileLayer.addTo(this.map);
  }

  focusOnMarker() {
    this.map.flyTo(this.panneauLateralComponent.carriere.getLatLng(), 12);
  }

  flyToLayer(type:String){
    if(type === "EtudeTerritoire"){
      if(this.layerEtudeTerritoire)
        this.map.flyToBounds(this.layerEtudeTerritoire.getLayers()[0].getLayers()[0].getBounds().pad(0.1));
    }
  }

  changeDisplay(name: string) {
    if (name === '0') {
      this.activeLayer = this.arrayUtils.arrayRemoveAny(this.activeLayer, 'etablissementsEtude');
      this.displayConfig = '';
      this.displayEtude = 'displayNone';
      this.displaySuiviEtude = 'displayNone';
      this.displayImportEtude = 'displayNone';
      this.removeLayersForEtude();
      this.stopAssociationZonageSubscription();
      this.activeLayersForCarto();
      this.openSideBarEvent(true);

      this.configCartoComponent.initEtudePublic();
    }
    if (name === '1') {
      this.displayConfig = 'displayNone';
      this.displayEtude = '';
      this.displaySuiviEtude = 'displayNone';
      this.displayImportEtude = 'displayNone';
      this.removeLayersForEtude();
      this.stopAssociationZonageSubscription();
      this.openSideBarEvent(true);
    }
    if (name === '2') {
      this.displayConfig = 'displayNone';
      this.displayEtude = 'displayNone';
      this.displayImportEtude = 'displayNone';
      this.displaySuiviEtude = '';
      this.removeLayersForEtude();
      this.stopAssociationZonageSubscription();
      this.suiviEtudeComponent.openEtudeSuiviSideBar();
    }
    if (name === '3') {
      this.displayConfig = 'displayNone';
      this.displayEtude = 'displayNone';
      this.displayImportEtude = '';
      this.displaySuiviEtude = 'displayNone';
      this.removeLayersForEtude();
      this.stopAssociationZonageSubscription();
      this.openSideBarImportEtudeEvent(false);
    }
  }

  openSideBarEvent(open: boolean) {
    this.configCartoComponent.visibleConfigCarto = open;
    this.etudeComponent.openCloseSideBar(open);
    this.suiviEtudeComponent.visibleAfficherSuiviEtudeSideBar = open;
  }

  openSideBarImportEtudeEvent(open: boolean) {
    this.openSideBarEvent(open);
    this.importEtudeComponent.openCloseSideBar(!open);
  }

  activeLayersForCarto() {
    for (let type of this.activeLayer) {
      if ( ['EtablissementNaturel','EtablissementRecycle','EtablissementNaturelRecycle'].includes(type.toString())) {
        this.callEtablissementService(type.toString());
      }
      else {
        this.callService(type.toString());
      }
    }

    if(this.etudeConsulte)
      this.displayEtudeConsulte(this.etudeConsulte);
  }

  clearLayers(dreal:boolean){
    if(this.layerMarkersNaturel)
      this.layerMarkersNaturel.removeFrom(this.map);
    if(this.layerMarkersRecycle)
      this.layerMarkersRecycle.removeFrom(this.map);
    if(this.layerMarkersNaturelRecycle)
      this.layerMarkersNaturelRecycle.removeFrom(this.map);
    if (this.layerRegion)
      this.layerRegion.removeFrom(this.map);
    if (this.layerDepartement)
      this.layerDepartement.removeFrom(this.map);
    if (this.layerEpci)
      this.layerEpci.removeFrom(this.map);
    if (this.layerBassinvie)
      this.layerBassinvie.removeFrom(this.map);
    if (this.layerZoneemploi)
      this.layerZoneemploi.removeFrom(this.map);
    if (this.layerCommune)
      this.layerCommune.removeFrom(this.map);
    if (this.layerTerritoire)
      this.layerTerritoire.removeFrom(this.map);
    if (this.layerZonageIn)
      this.layerZonageIn.removeFrom(this.map);
    if (this.layerZonageOut)
      this.layerZonageOut.removeFrom(this.map);
    if (this.layerZonageImportIn)
      this.layerZonageImportIn.removeFrom(this.map);
    if (this.layerZonageImportOut)
      this.layerZonageImportOut.removeFrom(this.map);
    if(this.layerZonageFromEtude)
      this.layerZonageFromEtude.removeFrom(this.map);
    if(this.layerEtudeTerritoire)
      this.layerEtudeTerritoire.removeFrom(this.map);
    if(this.layerEtudeContraintes)
      this.layerEtudeContraintes.removeFrom(this.map);
    if(this.layerEtudeContraintesExistantes)
      this.layerEtudeContraintesExistantes.removeFrom(this.map);
    if(this.layerEtudeInstallationStockages)
      this.layerEtudeInstallationStockages.removeFrom(this.map);
    if(this.layerEtudeInstallationStockagesExistantes)
      this.layerEtudeInstallationStockagesExistantes.removeFrom(this.map);
    if(this.layerEtudeChantiers)
      this.layerEtudeChantiers.removeFrom(this.map);
    if(this.layerEtudeChantiersExistants)
      this.layerEtudeChantiersExistants.removeFrom(this.map);
    if(this.layerEtudeInstallationStockages)
      this.layerEtudeInstallationStockages.removeFrom(this.map);
    if(this.layerEtudeInstallationStockagesExistantes)
      this.layerEtudeInstallationStockagesExistantes.removeFrom(this.map);
    if(this.layerEtudeInstallationStockagesWithNonActive)
    this.layerEtudeInstallationStockagesWithNonActive.removeFrom(this.map);
    if(this.layerEtablissementsEtude)
      this.layerEtablissementsEtude.removeFrom(this.map);
    this.layerMarkersNaturel = undefined;
    this.layerMarkersRecycle = undefined;
    this.layerMarkersNaturelRecycle = undefined;
    this.layerRegion = undefined;
    this.layerDepartement = undefined;
    this.layerCommune = undefined;
    this.layerBassinvie = undefined;
    this.layerZoneemploi = undefined;
    this.layerEpci = undefined;
    this.layerTerritoire = undefined;
    this.layerZonageIn = undefined;
    this.layerZonageOut = undefined;
    this.layerZonageImportIn = undefined;
    this.layerZonageImportOut = undefined;
    this.layerZonageFromEtude = undefined;
    this.layerEtudeTerritoire = undefined;
    this.layerEtudeContraintes = undefined;
    this.layerEtudeContraintesExistantes = undefined;
    this.layerEtudeInstallationStockages = undefined;
    this.layerEtudeInstallationStockagesExistantes = undefined;
    this.layerEtudeChantiers = undefined;
    this.layerEtudeChantiersExistants = undefined;
    this.layerEtudeInstallationStockages = undefined;
    this.layerEtudeInstallationStockagesExistantes = undefined;
    this.layerEtudeInstallationStockagesWithNonActive = undefined;
    this.layerEtablissementsEtude = undefined;

    if(dreal){
      if(this.layerUserRegion){
        this.layerUserRegion.removeFrom(this.map);
      }
      this.layerUserRegion = undefined;
    }

    if(this.toolTipTerritoire != null){
      this.map.closeTooltip(this.toolTipTerritoire);
      this.toolTipTerritoire = null;
    }
    if(this.toolTipZones.length !== 0){
      this.toolTipZones.forEach(t => this.map.closeTooltip(t));
      this.toolTipZones = [];
    }

    if(this.toolTipEtudeConsulte != null){
      this.toolTipEtudeConsulte.forEach((ts:L.Tooltip[],k:any) => {
        ts.forEach(t => this.map.closeTooltip(t))
      });
      this.toolTipEtudeConsulte = new Map();
    }

    if(this.layerEtudeConsultation != null){
      this.layerEtudeConsultation.forEach((layer:L.Layer,k:any) => layer.removeFrom(this.map));
      this.layerEtudeConsultation = new Map();
    }

    if(this.layerEtudeEtablissementCarto != null){
      this.layerEtudeEtablissementCarto.forEach((r:L.LayerGroup<any>,k:any) => r.removeFrom(this.map));
      this.layerEtudeEtablissementCarto = new Map();
    }

    if(this.etudeConsulteSubscription != null){
      this.etudeConsulteSubscription.forEach((r:any,k:any) => r.unsubscribe());
    }
    this.etudeConsulteSubscription = new Map();

    this.layersService.clearEtudeConsultation();

    this.stopSubscriptionForClearLayers();
    this.stopAssociationZonageSubscription();
  }

  removeLayersForEtude() {
    this.markersUtilsService.resetMarker();
    this.panneauLateralComponent.visiblePanneauLateral = false;
    this.panneauLateralComponent.carriere = undefined;
    this.panneauLateralComponent.forceRerender();
    this.clearLayers(true);
    this.featuresTerritoires = new Array();
    this.typeTerritoire = '';
    this.typeZonage = '';
    this.idRegionEtude = undefined;
    this.focusOnRegion = false;
  }

  removeLayersForEtudeDREAL() {
    this.clearLayers(false);
    this.typeZonage = '';
  }

  territoireEvent(feature: Feature) {
    this.associerzonageservice.onTerritoireStateChange(feature);
  }

  addGeoJsonLayersClick(features: any[], type: String) {
    let layerGroup = L.layerGroup();
    for (let feature of features) {
      this.setOnClickLayer(this.cartoUtils.createGeoJsonClick(this.featuresTerritoires, feature, type), feature, type)
        // Ajout du nom en Popup
        .bindPopup(feature.properties!.nom)
        // Ajout sur la map
        .addTo(layerGroup);
    }
    return layerGroup;
  }

  onChangeLayerTerritoire(type: String) {
    if (this.typeLayerTerritoire != undefined && this.typeLayerTerritoire != type) {
      this.featuresTerritoires = new Array();
    }
    this.typeLayerTerritoire = type;
  }

  private setOnClickLayer(layer: L.Layer, feature: Feature, type: String): L.Layer {
    return layer.on('click', (layerEvent) => {
      if (this.arrayUtils.includeFeature(this.featuresTerritoires, layerEvent.sourceTarget.feature)) {
        this.featuresTerritoires = this.arrayUtils.arrayRemoveFeature(this.featuresTerritoires, layerEvent.sourceTarget.feature);
        this.territoireEvent(layerEvent.sourceTarget.feature);
      }
      else {
        this.featuresTerritoires.push(layerEvent.sourceTarget.feature);
        this.territoireEvent(layerEvent.sourceTarget.feature);
      }
      layerEvent.target.setStyle(this.styleLayersUtils.getStyleClicked(this.featuresTerritoires, feature, type));
    });
  }

  stopAssociationZonageSubscription(){
    if(this.zonageInTerritoireSubscription){
      this.zonageInTerritoireSubscription.unsubscribe();
    }
    if(this.territoireLayerSubscription){
      this.territoireLayerSubscription.unsubscribe();
    }
    if(this.zonageImportBeforeAddSubscription){
      this.zonageImportBeforeAddSubscription.unsubscribe()
    }
  }

  stopSubscriptionForClearLayers(){
    if(this.layerEtablissementsEtudeSubscription){
      this.layerEtablissementsEtudeSubscription.unsubscribe();
    }
    if(this.zonageFromEtudeSubscription){
      this.zonageFromEtudeSubscription.unsubscribe();
    }
    this.stopAssociationZonageSubscription();
  }

  stopSubscription(){
    if (this.firstSubscription) {
      this.firstSubscription.unsubscribe();
    }
    if (this.secondSubscription) {
      this.secondSubscription.unsubscribe();
    }
    if (this.menuSourceSubscription) {
      this.menuSourceSubscription.unsubscribe();
    }
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
	  if (this.associationZonageResetSubscription) {
      this.associationZonageResetSubscription.unsubscribe();
    }
    if (this.toastSourceSubscription) {
      this.toastSourceSubscription.unsubscribe();
    }
    if (this.layersServiceSubscription) {
      this.layersServiceSubscription.unsubscribe();
    }
    if (this.layersEtudeServiceSubscription) {
      this.layersEtudeServiceSubscription.unsubscribe();
    }
    if (this.layersRefRegionServiceSubscription) {
      this.layersRefRegionServiceSubscription.unsubscribe();
    }
    if (this.extraLayersServiceSubscription) {
      this.extraLayersServiceSubscription.unsubscribe();
    }
    if (this.extraLayersEtudeServiceSubscription) {
      this.extraLayersEtudeServiceSubscription.unsubscribe();
    }
    if (this.backgroundLayersServiceSubscription) {
      this.backgroundLayersServiceSubscription.unsubscribe();
    }
    if (this.layerEtudeInstallationStockagesSubscription) {
      this.layerEtudeInstallationStockagesSubscription.unsubscribe();
    }
    if (this.layerEtudeInstallationStockagesExistantesSubscription) {
      this.layerEtudeInstallationStockagesExistantesSubscription.unsubscribe();
    }
    if (this.layerEtudeInstallationStockagesWithNonActiveSubscription) {
      this.layerEtudeInstallationStockagesWithNonActiveSubscription.unsubscribe();
    }
    if (this.layerEtudeContraintesSubscription) {
      this.layerEtudeContraintesSubscription.unsubscribe();
    }
    if (this.layerEtudeContraintesExistantesSubscription) {
      this.layerEtudeContraintesExistantesSubscription.unsubscribe();
    }
    if (this.layerEtudeChantiersSubscription) {
      this.layerEtudeChantiersSubscription.unsubscribe();
    }
    if (this.layerEtudeChantiersExistantsSubscription) {
      this.layerEtudeChantiersExistantsSubscription.unsubscribe();
    }

    this.stopSubscriptionForClearLayers();
  }

  ngOnDestroy(): void {
    this.stopSubscription();
  }
}
