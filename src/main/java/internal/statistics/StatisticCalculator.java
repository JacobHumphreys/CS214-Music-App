package internal.statistics;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;

import internal.Utilities;
import internal.data.DataBase;
import internal.data.DataOpperator;
import internal.types.User;

public class StatisticCalculator {

    /**
     * @param newValue
     * @param stdDev
     * @param mean
     * @param setSize  size of dataset after new value is added
     * @return array of size 2 where index 0 = stdDev, and index 1 = mean
     */
    public static double[] updateStats(Integer[] values, double stdDev, double mean) {
        double m2 = (stdDev * stdDev) * (values.length - 1);
        double delta = values[values.length - 1] - mean;
        mean += delta / values.length;
        m2 += delta * (values[values.length - 1] - mean);
        stdDev = Math.sqrt(m2 / values.length);
        return new double[] { stdDev, mean };
    }

    /**
     * normalizes a rating given a rating, stddev and a mean
     *
     * @param rating a rating value
     * @param mean   the mean of all ratings
     * @param stdDev the stddev of all reviews
     * @return the normalized rating
     */
    public static double normalizeRating(int rating, double mean, double stdDev) {
        return ((double) rating - mean) / stdDev;
    }

    public static final double USERS_CANNOT_BE_COMPARED = Double.NEGATIVE_INFINITY;

    public static final double NO_SHARED_SONGS = Double.POSITIVE_INFINITY;

    /**
     * compares two user's libraries
     *
     * @param user1Id user1's id
     * @param user2Id user2's id
     * @return the euclidean distance between all of the shared ratings
     */
    public static double compareUsers(UUID user1Id, UUID user2Id) {
        var commonSongs = DataOpperator.getCommonSongsBetweenUsers(user1Id, user2Id);
        if (commonSongs.length == 0)
            return NO_SHARED_SONGS;

        var user1 = DataBase.getUser(user1Id).orElseThrow();
        var user1Ratings = Arrays.stream(commonSongs)
                .map(id -> user1.getNormalizedSongRating(id))
                .filter(rating -> rating != User.UNABLE_TO_NORMALIZE)
                .toArray(Double[]::new);

        if (user1Ratings.length == 0) {
            return USERS_CANNOT_BE_COMPARED;
        }

        var user2 = DataBase.getUser(user2Id).orElseThrow();
        var user2Ratings = Arrays.stream(commonSongs)
                .map(id -> user2.getNormalizedSongRating(id))
                .filter(rating -> rating != User.UNABLE_TO_NORMALIZE)
                .toArray(Double[]::new);

        if (user2Ratings.length == 0) {
            return USERS_CANNOT_BE_COMPARED;
        }

        return new EuclideanDistance().compute(Utilities.doubleArrToPrimative(user1Ratings),
                Utilities.doubleArrToPrimative(user2Ratings));
    }
}
