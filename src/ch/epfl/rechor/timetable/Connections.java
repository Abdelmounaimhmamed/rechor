package ch.epfl.rechor.timetable;

/**
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * L'interface {@code Connections} du sous-paquetage {@code timetable},
 * publique et étendant {@link Indexed}, représente des liaisons indexées.
 *
 * Pour l'algorithme de recherche de voyages, les liaisons sont ordonnées par
 * heure de départ décroissante.
 *
 * Méthodes définies :
 * <ul>
 *   <li>{@code depStopId(int id)} :
 *       retourne l'index de l'arrêt de départ de la liaison d'index donné.</li>
 *   <li>{@code depMins(int id)} :
 *       retourne l'heure de départ (en minutes après minuit) de la liaison.</li>
 *   <li>{@code arrStopId(int id)} :
 *       retourne l'index de l'arrêt d'arrivée de la liaison d'index donné.</li>
 *   <li>{@code arrMins(int id)} :
 *       retourne l'heure d'arrivée (en minutes après minuit) de la liaison.</li>
 *   <li>{@code tripId(int id)} :
 *       retourne l'index de la course à laquelle appartient la liaison.</li>
 *   <li>{@code tripPos(int id)} :
 *       retourne la position de la liaison dans sa course (la première a 0).</li>
 *   <li>{@code nextConnectionId(int id)} :
 *       retourne l'index de la liaison suivante dans la course, ou la première
 *       si la liaison est la dernière.</li>
 * </ul>
 *
 * Toutes ces méthodes lancent une
 * {@link IndexOutOfBoundsException} si {@code id} est invalide
 * (inférieur à 0 ou supérieur ou égal à {@code size()}).
 *
 * Les index d'arrêts retournés par {@code depStopId} et
 * {@code arrStopId} peuvent désigner des gares ou des voies/quais.
 * Si l'index est inférieur au nombre de gares, il représente une gare ;
 * sinon, il représente une voie/quai (calculé en soustrayant le nombre de gares).
 *
 * Par exemple, s'il y a 1000 gares et 2000 voies/quais, l'index 500 représente
 * la gare d'index 500, et l'index 1700 représente la voie/quai d'index 700.
 */
public interface Connections extends Indexed {
    int depStopId(int id);
    int depMins(int id);
    int arrStopId(int id);
    int arrMins(int id);
    int tripId(int id);
    int tripPos(int id);
    int nextConnectionId(int id);
}
