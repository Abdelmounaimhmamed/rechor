package ch.epfl.rechor.timetable;

/**
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 * Représente des voies ou quais indexés.
 * <p>
 * Cette interface étend {@link Indexed} et fournit des méthodes
 * pour obtenir le nom d'une voie/quai et l'index de la gare associée.
 * </p>
 * <p>
 * Les méthodes lancent une
 * {@link IndexOutOfBoundsException} si l'index est invalide.
 * </p>
 */
public interface Platforms extends Indexed {
    String name(int id);
    int stationId(int id);
}
