import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { AccordionModule } from "primeng/accordion";
import { CheckboxModule } from "primeng/checkbox";
import { SidebarModule } from "primeng/sidebar";
import { PanneauLateralComponent } from "./view/panneaulateral.component";

@NgModule({
    declarations: [
      PanneauLateralComponent,
    ],
    imports: [
      CommonModule,
      SidebarModule,
      AccordionModule,
      CheckboxModule
    ],
    exports: [
        PanneauLateralComponent
    ]
  })
  export class PanneauLateralModule {
  }