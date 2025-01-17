import { Users } from '../../entities/users.model';
import { Roles } from '../../enums/roles.enums';

export const mockedUsers: Users = {
  email: 'test@geremi.fr',
  id: 1,
  idDirectionCourante: 1,
  roleActifList: [Roles.AdminGeremi],
  nbDirections: 0,
};

export const mockedUsersAdminGeremi: Users = {
  email: 'test2@geremi.fr',
  id: 2,
  idDirectionCourante: 1,
  roleActifList: [Roles.AdminGeremi],
  nbDirections: 0,
};

export const mockedUsersOperateur: Users = {
  email: 'test3@geremi.fr',
  id: 3,
  idDirectionCourante: 1,
  roleActifList: [Roles.Operateur],
  nbDirections: 0,
};

export const mockedUsersConsultant: Users = {
  email: 'test4@geremi.fr',
  id: 4,
  idDirectionCourante: 1,
  roleActifList: [Roles.Consultant],
  nbDirections: 0,
};

export const mockedAdminGeremiList: Users[] = [mockedUsers, mockedUsersAdminGeremi];
