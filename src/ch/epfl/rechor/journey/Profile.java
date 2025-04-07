package ch.epfl.rechor.journey;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un profil pour la planification d’un trajet, contenant les informations d’horaires,
 * la station d’arrivée cible et les fronts de Pareto pour différentes stations.
 *
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 */
public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {

    /**
     * Constructeur de Profile qui s’assure que la liste stationFront est immuable.
     *
     * @param timeTable    la table des horaires associée à ce profil
     * @param date         la date pour laquelle le profil est valide
     * @param arrStationId l’identifiant de la station d’arrivée
     * @param stationFront la liste des fronts de Pareto pour chaque station
     */
    public Profile {
        stationFront = List.copyOf(stationFront);
    }

    /**
     * Récupère les connexions disponibles pour la date du profil.
     *
     * @return les connexions pour la date spécifiée
     */
    public Connections connections() {
        return timeTable.connectionsFor(date);
    }

    /**
     * Récupère les trajets disponibles pour la date du profil.
     *
     * @return les trajets pour la date spécifiée
     */
    public Trips trips() {
        return timeTable().tripsFor(date);
    }

    /**
     * Récupère le front de Pareto pour une station spécifique.
     *
     * @param stationId l’identifiant de la station
     * @return le front de Pareto associé à cette station
     */
    public ParetoFront forStation(int stationId) {
        return stationFront.get(stationId);
    }

    /**
     * Classe Builder permettant de construire des objets Profile.
     */
    public static class Builder {
        private TimeTable timeTable;
        private LocalDate date;
        private int arrStationId;
        ParetoFront.Builder[] tripbuilder;
        ParetoFront.Builder[] stationbuilder;

        /**
         * Construit une instance de Builder avec les paramètres spécifiés.
         *
         * @param timeTable    la table des horaires à utiliser
         * @param date         la date pour laquelle le profil est valide
         * @param arrStationId l’identifiant de la station d’arrivée
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            // Stocker les arguments dans des attributs
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;
            stationbuilder = new ParetoFront.Builder[timeTable.stations().size()];
            tripbuilder = new ParetoFront.Builder[timeTable.tripsFor(date).size()];
        }

        /**
         * Récupère le builder du front de Pareto pour une station donnée.
         *
         * @param stationId l’identifiant de la station
         * @return le builder du front de Pareto pour cette station
         */
        public ParetoFront.Builder forStation(int stationId) {
            return stationbuilder[stationId];
        }

        /**
         * Définit le builder du front de Pareto pour une station donnée.
         *
         * @param stationId l’identifiant de la station
         * @param builder   le builder à affecter
         */
        public void setForStation(int stationId, ParetoFront.Builder builder) {
            stationbuilder[stationId] = builder;
        }

        /**
         * Récupère le builder du front de Pareto pour un trajet donné.
         *
         * @param tripId l’identifiant du trajet
         * @return le builder du front de Pareto pour ce trajet
         */
        public ParetoFront.Builder forTrip(int tripId) {
            return tripbuilder[tripId];
        }

        /**
         * Définit le builder du front de Pareto pour un trajet donné.
         *
         * @param tripId  l’identifiant du trajet
         * @param builder le builder à affecter
         */
        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            tripbuilder[tripId] = builder;
        }

        /**
         * Construit et retourne une instance de Profile à partir de l’état actuel du builder.
         *
         * @return une nouvelle instance de Profile
         */
        public Profile build() {
            List<ParetoFront> stationFront = new ArrayList<>(); // à vérifier : complexité
            for (ParetoFront.Builder builder : stationbuilder) {
                stationFront.add(builder == null ? ParetoFront.EMPTY : builder.build());
            }
            return new Profile(timeTable, date, arrStationId, stationFront);
        }
    }
}
