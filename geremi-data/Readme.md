## Gestion automatique des bases de données

Flyway permet de tenir à jour la base de données de l'application. Par défaut, il va se lancer automatiquement au
lancement de l'application, comparer les migrations disponibles, en fonction des informations dans sa table de suivi il
appliquera les migrations manquantes.

Au vu de l'utilisation qui est faite de cette base dans ASPRO, on utilise une base H2 persistée dans un fichier.

### Fichiers de migration

Les règles de nommage sont définies dans le [readme](src/main/resources/db/migration/Readme.md) du dossier de migration

### Pour effacer et (ré)installer une bdd à niveau manuellement via Maven utile pour les dev ou les tests d'intégration :

Mettre à jour le fichier flyway.properties en mettant entre autre le bon nom de fichier pour la base de données puis
lancer la ligne de commande suivante :

```shell
mvn clean compile flyway:clean flyway:migrate -e
```

Pour la base de test, le fichier flyway_test.properties puis :

```shell
mvn clean compile flyway:clean flyway:migrate -Dflyway.configFiles=src/main/resources/flyway_test.properties -e
```

