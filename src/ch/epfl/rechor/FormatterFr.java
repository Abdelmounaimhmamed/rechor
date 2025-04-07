package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Classe utilitaire fournissant des méthodes pour formater les informations
 relatives aux voyages en français.
 * <p>
 * Cette classe offre des méthodes statiques pour formater les durées, les heures,
 les noms de plateformes,
 * ainsi que les segments de voyage (à pied et en transport) en chaînes de caractères lisibles.
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class FormatterFr {

    // Constructeur privé afin d'empêcher l'instanciation.
    private FormatterFr(){}

    // Un formateur pour les durées (non directement utilisé dans les méthodes publiques).
    static DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY)
            .appendLiteral(" h ")
            .appendValue(ChronoField.MINUTE_OF_DAY)
            .appendLiteral(" min")
            .toFormatter();

    /**
     * Formate une {@link Duration} en une chaîne de caractères en français.
     * <p>
     * Si la durée est d'une heure ou plus, le format est "{heures} h {minutes} min".
     * Sinon, elle est formatée sous la forme "{minutes} min".
     * </p>
     *
     * @param duration la durée à formater
     * @return une chaîne représentant la durée
     */
    public static String formatDuration(Duration duration){
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        if (hours > 0) {
            return hours + " h " + minutes + " min";
        } else {
            return minutes + " min";
        }
    }

    /**
     * Formate un {@link LocalDateTime} en une chaîne représentant l'heure.
     * L'heure est formatée selon le modèle "Hhmm" (par exemple, "8h05").
     *
     * @param dateTime le date-time à formater
     * @return une chaîne représentant l'heure
     */
    public static String formatTime(LocalDateTime dateTime){
        DateTimeFormatter formater2 = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral("h")
                .appendValue(ChronoField.MINUTE_OF_HOUR,2)
                .toFormatter();
        return formater2.format(dateTime);
    }

    /**
     * Formate le nom de la plateforme d'un arrêt.
     * <p>
     * Si le nom de la plateforme est nul ou vide, retourne une chaîne vide.
     * Si le nom de la plateforme commence par un chiffre, il est préfixé par "voie ".
     * Sinon, il est préfixé par "quai ".
     * </p>
     *
     * @param stop l'arrêt dont le nom de la plateforme doit être formaté
     * @return le nom de la plateforme formaté, ou une chaîne vide si non disponible
     */
    public static String formatPlatformName(Stop stop) {
        String platformName = stop.plateformName();

        if (platformName == null || platformName.isEmpty()) {
            return "";
        }

        if (Character.isDigit(platformName.charAt(0))) {
            return "voie " + platformName;
        } else {
            return "quai " + platformName;
        }
    }

    /**
     * Formate un segment de trajet à pied en une chaîne de caractères en français.
     * <p>
     * Si le segment correspond à un changement, il est formaté sous la forme "changement ({durée})".
     * Sinon, il est formaté sous la forme "trajet à pied ({durée})".
     * </p>
     *
     * @param footLeg le segment de trajet à pied à formater
     * @return une chaîne représentant le segment à pied
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        if (footLeg.isTransfer()) {
            return "changement (" + formatDuration(footLeg.duration()) + ")";
        } else {
            return "trajet à pied (" + formatDuration(footLeg.duration()) + ")";
        }
    }

    /**
     * Formate un segment de trajet en transport en une chaîne de caractères en français.
     * <p>
     * La chaîne formatée inclut :
     * <ol>
     *<li>L'heure de départ.</li>
     *<li>Le nom de l'arrêt de départ (avec l'information de la plateforme, le cas échéant).</li>
     *<li>Une flèche (" → ") comme séparateur.</li>
     *<li>Le nom de l'arrêt d'arrivée.</li>
     *<li>L'heure d'arrivée et l'information de la plateforme (si disponible) entre parenthèses.</li>
     * </ol>
     * </p>
     *
     * @param leg le segment de transport à formater
     * @return une chaîne représentant le segment de transport
     */
    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder sb = new StringBuilder();
        // 1. Heure de départ
        sb.append(formatTime(leg.depTime())).append(" ");
        // 2. Nom de l'arrêt de départ
        sb.append(leg.depStop().name());
        // 3. Plateforme de départ si disponible
        String departurePlatform = formatPlatformName(leg.depStop());
        if (!departurePlatform.isEmpty()) {
            sb.append(" (").append(departurePlatform).append(")");
        }
        // 4. Flèche directionnelle
        sb.append(" → ");
        // 5. Nom de l'arrêt d'arrivée
        sb.append(leg.arrStop().name());
        // 6. Heure d'arrivée
        sb.append(" (arr. ").append(formatTime(leg.arrTime()));
        // 7. Plateforme d'arrivée si disponible
        String arrivalPlatform = formatPlatformName(leg.arrStop());

        if (!arrivalPlatform.isEmpty()) {
            sb.append(" ").append(arrivalPlatform);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Formate la destination d'une ligne de transport.
     * <p>
     * La chaîne formatée est de la forme "{route} Direction {destination}".
     * </p>
     *
     * @param transportLeg le segment de transport contenant les informations sur
    la ligne et la destination.
     * @return une chaîne représentant la destination de la ligne.
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return String.format("%s Direction %s", transportLeg.route(), transportLeg.destination());
    }
}
