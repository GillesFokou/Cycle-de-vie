package org.sebsy.compta.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * <p>The JDBC layer still uses this class: the annotations add a mapping, they remove nothing.
 * The two choices made in TP 02 (non-final class with a protected no-argument constructor,
 * {@code Long} identifier) are what the ORM requires, the second one for the lazy proxy.
 */
@Entity
@Table(name = "fournisseur")
public class Fournisseur {

    // IDENTITY matches the existing AUTO_INCREMENT column: the database assigns the key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mirrors VARCHAR(60) NOT NULL UNIQUE. VALIDATE does not compare lengths; the annotation
    // documents the column and is what a generated DDL would use.
    @Column(nullable = false, length = 60, unique = true)
    private String nom;

    // Three columns of this table. Without the override, the component codePostal would look
    // for a column named codePostal: "missing column [codePostal] in table [fournisseur]".
    @Embedded
    @AttributeOverride(name = "codePostal", column = @Column(name = "code_postal"))
    private Adresse adresse;

    // Inverse side: a read-only mirror of Article.fournisseur, never consulted to write.
    // mappedBy names the FIELD on Article, not the column. No cascade: an article outlives
    // any operation on its supplier (aggregation, not composition).
    // Not final: Hibernate writes its own collection here by reflection when it loads the
    // entity, and JPA 3.2 section 2.1 forbids final persistent fields (JDK 26 warns, JEP 500).
    @OneToMany(mappedBy = "fournisseur", fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    protected Fournisseur() {
        // For Hibernate, which instantiates by reflection before setting the fields.
    }

    /** A supplier not yet saved: no identifier. */
    public Fournisseur(String nom, Adresse adresse) {
        this.nom = Objects.requireNonNull(nom, "nom is mandatory");
        this.adresse = Objects.requireNonNull(adresse, "adresse is mandatory");
    }

    /** A supplier read by the JDBC layer, identifier known. */
    public Fournisseur(Long id, String nom, Adresse adresse) {
        this(nom, adresse);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = Objects.requireNonNull(nom);
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = Objects.requireNonNull(adresse);
    }

    /** Read-only copy: the collection only changes through ajouterArticle. */
    public List<Article> getArticles() {
        return List.copyOf(articles);
    }

    /**
     * Wires both sides. The collection keeps the in-memory graph consistent; the foreign key,
     * held by Article, is what gets written. There is no retirerArticle: id_fou is NOT NULL,
     * so an article never leaves a supplier without joining another one.
     */
    public void ajouterArticle(Article article) {
        Objects.requireNonNull(article, "article is mandatory");
        articles.add(article);
        article.setFournisseur(this);
    }

    /**
     * Identity on {@code nom}, a UNIQUE NOT NULL business key. The other side is read through
     * its getter, never its field: when it is a Hibernate proxy, its own fields stay null and
     * only the getter loads the real state.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Fournisseur autre && nom.equals(autre.getNom());
    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }

    @Override
    public String toString() {
        return "Fournisseur[id=%s, nom=%s, ville=%s]".formatted(id, nom, adresse.ville());
    }
}
