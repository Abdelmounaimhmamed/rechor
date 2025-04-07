package ch.epfl.rechor.timetable;


import java.time.LocalDate;
import java.util.Objects;


public final class CachedTimeTable implements TimeTable {

    private final TimeTable underlying;
    private LocalDate cachedDate;
    private Connections cachedConnections;
    private Trips cachedTrips;

    
    public CachedTimeTable(TimeTable underlying) {
        this.underlying = Objects.requireNonNull(underlying);
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        if (!date.equals(cachedDate) || cachedConnections == null) {
            cachedConnections = underlying.connectionsFor(date);
            cachedDate = date;
        }
        return cachedConnections;
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        if (!date.equals(cachedDate) || cachedTrips == null) {
            cachedTrips = underlying.tripsFor(date);
            cachedDate = date;
        }
        return cachedTrips;
    }


    @Override
    public Stations stations() {
        return underlying.stations();
    }

    @Override
    public Routes routes() {
        return underlying.routes();
    }

    @Override
    public Transfers transfers() {
        return underlying.transfers();
    }

    @Override
    public StationAliases stationAliases() {
        throw new UnsupportedOperationException("on l'implmeenté apres");
    }

    @Override
    public Platforms platforms() {
        throw new UnsupportedOperationException("on l'implmeenté apres");
    }

}
