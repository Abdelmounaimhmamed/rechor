package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Représente un voyage composé d'une séquence d'étapes (legs).
 * Chaque étape est une instance de {@link Leg} et les étapes sont vérifiées
 * pour garantir leur cohérence (alternance entre Transport et Foot, continuité des arrêts et horaires).
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 */
public record Journey(List<Leg> legs) {

    /**
     * Constructeur compact de {@code Journey}.
     * <p>
     * Vérifie que la liste des étapes n'est pas nulle et non vide, crée une copie immuable de cette liste,
     * et contrôle la cohérence entre chaque étape (alternance entre {@link Leg.Transport} et {@link Leg.Foot},
     * continuité des horaires et des arrêts).
     *
     * @param legs la liste des étapes constituant le voyage
     * @throws IllegalArgumentException si la liste est nulle, vide ou incohérente
     */
    public Journey {
        Preconditions.checkArgument(legs != null && !legs.isEmpty());

        // Copie immuable de la liste pour garantir l'immutabilité
        legs = List.copyOf(legs);

        // Vérifications des étapes
        for (int i = 1; i < legs.size(); i++) {
            Leg prev = legs.get(i - 1);
            Leg curr = legs.get(i);

            // Vérifie que les étapes alternent entre Transport et Foot
            boolean prevIsFoot = prev instanceof Leg.Foot;
            boolean currIsFoot = curr instanceof Leg.Foot;
            Preconditions.checkArgument(prevIsFoot != currIsFoot);

            // Vérifie que l'instant de départ ne précède pas l'instant d'arrivée de la précédente
            Preconditions.checkArgument(!curr.depTime().isBefore(prev.arrTime()));

            // Vérifie que l'arrêt de départ est identique à l'arrêt d'arrivée de la précédente
            Preconditions.checkArgument(curr.depStop().equals(prev.arrStop()));
        }
    }

    /**
     * Retourne l'arrêt de départ du voyage, correspondant à l'arrêt de départ de la première étape.
     *
     * @return le {@link Stop} de départ du voyage
     */
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    /**
     * Retourne l'arrêt d'arrivée du voyage, correspondant à l'arrêt d'arrivée de la dernière étape.
     *
     * @return le {@link Stop} d'arrivée du voyage
     */
    public Stop arrStop() {
        return legs.getLast().arrStop();
    }

    /**
     * Retourne l'heure de départ du voyage, issue de la première étape.
     *
     * @return l'heure de départ sous forme de {@link LocalDateTime}
     */
    public LocalDateTime depTime() {
        return legs.getFirst().depTime();
    }

    /**
     * Retourne l'heure d'arrivée du voyage, issue de la dernière étape.
     *
     * @return l'heure d'arrivée sous forme de {@link LocalDateTime}
     */
    public LocalDateTime arrTime() {
        return legs.getLast().arrTime();
    }

    /**
     * Calcule et retourne la durée totale du voyage.
     *
     * @return la durée totale du voyage sous forme de {@link Duration}
     */
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }

    /**
     * Représente une étape d'un voyage.
     * <p>
     * Une étape possède un arrêt de départ, un arrêt d'arrivée, un horaire de départ, un horaire d'arrivée
     * et éventuellement une liste d'arrêts intermédiaires.
     */
    public sealed interface Leg {
        /**
         * Retourne l'arrêt de départ de l'étape.
         *
         * @return le {@link Stop} de départ
         */
        Stop depStop();

        /**
         * Retourne l'heure de départ de l'étape.
         *
         * @return l'heure de départ sous forme de {@link LocalDateTime}
         */
        LocalDateTime depTime();

        /**
         * Retourne l'arrêt d'arrivée de l'étape.
         *
         * @return le {@link Stop} d'arrivée
         */
        Stop arrStop();

        /**
         * Retourne l'heure d'arrivée de l'étape.
         *
         * @return l'heure d'arrivée sous forme de {@link LocalDateTime}
         */
        LocalDateTime arrTime();

        /**
         * Retourne la liste des arrêts intermédiaires de l'étape.
         *
         * @return la liste immuable des arrêts intermédiaires
         */
        List<IntermediateStop> intermediateStops();

        /**
         * Calcule la durée de l'étape en soustrayant l'heure de départ de l'heure d'arrivée.
         *
         * @return la durée de l'étape sous forme de {@link Duration}
         */
        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }

        /**
         * Représente un arrêt intermédiaire durant une étape.
         * <p>
         * Contient l'arrêt intermédiaire, l'heure d'arrivée et l'heure de départ à cet arrêt.
         */
        record IntermediateStop(Stop interStop, LocalDateTime arrTime, LocalDateTime depTime) {

            /**
             * Constructeur compact pour {@code IntermediateStop}.
             * <p>
             * Vérifie que l'arrêt intermédiaire n'est pas null et que l'heure de départ
             * n'est pas antérieure à l'heure d'arrivée.
             *
             * @param interStop l'arrêt intermédiaire
             * @param arrTime l'heure d'arrivée à l'arrêt intermédiaire
             * @param depTime l'heure de départ de l'arrêt intermédiaire
             * @throws NullPointerException si {@code interStop} est null
             * @throws IllegalArgumentException si {@code depTime} est antérieur à {@code arrTime}
             */
            public IntermediateStop {
                Objects.requireNonNull(interStop);
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }

            /**
             * Retourne l'arrêt intermédiaire.
             *
             * @return le {@link Stop} correspondant à l'arrêt intermédiaire
             */
            public Stop stop() {
                return interStop;
            }
        }

        /**
         * Représente une étape de transport dans un voyage.
         * <p>
         * Cette étape contient des informations sur le véhicule utilisé, la ligne de transport (route)
         * et la destination, en plus des informations classiques d'une étape (arrêts et horaires).
         */
        record Transport(Stop depStop,
                         LocalDateTime depTime, Stop arrStop,
                         LocalDateTime arrTime, List<IntermediateStop> intermediateStops,
                         Vehicle vehicle, String route, String destination) implements Leg {

            /**
             * Retourne le véhicule utilisé pour cette étape de transport.
             *
             * @return le {@link Vehicle} associé à l'étape
             */
            @Override
            public Vehicle vehicle() {
                return vehicle;
            }

            /**
             * Retourne l'arrêt d'arrivée de l'étape de transport.
             *
             * @return le {@link Stop} d'arrivée
             */
            @Override
            public Stop arrStop() {
                return arrStop;
            }

            /**
             * Retourne la destination finale de cette étape de transport.
             *
             * @return la destination sous forme de {@link String}
             */
            @Override
            public String destination() {
                return destination;
            }

            /**
             * Retourne la ligne de transport (route) utilisée durant cette étape.
             *
             * @return le nom de la route sous forme de {@link String}
             */
            @Override
            public String route() {
                return route;
            }

            /**
             * Constructeur compact pour {@code Transport}.
             * <p>
             * Vérifie que les différents paramètres (arrêts, horaires, véhicule, ligne et destination) ne sont pas nulls,
             * et que l'heure d'arrivée n'est pas antérieure à l'heure de départ.
             * La liste des arrêts intermédiaires est copiée de manière immuable.
             *
             * @param depStop l'arrêt de départ
             * @param depTime l'heure de départ
             * @param arrStop l'arrêt d'arrivée
             * @param arrTime l'heure d'arrivée
             * @param intermediateStops la liste des arrêts intermédiaires
             * @param vehicle le véhicule utilisé
             * @param route la ligne de transport
             * @param destination la destination finale
             * @throws NullPointerException si un des paramètres obligatoires est null
             * @throws IllegalArgumentException si {@code arrTime} est antérieur à {@code depTime}
             */
            public Transport {
                Objects.requireNonNull(depStop, "L'arrêt de départ ne peut pas être null.");
                Objects.requireNonNull(depTime, "L'heure de départ ne peut pas être null.");
                Objects.requireNonNull(arrStop, "L'arrêt d'arrivée ne peut pas être null.");
                Objects.requireNonNull(arrTime, "L'heure d'arrivée ne peut pas être null.");
                Objects.requireNonNull(vehicle, "Le véhicule ne peut pas être null.");
                Objects.requireNonNull(route, "Le nom de la ligne ne peut pas être null.");
                Objects.requireNonNull(destination, "La destination ne peut pas être null.");

                // Vérification que l'heure d'arrivée n'est pas avant l'heure de départ
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
                intermediateStops = List.copyOf(intermediateStops);
            }
        }

        /**
         * Représente une étape de déplacement à pied dans un voyage.
         * <p>
         * Cette étape ne comporte pas d'arrêts intermédiaires.
         */
        record Foot(Stop depStop,
                    LocalDateTime depTime,
                    Stop arrStop,
                    LocalDateTime arrTime) implements Leg {

            /**
             * Constructeur compact pour {@code Foot}.
             * <p>
             * Vérifie que les paramètres (arrêts et horaires) ne sont pas nulls et que l'heure d'arrivée
             * n'est pas antérieure à l'heure de départ.
             *
             * @param depStop l'arrêt de départ
             * @param depTime l'heure de départ
             * @param arrStop l'arrêt d'arrivée
             * @param arrTime l'heure d'arrivée
             * @throws NullPointerException si un des paramètres est null
             * @throws IllegalArgumentException si {@code arrTime} est antérieur à {@code depTime}
             */
            public Foot {
                // Vérifications des arguments essentiels
                Objects.requireNonNull(depStop, "L'arrêt de départ ne peut pas être null.");
                Objects.requireNonNull(depTime, "L'heure de départ ne peut pas être null.");
                Objects.requireNonNull(arrStop, "L'arrêt d'arrivée ne peut pas être null.");
                Objects.requireNonNull(arrTime, "L'heure d'arrivée ne peut pas être null.");

                // Vérification de la cohérence des horaires
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            /**
             * Retourne la liste des arrêts intermédiaires pour une étape à pied.
             * <p>
             * Pour une étape à pied, la liste est toujours vide.
             *
             * @return une liste vide d'arrêts intermédiaires
             */
            @Override
            public List<IntermediateStop> intermediateStops() {
                return List.of();
            }

            /**
             * Indique si cette étape à pied correspond à un transfert,
             * c'est-à-dire si l'arrêt de départ et l'arrêt d'arrivée sont dans la même gare.
             *
             * @return {@code true} si c'est un transfert, sinon {@code false}
             */
            public boolean isTransfer() {
                return depStop.name().equals(arrStop.name()); // Vérifie si le départ et l'arrivée sont dans la même gare
            }
        }
    }
}
