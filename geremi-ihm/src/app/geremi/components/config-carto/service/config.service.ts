import { Injectable } from '@angular/core';
import { EtablissementService } from '../../carto/service/etablissement.service';
import { SelectItem } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  constructor(private etablissementService: EtablissementService) { }

  findEtablissement(): Promise<SelectItem[]> {
    return new Promise((resolve) => {
      this.etablissementService.getDistinctAnnees()
        .subscribe((uniqueAnnees: string[]) => {
          // Remplir yearOptions avec les années uniques
          const yearOptions = uniqueAnnees.map((annee: string) => {
            return { label: annee, value: annee };
          });

          resolve(yearOptions);
        });
    });
  }
}
