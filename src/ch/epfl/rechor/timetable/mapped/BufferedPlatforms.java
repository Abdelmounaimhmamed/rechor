package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.timetable.Platforms;
import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * BufferedPlatforms est une classe finale qui implémente l'interface {@link Platforms},
 * fournissant un accès aux données de quais stockées sous un format aplati.
 * <p>
 * Chaque enregistrement de quai est composé de deux champs :
 * <ul>
 *   <li><strong>Champ 0 (nameid) :</strong> une valeur U16 représentant un
 indice dans une table de chaînes pour le nom du quai.</li>
 *   <li><strong>Champ 1 (stationid) :</strong> une valeur U16 représentant un
 indice pour la station parente.</li>
 * </ul>
 * Les données aplaties sont stockées dans un {@link ByteBuffer} et accessibles via un
 {@link StructuredBuffer},
 * en utilisant une {@link Structure} pour décrire la disposition de chaque enregistrement.
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class BufferedPlatforms implements Platforms {
    private static final int NAME_ID = 0;
    private static final int STATION_ID = 1;
    private final List<String> stringTable;
    private final StructuredBuffer structuredbuffer;
    private static final Structure STRUCTURE = new Structure(

            field(NAME_ID, Structure.FieldType.U16),
            field(STATION_ID, Structure.FieldType.U16));

    /**
     * Construit une instance de BufferedPlatforms.
     *
     * @param stringTable une liste de chaînes utilisée comme table de correspondance
    pour retrouver les noms des quais.
     * @param buffer      le ByteBuffer contenant les données de quais sous forme aplatie.
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){
        this.stringTable = stringTable;
        this.structuredbuffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le nom du quai correspondant à l'index d'enregistrement donné.
     * <p>
     * Le nom du quai est déterminé en lisant la valeur U16 au champ d'index 0,
     * qui est ensuite utilisée comme indice dans la table de chaînes fournie.
     * </p>
     *
     * @param id l'index d'enregistrement du quai.
     * @return le nom du quai.
     */
    @Override
    public String name(int id) {
        int index = structuredbuffer.getU16(NAME_ID, id);

        return stringTable.get(index);
    }

    /**
     * Retourne l'identifiant de la station parente pour le quai à l'index d'enregistrement donné.
     * <p>
     * Cette valeur est obtenue en lisant la valeur U16 au champ d'index 1 du buffer structuré.
     * </p>
     *
     * @param id l'index d'enregistrement du quai.
     * @return l'identifiant de la station parente.
     */
    @Override
    public int stationId(int id) {
        return structuredbuffer.getU16(STATION_ID, id);
    }

    /**
     * Retourne le nombre d'enregistrements de quais stockés dans ce buffer.
     *
     * @return le nombre de quais.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
