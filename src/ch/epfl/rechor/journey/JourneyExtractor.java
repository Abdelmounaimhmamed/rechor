package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe utilitaire permettant d'extraire une liste de trajets
 * ({@link Journey}) à partir d'un {@link Profile} donné.
 * <p>
 * Le processus de reconstruction utilise les critères stockés dans les
 * fronts de Pareto, associés à des informations de connexion et de transfert
 * contenues dans le profil.
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 */
public final class JourneyExtractor {
    private JourneyExtractor() {}

    /**
     * Extrait et reconstruit tous les trajets possibles pour un profil donné
     * à partir d'une station de départ spécifiée.
     *
     * @param profile      le profil contenant les informations d’horaires, de connexions et de fronts de Pareto
     * @param depStationId l’identifiant de la station de départ
     * @return une liste de {@link Journey} triée par heure de départ puis d’arrivée
     */
    public static List<Journey> journeys(Profile profile, int depStationId) {
        ParetoFront paretoFront = profile.forStation(depStationId);
        List<Journey> journeys = new ArrayList<>();

        paretoFront.forEach((long criteria) -> {
            List<Journey.Leg> legList = new ArrayList<>();
            buildJourney(criteria, depStationId, profile, legList);
            journeys.add(new Journey(legList));
        });

        journeys.sort(
                Comparator.comparing(Journey::depTime)
                        .thenComparing(Journey::arrTime)
        );

        return journeys;
    }

    /**
     * Construit un trajet (Journey) complet à partir d’un critère donné
     * et d’un identifiant de station de départ.
     */
    private static Journey buildJourney(long criteria, int currentStationId, Profile profile, List<Journey.Leg> legList) {
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();

        int remainingTransfers = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
        int connectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
        int arrivalStopId;
        long updatedCriteria;

        for (int i = 1; i <= PackedCriteria.changes(criteria) + 1; i++) {
            if (currentStationId != timeTable.stationId(connections.depStopId(connectionId))) {
                addWalkingLeg(profile,
                        currentStationId,
                        connections.depStopId(connectionId),
                        PackedCriteria.depMins(criteria),
                        legList);
            }

            connectionId = addTransportLeg(profile, connectionId, legList, remainingTransfers);
            arrivalStopId = connections.arrStopId(connectionId);

            if (i <= PackedCriteria.changes(criteria)) {
                updatedCriteria = profile
                        .forStation(timeTable.stationId(arrivalStopId))
                        .get(PackedCriteria.arrMins(criteria), PackedCriteria.changes(criteria) - i);

                int arrivalTimeMinutes = connections.arrMins(connectionId);
                connectionId = Bits32_24_8.unpack24(PackedCriteria.payload(updatedCriteria));
                remainingTransfers = Bits32_24_8.unpack8(PackedCriteria.payload(updatedCriteria));

                addWalkingLeg(profile, arrivalStopId, connections.depStopId(connectionId), arrivalTimeMinutes, legList);
            } else if (timeTable.stationId(connections.arrStopId(connectionId)) != profile.arrStationId()) {
                addWalkingLeg(profile, arrivalStopId, profile.arrStationId(), connections.arrMins(connectionId), legList);
            }

            currentStationId = timeTable.stationId(connections.depStopId(connectionId));
        }

        return new Journey(legList);
    }

    /**
     * Ajoute une étape de transport à la liste des étapes du trajet.
     */
    private static int addTransportLeg(Profile profile,
                                       int connectionId,
                                       List<Journey.Leg> legList,
                                       int intermediateStopsCount) {
        List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
        Connections connections = profile.connections();

        Stop depStop = createStop(profile, connections.depStopId(connectionId));
        Stop arrStop = createStop(profile, connections.arrStopId(connectionId));
        LocalDateTime initialDepTime = formatDate(profile, connections.depMins(connectionId));
        LocalDateTime currentDepTime = formatDate(profile, connections.arrMins(connectionId));
        LocalDateTime currentArrTime;

        for (int j = 0; j < intermediateStopsCount; j++) {
            connectionId = connections.nextConnectionId(connectionId);
            currentArrTime = formatDate(profile, connections.depMins(connectionId));

            intermediateStops.add(new Journey.Leg.IntermediateStop(arrStop, currentDepTime, currentArrTime));

            currentDepTime = formatDate(profile, connections.arrMins(connectionId));
            arrStop = createStop(profile, connections.arrStopId(connectionId));
        }

        LocalDateTime finalArrTime = currentDepTime;
        currentDepTime = initialDepTime;

        int tripId = connections.tripId(connectionId);
        int routeId = profile.trips().routeId(tripId);

        Vehicle vehicle = profile.timeTable().routes().vehicle(routeId);
        String destination = profile.trips().destination(tripId);
        String routeName = profile.timeTable().routes().name(routeId);

        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                depStop,
                currentDepTime,
                arrStop,
                finalArrTime,
                intermediateStops,
                vehicle,
                routeName,
                destination
        );

        legList.add(transportLeg);
        return connectionId;
    }

    /**
     * Ajoute une étape à pied entre deux arrêts à un moment donné.
     */
    private static void addWalkingLeg(Profile profile,
                                      int depStopId,
                                      int arrStopId,
                                      int depMinutes,
                                      List<Journey.Leg> legList) {
        TimeTable timeTable = profile.timeTable();

        int depStationId = timeTable.stationId(depStopId);
        int arrStationId = timeTable.stationId(arrStopId);

        Stop depStop = createStop(profile, depStopId);
        Stop arrStop = createStop(profile, arrStopId);
        LocalDateTime depTime = formatDate(profile, depMinutes);
        LocalDateTime arrTime = depTime.plusMinutes(timeTable.transfers().minutesBetween(depStationId, arrStationId));

        Journey.Leg.Foot walkingLeg = new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
        legList.add(walkingLeg);
    }

    /**
     * Formate une heure donnée (en minutes depuis minuit) en {@link LocalDateTime}.
     */
    private static LocalDateTime formatDate(Profile profile, int time) {
        return profile.date().atStartOfDay().plusMinutes(time);
    }

    /**
     * Crée un objet {@link Stop} complet à partir d’un identifiant d’arrêt.
     */
    private static Stop createStop(Profile profile, int stopId) {
        int stationId = profile.timeTable().stationId(stopId);

        return new Stop(
                profile.timeTable().stations().name(stationId),
                profile.timeTable().platformName(stopId),
                profile.timeTable().stations().longitude(stationId),
                profile.timeTable().stations().latitude(stationId)
        );
    }
}
