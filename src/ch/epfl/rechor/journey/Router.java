package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.*;
import java.time.LocalDate;
import java.util.List;


public record Router(TimeTable timeTable) {

   
    public Profile profile(LocalDate date, int arrStationId) {
        
        Stations stations = timeTable.stations();
        int numStations = stations.size();
        int[] walkingTimes = new int[numStations];
        for (int i = 0; i < numStations; i++) {
            walkingTimes[i] = timeTable.transfers().minutesBetween(i, arrStationId);
        }
        
       
        Profile.Builder builder = new Profile.Builder(timeTable, date, arrStationId);
        for (int i = 0; i < numStations; i++) {
            builder.setForStation(i, new ParetoFront.Builder());
        }
        int numTrips = timeTable.tripsFor(date).size();
        for (int i = 0; i < numTrips; i++) {
            builder.setForTrip(i, new ParetoFront.Builder());
        }
        
        Connections connections = timeTable.connectionsFor(date);
        int numConnections = connections.size();
        
    
        for (int connId = 0; connId < numConnections; connId++) {
            ParetoFront.Builder f = new ParetoFront.Builder();
            
            int arrivalStopId = connections.arrStopId(connId);
            int stationArrival = timeTable.stationId(arrivalStopId);
            int walkTime = walkingTimes[stationArrival];
            if (walkTime >= 0) {
                int arrivalTime = connections.arrMins(connId) + walkTime;
                long criteria = PackedCriteria.pack(arrivalTime, 0, connId);
                f.add(criteria);
            }
            
            int tripId = connections.tripId(connId);
            ParetoFront.Builder tripBuilder = builder.forTrip(tripId);
            f.addAll(tripBuilder);
            
            int depStationId = timeTable.stationId(connections.depStopId(connId));
            ParetoFront.Builder stationBuilder = builder.forStation(depStationId);
            for (long crit : stationBuilder.getAll()) {
                if (PackedCriteria.depMins(crit) >= connections.arrMins(connId)) {
                    int newChanges = PackedCriteria.changes(crit) + 1;
                    long newCrit = PackedCriteria.pack(connections.arrMins(connId), newChanges, connId);
                    f.add(newCrit);
                }
            }
            
            tripBuilder.addAll(f);
            
          
            List<Integer> reachableStations = timeTable.transfers().reachableStations(depStationId);
            for (int s : reachableStations) {
                int walkingDuration = timeTable.transfers().minutesBetween(s, depStationId);
                ParetoFront.Builder stationFront = builder.forStation(s);
                for (long crit : f.getAll()) {
                    long newCrit = PackedCriteria.pack(connections.depMins(connId) - walkingDuration,
                                                       PackedCriteria.changes(crit),
                                                       PackedCriteria.payload(crit));
                    stationFront.add(newCrit);
                }
            }
        }
        
        return builder.build();
    }
}
