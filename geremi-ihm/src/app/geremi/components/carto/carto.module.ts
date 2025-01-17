import { NgModule } from "@angular/core";
import { LeafletModule } from "@asymmetrik/ngx-leaflet";
import { LeafletMarkerClusterModule } from "@asymmetrik/ngx-leaflet-markercluster";
import { CartoRoutingModule } from "./carto-routing.module";
import { CartoComponent } from "./view/carto.component";
import { SidebarModule } from "primeng/sidebar";
import { AccordionModule } from 'primeng/accordion';
import { CheckboxModule } from 'primeng/checkbox';
import { CommonModule } from "@angular/common";
import { ConfigCartoModule } from "../config-carto/config.module";
import { PanneauLateralModule } from "../panneau-lateral/panneaulateral.module";
import { EtudeModule } from "../etude/etude.module";
import { CardModule } from 'primeng/card';
import { ToastModule } from "primeng/toast";
import { ConfirmDialogModule } from "primeng/confirmdialog";
import { OverlayModule } from "../overlay/overlay.module";
import { SuiviEtudeModule } from "../etude-suivi/suivi-etude.module";
import { ImportEtudeModule } from "../import-etude/import-etude.module";

@NgModule({
  declarations: [
    CartoComponent
  ],
  imports: [
    CommonModule,
    CartoRoutingModule,
    LeafletModule,
    LeafletMarkerClusterModule,
    SidebarModule,
    AccordionModule,
    CheckboxModule,
    PanneauLateralModule,
    ConfigCartoModule,
    EtudeModule,
    SuiviEtudeModule,
    ImportEtudeModule,
    CardModule,
    ToastModule,
    ConfirmDialogModule,
    OverlayModule
  ],
  providers: [

  ]
})
export class CartoModule {
}
