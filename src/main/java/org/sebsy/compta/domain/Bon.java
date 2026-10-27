package org.sebsy.compta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A purchase order header, mapped on {@code bon}. State at the end of TP 08.
 *
 * <p>The supplier is mapped; the lines ({@code compo}) arrive in TP 09. No collection of orders
 * on Fournisseur: nothing in the application navigates in that direction yet.
 */
@Entity
@Table(name = "bon")
public class Bon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business key: UNIQUE, and never rewritten once issued.
    @Column(nullable = false, unique = true, updatable = false)
    private int numero;

    // java.time needs no annotation: the Java type says "date and time" (no @Temporal).
    // The column name differs from the field name, so it is given.
    @Column(name = "date_cmde", nullable = false, updatable = false)
    private LocalDateTime dateCommande;

    // STRING, never the ORDINAL default: the name survives a reordering of the enum, and the
    // column is a VARCHAR anyway. ORDINAL fails VALIDATE on it (expecting tinyint).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut = Statut.BROUILLON;

    // Integer, not int: NULL means "not delivered yet". An int cannot hold it, and Hibernate
    // refuses to load order 7 into one.
    private Integer delai;

    // Same shape as Article.fournisseur: owning side, LAZY, never null.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fou", nullable = false)
    private Fournisseur fournisseur;

    protected Bon() {
        // For Hibernate.
    }

    public Bon(int numero, LocalDateTime dateCommande, Fournisseur fournisseur) {
        this.numero = numero;
        this.dateCommande = Objects.requireNonNull(dateCommande, "dateCommande is mandatory");
        this.fournisseur = Objects.requireNonNull(fournisseur, "fournisseur is mandatory");
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public Statut getStatut() {
        return statut;
    }

    public Integer getDelai() {
        return delai;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    /** Identity on the order number, read through the getter for the same proxy reason as Article. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Bon autre && numero == autre.getNumero();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }

    @Override
    public String toString() {
        return "Bon[numero=%d, statut=%s, delai=%s]".formatted(numero, statut, delai);
    }
}
