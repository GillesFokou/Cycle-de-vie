package org.sebsy.compta.app;

import org.sebsy.compta.domain.Fournisseur;
import org.sebsy.compta.infra.Jpa;

public final class EssaiPortees {

    public static void main(String[] args) {
        // Outside the try: if the factory cannot be built, its error must not be replaced by
        // the one a second access to Jpa would raise in finally (NoClassDefFoundError).
        var emf = Jpa.factory();
        try {
            var stats = Jpa.statistiques(emf);

            stats.clear();
            boolean memeInstance = emf.callInTransaction(em ->
                    em.find(Fournisseur.class, 1L) == em.find(Fournisseur.class, 1L));
            System.out.println("une transaction   : " + stats.getPrepareStatementCount()
                    + " requête(s), même instance : " + memeInstance);

            stats.clear();
            Fournisseur a1 = emf.callInTransaction(em -> em.find(Fournisseur.class, 1L));
            Fournisseur a2 = emf.callInTransaction(em -> em.find(Fournisseur.class, 1L));
            System.out.println("deux transactions : " + stats.getPrepareStatementCount()
                    + " requête(s), même instance : " + (a1 == a2) + ", equals : " + a1.equals(a2));
        } finally {
            emf.close();
        }
    }
}
