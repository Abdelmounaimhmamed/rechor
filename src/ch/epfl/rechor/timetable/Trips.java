package ch.epfl.rechor.timetable;

/**
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente les courses de transport public indexées.
 * <p>
 * Cette interface étend {@link Indexed} et définit deux méthodes :
 * <ul>
 *   <li>{@code routeId(int id)} : retourne l'index de la ligne associée
 *       à la course d'index donné.</li>
 *   <li>{@code destination(int id)} : retourne la destination finale
 *       de la course d'index donné.</li>
 * </ul>
 * </p>
 * <p>
 * Ces méthodes lancent une {@link IndexOutOfBoundsException} si l'index fourni
 * est invalide.
 * </p>
 */
public interface Trips extends Indexed {
    int routeId(int id);
    String destination(int id);
}
