import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { ConfigService } from '../../config-carto/service/config.service';
import { DateService } from '../../carto/service/date.service';

@Component({
  selector: 'app-legend-container',
  templateUrl: './legend-container.component.html',
  styleUrls: ['./legend-container.component.scss']
})
export class LegendContainerComponent implements OnInit {

  @Input() ngClassCondition: any;
  @Output() yearChanged = new EventEmitter<number>();
  @Input() selectedYear: number;
  @Input() selectedYearMax: number;
  @Input() selectedYearMin: number;
  yearOptions: SelectItem[];
  openLegendContainer: boolean = false;


  constructor(private configService: ConfigService,
    private dateService: DateService) { }


  ngOnInit(): void {
    //Initialiser la liste de Date avec les Dates des Etablissement
    this.configService.findEtablissement().then((yearOptions: SelectItem[]) => {
      this.yearOptions = yearOptions;
      this.selectedYear = this.yearOptions.length > 0 ? this.yearOptions[0].value : '';
      this.selectedYear = parseInt(this.selectedYear.toString());
      this.dateService.changeSelectedYear(this.selectedYear);
    });
  }


  onLegendChange(event: any): void {
    this.yearChanged.emit(event.value);
    this.dateService.changeSelectedYear(event.value);
  }

  findEtablissement() {
    this.configService.findEtablissement()
      .then((yearOptions: SelectItem[]) => {
        this.yearOptions = yearOptions;
        this.selectedYear = this.yearOptions.length > 0 ? this.yearOptions[0].value : '';
      });
  }

  isDisabled(): boolean {
    return this.selectedYearMax == 2099 || this.selectedYearMin == 2099;
  }

  onOpenAccordionLegende(event: any) {
    this.openLegendContainer = true;
  }
  onCloseAccordionLegende(event: any) {
    this.openLegendContainer = false;
  }
} 