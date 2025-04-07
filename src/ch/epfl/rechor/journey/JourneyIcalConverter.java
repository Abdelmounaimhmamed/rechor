package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Une classe de conversion qui transforme un objet {@code Journey} en sa représentation au format iCalendar.
 * <p>
 * Cette classe utilise un {@link IcalBuilder} pour construire une chaîne de caractères au format iCalendar contenant
 * un composant VCALENDAR avec un seul composant VEVENT. L'événement est rempli avec un identifiant unique (UID),
 * un horodatage (DTSTAMP), des horaires de début et de fin (DTSTART et DTEND), un résumé, et une description détaillée
 * construite en itérant sur les étapes du voyage.
 * </p>
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 * @see IcalBuilder
 * @see FormatterFr
 */
public class JourneyIcalConverter {

    /**
     * Convertit le {@code Journey} spécifié en une chaîne formatée au format iCalendar.
     * <p>
     * Le processus de conversion comprend les étapes suivantes :
     * <ol>
     *     <li>Initialiser un {@link IcalBuilder} et commencer le composant VCALENDAR avec la version "2.0" et
     *         l'identifiant de produit "ReCHor".</li>
     *     <li>Démarrer un composant VEVENT, générer un UID unique avec {@link UUID#randomUUID()}, et définir le DTSTAMP
     *         à la date et l'heure actuelles.</li>
     *     <li>Définir les heures de début (DTSTART) et de fin (DTEND) de l'événement à partir des heures de départ et
     *         d’arrivée du voyage.</li>
     *     <li>Créer le résumé de l'événement en concaténant les noms des arrêts de la première et de la dernière étape
     *         du voyage, séparés par " → ".</li>
     *     <li>Construire la description de l'événement en itérant sur toutes les étapes du voyage et en les formatant
     *         avec {@link FormatterFr}.</li>
     *     <li>Terminer les composants VEVENT et VCALENDAR, puis retourner la chaîne iCalendar générée.</li>
     * </ol>
     * </p>
     *
     * @param journey le voyage à convertir en événement iCalendar
     * @return une {@code String} contenant la représentation iCalendar du voyage
     */
    public static String toIcalendar(Journey journey) {
        IcalBuilder builder = new IcalBuilder();
        String uid = UUID.randomUUID().toString();
        // Date/heure actuelle pour DTSTAMP
        LocalDateTime now = LocalDateTime.now();
        builder.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .add(IcalBuilder.Name.PRODID, "ReCHor");
        builder.begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.UID, uid)
                .add(IcalBuilder.Name.DTSTAMP, now)
                .add(IcalBuilder.Name.DTSTART, journey.depTime())
                .add(IcalBuilder.Name.DTEND, journey.arrTime())
                .add(IcalBuilder.Name.SUMMARY, journey.legs().getFirst().depStop().name() + " → " + journey.legs().getLast().arrStop().name());
        // Création de la chaîne DESCRIPTION
        StringJoiner descriptionJoiner = new StringJoiner("\\n");
        // Parcours des étapes du voyage et formatage selon le type
        for (Journey.Leg etapeDuVoyage : journey.legs()) {
            switch (etapeDuVoyage) {
                case Journey.Leg.Foot f ->
                        descriptionJoiner.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t ->
                        descriptionJoiner.add(FormatterFr.formatLeg(t));
            }
        }
        // Ajout de la description au builder
        builder.add(IcalBuilder.Name.DESCRIPTION, descriptionJoiner.toString());
        // Fin du composant VEVENT et VCALENDAR
        builder.end().end();
        // Retourne la chaîne iCalendar générée
        return builder.build();
    }

}
