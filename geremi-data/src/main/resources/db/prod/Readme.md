## Fichiers de description des migrations

Pour Flyway, toutes les modifications de base de données sont appelées des migrations. Elles peuvent être soit
versionnées, soit répétables. Celles qui sont versionnées ont deux formes : les régulières et les annulations.

Les migrations versionnées possèdent : une version, une description et un checksum. La version doit être unique. La
description est simplement informative pour se souvenir de ce qu'elle fait. Le checksum est calculé automatiquement et
permet d'éviter les changements accidentels.

Les migrations versionnées sont les plus courantes et sont appliquées exactement une seule fois.

De manière optionnelle, en cas d'échec d'une migration, il est possible de fournir une migration d'annulation. Mais il
faut faire attention aux applications partielles.

Les migrations répétables possèdent une description et un checksum, mais pas de version. Au lieu d'être exécuté une
seule fois, elles sont appliquées à chaque fois que leur checksum change.

Au cours d'un lancement d'une migration de la base de données, les migrations répétables sont toujours appliquées à la
fin, après que toutes les migrations versionnées éligibles soit passées. Elles sont appliquées dans l'ordre de leur
description.

De manière interne, pour faire le suivi des migrations passées, Flyway installe et tient à jour une table d'historique
lui permettant de savoir lesquelles ont déjà été appliquées, par qui et leur checksum.

### Convention de nommage

Le nom des fichiers est composé de :

- Préfixe : V pour les versionnées, U pour les annulations (Undo) et R pour les répétables
- Version : de la forme d'une date : AAAAMMJJHHMM (sauf pour les migrations répétables)
- Séparateur : __ (deux underscores)
- Description : on peut utiliser des espaces ou des underscores pour séparer les mots
- Suffixe : .sql

### Génération des migrations

Il est possible d'utiliser le plugin Intellij `JPA Buddy` pour générer les migrations à partir des changements des
entités JPA 