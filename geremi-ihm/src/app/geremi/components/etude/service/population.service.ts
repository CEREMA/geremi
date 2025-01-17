import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PopulationDTO } from '../model/population.model';
import { ApplicationConfigService } from 'src/app/shared/core/config/application-config.service';

@Injectable({
    providedIn: 'root'
})
export class PopulationService {

    private url = this.applicationConfigService.getEndpointFor("/populations");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getAllPopulations(): Observable<PopulationDTO[]> {
        return this.http.get<PopulationDTO[]>(this.url);
    }

    getPopulationById(id: number): Observable<PopulationDTO> {
        return this.http.get<PopulationDTO>(`${this.url}/${id}`);
    }

    addPopulations(idEtude: number, populations: PopulationDTO[]): Observable<PopulationDTO[]> {
        return this.http.put<PopulationDTO[]>(`${this.url}/${idEtude}/add`, populations);
    }

    updatePopulation(id: number, population: PopulationDTO): Observable<PopulationDTO> {
        return this.http.post<PopulationDTO>(`${this.url}/${id}`, population);
    }

    deletePopulation(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
