package org.sebsy.compta.infra;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.sebsy.compta.domain.Article;
import org.sebsy.compta.domain.Bon;
import org.sebsy.compta.domain.Fournisseur;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

/**
 * The application's single {@code EntityManagerFactory}. State at the end of TP 06: the only
 * change since TP 05 is the list of managed classes.
 */
public final class Jpa {

    // Built on first access; a mapping error surfaces as ExceptionInInitializerError.
    private static final EntityManagerFactory FACTORY = build();

    private Jpa() {
    }

    public static EntityManagerFactory factory() {
        return FACTORY;
    }

    public static void close() {
        FACTORY.close();
    }

    /** Hibernate's counters: outside the specification, so the one call that names the engine. */
    public static Statistics statistiques(EntityManagerFactory emf) {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    private static EntityManagerFactory build() {
        return new PersistenceConfiguration("compta")
                // Forgetting one here gives "Unknown entity" at the first query, not at startup.
                .managedClass(Fournisseur.class)
                .managedClass(Article.class)
                .managedClass(Bon.class)
                .property(PersistenceConfiguration.JDBC_URL, Env.required("DB_URL"))
                .property(PersistenceConfiguration.JDBC_USER, Env.required("DB_USER"))
                .property(PersistenceConfiguration.JDBC_PASSWORD, Env.required("DB_PASSWORD"))
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, "validate")
                // Engine-specific from here on: another provider ignores these keys.
                .property("hibernate.connection.pool_size", 10)
                // SQL is printed by the org.hibernate.SQL logger of logback.xml, not by show_sql.
                .property("hibernate.generate_statistics", true)
                .createEntityManagerFactory();
    }
}
