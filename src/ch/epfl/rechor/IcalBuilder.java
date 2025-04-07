package ch.epfl.rechor;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe permettant de construire un fichier iCalendar (iCal) de manière programmatique.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public final class IcalBuilder {


    /**
     * Enum représentant les composants iCalendar disponibles.
     */
    public enum Component { VCALENDAR, VEVENT }


    /**
     * Enum représentant les différents attributs des composants iCalendar.
     */
    public enum Name { BEGIN, END, PRODID, VERSION, UID, DTSTAMP, DTSTART, DTEND, SUMMARY, DESCRIPTION }


    private final List<Component> components = new ArrayList<>();
    private final StringBuilder builder = new StringBuilder();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final String CRLF = "\r\n";


    /**
     * Ajoute un attribut avec une valeur texte au fichier iCalendar.
     * @param name Nom de l'attribut.
     * @param value Valeur de l'attribut.
     * @return L'instance de {@code IcalBuilder} pour le chaînage des appels.
     * @throws IllegalArgumentException Si {@code name} est nul.
     */
    public IcalBuilder add(Name name, String value) {
        Preconditions.checkArgument(name != null);
        String line = name + ":" + value;
        builder.append(foldLine(line)).append(CRLF);
        return this;
    }


    /**
     * Ajoute un attribut avec une date/heure formatée au fichier iCalendar.
     * @param name Nom de l'attribut.
     * @param dateTime Date et heure à ajouter.
     * @return L'instance de {@code IcalBuilder} pour le chaînage des appels.
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        return add(name, dateTime.format(DATE_TIME_FORMATTER));
    }


    /**
     * Démarre un nouveau composant iCalendar.
     * @param component Composant à débuter (VCALENDAR ou VEVENT).
     * @return L'instance de {@code IcalBuilder} pour le chaînage des appels.
     */
    public IcalBuilder begin(Component component) {
        components.add(component);
        return add(Name.BEGIN, component.name());
    }


    /**
     * Termine le composant iCalendar en cours.
     * @return L'instance de {@code IcalBuilder} pour le chaînage des appels.
     * @throws IllegalArgumentException Si aucun composant n'est en cours.
     */
    public IcalBuilder end() {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("No component to end");
        }
        Component component = components.getLast();
        components.removeLast();
        return add(Name.END, component.name());
    }


    /**
     * Construit et retourne la représentation en texte du fichier iCalendar.
     * @return Une chaîne de caractères contenant la structure iCalendar.
     * @throws IllegalArgumentException Si des composants BEGIN n'ont pas été fermés par END.
     */
    public String build() {
        if (!components.isEmpty()) {
            throw new IllegalArgumentException("Unmatched component BEGIN");
        }
        return builder.toString();
    }


    /**
     * Formate une ligne iCalendar pour respecter la règle de pliage (folding) de 75 caractères.
     * @param line Ligne de texte à plier si nécessaire.
     * @return La ligne formatée avec un pliage conforme.
     */
    private String foldLine(String line) {
        int firstLineLimit = 75;
        int continuationLimit = 74; // 75 - 1 pour l'espace ajouté
        StringBuilder foldedLine = new StringBuilder();


// Ajoute le premier segment (jusqu'à 75 caractères)
        int end = Math.min(firstLineLimit, line.length());
        foldedLine.append(line, 0, end);


// Pour les segments suivants, ajoute CRLF + " " et ensuite jusqu'à 74 caractères
        int index = end;
        while (index < line.length()) {
            int nextChunkLength = Math.min(continuationLimit, line.length() - index);
            foldedLine.append("\r\n").append(" ");
            foldedLine.append(line, index, index + nextChunkLength);
            index += nextChunkLength;
        }
        return foldedLine.toString();
    }
}

