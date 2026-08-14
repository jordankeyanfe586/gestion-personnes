# Contexte du projet

**Nom :** gestion-personnes

**Objectif :**
API Spring Boot de gestion de personnes développée comme projet d'apprentissage Java/Spring Boot.

**État :** développement

## Architecture

- `controller/` : endpoints HTTP.
- `service/` : logique métier.
- `repository/` : accès aux données avec Spring Data JPA.
- `model/` : entités et types du domaine.
- `dto/` : données d'entrée et de sortie de l'API.
- `factory/` : création et transformation des objets.
- `exception/` : exceptions et gestion centralisée des erreurs.

## Stack technique

- **Langage :** Java
- **Framework :** Spring Boot
- **Build / dépendances :** Maven avec Maven Wrapper
- **Persistance :** Spring Data JPA
- **Base de données :** H2
- **Validation :** Jakarta Validation
- **Web :** Spring Web MVC

`pom.xml` reste la source de vérité pour les versions exactes.

## Commandes du projet

### Lancement

`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### Tests

`./mvnw test`

### Build

`./mvnw clean package`

## Conventions spécifiques

- Respecter l'architecture Controller → Service → Repository.
- Utiliser les DTO pour les échanges de l'API.
- Placer les règles métier dans la couche Service.
- Utiliser Jakarta Validation pour la validation des entrées.
- Adapter les tests lorsqu'un comportement est modifié.

## Contraintes et points d'attention

- Le profil `dev` utilise une base H2 locale dans `./data/`.
- Les tests utilisent une base H2 en mémoire.
- `data/` et `.DS_Store` ne doivent pas être versionnés.
- Éviter les refactors non nécessaires.
