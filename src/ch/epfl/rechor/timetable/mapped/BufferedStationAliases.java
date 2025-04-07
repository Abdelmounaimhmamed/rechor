package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;
import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.*;

/**
 * BufferedStationAliases est une implémentation finale de l'interface {@link StationAliases}
 * permettant d'accéder aux noms alternatifs des gares stockées sous forme aplatie dans un {@link ByteBuffer}.
 * <p>
 * Chaque enregistrement de données est composé de deux champs :
 * <ul>
 *   <li>Champ 0 (alias) de type U16 : l'index dans la table des chaînes du nom alternatif de la gare.</li>
 *   <li>Champ 1 (stationname) de type U16 : l'index dans la table des chaînes du nom officiel de la gare.</li>
 * </ul>
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class BufferedStationAliases implements StationAliases {
    private static final int ALIAS = 0;
    private static final int STATION_NAME = 1;
    private final List<String> stringTable;
    private final StructuredBuffer structuredbuffer;
    private static final Structure STRUCTURE = new Structure(
            field(ALIAS, FieldType.U16),
            field(STATION_NAME, FieldType.U16));

    /**
     * Construit une instance de BufferedStationAliases.
     *
     * @param stringTable la table des chaînes utilisée pour obtenir les noms à partir des indices.
     * @param buffer      le ByteBuffer contenant les données aplaties des alias.
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredbuffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nom alternatif de la gare pour l'enregistrement d'index {@code id}.
     * <p>
     * La valeur du champ alias (de type U16) est lue dans le buffer et interprétée comme un index
     * dans la table des chaînes.
     * </p>
     *
     * @param id l'index de l'enregistrement.
     * @return le nom alternatif correspondant.
     */
    @Override
    public String alias(int id) {
        int index = structuredbuffer.getU16(ALIAS, id);

        return stringTable.get(index);
    }

    /**
     * Retourne le nom officiel de la gare associé au nom alternatif pour l'enregistrement d'index
     {@code id}.
     * La valeur du champ stationname (de type U16) est lue dans le buffer et interprétée comme un
     index
     * dans la table des chaînes.
     *
     * @param id l'index de l'enregistrement.
     * @return le nom officiel de la gare correspondant.
     */
    @Override
    public String stationName(int id) {
        int index = structuredbuffer.getU16(STATION_NAME, id);

        return stringTable.get(index);
    }

    /**
     * Retourne le nombre total d'enregistrements disponibles dans ce BufferedStationAliases.
     *
     * @return le nombre d'alias.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
