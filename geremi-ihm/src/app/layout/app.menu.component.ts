import { Component, OnInit } from '@angular/core';
import { LayoutService } from './service/app.layout.service';
import { UsersService } from "../shared/service/users.service";
import { Roles } from "../shared/enums/roles.enums";
import pkg from '../../../../package.json';

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

  model: any[] = [];
  email: string = 'geremi@cerema.fr';
  version = pkg.version;

  constructor(public layoutService: LayoutService, public userService: UsersService) { }

  ngOnInit() {

    const userProfile:string = this.userService.currentUsers.libelle_profil;
    const notPublic = userProfile !== Roles.Public;
    const isAdminOrGestionnaire = userProfile === Roles.Administrateur || userProfile === Roles.GestionnaireDeDonnees;


    this.model = [
      { label: 'Cartographie', icon: 'pi pi-map', routerLink: ['/carto'] },
      { label: 'Etude', icon: 'pi pi-file-edit', routerLink: ['/carto/etude'], visible: notPublic },
      { label: 'Suivi Etude', icon: 'pi pi-file', routerLink: ['/carto/suivi-etude'], visible: notPublic },
      { label: 'Import des études', icon: 'pi pi-upload', routerLink: ['/carto/import'], visible: isAdminOrGestionnaire },
      { label: 'Communauté', icon: 'pi pi-globe', command: () => this.openLink('https://expertises-territoires.fr') },
      { label: 'Contact', icon: 'pi pi-envelope', command: () => this.openEmailClient()},
      { label: 'Mentions Légales', icon: 'pi pi-verified', command: () => this.openLink('/assets/geremi/html/mentions-legales.html', true) },
      { label: 'Manuel utilisateur', icon: 'pi pi-question-circle', command: () => this.openLink('/assets/geremi/pdf/Manuel_utilisateur.pdf') },
      { label: 'Partenaires', icon: 'pi pi-sitemap', command: () => this.openLink('/assets/geremi/html/partenaires.html', true) }
    ];
  }

  openLink(url: string, isPopup: boolean = false) {
    if (isPopup) {
      window.open(url, '', 'width=1100,height=720,resizable=no,scrollbars=no,location=no');
    } else {
      window.open(url, '_blank', 'noopener');
    }
  }

  openEmailClient() {
    window.location.href = `mailto:${this.email}`;
  }
}
