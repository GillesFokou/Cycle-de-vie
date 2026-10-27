# Projet de départ du TP 07 (noté) — demo-data-access

L'état du projet à la fin du TP 08 : les relations `fournisseur` ↔ `article` sont mappées, mais
`FournisseurDaoJpa` n'existe pas encore. C'est elle que le TP 07 vous demande d'écrire. Partez de
ce dossier si votre projet n'est pas terminé ou ne compile pas : tout le monde commence
l'évaluation au même point.

## Contenu

| Paquet     | Classes                                                                                   |
| ---------- | ----------------------------------------------------------------------------------------- |
| `infra`    | `Env`, `Db` (pool HikariCP), `Jpa` (fabrique en `validate`, trois entités déclarées)      |
| `domain`   | `Fournisseur` (avec `getArticles()` et `ajouterArticle`), `Adresse`, `Article`, `Bon`, `Statut` |
| `dao`      | `FournisseurDao`, `ArticleDao`, `PersistenceFailure`                                      |
| `dao.jdbc` | `Tx`, `FournisseurDaoJdbc`, `ArticleDaoJdbc`, `BonDaoJdbc`                                |
| `app`      | les programmes d'essai des TP 01 à 08, dont `EssaiFournisseurs`, encore branché sur `FournisseurDaoJdbc` |
| tests      | `IdentiteTest` (TP 06) et `RelationsTest` (TP 08)                                         |

Les deux relations sont en `LAZY`, et `Fournisseur.articles` n'a pas de `cascade`. C'est l'état
attendu à la fin de la question 13 du TP 08.

## Mise en route

1. Copiez le contenu de ce dossier dans votre dépôt `demo-data-access`, à la place de vos
   sources. Gardez votre `NOTES.md`.
2. `mvn -q test-compile` passe.
3. Base démarrée (`env/`, `docker compose up -d`). Depuis un terminal (bash, WSL, Git Bash) :
   ```bash
   DB_URL='jdbc:mariadb://localhost:3306/compta?useServerPrepStmts=true&connectionTimeZone=SERVER' DB_USER=app DB_PASSWORD=app mvn test
   ```
   Les tests passent au vert, et la base garde ses 4 fournisseurs et 12 articles.
4. Un commit : « Reprendre le projet de départ du TP 07 ».

Suivez ensuite l'énoncé du TP 07 à partir de l'étape 1. Là où il parle de « votre projet à la fin
du TP 06 », lisez « ce projet ».
