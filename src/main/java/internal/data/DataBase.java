package internal.data;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import internal.types.Song;
import internal.types.User;

/*
 * Static class for storing and accessing user and song data
 */
public class DataBase {
    private static Map<UUID, User> users = new TreeMap<UUID, User>();
    private static Map<UUID, Song> songs = new TreeMap<UUID, Song>();

    /**
     * Adds User to database if not present else returns the id of the user
     * 
     * @param name name of user to add
     * @return id of user with given name
     */
    public static UUID addUser(String name) {
        Optional<UUID> dbUserId = getUserId(name);
        if (dbUserId.isPresent()) {
            return dbUserId.get();
        }

        UUID newUserId = UUID.randomUUID();
        User newUser = new User(name, newUserId);
        users.put(newUserId, newUser);
        return newUser.getId();
    }

    /**
     * User type should be preffered for methods within the data package
     *
     * @param id the UUID associated with the user
     * @return the associated user if found, else null
     */
    public static Optional<User> getUser(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * getter for the size of the user database
     *
     * @return the number of users in database
     */
    public static int getUserCount() {
        return users.size();
    }

    /**
     * gets user's associated uuid from name
     *
     * @param name name of user to get
     * @return the associated song id if found, else null
     */
    public static Optional<UUID> getUserId(String name) {
        for (var entry : users.entrySet()) {
            String userName = entry.getValue().getName();
            if (userName.equals(name)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * gets an unsorted list of all user's ids
     *
     * @return the ids of every user in the database
     */
    public static UUID[] getAllUserIDs() {
        return users.keySet().toArray(UUID[]::new);
    }

    /**
     * Adds User to database if not present else returns the id of the song
     * 
     * @param name name of song to be added
     * @return added Song
     */
    public static UUID addSong(String name) {
        Optional<UUID> dbSongId = getSongId(name);
        if (dbSongId.isPresent()) {
            return dbSongId.get();
        }
        UUID newSongId = UUID.randomUUID();
        Song song = new Song(name, newSongId);
        songs.put(newSongId, song);
        return song.getId();
    }

    /**
     * gets song object from associated id
     *
     * @param id UUID identifier for the song
     * @return the song associated with the id
     */
    public static Optional<Song> getSong(UUID id) {
        return Optional.ofNullable(songs.get(id));
    }

    /**
     * gets the uuid of a song from the name
     *
     * @param name name of song
     * @return the associated song id if found, else throws an UnknownSongException
     */
    public static Optional<UUID> getSongId(String name) {
        for (var entry : songs.entrySet()) {
            String songName = entry.getValue().getName();
            if (songName.equals(name)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * getter for song database's size
     * 
     * @return the number of songs in the database
     */
    public static int getSongCount() {
        return songs.size();
    }

    /**
     * replaces all database data fields with empty ones
     */
    public static void clearDataBase() {
        songs = new TreeMap<UUID, Song>();
        users = new TreeMap<UUID, User>();
    }

    /**
     * for use within the internal.data package
     *
     * @return an array of all current users
     */
    static User[] getAllUsers() {
        return Arrays.stream(getAllUserIDs()).map(id -> getUser(id).get()).toArray(User[]::new);
    }

    /**
     * gets an unsorted list of all user's ids
     *
     * @return the ids of every user in the database
     */
    public static UUID[] getAllSongIDs() {
        return songs.keySet().toArray(UUID[]::new);
    }

    /**
     * for use within the internal.data package
     *
     * @return an array of all current users
     */
    static Song[] getAllSongs() {
        return Arrays.stream(getAllSongIDs()).map(id -> getSong(id).get()).toArray(Song[]::new);
    }

    public static void associateRatingToUserAndSong(UUID songId, UUID userId, int rating)
            throws Song.InvalidRatingException, InvalidIDException {
        var song = getSong(songId);
        var user = getUser(userId);
        if (song.isEmpty() || user.isEmpty()) {
            throw new InvalidIDException();
        }
        song.get().addOrChangeRating(userId, rating);
        user.get().addSongToLibrary(songId);
    }

    public static class InvalidIDException extends Exception {
    }
}
