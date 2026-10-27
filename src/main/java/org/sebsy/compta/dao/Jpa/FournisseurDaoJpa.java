
package org.sebsy.compta.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import org.sebsy.compta.dao.FournisseurDao;
import org.sebsy.compta.dao.PersistenceFailure;
import org.sebsy.compta.domain.Fournisseur;
import org.sebsy.compta.infra.Jpa;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class FournisseurDaoJpa implements FournisseurDao {

    private final EntityManagerFactory emf;

    public FournisseurDaoJpa() {
        this(Jpa.factory());
    }

    public FournisseurDaoJpa(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf, "emf");
    }

    private <T> T execute(Supplier<T> action) {
        try {
            return action.get();
        } catch (PersistenceException e) {
            throw new PersistenceFailure(
                    "Échec de la persistance du fournisseur", e);
        }
    }

    @Override
    public Optional<Fournisseur> parId(long id) {
        return execute(() ->
                emf.callInTransaction(em ->
                        Optional.ofNullable(
                                em.find(Fournisseur.class, id)
                        )
                )
        );
    }

    @Override
    public Optional<Fournisseur> parNom(String nom) {
        return execute(() ->
                emf.callInTransaction(em ->
                        em.createQuery(
                                        "SELECT f FROM Fournisseur f "
                                                + "WHERE f.nom = :nom",
                                        Fournisseur.class)
                                .setParameter("nom", nom)
                                .getResultStream()
                                .findFirst()
                )
        );
    }

    @Override
    public List<Fournisseur> parVille(String ville) {
        return execute(() ->
                emf.callInTransaction(em ->
                        em.createQuery(
                                        "SELECT f FROM Fournisseur f "
                                                + "WHERE f.adresse.ville = :ville",
                                        Fournisseur.class)
                                .setParameter("ville", ville)
                                .getResultList()
                )
        );
    }

    @Override
    public List<Fournisseur> toutes() {
        return execute(() ->
                emf.callInTransaction(em ->
                        em.createQuery(
                                        "SELECT f FROM Fournisseur f ORDER BY f.id",
                                        Fournisseur.class)
                                .getResultList()
                )
        );
    }

    @Override
    public Fournisseur creer(Fournisseur fournisseur) {
        Objects.requireNonNull(fournisseur, "fournisseur");

        return execute(() ->
                emf.callInTransaction(em -> {
                    em.persist(fournisseur);
                    return fournisseur;
                })
        );
    }

    @Override
    public boolean renommer(long id, String nouveauNom) {
        Objects.requireNonNull(nouveauNom, "nouveauNom");

        return execute(() ->
                emf.callInTransaction(em -> {
                    Fournisseur fournisseur =
                            em.find(Fournisseur.class, id);

                    if (fournisseur == null) {
                        return false;
                    }

                    fournisseur.setNom(nouveauNom);
                    return true;
                })
        );
    }

    @Override
    public boolean supprimer(long id) {
        return execute(() ->
                emf.callInTransaction(em -> {
                    Fournisseur fournisseur =
                            em.find(Fournisseur.class, id);

                    if (fournisseur == null) {
                        return false;
                    }

                    em.remove(fournisseur);
                    return true;
                })
        );
    }
}
