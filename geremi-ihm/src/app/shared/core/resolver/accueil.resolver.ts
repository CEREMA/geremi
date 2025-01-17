import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { Users } from '../../entities/users.model';
import { UsersService } from '../../service/users.service';
import { HttpResponse } from '@angular/common/http';
import { take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AccueilResolver implements Resolve<HttpResponse<Users>> {
  constructor(private usersService: UsersService) {
  }

  resolve(): Observable<HttpResponse<Users>> {
    return this.usersService.getCurrentUsers().pipe(take(1));
  }
}
