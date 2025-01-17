import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CartoComponent } from "./view/carto.component";

@NgModule({
  imports: [RouterModule.forChild([
    { path: 'suivi-etude', component: CartoComponent },
    { path: 'import', component: CartoComponent },
    { path: 'etude', component: CartoComponent },
    { path: '', component: CartoComponent }, 
  ])],
  exports: [RouterModule]
})
export class CartoRoutingModule {
}
