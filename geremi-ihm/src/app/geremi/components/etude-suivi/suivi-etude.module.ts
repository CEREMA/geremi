import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { CheckboxModule } from "primeng/checkbox";
import { DropdownModule } from "primeng/dropdown";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";
import { RadioButtonModule } from "primeng/radiobutton";
import { SidebarModule } from "primeng/sidebar";
import { TableModule } from "primeng/table";
import { SuiviEtudeComponent } from "./view/suivi-etude.component";
import { TooltipModule } from "primeng/tooltip";
import { LegendContainerModule } from "../legend/legend-container.module";

@NgModule({
  declarations: [
    SuiviEtudeComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    CheckboxModule,
    DropdownModule,
    ButtonModule,
    InputTextModule,
    InputTextareaModule,
    SidebarModule,
    TableModule,
    RadioButtonModule,
    TooltipModule,
    LegendContainerModule,
    
    RouterModule.forChild([
      {
        path: '',
        component: SuiviEtudeComponent
      }
    ])
  ],
  exports: [
    SuiviEtudeComponent
  ]
})
export class SuiviEtudeModule { }