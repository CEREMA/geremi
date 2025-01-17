--annule et remplace de la définition des métadonnées
truncate table geremi.metadonnee;
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'code_etab', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'nom_etablissement', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'siret', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'libelle_adresse', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'libelle_commune', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'code_postal_site', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'code_ape', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'activite_principale', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'long', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'lat', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'type_produit', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'max_production_annuelle_autorisee', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'moy_production_annuelle_autorisee', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'date_fin_autorisation', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'id_declaration', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'id_etab', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'annee', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'statut_declaration', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'statut_quota_emission', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'statut_quota_niveau_activite', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'progression', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'date_derniere_action_declarant', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'date_derniere_action_inspecteur', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'date_initialisation', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'nb_heure_exploitation', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('declaration', 'date_maj', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'id_extraction', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'id_etablissement', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'annee', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'reserve_restante_certaine', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'substance_a_recycler', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'famille_usage_debouche', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'precision_famille', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'sous_famille_usage_debouche_niv_1', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'precision_sous_famille_niv_1', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'sous_famille_usage_debouche_niv_2', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'precision_sous_famille_niv_2', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'quantite_annuelle_substance', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'tonnage_total_substance', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'tonnage_total_destination', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'quantite_annuelle_sterile', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'total_avec_quantite_sterile', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('extraction', 'date_maj', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'type_produit_destination', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'libelle_destination', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'annee', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'code_dechet', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'libelle_dechet', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'quantite_admise', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'id_commentaire', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'annee', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'code_etab', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'nom_table', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'libelle', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'commentaire', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('commentaire', 'date_maj', false);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'date_maj', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'annee', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'origin_mat', true);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'departement', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'tonnage_destination', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'id_entreprise', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'id_etablissement', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'id_destination', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'if_statut_sortie_dechet', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'annee', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'id_etablissement', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'the_geom', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'nb_employe', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'unite', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'code_ope', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'date_maj', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'libelle_ope', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('destination', 'famille_rattachement_destination', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'numero_notification', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'pays_origine', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'departement_origine', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'quantite_traitee', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('traitement_dechet', 'id_traitement_dechet', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'code_insee_commune', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'volume_production', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'id_etab', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'region', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'site_internet', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'service_inspection', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'date_debut', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'if_quota', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'type_carriere', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'date_fin', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'date_maj', NULL);
INSERT INTO geremi.metadonnee (nom_table, nom_champ, if_public) VALUES ('etablissement', 'if_carriere', NULL);

--annule et remplace de la nomenclature de matériaux
truncate table geremi.ref_materiaux;
INSERT INTO ref_materiaux(libelle,type,origine)
VALUES ('Sables marins','Primaire','Naturel'),
       ('Argiles','Primaire','Naturel'),
       ('Excédents de carrière','Primaire','Naturel'),
       ('Terres et cailloux','Primaire','Naturel'),
       ('Cendres de centrale thermique au charbon pulvérisé','Secondaire','Artificiel'),
       ('Laitiers sidérurgiques','Secondaire','Artificiel'),
       ('Sables de fonderie','Secondaire','Artificiel'),
       ('Mâchefers d''incinération de déchets non-dangereux','Secondaire','Artificiel'),
       ('Briques','Secondaire','Recyclé'),
       ('Tuiles et céramiques','Secondaire','Recyclé'),
       ('Mélanges de béton, briques, tuiles et céramique','Secondaire','Recyclé'),
       ('Mélanges bitumineux','Secondaire','Recyclé'),
       ('Agrégats d''enrobés ','Secondaire','Recyclé'),
       ('Matériaux de construction à base de gypse','Secondaire','Recyclé'),
       ('Stériles de carrière','Secondaire','Recyclé'),
       ('Boues de dragage','Secondaire','Recyclé'),
       ('Sédiments de dragage','Secondaire','Recyclé'),
       ('Boues de lavage des granulats','Secondaire','Recyclé');

--annule et remplace de la nomenclature des profils
truncate table geremi.profil;
INSERT INTO geremi.profil (id_profil, libelle) VALUES (1, 'PUBLIC');
INSERT INTO geremi.profil (id_profil, libelle) VALUES (2, 'DREAL');
INSERT INTO geremi.profil (id_profil, libelle) VALUES (3, 'CEREMA');
INSERT INTO geremi.profil (id_profil, libelle) VALUES (4, 'GEST');
INSERT INTO geremi.profil (id_profil, libelle) VALUES (5, 'ADMIN');

--annule et remplace de la nomenclature des types d'anomalies
truncate table geremi.ref_anomalie;
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R00', 'Ce champ ne peut pas contenir de valeur null');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R01', 'Valeur trop longue');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R02', 'La valeur contient des caractères autres que numériques');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R03', 'Date mal formatée');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R0', 'Données importées : carrières, ISDI et ISDND (onglet liste déclaration) ou L''établissement réceptionne / traite / stocke des déchets (onglet type activité)');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R1', 'L''année doit être unique et suivre immédiatement la dernière année en base');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R2', 'Création de l''établissement s''il n''existe pas en base en version valide (pas de fin de validité)');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R3', 'Le Oui (ou l''un des deux oui) conditionne la prise en compte de la ligne');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R4', 'Les données millésimées isolées sont sauvegardées si elles ne sont pas vides');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R5', 'strictement égal à WGS84');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R6', 'Impossible de créer la géométrie, coordonnées incorrectes');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R10', 'deux valeurs possibles Oui ou Non');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R11', 'L''application considère le nombre comme décimal sans tenir compte du séparateur décimal');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R12', 'valeur inférieure ou égale à 100');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R13', 'L''année est cohérente avec celle(s) de l''onglet  "info. Gén. Établissement"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R14', 'Les établissements sont filtrés selon la règle R17, et tous les établissements de cette liste filtrée doivent se trouver dans l''onglet');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R15', 'Tous les établissements de l''onglet doivent se trouver dans la liste filtrée selon la règle R17');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R16', 'Les lignes à 0 ou vides ne sont pas à importer');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R17', 'Seuls sont importés les établissement dont le booléen est à OUI sur le champs carrière de l''onglet des déclarations OU dont le code déchet de l''onglet Traitement de déchets est dans la liste suivante : (17 01 01, 17 01 02, 17 01 03, 17 01 07, 17 03 02, 17 05 04, 17 05 06, 17 05 08, 17 08 02, 17 09 04)');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('R18', 'Pour un établissement, la somme des tonnages de tous les types de matériaux pour toutes les destination alimentera une colonne en base, dans la même table que les tonnages d''extraction');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C1', 'Si l''année du millésime versé est supérieure à celle de la date de fin d''autorisation, les données de production du millésime sont marquées suspectes avec le motif "date de fin d''autorisation dépassée". Lors de l''étape de validation des corrections par la DREAL, celle-ci, en plus de la correction des données (production / date de fin d''autorisation) pourra déclarer un établissement comme ayant été fermé.');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C2', 'Si le total des productions de l''établissement est nul ou 0,  les données de production du millésime sont marquées suspectes avec le motif "pas de production ou production nulle". L''application propose une correction basé sur la moyenne des trois dernières années si les données existent');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C2bis', 'Si le contrôle C2 passe, si une prodution est < 0,01 kT ou >  à 5000 kT, les valeurs sont marquées suspectes avec les motifs respectifs "valeur anormalement basse" ou "valeur anormallement haute"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C3', 'Si le total des productions de l''établissement est supérieur à la production maximale autorisée, les données de production du millésime sont marquées suspectes avec le motif "production déclarée supérieure à la production maximale autorisée"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C4', 'Si le total des productions de l''établissement est supérieur au maximun du total des productions des 3 à 10 années précédentes (selon disponibilité) ou inférieur au minimum du total de ces mêmes productions, les données de production du millésime sont marquées suspectes avec les motifs respectifs "production déclarée supérieure au maximun des (n) dernières années" ou  "production déclarée inférieure au minimum des (n) dernières années"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C5', 'Si le contrôle C4 passe, et que le total de production est supérieur à la moyenne des productions des 3 à 10 années précédentes (selon disponibilité) augmenté de 1,25 x l''écart type de ces mêmes productions, ou inférieur à cette moyenne diminuée de 1,25 x cet écart type,  les données de production du millésime sont marquées suspectes avec les motifs respectifs "production déclarée supérieure au fuseau de production des (n) dernières années" ou  "production déclarée inférieure au fuseau de production des (n) dernières années"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C6', 'Si le total des pourcentages par établissement et famille de rattachement est différent de 100, les lignes de cette famille de rattachement sont marquées suspectes avec le motif "Total des parts modales différent de 100%"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C7', 'Si le total des tonnages par établissement pour les différentes destinations est différent du total des tonnages de production de l''établissement alors ces données sont marquées suspectes avec le motif "Total de production non cohérent avec la ventilation par département" - à prendre en compte dans le développement de GeReMi mais neutraliser cette règle tant que le Cerema n''a pas échangé avec l''INERIS');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C8', 'Si la production moyenne autorisée est renseignée et est supérieure à la valeur maximale autorisée, cette donnée est marquée suspecte avec le motif "production moyenne autorisée supérieure à la production maximale autorisée"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C9', 'Si une valeur de superficie restant à exploiter, de superficie exploitée dans l''année ou de superficie restituée est supérieure à la superficie cadastrale autorisée, alors les valeurs sont marquées suspectes avec les motifs respectifs "superficie restant à exploiter" ou "superficie exploitée dans l''année" ou "superficie restituée" + "supérieure à la superficie cadastrale autorisée"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C10', 'Si le total de la quantité de matériaux entrants destinés à être remblayés sur le site est différent de la somme de ces mêmes quantités en terre et cailloux et en autres matériaux, les valeurs sont marquées suspectes avec le motif "total de matériaux entrants destinés à être remblayés non concordant"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C11', 'Si le total de la quantité de matériaux entrants destinés à être recyclés est différent de la somme de ces mêmes quantités en terre et cailloux et en autres matériaux, les valeurs sont marquées suspectes avec le motif "total de matériaux entrants destinés à être recyclés non concordant"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C12', 'Si le total de la quantité de matériaux entrants est différent de la somme de ces mêmes quantités destinées à être remblayées et destinées à être recyclées, les valeurs sont marquées suspectes avec le motif "total de matériaux entrants non concordant"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C13', 'Quand non vides, les valeurs autres que "Validée" rendent les données de productions suspectes pour cette installation et ce millésime, avec le motif "Il existe des statuts de déclaration non valides"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C14', 'Les valeurs différentes de 100 rendent les données de productions suspectes pour cette installation et ce millésime, avec le motif "La déclaration n''est pas terminée"');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C15', 'Les coordonnées doivent être compatibles avec des longitudes et lattitudes (-180 < lon < 180, -90 < lat < 90)');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C16', 'Les établissements qui ont disparu entre deux millésime et dont la date de fin d''autorisation est dépassée sont considérés comme potentiellement fermés et doivent faire partie des données soumises à vérification des DREAL.');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C17', 'correspond au numero insee de la commune  spécifiée de l''onglet');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C18', 'correspond au nom de la région lié au numéro insee de l''onglet');
INSERT INTO geremi.ref_anomalie (code_anomalie, libelle_anomalie) VALUES ('C19', 'correspond au nom du département lié au numéro insee de l''onglet');

--annule et remplace de la nomenclature des états
truncate table geremi.ref_etat;
INSERT INTO geremi.ref_etat (id_ref_etat, libelle) VALUES (1, 'En attente validation');
INSERT INTO geremi.ref_etat (id_ref_etat, libelle) VALUES (2, 'Validé');
INSERT INTO geremi.ref_etat (id_ref_etat, libelle) VALUES (3, 'Refusé');

--annule et remplace de la nomenclature des statuts d'anomalie
truncate table geremi.ref_statut;
INSERT INTO geremi.ref_statut (id_ref_statut, statut) VALUES (1, 'CORRIGE');
INSERT INTO geremi.ref_statut (id_ref_statut, statut) VALUES (2, 'A_CORRIGER');
