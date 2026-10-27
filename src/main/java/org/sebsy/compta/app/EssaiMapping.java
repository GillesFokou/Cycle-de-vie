package org.sebsy.compta.app;

import org.sebsy.compta.domain.Article;
import org.sebsy.compta.domain.Bon;
import org.sebsy.compta.infra.Jpa;
import java.math.BigDecimal;

/** TP 06, questions 4, 6 and 9: reads through the new mappings, then tries one INSERT. */
public final class EssaiMapping {

    public static void main(String[] args) {
        var emf = Jpa.factory();
        try {
            Article a = emf.callInTransaction(em -> em.find(Article.class, 1L));
            System.out.println("article 1 : " + a + ", TTC " + a.getPrixTtc());
            emf.runInTransaction(em -> em.createQuery("from Bon b order by b.numero", Bon.class)
                    .getResultList()
                    .forEach(b -> System.out.println("  " + b)));
            try {
                emf.runInTransaction(em -> em.persist(new Article("TEST0001", "Essai", new BigDecimal("1.00"))));
            } catch (RuntimeException e) {
                System.out.println("persist(article) : " + e.getClass().getName());
                for (Throwable c = e.getCause(); c != null; c = c.getCause()) {
                    System.out.println("  cause : " + c.getClass().getName() + ": " + c.getMessage());
                }
            }
        } finally {
            emf.close();
        }
    }
}
