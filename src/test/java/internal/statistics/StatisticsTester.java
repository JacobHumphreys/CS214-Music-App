package internal.statistics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.data.DataBase;
import internal.types.Song;

public class StatisticsTester {
    @AfterEach
    public void cleanDataBase() {
        DataBase.clearDataBase();
    }

    @Test
    public void testRatingNormalization() {
        var user1 = DataBase.getUser(DataBase.addUser("User1"));

        String[] songNames = { "Track1", "Track2", "Track3" };
        Song[] songs = Arrays.stream(songNames)
                .map(name -> DataBase.addSong(name))
                .map(id -> DataBase.getSong(id).get())
                .toArray(Song[]::new);

        assertDoesNotThrow(() -> songs[0].addOrChangeRating(user1.get().getId(), 1));
        assertDoesNotThrow(() -> songs[1].addOrChangeRating(user1.get().getId(), 2));
        assertDoesNotThrow(() -> songs[2].addOrChangeRating(user1.get().getId(), 5));
        user1.get().addSongToLibrary(songs[0].getId());
        user1.get().addSongToLibrary(songs[1].getId());
        user1.get().addSongToLibrary(songs[2].getId());

        double normalizedRating = StatisticCalculator.normalizeRating(
            2,
                user1.get().getMeanRating(),
                user1.get().getStdDev());

        assertEquals(-0.392232270276368261, normalizedRating);
    }

    @Test
    public void testStatsUpdater() {
        ArrayList<Integer> ratings = new ArrayList<>(Arrays.asList(1, 2, 4, 1, 2));
        double[] stats = { 0d, 0d };
        for (int i = 0; i < ratings.size(); i++) {
            Integer[] sublist = ratings.subList(0, i + 1).toArray(Integer[]::new);
            stats = StatisticCalculator.updateStats(sublist, stats[0], stats[1]);
        }
        var stdDev = stats[0];
        var mean = stats[1];

        double maxAcceptableDifference = Math.pow(10, -6);
        double difference = stdDev - 1.0954451150103;
        assertTrue(Math.abs(difference) < maxAcceptableDifference);

        assertEquals(2, mean);
    }

    @Test
    public void testUserComparison() {
        var user1Id = DataBase.addUser("User1");
        var user2Id = DataBase.addUser("User2");
        var user3Id = DataBase.addUser("User3");
        String[] songNames = { "Track1", "Track2", "Track3" };
        Song[] songs = Arrays.stream(songNames)
                .map(name -> DataBase.addSong(name))
                .map(id -> DataBase.getSong(id).get())
                .toArray(Song[]::new);

        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[0].getId(), user1Id, 4));
        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[2].getId(), user1Id, 1));

        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[0].getId(), user2Id, 4));
        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[2].getId(), user2Id, 5));
        assertEquals(2.8284271247461903, StatisticCalculator.compareUsers(user1Id, user2Id));

        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[1].getId(), user2Id, 2));
        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[1].getId(), user3Id, 3));
        assertEquals(2.1949608574551536, StatisticCalculator.compareUsers(user1Id, user2Id));

        assertEquals(StatisticCalculator.NO_SHARED_SONGS, StatisticCalculator.compareUsers(user1Id, user3Id));


        assertDoesNotThrow(() -> DataBase.associateRatingToUserAndSong(songs[2].getId(), user3Id, 3));
        assertEquals(StatisticCalculator.USERS_CANNOT_BE_COMPARED, StatisticCalculator.compareUsers(user1Id, user3Id));
    }
}
