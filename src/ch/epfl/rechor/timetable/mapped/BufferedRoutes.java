package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * BufferedRoutes fournit un accès aux données aplaties des lignes (routes) de transport public.
 * <p>
 * Les lignes sont représentées de manière aplatie à l'aide d'un {@link StructuredBuffer} et d'une
 * structure décrite par deux champs :
 * <ul>
 *   <li><strong>NAME_ID</strong> (U16) : l'index de chaîne dans la table des chaînes, correspondant au nom de la ligne.</li>
 *   <li><strong>KIND</strong> (U8) : un entier indiquant le type de véhicule desservant la ligne (0 = TRAM, 1 = METRO, etc.).</li>
 * </ul>
 * <p>
 * La classe utilise la table des chaînes fournie (sous forme de {@code List<String>}) pour
 * traduire les indices en noms de lignes et l'énumération {@link Vehicle} pour obtenir le type de véhicule.
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class BufferedRoutes implements Routes {
    private static final int NAME_ID = 0;
    private static final int KIND = 1;

    private final List<String> stringTable;
    private final StructuredBuffer structuredbuffer;
    private static final Structure STRUCTURE = new Structure(
            field(NAME_ID, Structure.FieldType.U16),
            field(KIND, Structure.FieldType.U8));

    /**
     * Construit une instance de {@code BufferedRoutes} à partir d'un ByteBuffer et
     d'une table de chaînes.
     * <p>
     * Le ByteBuffer contient les données aplaties des lignes, qui sont interprétées
     selon la structure définie.
     * La table de chaînes est utilisée pour traduire les indices de nom en chaîne.
     * </p>
     *
     * @param stringTable la table des chaînes utilisée pour les noms des lignes.
     * @param buffer le ByteBuffer contenant les données aplaties des lignes.
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredbuffer = new StructuredBuffer(STRUCTURE, buffer);
    }

    /**
     * Retourne le type de véhicule desservant la ligne d'index donné.
     * <p>
     * Le type de véhicule est extrait comme un entier (U8) à partir du buffer,
     * puis il est utilisé pour récupérer l'instance correspondante dans la liste {@link Vehicle#ALL}.
     * </p>
     *
     * @param id l'index de la ligne dans les données aplaties.
     * @return le type de véhicule (instance de {@link Vehicle}) associé à la ligne.
     */
    @Override
    public Vehicle vehicle(int id) {
        int type = structuredbuffer.getU8(KIND, id);

        return Vehicle.ALL.get(type);
    }

    /**
     * Retourne le nom de la ligne d'index donné.
     * <p>
     * Le nom est obtenu en extrayant l'indice du nom (NAME_ID) (de type U16) depuis le buffer,
     * puis en récupérant la chaîne correspondante dans la table des chaînes.
     * </p>
     *
     * @param id l'index de la ligne dans les données aplaties.
     * @return le nom de la ligne.
     */
    @Override
    public String name(int id) {
        int index = structuredbuffer.getU16(NAME_ID, id);

        return stringTable.get(index);
    }

    /**
     * Retourne le nombre total de lignes (routes) présentes dans les données aplaties.
     *
     * @return la taille (nombre d'enregistrements) des données.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
