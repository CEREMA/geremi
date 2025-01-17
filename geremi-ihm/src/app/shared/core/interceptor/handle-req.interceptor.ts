import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from "@angular/router";

@Injectable()
export class HandleReqInterceptor implements HttpInterceptor {
  constructor(private router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      tap({
        error: (err: HttpErrorResponse) => {

          //TODO :  A REFAIRE

          if (err?.status === 500) {
          } else if (err.status === 401) {
            sessionStorage.clear();
            this.router.navigateByUrl('/deconnexion');
          }
        },
      })
    );
  }
}
