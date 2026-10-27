package org.sebsy.compta.app;

import org.sebsy.compta.domain.Article;
import org.sebsy.compta.infra.Jpa;
import jakarta.persistence.EntityManagerFactory;

/**
 * TP 08, questions 3 and 4: what a to-one association costs, and what LAZY hands back.
 * Run it once as is, then once with the fetch attribute removed from Article.fournisseur.
 */
public final class MesureChargement {

    public static void main(String[] args) {
        EntityManagerFactory emf = Jpa.factory();
        var stats = Jpa.statistiques(emf);
        try {
            stats.clear();
            Article un = emf.callInTransaction(em -> em.find(Article.class, 1L));
            System.out.println("find(Article, 1)         : " + stats.getPrepareStatementCount() + " requête(s)");

            stats.clear();
            int n = emf.callInTransaction(em -> em.createQuery("select a from Article a", Article.class)
                    .getResultList().size());
            System.out.println("select a from Article a  : " + n + " articles, "
                    + stats.getPrepareStatementCount() + " requête(s)");

            // What LAZY hands back, and when the SELECT actually leaves.
            emf.runInTransaction(em -> {
                Article a = em.find(Article.class, 1L);
                stats.clear();
                var f = a.getFournisseur();
                System.out.println("classe du fournisseur    : " + f.getClass().getName());
                System.out.println("getId()                  : " + f.getId() + ", "
                        + stats.getPrepareStatementCount() + " requête(s)");
                System.out.println("getNom()                 : " + f.getNom() + ", "
                        + stats.getPrepareStatementCount() + " requête(s)");
            });

            // Outside its transaction, the proxy has no session left to load from.
            try {
                System.out.println(un.getFournisseur().getNom());
            } catch (RuntimeException e) {
                System.out.println("hors transaction         : " + e);
            }
        } finally {
            Jpa.close();
        }
    }
}
