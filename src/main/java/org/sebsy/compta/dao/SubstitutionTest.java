
package org.sebsy.compta.dao;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sebsy.compta.dao.jdbc.FournisseurDaoJdbc;
import org.sebsy.compta.dao.jpa.FournisseurDaoJpa;
import org.sebsy.compta.domain.Fournisseur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubstitutionTest {

    static Stream<Arguments> daos() {
        return Stream.of(
                Arguments.of("JDBC", new FournisseurDaoJdbc()),
                Arguments.of("JPA", new FournisseurDaoJpa())
        );
    }

    @ParameterizedTest(name = "{0} : lectures")
    @MethodSource("daos")
    void lectures(String implementation, FournisseurDao dao) {
        List<Fournisseur> fournisseurs = dao.toutes();

        assertFalse(fournisseurs.isEmpty());

        Fournisseur premier = fournisseurs.get(0);

        assertTrue(dao.parId(premier.getId()).isPresent());
        assertTrue(dao.parId(Long.MAX_VALUE).isEmpty());

        assertEquals(
                Optional.of(premier),
                dao.parNom(premier.getNom())
        );

        assertTrue(
                dao.parVille(premier.getAdresse().ville())
                        .contains(premier)
        );
    }

    @ParameterizedTest(name = "{0} : écritures")
    @MethodSource("daos")
    void creerRenommerSupprimer(
            String implementation, FournisseurDao dao) {

        Fournisseur reference = dao.toutes().get(0);

        String nomInitial = "TP07_"
                + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 12);
        String nouveauNom = nomInitial + "_MOD";

        Long idCree = null;

        try {
            Fournisseur cree = dao.creer(
                    new Fournisseur(nomInitial, reference.getAdresse())
            );

            idCree = cree.getId();

            assertNotNull(idCree);
            assertEquals(nomInitial, cree.getNom());
            assertTrue(dao.parNom(nomInitial).isPresent());

            assertTrue(dao.renommer(idCree, nouveauNom));
            assertTrue(dao.parNom(nouveauNom).isPresent());
            assertTrue(dao.parNom(nomInitial).isEmpty());

            assertTrue(dao.supprimer(idCree));
            assertTrue(dao.parId(idCree).isEmpty());

            // La suppression a réussi : le nettoyage n'est plus nécessaire.
            idCree = null;
        } finally {
            // Nettoyage même si une assertion échoue.
            if (idCree != null) {
                dao.supprimer(idCree);
            }
        }
    }

    @ParameterizedTest(name = "{0} : suppression interdite")
    @MethodSource("daos")
    void supprimerFournisseurAvecArticles(
            String implementation, FournisseurDao dao) {

        assertThrows(
                PersistenceFailure.class,
                () -> dao.supprimer(1)
        );
    }

    @ParameterizedTest(name = "{0} : nom en double")
    @MethodSource("daos")
    void creerAvecNomExistant(
            String implementation, FournisseurDao dao) {

        Fournisseur existant = dao.toutes().get(0);

        assertThrows(
                PersistenceFailure.class,
                () -> dao.creer(
                        new Fournisseur(
                                existant.getNom(),
                                existant.getAdresse()
                        )
                )
        );
    }
}
