[![Coverage Status](https://coveralls.io/repos/github/BRUCELLA2/myerp_P9/badge.svg?branch=master)](https://coveralls.io/github/BRUCELLA2/myerp_P9?branch=master)

# MyERP

## Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs _docker_ utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application


## Environnement de développement

Les composants nécessaires lors du développement sont disponibles via des conteneurs _docker_.
L'environnement de développement est assemblé grâce à _docker-compose_
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données _PostgreSQL_ contenant un jeu de données de démo (`postgresql://127.0.0.1:9032/db_myerp`)



### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up


## Corrections apportés au projet initial

* Couche myerp-model, classe EcritureComptable, la méthode getTotalCredit() récupérait le débit de l'écriture comptable et non le crédit (@ANO #0001)
* Couche myerp-model, classe EcritureComptable, dans la méthode isEquilibree(), la comparaison n'était pas faite correctement (utilisation d'equals() au lieu de compareTo()) (@ANO #0002)
* Couche myerp-model, classe EcritureComptable, le pattern de l'attribut reference n'était pas correct. Il doit prendre en compte les caractères autre que des chiffres pour le code journal. (@ANO #0003)
* Couche myerp-model, classe SequenceEcritureComptable, l'attribut journalCode était manquant (ainsi que le getter et le setter associé). (@ANO #0004)
* Couche myerp-consumer, fichier sqlContext.xml, la requête SQLinsertListLigneEcritureComptable était incorrecte, une virgule était manquante. (@ANO #0005)
* Couche myerp-business, classe ComptabiliteManagerImpl, dans la méthode updateEcritureComptable(), la vérification de l'écriture comptable n'était pas faite avant l'update. (@ANO #0006)
