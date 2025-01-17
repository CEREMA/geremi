import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ExtraLayer } from '../../carto/model/extra-layer.model';
import { ExtraLayerService } from '../../carto/service/extra-layer.service';
import { ConfigService } from '../../config-carto/service/config.service';
import { LayersService } from '../../carto/service/layers.service';
import { Subscription } from 'rxjs';
import { ArrayUtilsService } from 'src/app/shared/service/arrayUtils.service';

@Component({
  selector: 'app-carto-config-etude',
  templateUrl: './carto-config-etude.component.html',
  styleUrls: ['./carto-config-etude.component.scss'],
  providers: [ArrayUtilsService]
})
export class CartoConfigEtudeComponent implements OnInit {
  isCollapse = true;
  @Output() isCollapseChange = new EventEmitter<boolean>();
  isCarriereSelected: boolean = false;

  @Input() boutonVisible: boolean = false;

  extraLayers: Array<ExtraLayer> = this.extraLayerService.all();
  activeExtraLayer: Array<String> = new Array<String>();

  private layersServiceSubscription: Subscription;
  private extraLayersServiceSubscription: Subscription;

  constructor(private layersService: LayersService,
    private extraLayerService: ExtraLayerService,
    private arrayUtils: ArrayUtilsService,
    private configService: ConfigService) { }

  ngOnInit(): void {
    this.extraLayersServiceSubscription = this.layersService.extraLayersSource$.subscribe(type => {
      if (this.activeExtraLayer.includes(type)) {
        this.activeExtraLayer = this.arrayUtils.arrayRemoveAny(this.activeExtraLayer, type);
      } else {
        this.activeExtraLayer = this.arrayUtils.addAny(this.activeExtraLayer, type);
      }
    });
  }

  collapse() {
    this.isCollapse = !this.isCollapse;
    this.isCollapseChange.emit(this.isCollapse);
  }

  toggleBackground(type: string) {
    this.layersService.onBackgroundLayersChange(type);
  }

  toggleLayer(type: string) {
    this.layersService.onLayersEtudeChange(type);
  }

  toggleExtraLayer(type: string) {
    this.layersService.onExtraLayersEtudeChange(type);
  }

  ngOnDestroy():void{
    if(this.extraLayersServiceSubscription)
      this.extraLayersServiceSubscription.unsubscribe();
  }
}
