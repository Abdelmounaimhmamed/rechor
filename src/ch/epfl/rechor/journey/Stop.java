package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

/**
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 *
 * Représente un arrêt dans un trajet.
 * Un arrêt est caractérisé par :
 *   <li>un nom d'arrêt ({@code stopName})</li>
 *   <li>le nom de la plateforme associée à cet arrêt ({@code plateformName})</li>
 *   <li>sa position géographique définie par une longitude et une latitude</li>
 *
 * Le constructeur vérifie que le nom de l'arrêt n'est pas {@code null} et que les coordonnées géographiques
 * sont valides : la longitude doit être comprise entre -180 et 180 degrés et la latitude entre -90 et 90 degrés.
 *
 * @param stopName      le nom de l'arrêt, qui ne peut être {@code null}.
 * @param plateformName le nom de la plateforme associée à cet arrêt.
 * @param longitude     la longitude de l'arrêt en degrés (entre -180 et 180).
 * @param latitude      la latitude de l'arrêt en degrés (entre -90 et 90).
 * @throw IllegalArgumentException si longitude n'est pas compris entre ±180° (inclus)
 * ou si latitude n'est pas compris entre ±90° (inclus)
 * @throws NullPointerException si name est null
 */
public record Stop(String stopName, String plateformName, double longitude, double latitude) {
    public Stop {
        stopName = Objects.requireNonNull(stopName, "Le nom du stop ne peut pas être null.");

        // Vérification des coordonnées (avec checkArgument)
        Preconditions.checkArgument(longitude >= -180 && longitude <= 180);
        Preconditions.checkArgument(latitude >= -90 && latitude <= 90);
    }

    /**
     * Retourne le nom de l'arrêt.
     *
     * @return le nom de l'arrêt.
     */
    public String name(){
        return stopName;
    }

    /**
     * Retourne le nom de la plateforme associée à l'arrêt.
     *
     * @return le nom de la plateforme.
     */
    public String platformName(){
        return plateformName;
    }
}
