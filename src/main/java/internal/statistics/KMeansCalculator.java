package internal.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;

import internal.Utilities;
import internal.data.DataOpperator;
import internal.types.Song;

public class KMeansCalculator {
    private ArrayList<Cluster> clusters;

    private Song[] songs;

    public KMeansCalculator(Song[] centroidValues, Song[] songs) {
        this.songs = songs;
        this.clusters = new ArrayList<>();

        for (Song song : centroidValues) {
            Centroid centroid = new Centroid(song);
            clusters.add(new Cluster(centroid));
        }
    }

    public void iterate() {
        clusters.stream().forEach(cluster -> cluster.songs.clear());

        for (Song song : songs) {
            Cluster mostSimilarCluster = getMostSimilarCluster(clusters, song);
            mostSimilarCluster.songs.add(song);
        }

        recalculateCentroids();
    }

    public Map<Song, List<Song>> getClusterMap() {
        Map<Song, List<Song>> clusterMap = new HashMap<>();

        for (Cluster cluster : clusters) {
            clusterMap.put(cluster.centroid.baseValue, cluster.songs);
        }

        return clusterMap;
    }

    private Cluster getMostSimilarCluster(List<Cluster> clusters, Song song) {
        Cluster mostSimilarCluster = null;
        for (Cluster cluster : clusters) {
            if (mostSimilarCluster == null) {
                mostSimilarCluster = cluster;
                continue;
            }
            double clusterSimilarity = cluster.getSimilarity(song);
            if (clusterSimilarity < mostSimilarCluster.getSimilarity(song)) {
                mostSimilarCluster = cluster;
            }
        }
        return mostSimilarCluster;
    }

    private void recalculateCentroids() {
        for (Cluster cluster : clusters) {
            Double[] totalRatings = new Double[cluster.centroid.values.length];
            Arrays.fill(totalRatings, Double.valueOf(0));

            for (Song song : cluster.songs) {
                List<Double> normalizedRatings = DataOpperator.normalizeSongRatingList(song);
                for (int i = 0; i < totalRatings.length; i++) {
                    totalRatings[i] = totalRatings[i] + normalizedRatings.get(i);
                }
            }

            if (cluster.songs.size() > 0) {
                cluster.centroid.values = Arrays.stream(totalRatings)
                        .map(rating -> rating / cluster.songs.size())
                        .toArray(Double[]::new);
            }
        }
    }

    @Override
    public String toString() {
        String output = "";
        for (Cluster cluster : clusters) {
            output += cluster;
        }
        return output;
    }

    private class Centroid {
        Song baseValue;
        Double[] values;

        public Centroid(Song song) {
            this.baseValue = song;
            this.values = DataOpperator.normalizeSongRatingList(song).toArray(Double[]::new);
        }

        @Override
        public String toString() {
            return String.format("%s:\n\t%s", this.baseValue.getName(), Arrays.toString(values));
        }

    }

    private class Cluster {
        Centroid centroid;
        List<Song> songs;

        public Cluster(Centroid centroid) {
            this.centroid = centroid;
            this.songs = new ArrayList<>();
        }

        public double getSimilarity(Song song) {
            var normalizedRatings = DataOpperator.normalizeSongRatingList(song).toArray(Double[]::new);
            var distanceCalculator = new EuclideanDistance();
            return distanceCalculator.compute(
                    Utilities.doubleArrToPrimative(centroid.values),
                    Utilities.doubleArrToPrimative(normalizedRatings));
        }

        @Override
        public String toString() {
            String output = centroid.toString() + "\n";
            output += "\tSongs:" +
                    Arrays.toString(
                            songs.stream().map(s -> s.getName()).toArray(String[]::new))
                    +
                    "\n";
            return output;
        }
    }
}
