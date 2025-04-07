package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * BufferedTransfers offre un accès aux données aplaties des changements (transfers).
 * <p>
 * Les données des changements sont stockées dans un ByteBuffer et organisées selon la structure
 suivante :
 * <ul>
 *   <li>Champ 0 (DEP_STATION_ID) : U16, représentant l'index de la gare de départ.</li>
 *   <li>Champ 1 (ARR_STATION_ID) : U16, représentant l'index de la gare d'arrivée.</li>
 *   <li>Champ 2 (TRANSFER_MINUTES) : U8, représentant la durée du changement en minutes.</li>
 * </ul>
 * <p>
 * La classe utilise également un tableau d'entiers {@code intervales} pour stocker,
 pour chaque gare d'arrivée,
 * l'intervalle empaqueté (selon la convention de {@link PackedRange#pack(int, int)})
 des indices des enregistrements
 * correspondant aux changements arrivant dans cette gare.
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public class BufferedTransfers implements Transfers {
    private static final int DEP_STATION_ID = 0;
    private static final int ARR_STATION_ID = 1;
    private static final int TRANSFER_MINUTES = 2;

    /**
     * Tableau contenant, pour chaque gare d'arrivée (index), l'intervalle empaqueté
     * des indices des enregistrements de changements qui y arrivent.
     */
    private final int[] intervales;

    /**
     * Buffer structuré qui offre un accès aux champs des enregistrements aplatis.
     */
    private final StructuredBuffer structuredbuffer;

    /**
     * Structure décrivant le format des enregistrements de changements aplatis.
     * <p>
     * La structure comporte :
     * <ul>
     *   <li>Champ 0 : DEP_STATION_ID (U16)</li>
     *   <li>Champ 1 : ARR_STATION_ID (U16)</li>
     *   <li>Champ 2 : TRANSFER_MINUTES (U8)</li>
     * </ul>
     * </p>
     */
    private static final Structure structure = new Structure(

            field(DEP_STATION_ID, Structure.FieldType.U16),
            field(ARR_STATION_ID, Structure.FieldType.U16),
            field(TRANSFER_MINUTES, Structure.FieldType.U8));

    /**
     * Construit une instance de BufferedTransfers à partir d'un ByteBuffer contenant
     les données aplaties.
     * <p>
     * Le constructeur crée un StructuredBuffer en se basant sur la structure définie et calcule,
     pour chaque enregistrement,
     * l'intervalle empaqueté des indices des changements dont le champ ARR_STATION_ID
     correspond à une gare donnée.
     * </p>
     *
     * @param buffer le ByteBuffer contenant les données des changements.
     */
    public BufferedTransfers(ByteBuffer buffer) {
        this.structuredbuffer = new StructuredBuffer(structure, buffer);

        int maxAr = 0;
        for (int i = 0; i < structuredbuffer.size(); i++) {

            if (structuredbuffer.getU16(ARR_STATION_ID, i) > maxAr)
                maxAr = structuredbuffer.getU16(ARR_STATION_ID, i);
        }
        maxAr += 1;
        intervales = new int[maxAr];
        for (int i = 0; i < structuredbuffer.size(); i++) {
            int arrstation = structuredbuffer.getU16(ARR_STATION_ID, i);

            int debut = i;
            while
            (i < structuredbuffer.size() && structuredbuffer.getU16(ARR_STATION_ID, i) == arrstation)
            {
                i++;
            }
            i--;
            int fin = i + 1;
            intervales[arrstation] = PackedRange.pack(debut, fin);
        }
    }

    /**
     * Retourne l'index de la gare de départ du changement à l'index donné.
     *
     * @param id l'index de l'enregistrement de changement.
     * @return l'index de la gare de départ.
     */
    @Override
    public int depStationId(int id) {
        return structuredbuffer.getU16(DEP_STATION_ID, id);
    }

    /**
     * Retourne la durée du changement en minutes pour l'enregistrement à l'index donné.
     *
     * @param id l'index de l'enregistrement de changement.
     * @return la durée du changement en minutes.
     */
    @Override
    public int minutes(int id) {
        return structuredbuffer.getU8(TRANSFER_MINUTES, id);
    }

    /**
     * Retourne l'intervalle empaqueté (selon la convention de {@link PackedRange#pack(int, int)})
     * des indices des enregistrements de changements dont la gare d'arrivée est celle dont
     l'index est fourni.
     *
     * @param stationId l'index de la gare d'arrivée.
     * @return un entier empaqueté représentant l'intervalle [début, fin) des enregistrements
    de changements.
     */
    @Override
    public int arrivingAt(int stationId) {
        return intervales[stationId];
    }

    /**
     * Retourne la durée du changement (en minutes) entre la gare de départ et
     la gare d'arrivée spécifiées.
     * <p>
     * La méthode recherche, dans l'intervalle des enregistrements de changements
     arrivant à la gare d'arrivée,
     * celui dont le champ DEP_STATION_ID correspond à la gare de départ.
     Si aucun changement correspondant n'est trouvé
     * une {@link NoSuchElementException} est levée.
     * </p>
     *
     * @param depStationId l'index de la gare de départ.
     * @param arrStationId l'index de la gare d'arrivée.
     * @return la durée du changement en minutes.
     * @throws NoSuchElementException si aucun changement correspondant n'est trouvé.
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int debut = PackedRange.startInclusive(arrivingAt(arrStationId));
        int fin = PackedRange.endExclusive(arrivingAt(arrStationId));
        for (int i = debut; i <= fin; i++) {
            if (depStationId(i) == depStationId)

                return structuredbuffer.getU8(TRANSFER_MINUTES, i);
        }
        throw new NoSuchElementException();
    }

    /**
     * Retourne le nombre total d'enregistrements de changements.
     *
     * @return la taille (nombre d'enregistrements) des données aplaties.
     */
    @Override
    public int size() {
        return structuredbuffer.size();
    }

    @Override
    public int stationCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stationCount'");
    }
}
