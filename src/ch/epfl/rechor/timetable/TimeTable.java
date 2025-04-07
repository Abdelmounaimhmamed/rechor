package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 *
 * Interface représentant une table des horaires de transport.
 * <p>
 * Elle fournit l'accès aux données associées aux stations, plateformes, trajets,
 * connexions, lignes, transferts et alias de stations pour une date donnée.
 */
public interface TimeTable {

    /**
     * Retourne les stations disponibles dans la table des horaires.
     *
     * @return un objet {@link Stations} représentant toutes les stations
     */
    Stations stations();

    /**
     * Retourne les alias de stations.
     *
     * @return un objet {@link StationAliases} permettant de gérer les alias
     */
    StationAliases stationAliases();

    /**
     * Retourne les plateformes associées aux stations.
     *
     * @return un objet {@link Platforms} contenant les plateformes
     */
    Platforms platforms();

    /**
     * Retourne les lignes de transport (routes) disponibles.
     *
     * @return un objet {@link Routes} représentant les lignes
     */
    Routes routes();

    /**
     * Retourne les correspondances à pied (transferts) entre les stations.
     *
     * @return un objet {@link Transfers} contenant les durées de transfert
     */
    Transfers transfers();

    /**
     * Retourne les trajets valides pour une date donnée.
     *
     * @param date la date souhaitée
     * @return un objet {@link Trips} contenant les trajets du jour
     */
    Trips tripsFor(LocalDate date);

    /**
     * Retourne les connexions valides pour une date donnée.
     *
     * @param date la date souhaitée
     * @return un objet {@link Connections} contenant les connexions du jour
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Vérifie si un identifiant donné correspond à une station.
     *
     * @param stopId l’identifiant à tester
     * @return {@code true} si c'est une station, {@code false} sinon
     */
    default boolean isStationId(int stopId){
        return (stopId < stations().size());
    }

    /**
     * Vérifie si un identifiant donné correspond à une plateforme.
     *
     * @param stopId l’identifiant à tester
     * @return {@code true} si c'est une plateforme, {@code false} sinon
     */
    default boolean isPlatformId(int stopId){
        return stopId >= stations().size();
    }

    /**
     * Retourne l'identifiant de la station associée à un arrêt donné (station ou plateforme).
     *
     * @param stopId l’identifiant de l’arrêt
     * @return l’identifiant de la station correspondante
     */
    default int stationId(int stopId){
        if (isPlatformId(stopId)){
            return platforms().stationId(stopId - stations().size());
        } else {
            return stopId;
        }
    }
    

    /**
     * Retourne le nom de la plateforme si l'identifiant correspond à une plateforme.
     *
     * @param stopId l’identifiant de l’arrêt
     * @return le nom de la plateforme, ou {@code null} si ce n’est pas une plateforme
     */
    default String platformName(int stopId) {
        if(stopId >= stations().size()){
            return platforms().name(stopId - stations().size());
        }
        return null;
    }
}
