package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class MyJourneyExtractorTest {
    public static void main(String[] args) throws IOException {
        // Charger l'horaire
        TimeTable tt = FileTimeTable.in(Path.of("timetable"));

        // Définir la date et la gare de destination
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int destinationStationId = 11486; // Gruyères
        int departureStationId = 7872;    // Ecublens VD, EPFL

        // Lire le profil depuis le fichier fourni
        Profile profile = readProfile(tt, date, destinationStationId);

        // Extraire les voyages à partir de la gare de départ
        List<Journey> journeys = JourneyExtractor.journeys(profile, departureStationId);

        // Vérifier qu'il y a au moins 33 voyages pour tester celui à l'index 32
        if (journeys.size() > 32) {
            String ical = JourneyIcalConverter.toIcalendar(journeys.get(32));
            System.out.println("✅ iCalendar output for journey index 32:");
            System.out.println(ical);
        } else {
            System.out.println("❌ Moins de 33 voyages trouvés.");
        }
    }

    private static Profile readProfile(TimeTable timeTable, LocalDate date, int arrStationId) throws IOException {
        Path path =
                Path.of("profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Profile.Builder builder = new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;
            while ((line = reader.readLine()) != null) {
                stationId++;
                if (line.isBlank()) continue;
                ParetoFront.Builder frontBuilder = new ParetoFront.Builder();
                for (String t : line.split(",")) {
                    frontBuilder.add(Long.parseLong(t, 16));
                }
                builder.setForStation(stationId, frontBuilder);
            }
            return builder.build();
        }
    }
}
