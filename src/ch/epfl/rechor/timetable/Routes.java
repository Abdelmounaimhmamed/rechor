package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente les lignes de transport public indexées.
 * <p>
 * Cette interface étend {@link Indexed} et définit deux méthodes
 * pour accéder aux informations d'une ligne :
 * <ul>
 *   <li>{@code vehicle(int id)} : retourne le type de véhicule
 *       associé à la ligne d'index donné.</li>
 *   <li>{@code name(int id)} : retourne le nom de la ligne
 *       correspondant à l'index donné.</li>
 * </ul>
 * </p>
 * <p>
 * Ces méthodes lancent une {@link IndexOutOfBoundsException}
 * si l'index fourni est invalide.
 * </p>
 */
public interface Routes extends Indexed {
    Vehicle vehicle(int id);
    String name(int id);
}
