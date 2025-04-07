package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * BufferedConnections fournit un accès aux données aplaties des liaisons de transport public,
 * ainsi qu'à l'index de la liaison suivante dans une course.
 * Les données des liaisons sont stockées dans un ByteBuffer et organisées selon la structure
 suivante :
 * <ul>
 *   <li>Champ 0 (DEP_STOP_ID) : U16, représente l'index de l'arrêt de départ.</li>
 *   <li>Champ 1 (DEP_MINUTES) : U16, représente l'heure de départ en minutes après minuit.</li>
 *   <li>Champ 2 (ARR_STOP_ID) : U16, représente l'index de l'arrêt d'arrivée.</li>
 *   <li>Champ 3 (ARR_MINUTES) : U16, représente l'heure d'arrivée en minutes après minuit.</li>
 *   <li>Champ 4 (TRIP_POS_ID) : S32, dont les 24 bits de poids fort représentent l'index
 de la course et les 8 bits de poids faible la position de la liaison dans la course.</li>
 * </ul>
 * En plus des liaisons, la classe reçoit un second ByteBuffer (succBuffer) qui contient,
 * pour chaque liaison, l'index de la liaison suivante
 (ou la première de la course, pour la dernière liaison),
 * empaqueté en un entier (S32).
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class BufferedConnections implements Connections {
    private static final int DEP_STOP_ID = 0;
    private static final int DEP_MINUTES = 1;
    private static final int ARR_STOP_ID = 2;
    private static final int ARR_MINUTES = 3;
    private static final int TRIP_POS_ID = 4;

    /**
     * Buffer d'entiers représentant, pour chaque liaison, l'index de la liaison suivante.
     */
    private final IntBuffer succBuffer;
    /**
     * StructuredBuffer permettant d'accéder aux champs des liaisons aplaties.
     */
    private final StructuredBuffer structuredbuffer;
    /**
     * Structure décrivant la disposition des champs des liaisons aplaties.
     */
    private static final Structure STRUCTURE = new Structure(
            field(DEP_STOP_ID, Structure.FieldType.U16),
            field(DEP_MINUTES, Structure.FieldType.U16),
            field(ARR_STOP_ID, Structure.FieldType.U16),
            field(ARR_MINUTES, Structure.FieldType.U16),
            field(TRIP_POS_ID, Structure.FieldType.S32));

    /**
     * Construit une instance de BufferedConnections.
     * Le constructeur crée un StructuredBuffer à partir du ByteBuffer contenant
     les données des liaisons
     * et convertit le succBuffer en un IntBuffer, ce qui permet d'accéder directement
     à l'index de la liaison suivante.
     *
     * @param buffer le ByteBuffer contenant les données aplaties des liaisons.
     * @param succBuffer le ByteBuffer contenant les indices (en int) des liaisons suivantes.
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        this.structuredbuffer = new StructuredBuffer(STRUCTURE, buffer);
        this.succBuffer = succBuffer.asIntBuffer();
    }

    /**
     * Retourne l'index de l'arrêt de départ de la liaison à l'index donné.
     *
     * @param id l'index de la liaison dans les données aplaties.
     * @return l'index de l'arrêt de départ (U16).
     */
    @Override
    public int depStopId(int id) {
        return structuredbuffer.getU16(DEP_STOP_ID, id);
    }

    /**
     * Retourne l'heure de départ de la liaison (exprimée en minutes après minuit)
     * pour l'enregistrement d'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'heure de départ (U16).
     */
    @Override
    public int depMins(int id) {
        return structuredbuffer.getU16(DEP_MINUTES, id);
    }

    /**
     * Retourne l'index de l'arrêt d'arrivée de la liaison à l'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'index de l'arrêt d'arrivée (U16).
     */
    @Override
    public int arrStopId(int id) {
        return structuredbuffer.getU16(ARR_STOP_ID, id);
    }

    /**
     * Retourne l'heure d'arrivée de la liaison (en minutes après minuit)
     * pour l'enregistrement d'index donné.
     *
     * @param id l'index de la liaison.
     * @return l'heure d'arrivée (U16).
     */
    @Override
    public int arrMins(int id) {
        return structuredbuffer.getU16(ARR_MINUTES, id);
    }

    /**
     * Retourne l'index de la course associée à la liaison à l'index donné.
     * Le champ TRIP_POS_ID est un entier S32 empaqueté où les 24 bits de poids fort
     représentent l'index
     * de la course. Cette méthode extrait ces 24 bits à l'aide de {@link Bits32_24_8#unpack24(int)}.
     *
     * @param id l'index de la liaison.
     * @return l'index de la course.
     */
    @Override
    public int tripId(int id) {
        return unpack24(structuredbuffer.getS32(TRIP_POS_ID, id));
    }

    /**
     * Retourne la position de la liaison dans la course associée,
     * extraite à partir des 8 bits de poids faible du champ TRIP_POS_ID.
     *
     * @param id l'index de la liaison.
     * @return la position de la liaison dans la course.
     */
    @Override
    public int tripPos(int id) {
        return unpack8(structuredbuffer.getS32(TRIP_POS_ID, id));
    }

    /**
     * Retourne l'index de la liaison suivante pour la liaison à l'index donné.
     * <p>
     * Le succBuffer, converti en IntBuffer, contient, pour chaque liaison,
     l'index de la liaison suivante
     * (ou la première liaison de la course, dans le cas de la dernière liaison).
     * </p>
     *
     * @param id l'index de la liaison.
     * @return l'index de la liaison suivante.
     */
    @Override
    public int nextConnectionId(int id) {
        return succBuffer.get(id);
    }

    /**
     * Retourne le nombre total de liaisons présentes dans les données aplaties.
     *
     * @return le nombre d'enregistrements de liaisons.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }
}
