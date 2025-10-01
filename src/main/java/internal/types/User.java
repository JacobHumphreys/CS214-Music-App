package internal.types;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

import internal.data.DataBase;
import internal.statistics.StatisticCalculator;

/*
 * A class representation of an app user
 */
public class User implements Comparable<User> {
    /**
     * A signal value for when the user's reviews cannot be normalized
     */
    public static final double UNABLE_TO_NORMALIZE = Double.NEGATIVE_INFINITY;
    private String name;

    private UUID id;

    private ArrayList<UUID> songLibrary;

    private double stdDev, meanRating;

    /**
     * constructs a new user filling in neccessary fields
     *
     * @param name value of user's name field
     * @param id   value of user's id field
     */
    public User(String name, UUID id) {
        this.name = name;
        this.id = id;
        this.songLibrary = new ArrayList<>();
    }

    /**
     * adds a song's id to the user's list of songs
     *
     * @param songId the uuid of the song
     */
    public void addSongToLibrary(UUID songId) {
        Optional<Song> song = DataBase.getSong(songId);
        if (song.isEmpty()) {
            return;
        }

        if (!songLibrary.contains(songId)) {
            songLibrary.add(songId);
        }

        Integer[] ratings = songLibrary.stream()
                .map(id -> DataBase.getSong(id).map(
                        libSong -> libSong.getUserRating(this.id))
                        .orElseThrow()
                        .orElseThrow())
                .toArray(Integer[]::new);
        var newStats = StatisticCalculator.updateStats(ratings, stdDev, meanRating);
        stdDev = newStats[0];
        meanRating = newStats[1];
    }

    /**
     * returns the rating the user gave for the song
     *
     * @param songID the id of the song we want the rating of
     * @return the rating the user gave, if no rating is present -1
     */
    public OptionalInt getSongRating(UUID songID) {
        return DataBase.getSong(songID)
                .map(song -> song.getUserRating(this.id))
                .orElse(OptionalInt.empty());
    }

    /**
     * normalizes the user's rating for a song given their mean and standard
     * deviations
     *
     * @param songID the id of the song whose rating we want normalized
     * @return the normalized rating
     */
    public double getNormalizedSongRating(UUID songID) {
        if (!songLibrary.contains(songID) || stdDev == 0) {
            return UNABLE_TO_NORMALIZE;
        }

        var song = DataBase.getSong(songID);
        var rating = song.orElseThrow().getUserRating(id).orElseThrow();

        return StatisticCalculator.normalizeRating(rating, this.meanRating, this.stdDev);
    }

    /**
     * get the user's name
     *
     * @return the name field
     */
    public String getName() {
        return name;
    }

    /**
     * get the users id
     *
     * @return the user's id
     */
    public UUID getId() {
        return id;
    }

    /**
     * get the users rating stdDev
     *
     * @return the user's rating stdDev
     */
    public double getStdDev() {
        return stdDev;
    }

    /**
     * get the users mean rating
     *
     * @return the user's mean rating
     */
    public double getMeanRating() {
        return meanRating;
    }

    /**
     * gets a copy of the user's library as an array
     * 
     * @return a copy of the user's internal library
     */
    public UUID[] getSongLibrary() {
        return this.songLibrary.toArray(new UUID[] {});
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(User u) {
        return name.compareTo(u.getName());
    }

}
