import { NgModule } from "@angular/core";
import { ScenarioComponent } from "./view/scenario.component";
import { ScenarioGeneraliteComponent } from "./scenario-generalite/view/scenario-generalite.component";
import { ScenarioChantierComponent } from "./scenario-chantier/view/scenario-chantier.component";
import { ScenarioContrainteComponent } from "./scenario-contrainte/view/scenario-contrainte.component";
import { ScenarioInstallationComponent } from "./scenario-installation/view/scenario-installation.component";
import { ScenarioMateriauComponent } from "./scenario-materiau/view/scenario-materiau.component";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AccordionModule } from "primeng/accordion";
import { AutoCompleteModule } from "primeng/autocomplete";
import { ButtonModule } from "primeng/button";
import { CalendarModule } from "primeng/calendar";
import { CardModule } from "primeng/card";
import { CheckboxModule } from "primeng/checkbox";
import { ConfirmDialogModule } from "primeng/confirmdialog";
import { DialogModule } from "primeng/dialog";
import { DividerModule } from "primeng/divider";
import { DropdownModule } from "primeng/dropdown";
import { FileUploadModule } from "primeng/fileupload";
import { InputNumberModule } from "primeng/inputnumber";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";
import { MessageModule } from "primeng/message";
import { MessagesModule } from "primeng/messages";
import { MultiSelectModule } from "primeng/multiselect";
import { RadioButtonModule } from "primeng/radiobutton";
import { SidebarModule } from "primeng/sidebar";
import { TableModule } from "primeng/table";
import { ToastModule } from "primeng/toast";
import { TooltipModule } from "primeng/tooltip";
import { LegendContainerModule } from "../../legend/legend-container.module";
import { ScenarioCreationComponent } from "./scenario-creation/view/scenario-creation.component";
import { ScenarioCalculComponent } from "./scenario-calcul/view/scenario-calcul.component";
import { SliderModule } from 'primeng/slider';

@NgModule({
    declarations:[
        ScenarioComponent,
        ScenarioGeneraliteComponent,
        ScenarioContrainteComponent,
        ScenarioChantierComponent,
        ScenarioInstallationComponent,
        ScenarioMateriauComponent,
        ScenarioCreationComponent,

        ScenarioCalculComponent
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
        DividerModule,
        MessagesModule,
        MessageModule,
        ConfirmDialogModule,
        ToastModule,
        FileUploadModule,
        AutoCompleteModule,
        CalendarModule,
        AccordionModule,
        ReactiveFormsModule,
        MultiSelectModule,
        CardModule,
        InputNumberModule,
        LegendContainerModule,
        TooltipModule,
        DialogModule,
        SliderModule
    ],
    exports: [
        ScenarioComponent
      ]
})
export class ScenarioModule {}