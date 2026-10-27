package org.sebsy.compta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sebsy.compta.infra.Env;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * TP 06, questions 11 and 12. Runs against the compta database (DB_URL, DB_USER, DB_PASSWORD);
 * every write is rolled back.
 */
class IdentiteTest {

    /** The IDE-generated identity: equals/hashCode on the generated id. Test-only mapping. */
    @Entity(name = "FournisseurParId")
    @Table(name = "fournisseur")
    static class FournisseurParId {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @Column(nullable = false)
        String nom;
        @Column(nullable = false)
        String ville;

        protected FournisseurParId() {
        }

        FournisseurParId(String nom, String ville) {
            this.nom = nom;
            this.ville = ville;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof FournisseurParId autre && Objects.equals(id, autre.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    private static EntityManagerFactory emf;

    @BeforeAll
    static void demarrer() {
        // A unit of its own: the test-only entity must not leak into the application's Jpa.
        emf = new PersistenceConfiguration("identite-test")
                .managedClass(FournisseurParId.class)
                .managedClass(Fournisseur.class)
                .managedClass(Article.class)
                .property(PersistenceConfiguration.JDBC_URL, Env.required("DB_URL"))
                .property(PersistenceConfiguration.JDBC_USER, Env.required("DB_USER"))
                .property(PersistenceConfiguration.JDBC_PASSWORD, Env.required("DB_PASSWORD"))
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, "validate")
                .createEntityManagerFactory();
    }

    @AfterAll
    static void arreter() {
        emf.close();
    }

    /** Runs {@code work} in a transaction that is always rolled back: the table stays as it was. */
    private static void sansTrace(Consumer<EntityManager> work) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                work.accept(em);
            } finally {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    void identiteSurIdGenere_perdLObjetApresPersist() {
        sansTrace(em -> {
            var f = new FournisseurParId("Essai HashSet par id", "Nantes");
            Set<FournisseurParId> panier = new HashSet<>();
            panier.add(f);                 // hashCode of a null id

            em.persist(f);                 // IDENTITY: INSERT now, id assigned

            assertThat(f.id).isNotNull();
            assertThat(panier).hasSize(1);
            assertThat(panier.contains(f)).isFalse();   // looked up in another bucket
        });
    }

    @Test
    void identiteSurCleMetier_survitAuPersist() {
        sansTrace(em -> {
            var f = new Fournisseur("Essai HashSet par nom", Adresse.ville("Nantes"));
            Set<Fournisseur> panier = new HashSet<>();
            panier.add(f);

            em.persist(f);

            assertThat(f.getId()).isNotNull();
            assertThat(panier.contains(f)).isTrue();
        });
    }

    @Test
    void unArticleEstEgalASonProxy() {
        Article charge;
        try (EntityManager em = emf.createEntityManager()) {
            charge = em.find(Article.class, 1L);
        }
        try (EntityManager em = emf.createEntityManager()) {
            Article reference = em.getReference(Article.class, 1L);

            assertThat(reference.getClass()).isNotEqualTo(Article.class);   // a generated subclass
            assertThat(charge.equals(reference)).isTrue();                  // false with autre.ref
            assertThat(reference.equals(charge)).isTrue();
        }
    }
}
