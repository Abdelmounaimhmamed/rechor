package ch.epfl.rechor.timetable;

/**
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente les alias des gares indexées.
 * <p>
 * Cette interface étend {@link Indexed} et permet d'obtenir :
 * <ul>
 *   <li>{@code alias(int id)} : l'alias de la gare à l'index donné,</li>
 *   <li>{@code stationName(int id)} : le nom officiel de la gare
 *       associé à cet alias.</li>
 * </ul>
 * </p>
 * <p>
 * Ces méthodes lancent une
 * {@link IndexOutOfBoundsException} si l'index est invalide.
 * </p>
 */
public interface StationAliases extends Indexed {
    String alias(int id);
    String stationName(int id);
}
