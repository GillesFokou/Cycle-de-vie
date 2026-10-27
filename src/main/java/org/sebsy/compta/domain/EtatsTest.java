
package org.sebsy.compta.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.sebsy.compta.infra.Jpa;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EtatsTest {

    private final EntityManagerFactory emf = Jpa.factory();

    private Statistics statistiques() {
        return Jpa.statistiques(emf);
    }
    private Fournisseur creerFournisseur() {
        String nom = "TP07_ETAT_" + UUID.randomUUID();

        return emf.callInTransaction(em -> {
            Fournisseur reference = em.find(Fournisseur.class, 1L);
            assertNotNull(reference, "Le fournisseur de référence doit exister");

            Fournisseur f = new Fournisseur(nom, reference.getAdresse());
            em.persist(f);
            return f;
        });
    }

    private void supprimerFournisseur(Long id) {
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

    @Test
    void transient_estNonGereSansRequete() {
        var adresse = emf.callInTransaction(
                em -> em.find(Fournisseur.class, 1L).getAdresse()
        );

        statistiques().clear();

        EntityManager em = emf.createEntityManager();
        try {
            Fournisseur f = new Fournisseur(
                    "TP07_TRANSIENT_" + UUID.randomUUID(),
                    adresse
            );

            assertFalse(em.contains(f));
            assertNull(f.getId());
            assertEquals(0, statistiques().getPrepareStatementCount());
        } finally {
            em.close();
        }
    }

    @Test
    void managed_unSetterProduitUnUpdate() {
        Fournisseur cree = creerFournisseur();
        Long id = cree.getId();

        try {
            statistiques().clear();

            emf.runInTransaction(em -> {
                Fournisseur f = em.find(Fournisseur.class, id);

                assertNotNull(f);
                assertTrue(em.contains(f));

                f.setNom("TP07_MODIFIE_" + UUID.randomUUID());
            });

            assertEquals(1, statistiques().getEntityUpdateCount());
        } finally {
            supprimerFournisseur(id);
        }
    }

    @Test
    void detached_unSetterNeProduitPasDUpdate() {
        Fournisseur cree = creerFournisseur();
        Long id = cree.getId();

        try {
            EntityManager em = emf.createEntityManager();
            Fournisseur detache;

            try {
                em.getTransaction().begin();
                detache = em.find(Fournisseur.class, id);
                assertNotNull(detache);
                assertTrue(em.contains(detache));

                em.getTransaction().commit();
            } finally {
                em.close();
            }

            statistiques().clear();

            detache.setNom("TP07_DETACHE_" + UUID.randomUUID());

            assertEquals(0, statistiques().getEntityUpdateCount());
            assertEquals(0, statistiques().getPrepareStatementCount());
        } finally {
            supprimerFournisseur(id);
        }
    }

    @Test
    void removed_leDeleteEstExecuteAuCommit() {
        Fournisseur cree = creerFournisseur();
        Long id = cree.getId();

        statistiques().clear();

        emf.runInTransaction(em -> {
            Fournisseur f = em.find(Fournisseur.class, id);
            assertNotNull(f);
            assertTrue(em.contains(f));

            em.remove(f);

            assertFalse(em.contains(f));
            assertEquals(0, statistiques().getEntityDeleteCount());
        });

        assertEquals(1, statistiques().getEntityDeleteCount());

        emf.runInTransaction(em ->
                assertNull(em.find(Fournisseur.class, id))
        );
    }
}
