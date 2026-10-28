# Projet de départ TP — demo-data-access

L'état du projet à la fin du TP suivant : les relations `fournisseur` ↔ `article` sont mappées, mais
`FournisseurDaoJpa` n'existe pas encore. C'est elle que le TP  vous demande d'écrire.

## Contenu

| Paquet     | Classes                                                                                   |
| ---------- | ----------------------------------------------------------------------------------------- |
| `infra`    | `Env`, `Db` (pool HikariCP), `Jpa` (fabrique en `validate`, trois entités déclarées)      |
| `domain`   | `Fournisseur` (avec `getArticles()` et `ajouterArticle`), `Adresse`, `Article`, `Bon`, `Statut` |
| `dao`      | `FournisseurDao`, `ArticleDao`, `PersistenceFailure`                                      |
| `dao.jdbc` | `Tx`, `FournisseurDaoJdbc`, `ArticleDaoJdbc`, `BonDaoJdbc`                                |
| `app`      | les programmes d'essai des TP 01 à 08, dont `EssaiFournisseurs`, encore branché sur `FournisseurDaoJdbc` |
| tests      | `IdentiteTest` (TP précèdent) et `RelationsTest`                                          |

Les deux relations sont en `LAZY`, et `Fournisseur.articles` n'a pas de `cascade`. C'est l'état
attendu à la fin de la question 13 du TP suivant.

## Mise en route

1. Copiez le contenu de ce dossier dans votre dépôt `demo-data-access`, à la place de vos
   sources. Gardez votre `NOTES.md`.
2. `mvn -q test-compile` passe.
3. Base démarrée (`env/`, `docker compose up -d`). Depuis un terminal (bash, WSL, Git Bash) :
   Les tests passent au vert, et la base garde ses 4 fournisseurs et 12 articles.
4. Un commit : « Reprendre le projet de départ du TP».

Suivez ensuite l'énoncé du TP  à partir de l'étape 1. Là où il parle de « votre projet à la fin
du TP précèdent », lisez « ce projet ».
