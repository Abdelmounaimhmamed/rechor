package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;


/**
 * Classe utilitaire pour empaqueter et manipuler des critères sous forme de valeurs long.
 *
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 */
public class PackedCriteria {

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe utilitaire.
     */
    private PackedCriteria() {}

    /**
     * Empaquète les critères sous forme d'une valeur long.
     * @param arrMins Minutes d'arrivée (entre -240 et 2879 inclus).
     * @param changes Nombre de changements (entre 0 et 127).
     * @param payload Charge utile stockée dans les 32 bits inférieurs.
     * @return Une valeur long contenant les critères empaquetés.
     * @throws IllegalArgumentException Si les valeurs ne sont pas dans les limites spécifiées.
     *
     *
     */
    public static long pack(int arrMins, int changes, int payload) {
        if (arrMins < -240 || arrMins > 47 * 60 + 59) {
            throw new IllegalArgumentException("Heure d'arrivée invalide");
        }
        if (changes > 127 || changes < 0) {
            throw new IllegalArgumentException("Changes invalides ");
        }
        long result = 0L;
        result |= Integer.toUnsignedLong(payload);
        result |= (long) changes << 32;
        result |= (long) (arrMins + 240) << 39;
        return result;
    }

    /**
     * Vérifie si le critère contient des minutes de départ.
     * @param criteria Critère empaqueté.
     * @return True si des minutes de départ sont présentes, sinon false.
     */
    public static boolean hasDepMins(long criteria) {
        return (criteria >>> 51) != 0;
    }

    /**
     * Extrait les minutes de départ.
     * @param criteria Critère empaqueté.
     * @return Minutes de départ.
     * @throws IllegalArgumentException Si aucune minute de départ n'est stockée.
     */
    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        return (int) (4095 - (criteria >> 51) - 240);
    }

    /**
     * Extrait les minutes d'arrivée.
     * @param criteria Critère empaqueté.
     * @return Minutes d'arrivée.
     */
    public static int arrMins(long criteria) {
        return (int) ((criteria >>> 39) & 0b111111111111) - 240;
    }

    /**
     * Extrait le nombre de changements.
     * @param criteria Critère empaqueté.
     * @return Nombre de changements.
     */
    public static int changes(long criteria) {
        return (int) ((criteria >>> 32) & 0x7F);
    }

    /**
     * Extrait la charge utile.
     * @param criteria Critère empaqueté.
     * @return Valeur de la charge utile.
     */
    public static int payload(long criteria) {
        return (int) (criteria & 0xFFFFFFFF);
    }

    /**
     * Vérifie si un critère en domine un autre ou est égal.
     * @param criteria1 Premier critère.
     * @param criteria2 Deuxième critère.
     * @return True si criteria1 domine ou est égal à criteria2.
     * @throws IllegalArgumentException Si les critères ne sont pas compatibles.
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        boolean hasDepMins1 = hasDepMins(criteria1);
        boolean hasDepMins2 = hasDepMins(criteria2);
        if (hasDepMins1 != hasDepMins2) {
            throw new IllegalArgumentException();
        }
        boolean dp = true;
        if (hasDepMins1) {
            dp = (depMins(criteria1) >= depMins(criteria2));
        }
        return dp && (arrMins(criteria1) <= arrMins(criteria2)) &&
                (changes(criteria1) <= changes(criteria2));
    }

    /**
     * Supprime les minutes de départ d'un critère.
     * @param criteria Critère empaqueté.
     * @return Nouvelle valeur sans les minutes de départ.
     */
    public static long withoutDepMins(long criteria) {
        long mask = ((1L << 51) - 1) | (-1L << 63);
        return criteria & mask;
    }

    /**
     * Ajoute les minutes de départ à un critère.
     * @param criteria Critère empaqueté.
     * @param depMins1 Minutes de départ à ajouter.
     * @return Nouvelle valeur contenant les minutes de départ.
     */
    public static long withDepMins(long criteria, int depMins1) {

        return ((long) (4095 - (depMins1 + 240)) << 51) | criteria;
    }

    /**
     * Ajoute un changement supplémentaire.
     * @param criteria Critère empaqueté.
     * @return Nouvelle valeur avec un changement supplémentaire.
     */
    public static long withAdditionalChange(long criteria) {
        int changes = changes(criteria) + 1;
        if (hasDepMins(criteria)) {
            return withDepMins(pack(arrMins(criteria), changes, payload(criteria)), depMins(criteria));
        } else {
            return pack(arrMins(criteria), changes, payload(criteria));
        }
    }

    /**
     * Met à jour la charge utile d'un critère.
     * @param criteria Critère empaqueté.
     * @param payload1 Nouvelle charge utile.
     * @return Nouvelle valeur avec la charge utile mise à jour.
     */
    public static long withPayload(long criteria, int payload1) {
        long payloadAsLong = Integer.toUnsignedLong(payload1);
        long upperBits = criteria & 0xFFFFFFFF00000000L;
        payloadAsLong = payloadAsLong & 0xFFFFFFFFL;
        return upperBits | payloadAsLong;
    }
}
