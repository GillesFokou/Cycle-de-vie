package org.sebsy.compta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 *
 * <p>{@code id_fou} is now mapped as a reference to the supplier. {@code version} stays unmapped
 * until TP 13: VALIDATE only checks what is mapped, and the column has a default.
 */
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The business key behind equals/hashCode: it must never change once the row exists.
    // updatable = false makes Hibernate leave it out of every UPDATE it generates.
    @Column(nullable = false, length = 13, unique = true, updatable = false)
    private String ref;

    @Column(nullable = false, length = 255)
    private String designation;

    // Mirrors DECIMAL(7,2). A double here fails VALIDATE: found [decimal], expecting [float(53)].
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal prix;

    // BOOLEAN in MariaDB is TINYINT(1); Hibernate maps it to boolean with no annotation.
    @Column(nullable = false)
    private boolean actif = true;

    // Owning side: this field carries the foreign key, so it alone decides the SQL.
    // LAZY because the specification's default for a to-one is EAGER.
    // optional = false makes Hibernate refuse a null supplier before any SQL is sent
    // (PropertyValueException); nullable = false documents the NOT NULL column.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fou", nullable = false)
    private Fournisseur fournisseur;

    // Derived value, absent from the table. The annotation, not the Java keyword `transient`,
    // which is about serialization and says nothing to the mapper.
    @Transient
    private BigDecimal prixTtc;

    protected Article() {
        // For Hibernate.
    }

    public Article(String ref, String designation, BigDecimal prix) {
        this.ref = Objects.requireNonNull(ref, "ref is mandatory");
        this.designation = Objects.requireNonNull(designation, "designation is mandatory");
        setPrix(prix);
    }

    public Article(Long id, String ref, String designation, BigDecimal prix, boolean actif) {
        this(ref, designation, prix);
        this.id = id;
        this.actif = actif;
    }

    public Long getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = Objects.requireNonNull(designation);
    }

    public BigDecimal getPrix() {
        return prix;
    }

    /**
     * Refuses a negative price and keeps the column's scale, so that the object holds what the
     * database will store. Dirty checking does not need it: Hibernate compares BigDecimal
     * values with compareTo, 74.99 and 74.990 are the same value to it (TP 07).
     */
    public void setPrix(BigDecimal prix) {
        Objects.requireNonNull(prix, "prix is mandatory");
        if (prix.signum() < 0) {
            throw new IllegalArgumentException("prix must be >= 0 (CHECK constraint ck_article_prix)");
        }
        this.prix = prix.setScale(2, RoundingMode.HALF_UP);
        this.prixTtc = null;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    // Package-private: callers go through Fournisseur.ajouterArticle, which wires both sides.
    void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    /** Computed once per instance, never stored. */
    public BigDecimal getPrixTtc() {
        if (prixTtc == null) {
            prixTtc = prix.multiply(new BigDecimal("1.20")).setScale(2, RoundingMode.HALF_UP);
        }
        return prixTtc;
    }

    /**
     * Identity on {@code ref}. The other side is read through {@code getRef()}: a Hibernate
     * proxy is a subclass whose own fields stay null, so {@code autre.ref} would read null and
     * an article would not equal its own proxy.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Article autre && ref.equals(autre.getRef());
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    @Override
    public String toString() {
        return "Article[id=%s, ref=%s, prix=%s]".formatted(id, ref, prix);
    }
}
