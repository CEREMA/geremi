import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DateService {
    private currentYear = new Date().getFullYear();
    private selectedYearSource = new BehaviorSubject<number>(this.currentYear);
    selectedYear$ = this.selectedYearSource.asObservable();
     

    constructor() { }

    changeSelectedYear(year: number): void {
      this.selectedYearSource.next(year);
    }
}
