
package org.sebsy.compta.app;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.sebsy.compta.domain.Fournisseur;
import org.sebsy.compta.infra.Jpa;

import java.util.UUID;

public class PersistOuMerge {

    private static final EntityManagerFactory emf = Jpa.factory();

    private static Statistics stats() {
        return Jpa.statistiques(emf);
    }

    private static Fournisseur nouveauFournisseur(String prefixe) {
        return emf.callInTransaction(em -> {
            Fournisseur reference = em.find(Fournisseur.class, 1L);

            if (reference == null) {
                throw new IllegalStateException(
                        "Le fournisseur de référence 1 est introuvable"
                );
            }

            String nom = prefixe + UUID.randomUUID();
            return new Fournisseur(nom, reference.getAdresse());
        });
    }

    private static void afficherStats(String scenario) {
        Statistics s = stats();

        System.out.println("\n--- " + scenario + " ---");
        System.out.println("Préparations SQL : "
                + s.getPrepareStatementCount());
        System.out.println("INSERT/UPDATE/DELETE (entités) : "
                + s.getEntityInsertCount() + "/"
                + s.getEntityUpdateCount() + "/"
                + s.getEntityDeleteCount());
    }

    private static void nettoyer(Long id) {
        if (id == null) {
            return;
        }

        emf.runInTransaction(em -> {
            Fournisseur f = em.find(Fournisseur.class, id);
            if (f != null) {
                em.remove(f);
            }
        });
    }

    public static void main(String[] args) {
        Fournisseur premier = nouveauFournisseur("TP07_PERSIST_");
        Fournisseur neufPourMerge = nouveauFournisseur("TP07_MERGE_");

        Long idPremier = null;
        Long idFusionne = null;

        try {
            // Scénario 1 : persist d'un objet neuf.
            stats().clear();

            emf.runInTransaction(em -> em.persist(premier));

            idPremier = premier.getId();

            System.out.println("1. persist d'un objet neuf");
            System.out.println("ID après persist : " + idPremier);
            System.out.println("Objet géré ? "
                    + "(il est détaché après la transaction)");
            afficherStats("Scénario 1");

            // Scénario 2 : merge d'un objet neuf.
            stats().clear();

            Fournisseur fusionne = emf.callInTransaction(
                    em -> em.merge(neufPourMerge)
            );

            idFusionne = fusionne.getId();

            System.out.println("\n2. merge d'un objet neuf");
            System.out.println("ID de l'argument après merge : "
                    + neufPourMerge.getId());
            System.out.println("ID de l'objet renvoyé : "
                    + fusionne.getId());
            System.out.println("Même objet ? "
                    + (fusionne == neufPourMerge));
            afficherStats("Scénario 2");

            stats().clear();

            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();

                try {
                    em.persist(premier);
                    em.flush();

                    System.out.println(
                            "\n3. ATTENTION : persist n'a pas échoué"
                    );
                } catch (RuntimeException ex) {
                    System.out.println(
                            "\n3. persist d'un objet détaché"
                    );
                    System.out.println("Type : "
                            + ex.getClass().getName());
                    System.out.println("Message : "
                            + ex.getMessage());

                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                }

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } finally {
                em.close();
            }

            afficherStats("Scénario 3");

            stats().clear();

            premier.setNom("TP07_MERGE_DETACHE_"
                    + UUID.randomUUID());

            Fournisseur fusionneDetache = emf.callInTransaction(
                    em -> em.merge(premier)
            );

            System.out.println("\n4. merge d'un objet détaché modifié");
            System.out.println("ID de l'argument : " + premier.getId());
            System.out.println("ID de l'objet renvoyé : "
                    + fusionneDetache.getId());
            System.out.println("Même objet ? "
                    + (premier == fusionneDetache));

            afficherStats("Scénario 4");

        } finally {
            nettoyer(idPremier);
            nettoyer(idFusionne);
        }
    }
}
