import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { UsersService } from '../../service/users.service';
import { mergeMap } from 'rxjs/operators';
import { Roles } from '../../enums/roles.enums';

@Injectable({
  providedIn: 'root',
})
export class CanModifyGuard implements CanActivate {
  constructor(private usersService: UsersService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.usersService.currentUsers) {
      if (this.usersService.userHasRole(Roles.Administrateur) || this.usersService.userHasRole(Roles.GestionnaireDeDonnees)) {
        return true;
      }
      return this.router.parseUrl('/');
    }
    return this.usersService.getCurrentUsers().pipe(
      mergeMap(res => {
        if (res.body) {
          this.usersService.currentUsers = res.body;
          if (this.usersService.userHasRole(Roles.Administrateur) || this.usersService.userHasRole(Roles.GestionnaireDeDonnees)) {
            return of(true);
          }
        }
        return of(this.router.parseUrl('/'));
      })
    );
  }
}
