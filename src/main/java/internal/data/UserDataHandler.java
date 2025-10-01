package internal.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import internal.statistics.StatisticCalculator;
import internal.types.Song;
import internal.types.User;

/*
 * Internal class used for handling user data in ways not
 * neccessary for outside module access
 */
class UserDataHandler {
    static UUID[] getCommonSongsBetweenUsers(User user1, User user2) {
        if (user1 == null || user2 == null)
            return new UUID[] {};

        return Arrays.stream(user1.getSongLibrary())
                .filter(s -> Arrays.asList(user2.getSongLibrary()).contains(s))
                .toArray(UUID[]::new);
    }

    /**
     * sorts a list of users by name
     *
     * @param users a list of users to be sorted
     * @return a copy of the input list that is sorted by name
     */
    static User[] sortUsersByName(User[] users) {
        users = Arrays.copyOf(users, users.length);
        for (int i = 0; i < users.length; i++) {
            var min = i;
            for (int j = i + 1; j < users.length; j++) {
                if (users[min].getName().compareTo(users[j].getName()) > 0) {
                    min = j;
                }
            }
            var temp = users[i];
            users[i] = users[min];
            users[min] = temp;
        }
        return users;
    }

    /**
     * User records for output csv of user data
     *
     * @param users a sorted list of users
     * @return a list of records with user comparison data
     */
    static List<String> getRecords(User[] users) {
        var sortedUsers = sortUsersByName(users);
        var recordList = new ArrayList<String>();

        for (int i = 0; i < sortedUsers.length - 1; i++) {
            var user1 = sortedUsers[i];
            for (int j = i + 1; j < sortedUsers.length; j++) {
                var user2 = sortedUsers[j];
                var similarity = StatisticCalculator.compareUsers(user1.getId(), user2.getId());

                if (similarity == StatisticCalculator.USERS_CANNOT_BE_COMPARED)
                    continue;
                if (similarity == StatisticCalculator.NO_SHARED_SONGS)
                    similarity = Double.NaN;

                recordList.add(String.format("%s,%s,", user1.getName(), user2.getName()) + similarity);
            }
        }

        return recordList;
    }

    /**
     * @param targetUser the user to be compared to
     * @param users      the list of users to compare
     * @return a new list of users sorted by similarity to the target user
     */
    static User[] sortUsersBySimilarityToUser(User targetUser, User[] users) {
        users = Arrays.copyOf(users, users.length);

        for (int i = 0; i < users.length - 1; i++) {
            var currentUser = users[i];
            var currentSim = !currentUser.equals(targetUser)
                    ? StatisticCalculator.compareUsers(targetUser.getId(), currentUser.getId())
                    : Double.POSITIVE_INFINITY;
            var minimumIndex = i;
            for (int j = i; j < users.length; j++) {
                var nextUser = users[j];
                var nextSim = !nextUser.equals(targetUser)
                        ? StatisticCalculator.compareUsers(targetUser.getId(), nextUser.getId())
                        : Double.POSITIVE_INFINITY;
                if (currentSim > nextSim) {
                    minimumIndex = j;
                    currentSim = nextSim;
                }
            }
            var temp = users[i];
            users[i] = users[minimumIndex];
            users[minimumIndex] = temp;
        }
        return users;
    }

    /**
     * Used to Predict A Users rating for a song
     *
     * @return Long.MIN_VALUE if user rating cannot be predicted otherwise a
     *         predicted rating between 1 and 5
     */
    static long predictUserRating(User user, Song song, User[] users) {
        var sortedSimilarUsers = sortUsersBySimilarityToUser(user, users);
        for (User otherUser : sortedSimilarUsers) {
            if (otherUser.equals(user)) {
                continue;
            }
            var userSim = StatisticCalculator.compareUsers(otherUser.getId(), user.getId());

            if (userSim == StatisticCalculator.USERS_CANNOT_BE_COMPARED
                    || userSim == StatisticCalculator.NO_SHARED_SONGS) {
                continue;
            }

            var otherUserRating = otherUser.getNormalizedSongRating(song.getId());
            if (otherUserRating == User.UNABLE_TO_NORMALIZE)
                continue;

            return clamp(Math.round(otherUserRating * user.getStdDev() + user.getMeanRating()), 1, 5);
        }
        return Long.MIN_VALUE;
    }

    private static long clamp(long value, int min, int max) {
        return value > min ? value < max ? value : max : min;
    }
}
