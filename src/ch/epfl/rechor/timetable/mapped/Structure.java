package ch.epfl.rechor.timetable.mapped;

/**
 * Représente la structure d'un enregistrement aplati.
 * <p>
 * Une instance de {@code Structure} décrit la disposition (les champs) d'un enregistrement
 * dans des données aplaties. Elle permet de calculer la taille totale d'un enregistrement
 * ainsi que l'offset (la position en octets) de chacun de ses champs.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public class Structure {

    /**
     * Enumération des types de champs disponibles.
     * <ul>
     *   <li>{@code U8} : champ sur 8 bits non signé</li>
     *   <li>{@code U16} : champ sur 16 bits non signé</li>
     *   <li>{@code S32} : champ sur 32 bits signé</li>
     * </ul>
     */
    public enum FieldType { U8, U16, S32 }

    /**
     * Représente un champ dans une structure aplatie.
     * <p>
     * Un champ est défini par son index (la position dans la structure)
     * et par son type. Le constructeur du record lève une
     * {@code NullPointerException} si le type est {@code null}.
     * </p>
     */
    public record Field(int index, FieldType type) {
        public Field {
            if (type == null)
                throw new NullPointerException();
        }
    }

    private final Field[] fields;
    private final int[] positions;
    private int totalsize;

    /**
     * Crée un champ avec l'index et le type spécifiés.
     *
     * @param index l'index du champ dans la structure
     * @param type le type du champ (U8, U16 ou S32)
     * @return une instance de {@code Field} correspondant aux paramètres donnés
     */
    public static Field field(int index, FieldType type) {
        return new Field(index, type);
    }

    /**
     * Construit une structure à partir d'un ensemble de champs.
     * <p>
     * Les champs doivent être fournis dans l'ordre croissant de leurs indices (0, 1, 2, ...).
     * Si un champ n'est pas à la position attendue, une {@code IllegalArgumentException} est levée.
     * Le constructeur calcule la taille totale d'un enregistrement et les positions (offsets)
     * de chaque champ dans cet enregistrement.
     * </p>
     *
     * @param fields les champs constituant la structure
     */
    public Structure(Field... fields) {
        //if (fields.length == 0) throw new IllegalArgumentException();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].index != i) throw new IllegalArgumentException();
        }
        this.fields = fields;
        positions = new int[fields.length];
        for (int i = 0; i < positions.length; i++) {
            switch (fields[i].type()) {
                case U8:
                    positions[i] = totalsize;
                    totalsize += 1;
                    break;
                case U16:
                    positions[i] = totalsize;
                    totalsize += 2;
                    break;
                case S32:
                    positions[i] = totalsize;
                    totalsize += 4;
                    break;
            }
        }
    }

    /**
     * Retourne la taille totale en octets d'un enregistrement défini par cette structure.
     *
     * @return la taille totale en octets
     */
    public int totalSize() {
        return totalsize;
    }

    /**
     * Calcule l'offset (la position en octets) du champ d'index donné pour l'élément spécifié.
     * <p>
     * L'offset est calculé en ajoutant à la position du champ (dans l'enregistrement)
     * le produit du numéro d'élément par la taille totale de l'enregistrement.
     * </p>
     *
     * @param fieldIndex l'index du champ
     * @param elementIndex l'index de l'élément (enregistrement)
     * @return l'offset en octets du champ pour cet élément
     * @throws IndexOutOfBoundsException si l'index du champ est invalide
     */
    public int offset(int fieldIndex, int elementIndex) {
        return positions[fieldIndex] + elementIndex * totalsize;
    }
}
