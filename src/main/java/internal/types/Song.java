package internal.types;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

import internal.statistics.StatisticCalculator;

public class Song implements Comparable<Song> {
    private String name;
    private UUID id;

    Map<UUID, Integer> ratings;
    private double meanRating, stdDev;

    public Song(String name, UUID id) {
        this.name = name;
        this.id = id;
        this.ratings = new HashMap<>();
    }

    public void addOrChangeRating(UUID userID, Integer rating) throws InvalidRatingException {
        if (rating < 1 || rating > 5) {
            throw new InvalidRatingException(rating);
        }
        ArrayList<Integer> ratingValues = new ArrayList<>(ratings.values());
        if (ratings.put(userID, rating) == null) {
            ratingValues.add(rating);

        }

        var newStats = StatisticCalculator.updateStats(ratingValues.toArray(Integer[]::new), stdDev, meanRating);
        stdDev = newStats[0];
        meanRating = newStats[1];
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    /**
     * @param userId id of user to check for ratings of
     * @return Optional user rating between 1 and 5
     */
    public OptionalInt getUserRating(UUID userId) {
        var rating = ratings.get(userId);
        if (rating == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(rating.intValue());
    }

    public int getNumberOfRatings() {
        return ratings.size();
    }

    public double getStdDev() {
        return stdDev;
    }

    public double getMeanRating() {
        return meanRating;
    }

    public Map<UUID, Integer> getUserRatings() {
        return Map.copyOf(this.ratings);
    }

    @Override
    public String toString() {
        var dFormat = new DecimalFormat("0.0######");
        return String.format("%s,%d,%s,%s",
                this.name,
                getNumberOfRatings(),
                dFormat.format(meanRating),
                dFormat.format(stdDev));
    }

    @Override
    public int compareTo(Song s) {
        return name.compareTo(s.getName());
    }

    public static class InvalidRatingException extends Exception {
        public InvalidRatingException(Integer rating) {
            super("Invalid Rating Found: " + rating);
        }
    }

}
