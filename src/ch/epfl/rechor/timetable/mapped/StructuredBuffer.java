package ch.epfl.rechor.timetable.mapped;
import java.nio.ByteBuffer;
import static ch.epfl.rechor.Preconditions.checkArgument;

/**
 * Représente un tampon structuré qui fournit un accès pratique à un ByteBuffer
 * contenant des données aplaties organisées selon une {@link Structure} donnée.
 * <p>
 * Le {@code StructuredBuffer} est construit sur un {@code ByteBuffer} dont la capacité
 * doit être un multiple de la taille totale d'un enregistrement (tel que défini
 par la {@link Structure}).
 * Cette classe fournit des méthodes pour récupérer des valeurs de différents
 types de champs (U8, U16 et S32)
 * à partir des données aplaties.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public class StructuredBuffer {
    private final Structure structure;
    private final ByteBuffer buffer;

    /**
     * Construit un {@code StructuredBuffer} avec la structure spécifiée et le ByteBuffer.
     *
     * @param structure la structure décrivant la disposition de chaque enregistrement.
     * @param buffer    le ByteBuffer contenant les données aplaties.
     * @throws IllegalArgumentException si la capacité du buffer n'est pas un multiple
    de la taille totale
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer) {

        checkArgument(buffer.capacity() % structure.totalSize() == 0);
        this.structure = structure;
        this.buffer = buffer;
    }

    /**
     * Retourne le nombre d'enregistrements contenus dans le buffer.
     *
     * @return le nombre d'enregistrements.
     */
    public int size() {
        return buffer.capacity() / structure.totalSize();
    }

    /**
     * Récupère la valeur entière non signée sur 8 bits (U8) du champ spécifié
     * de l'enregistrement spécifié.
     *
     * @param fieldIndex   l'indice du champ.
     * @param elementIndex l'indice de l'enregistrement.
     * @return la valeur entière non signée sur 8 bits du champ.
     */
    public int getU8(int fieldIndex, int elementIndex) {

        int pos = structure.offset(fieldIndex, elementIndex);

        return Byte.toUnsignedInt(buffer.get(pos));
    }

    /**
     * Récupère la valeur entière non signée sur 16 bits (U16) du champ spécifié
     * de l'enregistrement spécifié.
     *
     * @param fieldIndex   l'indice du champ.
     * @param elementIndex l'indice de l'enregistrement.
     * @return la valeur entière non signée sur 16 bits du champ.
     */
    public int getU16(int fieldIndex, int elementIndex) {

        int pos = structure.offset(fieldIndex, elementIndex);

        return Short.toUnsignedInt(buffer.getShort(pos));
    }

    /**
     * Récupère la valeur entière signée sur 32 bits (S32) du champ spécifié
     * de l'enregistrement spécifié.
     *
     * @param fieldIndex   l'indice du champ.
     * @param elementIndex l'indice de l'enregistrement.
     * @return la valeur entière signée sur 32 bits du champ.
     */
    public int getS32(int fieldIndex, int elementIndex) {

        int pos = structure.offset(fieldIndex, elementIndex);

        return buffer.getInt(pos);
    }
}
