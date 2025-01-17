import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MainComponent } from './main/main.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: MainComponent,
        children: [
          {
            path: '',
            redirectTo: 'carto',
            pathMatch: 'full',
          },
          {
            path: 'carto',
            data: { breadcrumb: 'Cartographie' },
            loadChildren: () => import('../geremi/components/carto/carto.module').then(m => m.CartoModule)
          },
        ],
      },
    ]),
  ],
  exports: [RouterModule],
})
export class MainRoutingModule {
}
