import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { LayoutService } from "./service/app.layout.service";
import { Users } from "../shared/entities/users.model";
import { UsersService } from "../shared/service/users.service";
import { MainComponent } from "./main/main.component";

@Component({
  selector: 'app-topbar',
  templateUrl: './app.topbar.component.html'
})
export class AppTopBarComponent implements OnInit {

  menu: MenuItem[] = [];
  usersActif!: Users;

  @ViewChild('searchinput') searchInput!: ElementRef;

  @ViewChild('menubutton') menuButton!: ElementRef;

  searchActive: boolean = false;

  constructor(public layoutService: LayoutService,
              public usersService: UsersService,
              public appMain: MainComponent) {
  }

  ngOnInit(): void {
    this.usersActif = this.usersService.currentUsers;
  }

  onMenuButtonClick() {
    this.layoutService.onMenuToggle();
  }

  activateSearch() {
    this.searchActive = true;
    setTimeout(() => {
      this.searchInput.nativeElement.focus();
    }, 100);
  }

  deactivateSearch() {
    this.searchActive = false;
  }

  removeTab(event: MouseEvent, item: MenuItem, index: number) {
    this.layoutService.onTabClose(item, index);
    event.preventDefault();
  }

  get layoutTheme(): string {
    return this.layoutService.config.layoutTheme;
  }

  get colorScheme(): string {
    return this.layoutService.config.colorScheme;
  }

  get logo(): string {
    /*
        const path = 'assets/layout/images/logo-';
        const logo = this.layoutTheme === 'primaryColor' ? 'light.png' : (this.colorScheme === 'light' ? 'dark.png' : 'light.png');
        return path + logo;
    */
    return 'assets/layout/images/LogoCerema.svg';
  }

  get tabs(): MenuItem[] {
    return this.layoutService.tabs;
  }
}
