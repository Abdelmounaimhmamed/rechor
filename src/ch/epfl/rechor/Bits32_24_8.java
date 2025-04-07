package ch.epfl.rechor;

/**
 * Classe utilitaire permettant d'empaqueter et de désassembler des valeurs 32 bits
 * en une partie de 24 bits et une partie de 8 bits.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public class Bits32_24_8 {

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe utilitaire.
     */
    private Bits32_24_8() {}

    /**
     * Empaquète une valeur de 24 bits et une valeur de 8 bits en une seule valeur 32 bits.
     * @param bits24 Valeur sur 24 bits (doit tenir dans 24 bits).
     * @param bits8 Valeur sur 8 bits (doit tenir dans 8 bits).
     * @return Une valeur 32 bits combinant les deux valeurs d'entrée.
     * @throws IllegalArgumentException Si bits24 dépasse 24 bits ou si bits8 dépasse 8 bits.
     */
    public static int pack(int bits24, int bits8) {
        if ((bits24 >> 24) != 0) {
            throw new IllegalArgumentException("bits24 nécessite plus de bits que prévu");
        }
        if ((bits8 >> 8) != 0) {
            throw new IllegalArgumentException("bits8 nécessite plus de 8 bits");
        }
        return ((bits24 << 8) | bits8);
    }

    /**
     * Extrait la partie 24 bits d'une valeur 32 bits.
     * @param bits32 Valeur 32 bits contenant les données empaquetées.
     * @return La valeur sur 24 bits extraite.
     */
    public static int unpack24(int bits32) {
        return (bits32 >> 8) & ((1 << 24) - 1);
    }

    /**
     * Extrait la partie 8 bits d'une valeur 32 bits.
     * @param bits32 Valeur 32 bits contenant les données empaquetées.
     * @return La valeur sur 8 bits extraite.
     */
    public static int unpack8(int bits32) {
        int mask = ((1 << 8) - 1);
        return (bits32 & mask);
    }
}
