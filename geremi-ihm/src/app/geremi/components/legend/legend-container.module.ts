import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { InputNumberModule } from 'primeng/inputnumber'; 
import { LegendContainerComponent } from './view/legend-container.component';
import { AccordionModule } from 'primeng/accordion';

@NgModule({
  declarations: [LegendContainerComponent],
  imports: [
    CommonModule, 
    FormsModule,
    InputNumberModule,
    AccordionModule  
  ],
  exports: [LegendContainerComponent]
})
export class LegendContainerModule { }
