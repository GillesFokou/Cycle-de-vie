package org.sebsy.compta.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.hibernate.LazyInitializationException;
import org.hibernate.PropertyValueException;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.sebsy.compta.infra.Jpa;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

/**
 * TP 08, questions 4 and 8 to 10. Runs against compta (DB_URL, DB_USER, DB_PASSWORD); the only
 * row a test creates is the article of {@link #lesDeuxCotes_larticleEstInsere}, deleted after.
 */
class RelationsTest {

    private static final String REF = "TST-REL";

    private final EntityManagerFactory emf = Jpa.factory();
    private final Statistics stats = Jpa.statistiques(emf);

    @AfterEach
    void nettoyer() {
        emf.runInTransaction(em -> em.createQuery("delete from Article a where a.ref = :ref")
                .setParameter("ref", REF)
                .executeUpdate());
    }

    @Test
    void miroirSeul_hibernateRefuseAvantToutSql() {
        stats.clear();
        assertThatThrownBy(() -> emf.runInTransaction(em -> {
            Fournisseur f = em.find(Fournisseur.class, 3L);
            Article a = new Article(REF, "Miroir seul", new BigDecimal("12.50"));
            collectionBrute(f).add(a);   // the inverse side only: Article.fournisseur stays null
            em.persist(a);
        }))
                // Hibernate reads Article.fournisseur, never the collection. optional = false
                // turns the null into a PropertyValueException before any INSERT is sent.
                .isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Article.fournisseur");
        assertThat(stats.getPrepareStatementCount()).as("only the find reached the database").isEqualTo(1);
    }

    @Test
    void lesDeuxCotes_larticleEstInsere() {
        Long id = emf.callInTransaction(em -> {
            Fournisseur f = em.find(Fournisseur.class, 3L);
            Article a = new Article(REF, "Deux côtés", new BigDecimal("12.50"));
            f.ajouterArticle(a);
            em.persist(a);
            return a.getId();
        });
        Long idFou = emf.callInTransaction(em -> em.find(Article.class, id).getFournisseur().getId());
        assertThat(idFou).isEqualTo(3L);
    }

    @Test
    void navigation_leFournisseur1aQuatreArticles() {
        int n = emf.callInTransaction(em -> em.find(Fournisseur.class, 1L).getArticles().size());
        assertThat(n).isEqualTo(4);
    }

    @Test
    void lazy_unProxyQuiNeSeChargeQueDansSaTransaction() {
        Article a = emf.callInTransaction(em -> {
            Article lu = em.find(Article.class, 1L);
            stats.clear();
            assertThat(lu.getFournisseur().getClass().getName()).endsWith("Fournisseur$HibernateProxy");
            assertThat(lu.getFournisseur().getId()).isEqualTo(1L);
            assertThat(stats.getPrepareStatementCount()).as("the id is the foreign key, already read").isZero();
            return lu;
        });
        assertThatThrownBy(() -> a.getFournisseur().getNom())
                .isInstanceOf(LazyInitializationException.class)
                .hasMessageContaining("no session");
    }

    // Test-only access to the mapped collection, to bypass ajouterArticle on purpose.
    @SuppressWarnings("unchecked")
    private static List<Article> collectionBrute(Fournisseur f) {
        try {
            Field champ = Fournisseur.class.getDeclaredField("articles");
            champ.setAccessible(true);
            return (List<Article>) champ.get(f);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
