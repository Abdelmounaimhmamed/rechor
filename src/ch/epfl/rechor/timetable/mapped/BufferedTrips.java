package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * BufferedTrips est une classe finale qui implémente l'interface {@link Trips}
 * et qui permet d'accéder aux données aplaties des courses de transport public.
 * <p>
 * Les courses sont représentées de manière aplatie à l'aide d'un {@link StructuredBuffer} et d'une
 * structure définie par deux champs de type U16 :
 * <ul>
 *   <li><strong>ROUTE_ID</strong> : index de la ligne de la course.</li>
 *   <li><strong>DESTINATION_ID</strong> : index de chaîne correspondant à la destination finale.
 </li>
 * </ul>
 * La table des chaînes, fournie sous forme de {@code List<String>},
 permet d'obtenir le nom de la destination.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class BufferedTrips implements Trips {
    private static final int ROUTE_ID = 0;
    private static final int DESTINATION_ID = 1;

    private final List<String> stringTable;
    private final StructuredBuffer structuredbuffer;
    private static final Structure structure = new Structure(

            field(ROUTE_ID, Structure.FieldType.U16),
            field(DESTINATION_ID, Structure.FieldType.U16));

    /**
     * Construit une instance de {@code BufferedTrips}.
     * <p>
     * Ce constructeur initialise le {@link StructuredBuffer} à partir du {@link ByteBuffer}
     * contenant les données aplaties des courses, et stocke la table des chaînes utilisée
     * pour récupérer les destinations.
     * </p>
     *
     * @param stringTable la table des chaînes servant à traduire les index de destination.
     * @param buffer le {@link ByteBuffer} contenant les données aplaties des courses.
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredbuffer = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne l'index de la ligne (routeId) associée à la course dont l'index est spécifié.
     *
     * @param id l'index de la course dans les données aplaties.
     * @return l'index de la ligne de la course.
     */
    @Override
    public int routeId(int id) {
        return structuredbuffer.getU16(ROUTE_ID, id);
    }

    /**
     * Retourne la destination finale de la course dont l'index est spécifié.
     * <p>
     * La méthode lit l'index de chaîne (DESTINATION_ID) depuis le {@link StructuredBuffer}
     * et renvoie la chaîne correspondante depuis la table des chaînes.
     * </p>
     *
     * @param id l'index de la course dans les données aplaties.
     * @return la destination finale de la course.
     */
    @Override
    public String destination(int id) {
        int index = structuredbuffer.getU16(DESTINATION_ID, id);

        return stringTable.get(index);
    }

    /**
     * Retourne le nombre de courses présentes dans les données aplaties.
     *
     * @return le nombre de courses.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
