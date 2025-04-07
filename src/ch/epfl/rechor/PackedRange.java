package ch.epfl.rechor;

import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Classe utilitaire pour empaqueter et désempaqueter des intervalles entiers dans une seule valeur
 sur 32 bits.
 * <p>
 * Dans la représentation empaquetée, les 24 bits les plus significatifs stockent
 le début de l'intervalle
 * (inclus) et les 8 bits les moins significatifs stockent la longueur de l'intervalle.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public class PackedRange {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private PackedRange() {
    }

    /**
     * Empaquète un intervalle dans un entier sur 32 bits.
     * <p>
     * L'intervalle est défini par son début inclusif ({@code startInclusive})
     et sa fin exclusive ({@code endExclusive}).
     * La valeur empaquetée se compose des 24 bits les plus significatifs contenant le début,
     et des 8 bits les moins significatifs contenant la longueur
     * de l'intervalle (c'est-à-dire {@code endExclusive - startInclusive}).
     * <br>
     * Préconditions (vérifiées via {@code checkArgument}) :
     * <ul>
     *   <li>{@code startInclusive} doit être positif ou nul et représentable sur 24 bits.</li>
     *   <li>La longueur de l'intervalle (c'est-à-dire {@code endExclusive - startInclusive})
     doit être positive et représentable sur 8 bits.</li>
     * </ul>
     * </p>
     *
     * @param startInclusive le début inclusif de l'intervalle
     * @param endExclusive   la fin exclusive de l'intervalle
     * @return l'entier sur 32 bits représentant l'intervalle empaqueté
     * @throws IllegalArgumentException si le début ou la longueur de l'intervalle
    n'est pas représentable sur le nombre de bits requis
     */
    public static int pack(int startInclusive, int endExclusive) {
        checkArgument(startInclusive >= 0);
        int length = endExclusive - startInclusive;
        checkArgument(length > 0 || (length >> 8) == 0);

        return Bits32_24_8.pack(startInclusive,
                length);
    }

    /**
     * Extrait la longueur de l'intervalle à partir de l'entier empaqueté.
     * <p>
     * La longueur est stockée dans les 8 bits les moins significatifs de la valeur empaquetée.
     * </p>
     *
     * @param interval l'intervalle empaqueté sous forme d'entier sur 32 bits
     * @return la longueur de l'intervalle
     */
    public static int length(int interval) {
        return Bits32_24_8.unpack8(interval);
    }

    /**
     * Extrait le début inclusif de l'intervalle à partir de l'entier empaqueté.
     * <p>
     * Le début est stocké dans les 24 bits les plus significatifs de la valeur empaquetée.
     * </p>
     *
     * @param interval l'intervalle empaqueté sous forme d'entier sur 32 bits
     * @return le début inclusif de l'intervalle
     */
    public static int startInclusive(int interval) {
        return (interval >>> 8);
    }

    /**
     * Calcule la fin exclusive de l'intervalle à partir de l'entier empaqueté.
     * <p>
     * La fin exclusive est calculée comme la somme du début inclusif
     * (extrait des 24 bits les plus significatifs)
     * et de la longueur (extrait des 8 bits les moins significatifs).
     * </p>
     *
     * @param interval l'intervalle empaqueté sous forme d'entier sur 32 bits
     * @return la fin exclusive de l'intervalle
     */
    public static int endExclusive(int interval) {
        return startInclusive(interval) + length(interval);
    }

}
