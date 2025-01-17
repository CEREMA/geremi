create sequence if not exists geremi.etude_id_seq;

alter sequence geremi.etude_id_seq owner to geremi;

create sequence if not exists geremi.seq_ref_bassin_vie;

alter sequence geremi.seq_ref_bassin_vie owner to geremi;

create sequence if not exists geremi.seq_ref_commune;

alter sequence geremi.seq_ref_commune owner to geremi;

create sequence if not exists geremi.seq_ref_departement;

alter sequence geremi.seq_ref_departement owner to geremi;

create sequence if not exists geremi.seq_ref_epci;

alter sequence geremi.seq_ref_epci owner to geremi;

create sequence if not exists geremi.ref_etat_id_seq;

alter sequence geremi.ref_etat_id_seq owner to geremi;

create sequence if not exists geremi.profil_id_seq;

alter sequence geremi.profil_id_seq owner to geremi;

create sequence if not exists geremi.seq_ref_region;

alter sequence geremi.seq_ref_region owner to geremi;

create sequence if not exists geremi.seq_ref_zone_emploi;

alter sequence geremi.seq_ref_zone_emploi owner to geremi;

create sequence if not exists geremi.rel_etude_user_procuration_id_seq;

alter sequence geremi.rel_etude_user_procuration_id_seq owner to geremi;

create sequence if not exists geremi.territoire_id_seq;

alter sequence geremi.territoire_id_seq owner to geremi;

create sequence if not exists geremi.users_id_seq;

alter sequence geremi.users_id_seq owner to geremi;

create sequence if not exists geremi.zone_id_seq;

alter sequence geremi.zone_id_seq owner to geremi;

create sequence if not exists geremi.id_population_seq;

alter sequence geremi.id_population_seq owner to geremi;

create sequence if not exists geremi.contrainte_environnementale_id_seq;

alter sequence geremi.contrainte_environnementale_id_seq owner to geremi;

create sequence if not exists geremi.ref_pays_id_seq;

alter sequence geremi.ref_pays_id_seq owner to geremi;

create sequence if not exists geremi.chantier_envergure_id_seq;

alter sequence geremi.chantier_envergure_id_seq owner to geremi;

create sequence if not exists geremi.installation_stockage_id_seq;

alter sequence geremi.installation_stockage_id_seq owner to geremi;

create sequence if not exists geremi.scenario_id_seq;

alter sequence geremi.scenario_id_seq owner to geremi;

create sequence if not exists geremi.tracabilite_etape_id_seq;

alter sequence geremi.tracabilite_etape_id_seq owner to geremi;

create sequence if not exists geremi.materiau_id_seq;

alter sequence geremi.materiau_id_seq owner to geremi;

create sequence if not exists geremi.etablissement_id_seq;

alter sequence geremi.etablissement_id_seq owner to geremi;

create sequence if not exists geremi.traitement_dechet_id_seq;

alter sequence geremi.traitement_dechet_id_seq owner to geremi;

create sequence if not exists geremi.commentaire_id_seq;

alter sequence geremi.commentaire_id_seq owner to geremi;

create sequence if not exists geremi.anomalie_id_seq;

alter sequence geremi.anomalie_id_seq owner to geremi;

create sequence if not exists geremi.ref_statut_id_seq;

alter sequence geremi.ref_statut_id_seq owner to geremi;

create sequence if not exists geremi.extraction_id_seq;

alter sequence geremi.extraction_id_seq owner to geremi;

create sequence if not exists geremi.destination_id_seq;

alter sequence geremi.destination_id_seq owner to geremi;

create sequence if not exists geremi.declaration_id_seq;

alter sequence geremi.declaration_id_seq owner to geremi;

create sequence if not exists geremi.ref_materiau_id_seq;

alter sequence geremi.ref_materiau_id_seq owner to geremi;

create sequence if not exists geremi.metadonnee_id_seq;

alter sequence geremi.metadonnee_id_seq owner to geremi;

create sequence if not exists geremi.entreprise_id_seq;

alter sequence geremi.entreprise_id_seq owner to geremi;

create sequence if not exists geremi.resultat_calcul_id_seq;

alter sequence geremi.resultat_calcul_id_seq owner to geremi;

create sequence if not exists geremi.rel_resultat_zone_id_seq;

alter sequence geremi.rel_resultat_zone_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_zone_id_seq;

alter sequence geremi.rel_scenario_zone_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_departement_id_seq;

alter sequence geremi.rel_scenario_departement_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_contrainte_id_seq;

alter sequence geremi.rel_scenario_contrainte_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_chantier_id_seq;

alter sequence geremi.rel_scenario_chantier_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_installation_id_seq;

alter sequence geremi.rel_scenario_installation_id_seq owner to geremi;

create sequence if not exists geremi.rel_scenario_materiau_id_seq;

alter sequence geremi.rel_scenario_materiau_id_seq owner to geremi;


create table if not exists geremi.ref_bassin_vie
(
  id              bigint default nextval('seq_ref_bassin_vie'::regclass) not null
  constraint pk_ref_bassin_vie
  primary key,
  code_bassin_vie varchar(5),
  nom_bassin_vie  varchar(50)
  );

comment on table geremi.ref_bassin_vie is 'INSEE - Bassins de vie 2012 sur la base du COG 2022';

alter table geremi.ref_bassin_vie
  owner to geremi;

create table if not exists geremi.ref_bassin_vie_geometry
(
  id       bigint not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326)
  );

alter table geremi.ref_bassin_vie_geometry
  owner to geremi;

create table if not exists geremi.ref_commune_geometry
(
  id       bigint not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326)
  );

alter table geremi.ref_commune_geometry
  owner to geremi;

create table if not exists geremi.ref_departement_geometry
(
  id       bigint               not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326),
  srid     integer default 2154 not null
  );

alter table geremi.ref_departement_geometry
  owner to geremi;

create table if not exists geremi.ref_epci
(
  id          bigint default nextval('seq_ref_epci'::regclass) not null
  constraint pk_ref_epci
  primary key,
  siren_epci  varchar(9),
  nom_epci    varchar(230),
  nature_epci varchar(150)
  );

comment on table geremi.ref_epci is 'IGN - EPCI ADMIN EXPRESS - COG 2022';

alter table geremi.ref_epci
  owner to geremi;

create table if not exists geremi.ref_epci_geometry
(
  id       bigint not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326)
  );

alter table geremi.ref_epci_geometry
  owner to geremi;

create table if not exists geremi.ref_etat
(
  id_ref_etat integer default nextval('ref_etat_id_seq'::regclass) not null
  primary key,
  libelle     varchar default 'En attente'::character varying      not null
  );

alter table geremi.ref_etat
  owner to geremi;

create table if not exists geremi.profil
(
  id_profil integer default nextval('profil_id_seq'::regclass) not null
  primary key,
  libelle   varchar default 'PUBLIC'::character varying        not null
  );

alter table geremi.profil
  owner to geremi;

create table if not exists geremi.ref_region
(
  id           bigint default nextval('seq_ref_region'::regclass) not null
  constraint pk_ref_region
  primary key,
  insee_region varchar(2),
  nom_region   varchar(35)
  );

comment on table geremi.ref_region is 'IGN - régions ADMIN EXPRESS - COG 2022';

alter table geremi.ref_region
  owner to geremi;

create table if not exists geremi.ref_departement
(
  id                bigint default nextval('seq_ref_departement'::regclass) not null
  constraint pk_ref_departement
  primary key,
  insee_departement varchar(3),
  nom_departement   varchar(30),
  id_region         bigint
  constraint fk_ref_departement_ref_region
  references geremi.ref_region
  );

comment on table geremi.ref_departement is 'IGN - départements ADMIN EXPRESS - COG 2022';

alter table geremi.ref_departement
  owner to geremi;

create table if not exists geremi.ref_region_geometry
(
  id       bigint not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326)
  );

alter table geremi.ref_region_geometry
  owner to geremi;

create table if not exists geremi.ref_zone_emploi
(
  id               bigint default nextval('seq_ref_zone_emploi'::regclass) not null
  constraint pk_ref_zone_emploi
  primary key,
  code_zone_emploi varchar(4),
  nom_zone_emploi  varchar
  );

comment on table geremi.ref_zone_emploi is 'INSEE - Zone d''emploi 2020 sur la base du COG 2022';

alter table geremi.ref_zone_emploi
  owner to geremi;

create table if not exists geremi.ref_commune
(
  id             bigint default nextval('seq_ref_commune'::regclass) not null
  constraint pk_ref_commune
  primary key,
  insee_commune  varchar(5),
  nom_commune    varchar(50),
  id_departement bigint
  constraint fk_ref_commune_ref_departement
  references geremi.ref_departement,
  id_epci        bigint
  constraint fk_ref_commune_ref_epci
  references geremi.ref_epci,
  id_bassin_vie  bigint
  constraint fk_ref_commune_ref_bassin_vie
  references geremi.ref_bassin_vie,
  id_zone_emploi bigint
  constraint fk_ref_commune_ref_zone_emploi
  references geremi.ref_zone_emploi
  );

comment on table geremi.ref_commune is 'IGN - communes ADMIN EXPRESS - COG 2022';

alter table geremi.ref_commune
  owner to geremi;

create table if not exists geremi.ref_zone_emploi_geometry
(
  id       bigint not null
  primary key,
  the_geom public.geometry(MultiPolygon, 4326)
  );

alter table geremi.ref_zone_emploi_geometry
  owner to geremi;

create table if not exists geremi.users
(
  id_users      integer default nextval('users_id_seq'::regclass) not null
  primary key,
  nom           varchar                                           not null,
  prenom        varchar                                           not null,
  mail          varchar                                           not null,
  id_ref_etat   integer                                           not null
  constraint fk_users_ref_etat
  references geremi.ref_etat,
  id_profil     integer                                           not null
  constraint fk_users_profil
  references geremi.profil,
  id_ref_region bigint
  constraint fk_users_ref_region
  references geremi.ref_region,
  date_creation date                                              not null
  );

alter table geremi.users
  owner to geremi;

create table if not exists geremi.etude
(
  id_etude      integer default nextval('etude_id_seq'::regclass) not null
  primary key,
  nom           varchar                                           not null,
  description   text,
  date_creation date                                              not null,
  if_src        boolean default false                             not null,
  annee_ref     integer                                           not null,
  annee_fin     integer,
  id_users      integer                                           not null
  constraint fk_etude_users
  references geremi.users,
  if_public     boolean default false                             not null,
  if_importe    boolean default false                             not null
  );

alter table geremi.etude
  owner to geremi;

create table if not exists geremi.rel_etude_user_procuration
(
  id_rel_etude_user integer default nextval('rel_etude_user_procuration_id_seq'::regclass) not null
  primary key,
  id_etude          integer                                                                not null
  constraint fk_rel_etude_user_etude
  references geremi.etude,
  id_users          integer                                                                not null
  constraint fk_rel_etude_user_user
  references geremi.users,
  if_droit_ecriture boolean default false                                                  not null
  );

alter table geremi.rel_etude_user_procuration
  owner to geremi;

create table if not exists geremi.territoire
(
  id_territoire    integer default nextval('territoire_id_seq'::regclass) not null
  primary key,
  type             varchar                                                not null,
  nom              varchar                                                not null,
  description      text,
  the_geom         public.geometry(Multipolygon,4326)                     not null,
  id_etude         integer                                                not null
  constraint fk_territoire_etude
  references geremi.etude,
  liste_territoire varchar(255)
  );

alter table geremi.territoire
  owner to geremi;

create table if not exists geremi.zone
(
  id_zone     integer default nextval('zone_id_seq'::regclass) not null
  primary key,
  type        varchar                                          not null,
  nom         varchar                                          not null,
  description text,
  the_geom    public.geometry(Multipolygon,4326)               not null,
  id_etude    integer                                          not null
  constraint fk_zone_etude
  references geremi.etude,
  code        varchar
  );

alter table geremi.zone
  owner to geremi;

create table if not exists geremi.population
(
  id_population       integer default nextval('id_population_seq'::regclass) not null
  primary key,
  id_zone             integer
  references geremi.zone,
  id_etude            integer
  references geremi.etude,
  annee               integer,
  population_basse    integer,
  population_centrale integer,
  population_haute    integer
  );

alter table geremi.population
  owner to geremi;

create table if not exists geremi.contrainte_environnementale
(
  id_contr_env integer default nextval('contrainte_environnementale_id_seq'::regclass) not null
  primary key,
  nom          varchar(255)                                                            not null,
  description  varchar(255),
  niveau       varchar(10)
  constraint contrainte_environnementale_niveau_check
  check ((niveau)::text = ANY
((ARRAY ['Faible'::character varying, 'Moyenne'::character varying, 'Forte'::character varying])::text[])),
  the_geom     public.geometry(Multipolygon,4326)                                      not null,
  id_source    integer,
  id_etude     integer                                                                 not null
  constraint fk_contrainte_environnementale_id_etude
  references geremi.etude
  );

alter table geremi.contrainte_environnementale
  owner to geremi;

create table if not exists geremi.ref_pays
(
  id_pays  integer default nextval('ref_pays_id_seq'::regclass) not null,
  nom      varchar(255)                                         not null,
  the_geom public.geometry(Multipolygon,4326)                   not null
  );

alter table geremi.ref_pays
  owner to geremi;

create table if not exists geremi.chantier_envergure
(
  id_chantier integer default nextval('chantier_envergure_id_seq'::regclass) not null
  primary key,
  nom         varchar(255)                                                   not null,
  description varchar(255),
  annee_debut integer                                                        not null,
  annee_fin   integer                                                        not null,
  beton_pref  integer,
  viab_autre  integer,
  ton_tot     integer                                                        not null,
  the_geom    public.geometry(Geometry,4326)                                 not null,
  id_source   integer,
  id_etude    integer                                                        not null
  constraint fk_chantier_envergure_id_etude
  references geremi.etude,
  id_frere    integer
  );

alter table geremi.chantier_envergure
  owner to geremi;

create table if not exists geremi.installation_stockage
(
  id_stockage integer default nextval('installation_stockage_id_seq'::regclass) not null
  primary key,
  nom_etab    varchar(255)                                                      not null,
  code_etab   varchar(255),
  description varchar(255),
  annee_debut integer                                                           not null,
  annee_fin   integer                                                           not null,
  beton_pref  integer,
  viab_autre  integer,
  ton_tot     integer                                                           not null,
  the_geom    public.geometry(Point,4326)                                       not null,
  id_source   integer,
  id_etude    integer                                                           not null
  constraint fk_installation_stockage_id_etude
  references geremi.etude,
  id_frere    integer
  );

alter table geremi.installation_stockage
  owner to geremi;

create table if not exists geremi.scenario
(
  id_scenario                   integer default nextval('scenario_id_seq'::regclass) not null
  primary key,
  id_etude                      integer                                              not null
  references geremi.etude,
  nom                           varchar                                              not null,
  description                   varchar(255),
  dynamique_demographique       varchar,
  tx_renouvellement_hc          integer,
  if_retenu                     boolean,
  date_maj                      date,
  ponderation_surface_beton     integer,
  ponderation_surface_viabilite integer
  );

alter table geremi.scenario
  owner to geremi;

create table if not exists geremi.tracabilite_etape
(
  id          integer default nextval('tracabilite_etape_id_seq'::regclass) not null
  primary key,
  id_etude    integer                                                       not null
  references geremi.etude,
  id_scenario integer
  references geremi.scenario,
  etape       varchar                                                       not null,
  action      varchar                                                       not null,
  id_users    integer                                                       not null
  references geremi.users,
  date_maj    timestamp                                                     not null
  );

alter table geremi.tracabilite_etape
  owner to geremi;

create table if not exists geremi.ref_anomalie
(
  code_anomalie    varchar not null
  primary key,
  libelle_anomalie varchar
);

alter table geremi.ref_anomalie
  owner to geremi;

create table if not exists geremi.ref_statut
(
  id_ref_statut integer default nextval('ref_statut_id_seq'::regclass) not null
  primary key,
  statut        varchar
  );

alter table geremi.ref_statut
  owner to geremi;

create table if not exists geremi.materiau
(
  id_materiau integer default nextval('materiau_id_seq'::regclass) not null
  primary key,
  libelle     varchar(255)                                         not null,
  type        varchar(255)                                         not null,
  origine     varchar(255)                                         not null,
  tonnage     integer,
  id_etude    integer
  constraint fk_materiau_id_etude
  references geremi.etude
  );

alter table geremi.materiau
  owner to geremi;

create table if not exists geremi.ref_materiaux
(
  id      integer default nextval('ref_materiau_id_seq'::regclass) not null,
  libelle varchar(255)                                             not null,
  type    varchar(255)                                             not null,
  origine varchar(255)                                             not null
  );

alter table geremi.ref_materiaux
  owner to geremi;

create table if not exists geremi.resultat_calcul
(
  id_resultat                               integer default nextval('resultat_calcul_id_seq'::regclass) not null
  primary key,
  id_scenario                               integer                                                     not null
  constraint fk_resultat_calcul_id_scenario
  references geremi.scenario,
  id_territoire                             integer                                                     not null
  constraint fk_resultat_calcul_id_territoire
  references geremi.territoire,
  annee                                     integer                                                     not null,
  if_projection                             boolean,
  besoin_territoire_total_chantier          double precision,
  besoin_territoire_primaire                double precision,
  besoin_territoire_secondaire              double precision,
  besoin_territoire_total                   double precision,
  production_territoire_primaire            double precision,
  production_territoire_secondaire          double precision,
  production_territoire_total               double precision,
  production_territoire_primaire_intra      double precision,
  production_territoire_primaire_brut       double precision,
  pourcent_production_territoire_secondaire double precision,
  date_maj                                  date
  );

alter table geremi.resultat_calcul
  owner to geremi;

create table if not exists geremi.rel_resultat_zone
(
  id_rel_resultat_zone                integer default nextval('rel_resultat_zone_id_seq'::regclass) not null
  primary key,
  id_resultat                         integer                                                       not null
  constraint fk_rel_resultat_zone_id_resultat
  references geremi.resultat_calcul,
  id_zone                             integer                                                       not null
  constraint fk_rel_resultat_zone_id_zone
  references geremi.zone,
  besoin_zone_total_chantier          double precision,
  besoin_zone_primaire                double precision,
  besoin_zone_secondaire              double precision,
  besoin_zone_total                   double precision,
  production_zone_primaire            double precision,
  production_zone_secondaire          double precision,
  production_zone_total               double precision,
  production_zone_primaire_intra      double precision,
  production_zone_primaire_brut       double precision,
  pourcent_production_zone_secondaire double precision,
  date_maj                            date
  );

alter table geremi.rel_resultat_zone
  owner to geremi;

create table if not exists geremi.rel_scenario_zone
(
  id_rel_scenario_zone           integer default nextval('rel_scenario_zone_id_seq'::regclass) not null
  primary key,
  id_scenario                    integer                                                       not null
  constraint fk_rel_scenario_zone_id_scenario
  references geremi.scenario,
  id_zone                        integer                                                       not null
  constraint fk_rel_scenario_zone_id_zone
  references geremi.zone,
  projection_secondaire_echeance integer                                                       not null,
  date_maj                       date
  );

alter table geremi.rel_scenario_zone
  owner to geremi;

create table if not exists geremi.rel_scenario_departement
(
  id_rel_scenario_departement       integer default nextval('rel_scenario_departement_id_seq'::regclass) not null
  primary key,
  id_scenario                       integer                                                              not null
  constraint fk_rel_scenario_departement_id_scenario
  references geremi.scenario,
  id_departement                    integer                                                              not null
  constraint fk_rel_scenario_departement_id_departement
  references geremi.ref_departement,
  date_maj                          date,
  repartition_departement_beton     integer,
  repartition_departement_viabilite integer
  );

alter table geremi.rel_scenario_departement
  owner to geremi;

create table if not exists geremi.rel_scenario_contrainte
(
  id_rel_scenario_contrainte   integer default nextval('rel_scenario_contrainte_id_seq'::regclass) not null
  primary key,
  id_scenario                  integer                                                             not null
  constraint fk_rel_scenario_contrainte_id_scenario
  references geremi.scenario,
  id_contrainte                integer                                                             not null
  constraint fk_rel_scenario_contrainte_id_contrainte
  references geremi.contrainte_environnementale,
  tx_renouvellement_contrainte integer                                                             not null
  );

alter table geremi.rel_scenario_contrainte
  owner to geremi;

create table if not exists geremi.rel_scenario_chantier
(
  id_rel_scenario_chantier integer default nextval('rel_scenario_chantier_id_seq'::regclass) not null
  primary key,
  id_scenario              integer                                                           not null
  constraint fk_rel_scenario_chantier_id_scenario
  references geremi.scenario,
  id_chantier              integer                                                           not null
  constraint fk_rel_scenario_chantier_id_chantier
  references geremi.chantier_envergure
  );

alter table geremi.rel_scenario_chantier
  owner to geremi;

create table if not exists geremi.rel_scenario_installation
(
  id_rel_scenario_installation integer default nextval('rel_scenario_installation_id_seq'::regclass) not null
  primary key,
  id_scenario                  integer                                                               not null
  constraint fk_rel_scenario_installation_id_scenario
  references geremi.scenario,
  id_installation              integer                                                               not null
  constraint fk_rel_scenario_installation_id_installation
  references geremi.installation_stockage
  );

alter table geremi.rel_scenario_installation
  owner to geremi;

create table if not exists geremi.rel_scenario_materiau
(
  id_rel_scenario_materiau integer default nextval('rel_scenario_materiau_id_seq'::regclass) not null
  primary key,
  id_scenario              integer                                                           not null
  constraint fk_rel_scenario_materiau_id_scenario
  references geremi.scenario,
  id_materiau              integer                                                           not null
  constraint fk_rel_scenario_materiau_id_materiau
  references geremi.materiau,
  id_zone                  integer                                                           not null
  constraint fk_rel_scenario_materiau_id_zone
  references geremi.zone,
  tonnage                  numeric                                                           not null,
  pourcent                 numeric
  );

alter table geremi.rel_scenario_materiau
  owner to geremi;

create table if not exists geremi.metadonnee
(
  id_metadonnee integer default nextval('metadonnee_id_seq'::regclass) not null
  primary key,
  nom_table     text,
  nom_champ     text,
  if_public     boolean
  );

alter table geremi.metadonnee
  owner to geremi;

create table if not exists geremi.commentaire
(
  id_commentaire integer   default nextval('commentaire_id_seq'::regclass) not null
  primary key,
  annee          varchar(4),
  code_etab      varchar(10),
  nom_table      varchar(30),
  libelle        varchar(50),
  commentaire    text,
  date_maj       timestamp default CURRENT_TIMESTAMP
  );

alter table geremi.commentaire
  owner to geremi;

create index if not exists commentaire_idx
  on geremi.commentaire (code_etab, annee);

create table if not exists geremi.anomalie
(
  id_anomalie       integer   default nextval('anomalie_id_seq'::regclass) not null
  primary key,
  date_creation     date,
  nom_table         varchar,
  nom_champ         varchar,
  id_etablissement  integer,
  id_verification   integer,
  id_statut_ano     integer
  references geremi.ref_statut,
  date_maj          timestamp default CURRENT_TIMESTAMP,
  bloquante         boolean                                                not null,
  code_ref_anomalie varchar
  constraint anomalie_id_ref_anomalie_fkey
  references geremi.ref_anomalie,
  id_ligne          varchar,
  annee             integer
  );

alter table geremi.anomalie
  owner to geremi;

create table if not exists geremi.etablissement
(
  id_etab                           integer   default nextval('etablissement_id_seq'::regclass) not null
  constraint etablissement_pkey2
  primary key,
  code_etab                         varchar(10),
  annee                             integer,
  nom_etablissement                 text,
  siret                             bigint,
  region                            varchar(30),
  departement                       varchar(30),
  libelle_adresse                   text,
  code_insee_commune                varchar(5),
  libelle_commune                   text,
  code_ape                          varchar(6),
  activite_principale               text,
  long                              double precision,
  lat                               double precision,
  volume_production                 numeric,
  unite                             varchar(10),
  type_produit                      text,
  nb_employe                        integer,
  site_internet                     text,
  the_geom                          public.geometry(Point, 4326),
  service_inspection                varchar(10),
  if_carriere                       boolean                                                      not null,
  if_quota                          boolean                                                      not null,
  origin_mat                        varchar(30),
  max_production_annuelle_autorisee numeric,
  moy_production_annuelle_autorisee numeric,
  date_fin_autorisation             date,
  type_carriere                     varchar(50),
  date_debut                        date,
  date_fin                          date,
  date_maj                          timestamp default CURRENT_TIMESTAMP,
  code_postal_site                  integer
  );

alter table geremi.etablissement
  owner to geremi;

create unique index if not exists etablissement_idx
  on geremi.etablissement (code_etab, annee);

create table if not exists geremi.traitement_dechet
(
  id_traitement_dechet    integer   default nextval('traitement_dechet_id_seq'::regclass) not null
  primary key,
  id_etablissement        integer
  references geremi.etablissement,
  annee                   integer,
  code_dechet             varchar(10),
  libelle_dechet          varchar(200),
  if_statut_sortie_dechet boolean,
  departement_origine     varchar(50),
  pays_origine            varchar(50),
  quantite_admise         numeric,
  quantite_traitee        numeric,
  code_ope                varchar(10),
  libelle_ope             varchar(255),
  numero_notification     varchar(512),
  date_maj                timestamp default CURRENT_TIMESTAMP
  );

alter table geremi.traitement_dechet
  owner to geremi;

create table if not exists geremi.extraction
(
  id_extraction                     integer   default nextval('extraction_id_seq'::regclass) not null
  primary key,
  id_etablissement                  integer
  references geremi.etablissement,
  annee                             integer,
  reserve_restante_certaine         varchar(20),
  substance_a_recycler              varchar(255),
  famille_usage_debouche            varchar(255),
  precision_famille                 varchar(255),
  sous_famille_usage_debouche_niv_1 varchar(255),
  precision_sous_famille_niv_1      varchar(255),
  sous_famille_usage_debouche_niv_2 varchar(255),
  precision_sous_famille_niv_2      varchar(255),
  quantite_annuelle_substance       numeric,
  tonnage_total_substance           numeric,
  tonnage_total_destination         numeric,
  quantite_annuelle_sterile         numeric,
  total_avec_quantite_sterile       numeric,
  date_maj                          timestamp default CURRENT_TIMESTAMP
  );

alter table geremi.extraction
  owner to geremi;

create table if not exists geremi.destination
(
  id_destination                   integer   default nextval('destination_id_seq'::regclass) not null
  primary key,
  id_etablissement                 integer
  references geremi.etablissement,
  annee                            integer,
  famille_rattachement_destination varchar(255),
  type_produit_destination         varchar(255),
  libelle_destination              varchar(255),
  tonnage_destination              numeric,
  date_maj                         timestamp default CURRENT_TIMESTAMP
  );

alter table geremi.destination
  owner to geremi;

create table if not exists geremi.declaration
(
  id_declaration                  integer   default nextval('declaration_id_seq'::regclass) not null
  primary key,
  id_etab                         integer
  constraint declaration_id_etablissement_fkey
  references geremi.etablissement,
  annee                           integer,
  statut_declaration              text,
  statut_quota_emission           text,
  statut_quota_niveau_activite    text,
  progression                     integer,
  date_derniere_action_declarant  date,
  date_derniere_action_inspecteur date,
  date_initialisation             date,
  nb_heure_exploitation           integer,
  date_maj                        timestamp default CURRENT_TIMESTAMP
  );

alter table geremi.declaration
  owner to geremi;


create view geremi.v_departement_intersect_etude(id_etude, id_departement, nom_departement, dept_geom, dept_srid) as
SELECT t.id_etude,
       dept_geom.id                                        AS id_departement,
       TRIM(BOTH ' '::text FROM dept_geom.nom_departement) AS nom_departement,
       dept_geom.the_geom                                  AS dept_geom,
       dept_geom.srid                                      AS dept_srid
FROM territoire t
       JOIN (SELECT d.id,
                    d.nom_departement,
                    dg.the_geom,
                    dg.srid
             FROM ref_departement d
                    JOIN ref_departement_geometry dg ON d.id = dg.id) dept_geom
            ON public.st_area(public.st_reduceprecision(public.st_intersection(t.the_geom, dept_geom.the_geom),
                                                        0.000001::double precision)) > 0::double precision;

alter table geremi.v_departement_intersect_etude
  owner to geremi;

