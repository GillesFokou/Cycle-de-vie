package org.sebsy.compta.app;

import org.sebsy.compta.domain.Adresse;
import org.sebsy.compta.domain.Article;
import org.sebsy.compta.domain.Fournisseur;
import org.sebsy.compta.infra.Jpa;
import java.math.BigDecimal;

/**
 * TP 08, questions 11 and 12: who refuses to delete a supplier, Hibernate or MariaDB, and what
 * cascade = ALL on Fournisseur.articles changes. Run it without the cascade, then with it.
 * Whatever happens, the test supplier and its articles are gone at the end.
 */
public final class SuppressionFournisseur {

    public static void main(String[] args) {
        var emf = Jpa.factory();
        var stats = Jpa.statistiques(emf);
        try {
            System.out.println("--- fournisseur 1 (4 articles, dont 3 commandés, et 2 bons)");
            tenter(() -> emf.runInTransaction(em -> em.remove(em.find(Fournisseur.class, 1L))));

            System.out.println("--- fournisseur de test, 2 articles jamais commandés");
            Long id = emf.callInTransaction(em -> {
                Fournisseur f = new Fournisseur("Essai cascade", Adresse.ville("Nantes"));
                em.persist(f);
                for (String ref : new String[] {"TST-C1", "TST-C2"}) {
                    Article a = new Article(ref, "Essai " + ref, new BigDecimal("1.00"));
                    f.ajouterArticle(a);
                    em.persist(a);
                }
                return f.getId();
            });
            stats.clear();
            tenter(() -> emf.runInTransaction(em -> em.remove(em.find(Fournisseur.class, id))));
            System.out.println("DELETE exécutés : " + stats.getEntityDeleteCount());
        } finally {
            // Bulk statements, on purpose: the cleanup must not depend on the mapping under test.
            emf.runInTransaction(em -> {
                em.createQuery("delete from Article a where a.ref like 'TST-C%'").executeUpdate();
                em.createQuery("delete from Fournisseur f where f.nom = 'Essai cascade'").executeUpdate();
            });
            Jpa.close();
        }
    }

    private static void tenter(Runnable action) {
        try {
            action.run();
            System.out.println("supprimé");
        } catch (RuntimeException e) {
            Throwable racine = e;
            while (racine.getCause() != null) {
                racine = racine.getCause();
            }
            System.out.println("refusé : " + e.getClass().getSimpleName() + " <- " + racine.getMessage());
        }
    }
}
