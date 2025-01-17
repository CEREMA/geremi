--script permettant de rafraichir les données de découpage adminitratif à partir de tables issues de l'INSEE
-- * les géométries sont extraites et séparées dans des tables liées pour des raisons de performance
-- * les SRID sont repositionnés pour les territoires ultramarins
-- * la géométrie de la france entière est recalculée par union des géométries des régions

--extraction des géométries depuis les tables issues du dump
truncate table ref_region_geometry;
INSERT into ref_region_geometry (id,the_geom) select id,the_geom from ref_region;
ALTER TABLE ref_region DROP COLUMN the_geom;

truncate table ref_departement_geometry;
INSERT into ref_departement_geometry (id,the_geom) select id,the_geom from ref_departement;
ALTER TABLE ref_departement DROP COLUMN the_geom;

truncate table ref_commune_geometry;
INSERT into ref_commune_geometry (id,the_geom) select id,the_geom from ref_commune;
ALTER TABLE ref_commune DROP COLUMN the_geom;

truncate table ref_bassin_vie_geometry;
INSERT into ref_bassin_vie_geometry (id,the_geom) select id,the_geom from ref_bassin_vie;
ALTER TABLE ref_bassin_vie DROP COLUMN the_geom;

truncate table ref_zone_emploi_geometry;
INSERT into ref_zone_emploi_geometry (id,the_geom) select id,the_geom from ref_zone_emploi;
ALTER TABLE ref_zone_emploi DROP COLUMN the_geom;

truncate table ref_epci_geometry;
INSERT into ref_epci_geometry (id,the_geom) select id,the_geom from ref_epci;
ALTER TABLE ref_epci DROP COLUMN the_geom;

--MAJ des SRID pour les territoires ultramarins (les autres ont 2154 par defaut)
with param as (
  select 'Guadeloupe' as dept , 5490 as SRID
  union select 'Martinique' as dept , 5490 as SRID
  union select 'Guyane' as dept , 2972 as SRID
  union select 'La Réunion' as dept , 2975 as SRID
  union select 'Mayotte' as dept , 4471 as SRID
)
UPDATE ref_departement_geometry
SET
  srid=sub.srid
  FROM (select param.srid, dept.id
      from ref_departement dept
      JOIN param ON param.dept = dept.nom_departement
      ) sub
where sub.id = ref_departement_geometry.id

--recalcul de la géométrie de la france par union des régions
truncate TABLE geremi.ref_pays;
INSERT INTO geremi.ref_pays (nom,the_geom) select 'France', public.ST_Union(the_geom) from ref_region_geometry;
