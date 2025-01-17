import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { SidebarModule } from 'primeng/sidebar';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { RadioButtonModule } from 'primeng/radiobutton';
import { DividerModule } from 'primeng/divider';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { FileUploadModule } from 'primeng/fileupload';
import { EtudeComponent } from './etude.component';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CalendarModule } from 'primeng/calendar';
import { ConfigCartoModule } from '../config-carto/config.module';
import { AccordionModule } from 'primeng/accordion';
import { CartoConfigEtudeComponent } from './carto-config-etude/carto-config-etude.component';
import { MultiSelectModule } from 'primeng/multiselect';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { InputNumberModule } from 'primeng/inputnumber';
import { LegendContainerModule } from '../legend/legend-container.module';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ContrainteEnvironnementaleComponent } from './contraintes-environnementales/contrainte-env.component';
import { ChantiersEnvergureComponent } from './chantiers-envergure/chantiers-env.component';
import { InstallationStockageComponent } from './installation-stockage/installation-stockage.component';
import { DialogModule } from 'primeng/dialog';
import { AutresMateriauxComponent } from './autres-materiaux/autres-materiaux.component';
import { ScenarioModule } from './scenario/scenario.module';
import { ImportEtudeModule } from '../import-etude/import-etude.module';
import {KeyFilterModule} from "primeng/keyfilter";

@NgModule({
  declarations: [
    EtudeComponent,
    CartoConfigEtudeComponent,
    ContrainteEnvironnementaleComponent,
    ChantiersEnvergureComponent,
    InstallationStockageComponent,
    AutresMateriauxComponent
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
        ConfigCartoModule,
        AccordionModule,
        ReactiveFormsModule,
        MultiSelectModule,
        CardModule,
        InputNumberModule,
        LegendContainerModule,
        TooltipModule,
        DialogModule,
        ScenarioModule,
        ImportEtudeModule,

        RouterModule.forChild([
            {
                path: '',
                component: EtudeComponent
            }
        ]),
        KeyFilterModule
    ],
  exports: [
    EtudeComponent
  ]
})
export class EtudeModule { }
