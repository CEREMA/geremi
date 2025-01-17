import { NgModule } from "@angular/core";
import { ImportEtudeComponent } from "./view/import-etude.component";
import { SidebarModule } from "primeng/sidebar";
import { DropdownModule } from "primeng/dropdown";
import { TableModule } from "primeng/table";
import { AutoCompleteModule } from "primeng/autocomplete";
import { TooltipModule } from "primeng/tooltip";
import { CheckboxModule } from "primeng/checkbox";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { InputTextModule } from "primeng/inputtext";
import { ButtonModule } from "primeng/button";
import { LegendContainerModule } from "../legend/legend-container.module";
import { FileUploadModule } from "primeng/fileupload";

@NgModule({
    declarations: [
        ImportEtudeComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        SidebarModule,
        DropdownModule,
        TableModule,
        AutoCompleteModule,
        TooltipModule,
        CheckboxModule,
        ReactiveFormsModule,
        InputTextModule,  
        ButtonModule,
        LegendContainerModule,
        FileUploadModule,
    ],
    exports: [
        ImportEtudeComponent
    ]
})
export class ImportEtudeModule {

}