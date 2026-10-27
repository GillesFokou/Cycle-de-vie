package org.sebsy.compta.domain;

/**
 * Lifecycle of a purchase order.
 *
 * <p>Stored by NAME ({@code @Enumerated(EnumType.STRING)}), never by position: the column is a
 * VARCHAR(20) whose CHECK constraint lists these four names, so the order of the constants
 * below carries no meaning and can change freely.
 */
public enum Statut {
    BROUILLON,
    VALIDE,
    LIVRE,
    ANNULE
}
