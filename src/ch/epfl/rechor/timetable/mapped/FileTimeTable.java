package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

/**
 * Représente un horaire qui charge ses données à partir de fichiers binaires
 * stockés dans un répertoire.
 *
 *@author Ismael Errhaimini (391287)
 *@author Mahde Mohamed El Arabi (397844)
 */
public record FileTimeTable(Path directory,
                            List<String> stringTable,
                            Stations stations,
                            StationAliases stationAliases,
                            Platforms platforms,
                            Routes routes,
                            Transfers transfers)
        implements TimeTable {

    /**
     * Charge un horaire à partir d'un répertoire spécifié.
     *
     * @param directory le chemin vers le répertoire contenant les données horaires
     * @return une instance de FileTimeTable avec les données chargées
     * @throws IOException si une erreur se produit lors de la lecture des fichiers
     */
    public static TimeTable in(Path directory) throws IOException {
        Path strings = directory.resolve("strings.txt");

        List<String> stringread = Files.readAllLines(strings, StandardCharsets.ISO_8859_1);

        BufferedRoutes bufferr = new BufferedRoutes(stringread,RESOLVE("routes.bin",directory));
        BufferedPlatforms bufferp = new BufferedPlatforms(stringread,RESOLVE("platforms.bin",directory));
        BufferedStations buffers = new BufferedStations(stringread,RESOLVE("stations.bin",directory));
        BufferedTransfers buffert = new BufferedTransfers(RESOLVE("transfers.bin",directory));
        BufferedStationAliases buffera = new BufferedStationAliases(stringread,RESOLVE("station-aliases.bin",directory));

        return new FileTimeTable(
                directory,
                List.copyOf(stringread),
                buffers,
                buffera,
                bufferp,
                bufferr,
                buffert);
    }

    /**
     * Retourne les courses disponibles pour une date donnée.
     *
     * @param date la date pour laquelle les courses doivent être récupérées
     * @return une instance de Trips contenant les courses pour la date donnée
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        Path datedirectory = directory.resolve(date.toString());

        Path tripfile = datedirectory.resolve("trips.bin");

        try (FileChannel filetab = FileChannel.open(tripfile)) {
            ByteBuffer filebuf = filetab.map(READ_ONLY, 0, filetab.size());
            return new BufferedTrips(stringTable, filebuf);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Retourne les liaisons disponibles pour une date donnée.
     *
     * @param date la date pour laquelle les liaisons doivent être récupérées
     * @return une instance de Connections contenant les liaisons pour la date donnée
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        Path datedirec = directory.resolve(date.toString());
        Path connectionfile = datedirec.resolve("connections.bin");
        Path succfile = datedirec.resolve("connections-succ.bin");

        try (FileChannel filetab = FileChannel.open(connectionfile);
             FileChannel succbufftab = FileChannel.open(succfile)) {

            ByteBuffer connectbuf = filetab.map(READ_ONLY, 0, filetab.size());

            ByteBuffer succbuff = succbufftab.map(READ_ONLY, 0, succbufftab.size());

            return new BufferedConnections(connectbuf, succbuff);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Permet de remplacer le premier bloc ou la répétition de try sur Bufferedroutes et les 4 autres
     * retourne un Bytebuffer
     * @param s c'est le string qu'on donne pour la création du byte buffer
     * @param directory le path directory permet la construction du ByteBuffer
     * @return ca retourne un ByteBuffer
     * @throws IOException it throws an IOEXCEPTION
     */
    private static ByteBuffer RESOLVE(String s,Path directory) throws IOException{
        try (FileChannel plateformtab = FileChannel.open(directory.resolve(s))) {

            ByteBuffer plat = plateformtab.map(READ_ONLY, 0, plateformtab.size());

            return plat;
        }
    }
}
