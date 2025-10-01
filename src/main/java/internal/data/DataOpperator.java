package internal.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;

import internal.statistics.StatisticCalculator;
import internal.types.Song;
import internal.types.User;

public class DataOpperator {
    /**
     * gets an unsorted list of songs user's share
     *
     * @param userID1 id of user1
     * @param userID2 id of user2
     * @return the list of shared songs each user has
     */
    public static UUID[] getCommonSongsBetweenUsers(UUID userID1, UUID userID2) {
        var user1 = DataBase.getUser(userID1);
        var user2 = DataBase.getUser(userID2);
        return UserDataHandler.getCommonSongsBetweenUsers(user1.orElseThrow(), user2.orElseThrow());
    }

    /**
     * @return the predicted user rating of the song
     */
    public static int predictUserRating(UUID user, UUID song) {
        return (int) UserDataHandler.predictUserRating(
                DataBase.getUser(user).orElseThrow(),
                DataBase.getSong(song).orElseThrow(),
                DataBase.getAllUsers());
    }

    /**
     * gets a list of all user data records for csv printing
     *
     * @return a list of records with user similarity data
     */
    public static List<String> getUserRecords() {
        return UserDataHandler.getRecords(DataBase.getAllUsers());
    }

    /**
     * returns a list of strings containing song data for csv printing
     *
     * @return list of strings formatted to contain song information
     */
    public static List<String> getSongRecords() {
        return SongDataHandler.getRecords(DataBase.getAllSongs());
    }

    /**
     * @return a sorted list of records with song,user,prediction for every possible
     *         prediction
     */
    public static List<String> getSongRecordsWithPredictions() {
        List<String> records = new LinkedList<>();
        var sortedSongs = SongDataHandler.sortSongsByName(DataBase.getAllSongs());
        var sortedUsers = UserDataHandler.sortUsersByName(DataBase.getAllUsers());

        for (Song song : sortedSongs) {
            for (User user : sortedUsers) {
                OptionalInt rating = song.getUserRating(user.getId());
                if (rating.isPresent()) {
                    continue;
                }

                int predictedRating = predictUserRating(user.getId(), song.getId());
                Double outputRating = Double.NaN;
                if (predictedRating != 0) {
                    outputRating = Double.valueOf(predictedRating);
                }

                records.add(
                        String.format("%s,%s,%.0f", song.getName(),
                                user.getName(),
                                outputRating));

            }
        }
        return records;
    }

    public static List<String> getSongReccomendationRecords(String[] songNames)
            throws CannotRateException {
        Song[] targetSongs = new Song[songNames.length];
        for (int i = 0; i < songNames.length; i++) {
            var id = DataBase.getSongId(songNames[i]).orElseThrow();
            targetSongs[i] = DataBase.getSong(id).orElseThrow();
        }

        Map<Song, List<Song>> reccomendations = SongDataHandler.getReccomendations(targetSongs);

        List<String> records = new LinkedList<>();
        for (var entry : reccomendations.entrySet()) {
            for (Song reccomendedSong : entry.getValue()) {
                if (Arrays.asList(songNames).contains(reccomendedSong.getName())) {
                    continue;
                }
                records.add(String.format("%s,%s", entry.getKey().getName(), reccomendedSong.getName()));
            }
        }

        records.sort(Comparator.naturalOrder());
        return records;
    }

    /**
     * @param ratings
     * @return a sorted List of the song's user ratings sorted by user name
     */
    private static List<Integer> getSortedUserRatingsFromSong(Song song) {
        User[] users = song.getUserRatings().keySet().stream()
                .map(id -> DataBase.getUser(id).orElseThrow())
                .toArray(User[]::new);
        var sortedUsers = UserDataHandler.sortUsersByName(users);
        List<Integer> ratings = Arrays.stream(sortedUsers)
                .map(user -> song.getUserRating(user.getId()).orElseThrow())
                .collect(Collectors.toList());
        return ratings;
    }

    public static List<Double> normalizeSongRatingList(Song song) {
        var ratings = getSortedUserRatingsFromSong(song);
        List<Double> normalizedRatings = ratings.stream()
                .map(rating -> StatisticCalculator.normalizeRating(
                        rating, song.getMeanRating(), song.getStdDev()))
                .collect(Collectors.toList());
        return normalizedRatings;
    }

    public static List<String> getDataBaseRecords() {
        List<String> records = new LinkedList<>();
        var songs = DataBase.getAllSongs();
        songs = SongDataHandler.sortSongsByName(songs);
        var users = DataBase.getAllUsers();
        users = UserDataHandler.sortUsersByName(users);

        for (Song song : songs) {
            for (User user : users) {
                OptionalInt rating = song.getUserRating(user.getId());
                if (rating.isEmpty()) {
                    continue;
                }
                records.add(song.getName() + "," + user.getName() + "," + rating.orElseThrow());
            }
        }

        return records;
    }

    public static class CannotRateException extends Exception {
        public CannotRateException(String message) {
            super(message);
        }
    }
}
