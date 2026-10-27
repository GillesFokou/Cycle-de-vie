package org.sebsy.compta.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * A supplier's postal address: a value, not an entity. State at the end of TP 05.
 *
 * <p>No identity and no table of its own: its three components are columns of
 * {@code fournisseur}. Jakarta Persistence 3.2 accepts a record as an embeddable, and a record
 * already has the right equality (by value).
 *
 * <p>Nothing here names a column: the owner decides, through {@code @AttributeOverride}, so
 * the same type could be embedded in a table whose columns are named differently.
 */
@Embeddable
public record Adresse(String rue, String codePostal, String ville) {

    public Adresse {
        Objects.requireNonNull(ville, "ville is mandatory (column is NOT NULL)");
    }

    /** The frequent case where only the town is known. */
    public static Adresse ville(String ville) {
        return new Adresse(null, null, ville);
    }

    public boolean estComplete() {
        return rue != null && codePostal != null;
    }
}
