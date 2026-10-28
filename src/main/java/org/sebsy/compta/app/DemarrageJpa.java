package org.sebsy.compta.app;

import org.sebsy.compta.domain.Fournisseur;
import org.sebsy.compta.infra.Jpa;

public final class DemarrageJpa {

    public static void main(String[] args) {
        // Outside the try: if the factory cannot be built, its error must not be replaced by
        // the one a second access to Jpa would raise in finally (NoClassDefFoundError).
        var emf = Jpa.factory();
        try {
            System.out.println("Fabrique : " + emf.getClass().getName());
            Fournisseur f = emf.callInTransaction(em -> em.find(Fournisseur.class, 4L));
            System.out.println("find(4)  : " + f + ", adresse complète : " + f.getAdresse().estComplete());
        } finally {
            emf.close();
        }
    }
}
