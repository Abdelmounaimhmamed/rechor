package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;
import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Math.scalb;

/**
 * BufferedStations est une implémentation de l'interface {@link Stations} qui permet
 * d'accéder aux données des gares stockées de manière aplatie dans un {@link ByteBuffer}.
 * <p>
 * Les données de chaque gare sont structurées en trois champs :
 * <ul>
 * <li>Champ 0 (NAMEID, de type U16) : index dans la table des chaînes du nom de la gare.</li>
 * <li>Champ 1 (longitude, de type S32) : valeur brute représentant la longitude en unité anonyme.</li>
 * <li>Champ 2 (latitude, de type S32) : valeur brute représentant la latitude en unité anonyme.</li>
 * </ul>
 * La conversion des valeurs brutes en degrés s'effectue selon la formule :
 * <br>
 * {@code valeurEnDegres = valeurBrute * 360 / 2^32} (implémentée ici via {@code scalb(360.0, -32)}).
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
final public class BufferedStations implements Stations {
    private static final int NAME_ID = 0;
    private static final int LONGI = 1;
    private static final int LATI = 2;

    private final List<String> stringTable;
    private final StructuredBuffer structuredbuffer;
    private static final Structure STRUCTURE = new Structure(

            field(NAME_ID, Structure.FieldType.U16),
            field(LONGI, Structure.FieldType.S32),
            field(LATI, Structure.FieldType.S32));

    /**
     * Construit une instance de BufferedStations.
     *
     * @param stringTable la table des chaînes contenant les noms (et autres chaînes)
    référencés par les gares.
     * @param buffer      le ByteBuffer contenant les données aplaties des gares.
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredbuffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nom de la gare d'index donné.
     * <p>
     * Pour cela, la valeur du champ NAMEID (de type U16) est lue dans le buffer et interprétée
     * comme un index dans la table des chaînes.
     * </p>
     *
     * @param id l'index de la gare.
     * @return le nom de la gare correspondant à cet index.
     */
    @Override
    public String name(int id) {
        int index = structuredbuffer.getU16(NAME_ID, id);

        return stringTable.get(index);
    }

    /**
     * Retourne la longitude de la gare d'index donné, convertie en degrés.
     * <p>
     * La valeur brute (de type S32) est lue dans le buffer et convertie en degrés selon la formule :
     * <br>
     * {@code longitudeEnDegres = valeurBrute * scalb(360.0, -32)}
     * </p>
     *
     * @param id l'index de la gare.
     * @return la longitude en degrés.
     */
    @Override
    public double longitude(int id) {
        int longi = structuredbuffer.getS32(LONGI, id);

        return longi * scalb(360.0, -32);
    }

    /**
     * Retourne la latitude de la gare d'index donné, convertie en degrés.
     * <p>
     * La valeur brute (de type S32) est lue dans le buffer et convertie en degrés selon la formule :
     * <br>
     * {@code latitudeEnDegres = valeurBrute * scalb(360.0, -32)}
     * </p>
     *
     * @param id l'index de la gare.
     * @return la latitude en degrés.
     */
    @Override
    public double latitude(int id) {
        int lati = structuredbuffer.getS32(LATI, id);

        return lati * scalb(360.0, -32);
    }

    /**
     * Retourne le nombre total de gares disponibles dans ce BufferedStations.
     *
     * @return le nombre de gares.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
