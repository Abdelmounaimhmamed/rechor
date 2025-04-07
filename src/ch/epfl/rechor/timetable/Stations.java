package ch.epfl.rechor.timetable;

/**
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente les gares indexées.
 * <p>
 * Cette interface étend {@link Indexed} et fournit les méthodes pour obtenir
 * les informations d'une gare :
 * <ul>
 *   <li>{@code name(int id)} : retourne le nom de la gare d'index donné,</li>
 *   <li>{@code longitude(int id)} : retourne la longitude (en degrés) de la gare,</li>
 *   <li>{@code latitude(int id)} : retourne la latitude (en degrés) de la gare.</li>
 * </ul>
 * </p>
 * <p>
 * Ces méthodes lancent une {@link IndexOutOfBoundsException} si l'index fourni est invalide.
 * </p>
 */
public interface Stations extends Indexed {
    String name(int id);
    double longitude(int id);
    double latitude(int id);
}
