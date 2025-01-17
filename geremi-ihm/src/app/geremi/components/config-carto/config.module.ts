import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { AccordionModule } from "primeng/accordion";
import { CheckboxModule } from "primeng/checkbox";
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { SidebarModule } from "primeng/sidebar";
import { ConfigCartoComponent } from "./view/config.component";
import { LegendContainerModule } from "../legend/legend-container.module";
import { DialogModule } from 'primeng/dialog';

@NgModule({
    declarations: [
      ConfigCartoComponent,
    ],
    imports: [
      CommonModule,
      SidebarModule,
      AccordionModule,
      CheckboxModule,
      InputNumberModule,
      FormsModule,
      LegendContainerModule,
      DialogModule
    ],
    exports: [
        ConfigCartoComponent
    ]
  })
  export class ConfigCartoModule {
  }