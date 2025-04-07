package ch.epfl.rechor;

/**
 * Classe utilitaire pour valider les arguments des méthodes.
 * <p>
 * Cette classe fournit une méthode statique qui vérifie qu'une condition est vraie,
 * et lance une {@link IllegalArgumentException} avec le message par défaut "Argument invalide"
 * si la condition n'est pas remplie.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class Preconditions {

    /**
     * Constructeur privé afin d'empêcher l'instanciation.
     */
    private Preconditions() {}

    /**
     * Vérifie que la condition spécifiée est {@code true}.
     * <p>
     * Si la condition est {@code false}, une {@link IllegalArgumentException}
     * est lancée avec le message "Argument invalide".
     * </p>
     *
     * @param shouldBeTrue une condition booléenne qui doit être {@code true}
     * @throws IllegalArgumentException si {@code shouldBeTrue} est {@code false}
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException("Argument invalide");
        }
    }
}
