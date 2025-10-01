package internal.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import internal.statistics.KMeansCalculator;
import internal.types.Song;
import internal.types.Song.InvalidRatingException;
import internal.types.User;

/*
 * Internal class used for handling song data in ways not
 * neccessary for outside module access
 */
class SongDataHandler {
    static List<String> getRecords(Song[] songs) {
        return Arrays.stream(songs).map(s -> s.toString()).sorted().collect(Collectors.toList());
    }

    /**
     * sorts a list of users by name
     *
     * @param users a list of users to be sorted
     * @return a copy of the input list that is sorted by name
     */
    public static Song[] sortSongsByName(Song[] songs) {
        for (int i = 0; i < songs.length; i++) {
            var min = i;
            for (int j = i + 1; j < songs.length; j++) {
                if (songs[min].compareTo(songs[j]) > 0) {
                    min = j;
                }
            }
            var temp = songs[i];
            songs[i] = songs[min];
            songs[min] = temp;
        }
        return songs;
    }

    /**
     * @param targetSong song to use to reccomend others
     * @return list of songs which are reccomended based on targetSong
     */
    static Map<Song, List<Song>> getReccomendations(Song[] targetSongs) throws DataOpperator.CannotRateException {
        List<Song> predictions = getCompletePredicitonTable();

        for (int i = 0; i < targetSongs.length; i++) {
            for (Song song : predictions) {
                if (song.getName().equals(targetSongs[i].getName())) {
                    targetSongs[i] = song;
                }
            }
        }

        verifyInputSet(targetSongs);

        var kclac = new KMeansCalculator(targetSongs, predictions.toArray(Song[]::new));
        for (int i = 0; i < 10; i++) {
            kclac.iterate();
        }

        return kclac.getClusterMap();
    }

    private static void verifyInputSet(Song[] songs) throws DataOpperator.CannotRateException {
        for (int i = 0; i < songs.length - 1; i++) {
            if (songs[i].getStdDev() == 0) {
                throw new DataOpperator.CannotRateException(
                        "selected songs must have more than one distinct rating");
            }
            for (int j = i + 1; j < songs.length; j++) {
                if (songs[i] == songs[j]) {
                    throw new DataOpperator.CannotRateException(
                            "user selection must not contain duplicate entries.");
                }
            }
        }
    }

    /**
     * @return a list of songs with predicted ratings added to internal rating list
     */
    private static List<Song> getCompletePredicitonTable() throws DataOpperator.CannotRateException {
        List<Song> predictionMap = new ArrayList<>();
        for (Song song : DataBase.getAllSongs()) {
            predictionMap.add(predictSongRatingsForAllUsers(song));
        }
        return predictionMap;
    }

    /**
     * @param song the song for which ratings will be predicted
     * @return a copy of the original song containing predicted ratings, original
     *         song found in database not affected
     */
    private static Song predictSongRatingsForAllUsers(Song song) throws DataOpperator.CannotRateException {
        Song hypotheticalSong = new Song(song.getName(), song.getId());
        boolean predictedSomeRating = false;

        for (User user : DataBase.getAllUsers()) {
            try {
                OptionalInt realRating = song.getUserRating(user.getId());
                if (realRating.isPresent()) {
                    hypotheticalSong.addOrChangeRating(user.getId(), realRating.orElseThrow());
                    continue;
                }
                predictedSomeRating = true;

                long sophisticatedPrediction = UserDataHandler.predictUserRating(user, song, DataBase.getAllUsers());
                if (sophisticatedPrediction != Long.MIN_VALUE) {
                    hypotheticalSong.addOrChangeRating(user.getId(), (int) sophisticatedPrediction);
                    continue;
                }

                long basicPrediciton = Math.round((song.getMeanRating() * song.getNumberOfRatings()
                        + user.getMeanRating() * user.getSongLibrary().length)
                        / (song.getNumberOfRatings() + user.getSongLibrary().length));
                hypotheticalSong.addOrChangeRating(user.getId(), (int) basicPrediciton);
            } catch (InvalidRatingException e) {
                return hypotheticalSong;
            }
        }

        if (!predictedSomeRating) {
            throw new DataOpperator.CannotRateException(
                    "no songs to recommend. Songs may have been removed."
                            + " Try with a larger file or fewer selections.");
        }
        return hypotheticalSong;
    }

}
