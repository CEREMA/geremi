import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IndicComponent } from './components/indic/indic.component';

@NgModule({
  declarations: [
    IndicComponent
  ],
  imports: [CommonModule],
  exports: [
    IndicComponent
  ],
})
export class SharedModule {
}
