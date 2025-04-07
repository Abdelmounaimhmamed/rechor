package ch.epfl.rechor.timetable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente des changements indexés.
 * <p>
 * Cette interface étend {@link Indexed} et fournit :
 * <ul>
 *   <li>{@code depStationId(int id)} : l'index de la gare de départ.</li>
 *   <li>{@code minutes(int id)} : la durée du changement (en minutes).</li>
 *   <li>{@code arrivingAt(int stationId)} : l'intervalle empaqueté
 *       des index des changements arrivant à la gare donnée.</li>
 *   <li>{@code minutesBetween(int depStationId, int arrStationId)} :
 *       la durée du changement entre deux gares.</li>
 * </ul>
 * </p>
 * <p>
 * Chaque méthode lance une
 * {@link IndexOutOfBoundsException} si l'index est invalide.
 * </p>
 */
public interface Transfers extends Indexed {
    int depStationId(int id);
    int minutes(int id);
    int arrivingAt(int stationId);
    int minutesBetween(int depStationId, int arrStationId);



    /**
     * Returns the total number of stations (needed by reachableStations).
     */
    int stationCount();

    /**
     * Returns a list of station IDs from which one can walk to the given station (stationId).
     * In this example, a station i is included if minutesBetween(i, stationId) >= 0.
     */
    default List<Integer> reachableStations(int stationId) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < stationCount(); i++) {
            if (minutesBetween(i, stationId) >= 0) {
                result.add(i);
            }
        }
        return result;
    }
}
