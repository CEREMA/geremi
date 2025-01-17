import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { OAuthModule, OAuthStorage } from 'angular-oauth2-oidc';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { SharedModule } from './shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { MyStorageService } from './shared/core/storage/MyStorageService';
import { MyLocalStorageService } from './shared/core/storage/MyLocalStorageService';
import { environment } from '../environments/environment';
import { HttpInterceptorProviders } from './shared/core/interceptor';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ApplicationConfigService } from './shared/core/config/application-config.service';
import { DialogService, DynamicDialogModule, DynamicDialogRef } from 'primeng/dynamicdialog';
import { LeafletMarkerClusterModule } from "@asymmetrik/ngx-leaflet-markercluster";
import { AccordionModule } from "primeng/accordion";
import { CheckboxModule } from 'primeng/checkbox';
import { LeafletModule } from "@asymmetrik/ngx-leaflet";
import { LeafletDrawModule } from "@asymmetrik/ngx-leaflet-draw";
import { TranslateLoader, TranslateModule, TranslateService } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { HIGHLIGHT_OPTIONS, HighlightModule } from "ngx-highlightjs";
import { OverlayModule } from './geremi/components/overlay/overlay.module';

registerLocaleData(localeFr, 'fr');

@NgModule({
  declarations: [
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    DynamicDialogModule,
    LeafletModule,
    LeafletDrawModule,
    LeafletMarkerClusterModule,
    AccordionModule,
    CheckboxModule,
    SharedModule,
    HighlightModule,
    OverlayModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: environment.allowedUrls,
        sendAccessToken: true,
      },
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],
  providers: [
    /*MenuService, TopbarMenuService,*/ DialogService, MyLocalStorageService, DynamicDialogRef,
    {
      provide: OAuthStorage,
      useClass: MyStorageService,
    },
    HttpInterceptorProviders,
    {
      provide: LOCALE_ID,
      useFactory: () => {

      },
      deps: [],
    },
    {
      provide: LOCALE_ID,
      useFactory: (translate: TranslateService) => {
        return translate.getBrowserLang();
      },
      deps: [TranslateService],
    },
    {
      provide: HIGHLIGHT_OPTIONS,
      useValue: {
        coreLibraryLoader: () => import('highlight.js/lib/core'),
        languages: {
          typescript: () => import('highlight.js/lib/languages/typescript'),
          css: () => import('highlight.js/lib/languages/css'),
          xml: () => import('highlight.js/lib/languages/xml'),
          java: () => import('highlight.js/lib/languages/java')
        },
        // themePath: 'path-to-theme.css' // Optional, and useful if you want to change the theme dynamically
      }
    }
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor(applicationConfigService: ApplicationConfigService) {
    applicationConfigService.endpointPrefix = environment.serverApiUrl;
    applicationConfigService.endpointPrefixApiOpen = environment.serverApiOpenUrl;
    applicationConfigService.endpointGeoApiGouvFr = "https://geo.api.gouv.fr/";
  }
}

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, 'assets/i18n/');
}

