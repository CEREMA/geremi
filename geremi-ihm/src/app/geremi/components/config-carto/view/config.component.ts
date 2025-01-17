import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { ExtraLayer } from "../../carto/model/extra-layer.model";
import { ExtraLayerService } from "../../carto/service/extra-layer.service";
import { ConfigService } from "../service/config.service";
import { ZoomMiniService } from "../service/zoom-mini.service";
import { ZoomChangeService } from "../service/zoom-change.service";
import { LayersService } from "../../carto/service/layers.service";
import { Subscription } from "rxjs";
import { ArrayUtilsService } from "src/app/shared/service/arrayUtils.service";
import { EtudeService } from "../../etude/service/etude.service";
import { Etude } from "../../etude/model/etude.model";
import { Region } from "../../etude/model/region.model";

@Component({
  selector: 'app-config-carto',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss'],
  providers: [ZoomMiniService,ArrayUtilsService]
})
export class ConfigCartoComponent implements OnInit{
  @Output() openSideBarEvent = new EventEmitter<boolean>();
  @Output() selectedYearConfigEvent = new EventEmitter<number>();

  extraLayers: Array<ExtraLayer> = this.extraLayerService.all();
  activeExtraLayer: Array<String> = new Array<String>();

  visibleConfigCarto: any = false;
  @Input() selectedYear: number;
  isEtablissementNaturelSelected: boolean = false;
  isEtablissementRecycleSelected: boolean = false;
  isEtablissementNaturelRecycleSelected: boolean = false;

  activeCommune: boolean;
  activeBassinDeVie: boolean;
  activeEpci: boolean;

  communeValue: boolean = false;
  bassinvieValue: boolean = false;
  epciValue: boolean = false;

  listeEtudePublic:Map<number,Etude[]> = new Map();
  listeRegion:Region[] = [];
  etudeConsulte:Etude[] = [];

  etudeMapConsulte:any /*Map<number,Etude[]>*/ = new Map();

  private layersServiceSubscription: Subscription;
  private extraLayersServiceSubscription: Subscription;

  constructor(private cdr: ChangeDetectorRef,
    private arrayUtils: ArrayUtilsService,
    private extraLayerService: ExtraLayerService,
    private layersService: LayersService,
    private configService: ConfigService,
    private etudeService: EtudeService,
    private zoomMiniService: ZoomMiniService,
    private zoomChangeService: ZoomChangeService) {
  }

 ngOnInit() {

    this.activeCommune=true;
    this.activeBassinDeVie=true;
    this.activeEpci=true;
    this.zoomChangeService.zommChangeSource$.subscribe((data) => {
      if(this.zoomMiniService.getZoomMini('commune') <= data) {
        this.activeCommune=false;
      } else {
        this.activeCommune=true;
        if(this.communeValue===true){
          this.layersService.onLayersChange('commune');
        }
        this.communeValue=false;
      }
      if(this.zoomMiniService.getZoomMini('bassindevie') <= data) {
        this.activeBassinDeVie=false;
      } else {
        this.activeBassinDeVie=true;
        if(this.bassinvieValue===true){
          this.layersService.onLayersChange('bassinvie');
        }
        this.bassinvieValue=false;
      }
      if(this.zoomMiniService.getZoomMini('epci') <= data) {
        this.activeEpci=false;
      } else {
        this.activeEpci=true;
        if(this.epciValue===true){
          this.layersService.onLayersChange('epci');
        }
        this.epciValue=false;
      }
    });

    this.layersServiceSubscription = this.layersService.layersEtudeSource$.subscribe(type => {
      if (type === 'EtablissementNaturel') {
        this.isEtablissementNaturelSelected = !this.isEtablissementNaturelSelected;
      }
      if (type === 'EtablissementRecycle') {
        this.isEtablissementRecycleSelected = !this.isEtablissementRecycleSelected;
      }
      if (type === 'EtablissementNaturelRecycle') {
        this.isEtablissementNaturelRecycleSelected = !this.isEtablissementNaturelRecycleSelected;
      }
    });
    this.extraLayersServiceSubscription = this.layersService.extraLayersEtudeSource$.subscribe(type => {
      if (this.activeExtraLayer.includes(type)) {
        this.activeExtraLayer = this.arrayUtils.arrayRemoveAny(this.activeExtraLayer, type);
      } else {
        this.activeExtraLayer = this.arrayUtils.addAny(this.activeExtraLayer, type);
      }
    });
    this.initEtudePublic();
    this.layersService.clearEtudeConsultationSource$.subscribe(() =>{
      this.etudeConsulte = [];
      this.clearEtudeConsulte()
    })
  }


  toggleBackground(type: string) {
    this.layersService.onBackgroundLayersChange(type);
  }

  toggleLayer(type: string) {
    this.layersService.onLayersChange(type);
  }

  toggleExtraLayer(type: string) {
    this.layersService.onExtraLayersChange(type);
  }

  openConfigCarto() {
    this.visibleConfigCarto = true;
    this.cdr.detectChanges();
    this.openSideBarEvent.emit(true);
  }

  closeConfigCarto() {
    this.visibleConfigCarto = false;
    this.cdr.detectChanges();
    this.openSideBarEvent.emit(false);
  }

  handleYearChanged(event: any): void {
    if (this.isEtablissementNaturelSelected || this.isEtablissementRecycleSelected || this.isEtablissementNaturelRecycleSelected || this.etudeConsulte.length > 0) {
      this.selectedYearConfigEvent.emit(event);
    }
  }

  consulteEtude(etude:any,idRegion:number){
    if(this.etudeConsulte.map(e => e.proprietaire.id_region).includes(idRegion)){

      this.etudeConsulte = this.etudeConsulte.filter(function (ec) {
        return ec.proprietaire.id_region != etude.proprietaire.id_region || ec.id == etude.id;
      });

    }

    if(this.etudeMapConsulte.get(idRegion) == null || this.etudeMapConsulte.get(idRegion)?.length == 0){
      this.etudeMapConsulte.set(idRegion,[etude])
    } else if(this.etudeMapConsulte.get(idRegion)?.map((e:Etude) => e.id).includes(etude.id)){
      this.etudeMapConsulte.set(idRegion,[]);
    } else {
      this.etudeMapConsulte.set(idRegion,[etude])
    }

    if(this.etudeConsulte.length > 0){
      if(this.isEtablissementNaturelRecycleSelected){
        this.layersService.onLayersChange('EtablissementNaturelRecycle');
        this.isEtablissementNaturelRecycleSelected = false;
      }
      if(this.isEtablissementNaturelSelected){
        this.layersService.onLayersChange('EtablissementNaturel');
        this.isEtablissementNaturelSelected = false;
      }
      if(this.isEtablissementRecycleSelected){
        this.layersService.onLayersChange('EtablissementRecycle');
        this.isEtablissementRecycleSelected = false
      }
    }

    this.layersService.displayEtudeConsultation(this.etudeConsulte);
  }

  isChecked(idRegion:number,etude:Etude){
    const index = this.etudeMapConsulte.get(idRegion)?.findIndex((e:Etude) => etude.id === e.id);
    if (index == null) {
      return false;
    }
    if (index !== -1) {
      return true;
    }
    return false;
  }

  choixEtabDisable(){
    return this.etudeConsulte.length > 0;
  }

  clearEtudeConsulte(){
    this.etudeMapConsulte = new Map();
    this.layersService.displayEtudeConsultation(this.etudeConsulte);
  }

  initEtudePublic(){
    this.etudeService.getAllEtudesPublic().subscribe({
      next:(value: any /*Map<Region,Etude[]>*/) => {
        this.listeRegion = [];
        let mapRegionEtude = new Map();

        Object.values(value).forEach((etudes:any) => {
          for(let e of etudes){
            let r = new Region()
            r.id = e.proprietaire.id_region;
            r.nom = e.proprietaire.nom_region;
            r.code = e.proprietaire.insee_region;

            if(!this.arrayUtils.includeId(this.listeRegion,r)){
              this.listeRegion.push(r);
            }

            if(mapRegionEtude.get(r.id) == null){
              mapRegionEtude.set(r.id, [e]);
            } else {
              let tmplist = mapRegionEtude.get(r.id);
              tmplist.push(e);
              mapRegionEtude.set(r.id,tmplist);
            }
          }
        });
        this.listeEtudePublic = mapRegionEtude;

        for(let reg of this.listeRegion){
          this.etudeMapConsulte.set(reg.id,[]);
        }

        this.clearEtudeConsulte();
      }
    });
  }
}
