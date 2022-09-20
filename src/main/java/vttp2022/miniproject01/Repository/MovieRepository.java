package vttp2022.miniproject01.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import vttp2022.miniproject01.Model.Genre;
import vttp2022.miniproject01.Model.Movie;

@Repository
public class MovieRepository {

    @Autowired
    @Qualifier("redislab")
    private RedisTemplate<String, String> template;

 //================================================== Movies Stuff ==================================================
    
    // JUST FOR TESTING
    // public void save (List<Movie> savedWatchlist) {
    //     Map<String, String> map = new HashMap<>();
    //     for (Movie mo : savedWatchlist) {
    //         map.put(mo.getId(), mo.toJson().toString());
    //     }
    //     template.opsForValue().multiSet(map);

    //     for (String id : map.keySet()) {
    //         template.expire(id, Duration.ofMinutes(5));
    //     }
    // }

    //-------------------------------------------------- saveList1 --------------------------------------------------

    /*
     * (DEPRECATED : Replaced by saveList2)
     * TASK: -
     * FUNCTION: SAVING movie(s)
     */

    // saveList1 IS ADDING W/O CHECKING FOR REPEATS
    // public void saveList1 (List<Movie> savedWatchList) {
    //     String keyName = "savedWatchList";
    //     ListOperations<String, String> listOp = template.opsForList();
        
    //     for (Movie movie : savedWatchList) {
    //         listOp.rightPush(keyName, movie.toJson().toString());
    //     }
    // }

    //-------------------------------------------------- saveList2 --------------------------------------------------

    /*
     * TASK: -
     * FUNCTION: SAVING movie(s)
     */

    // Version 1 - saveList2 (IS ADDING W CHECKING FOR REPEATS (W/O USER))
    // public void saveList2 (List<Movie> savedWatchList) {
    //     String keyName = "savedWatchList";
    //     ListOperations<String, String> listOp = template.opsForList();

    //     long l = listOp.size(keyName);
    //     if (l > 0) {
            
    //         List<Movie> redisMovies = new LinkedList<>();

    //         for (int i = 0; i < listOp.size(keyName); i++) {
    //             redisMovies.add(Movie.create2(listOp.index(keyName, i)));
    //         }

    //         List<String> redisMoviesIds = new LinkedList<>();
    //         for (Movie movie : redisMovies) {
    //             redisMoviesIds.add(movie.getId());
    //         }

    //         for (Movie movie : savedWatchList) {
    //             if (!(redisMoviesIds.contains(movie.getId()))) {
    //                 redisMovies.add(movie);
    //             }
    //         }

    //         for (Long i = listOp.size(keyName); i > 0; i--) {
    //             listOp.rightPop(keyName);
    //         }
            
    //         for (Movie movie : redisMovies) {
    //             listOp.rightPush(keyName, movie.toJson().toString());
    //         }
    //     } else {
    //         for (Movie movie : savedWatchList) {
    //             listOp.rightPush(keyName, movie.toJson().toString());
    //         }
    //     }
    // }

    // Version 2 - saveList2 (IS ADDING W CHECKING FOR REPEATS (W USER))
    public void saveList2 (List<Movie> savedWatchList, String name) {

        // ACCESS REDIS KEY-PAIR: <NAME> , Movies (ListOps)
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();

        // checking for repeats if there are saved movies
        long l = listOp.size(keyName);
        if (l > 0) {
            
            // Comparator A #1 - empty list of movies for "saved in redis"
            List<Movie> redisMovies = new LinkedList<>();

            // Comparator A #1 - filled list of movies for "saved in redis"
            for (int i = 0; i < listOp.size(keyName); i++) {
                redisMovies.add(Movie.create2(listOp.index(keyName, i)));
            }

            // Comparator A #2 - empty list of movie ids for "saved in redis"
            List<String> redisMoviesIds = new LinkedList<>();

            // Comparator A #2 - filled list of movie ids for "saved in redis"
            for (Movie movie : redisMovies) {
                redisMoviesIds.add(movie.getId());
            }

            // Comparator A #2 (String id) <-> Comparator B #1 (convert Movie to String id)
            // (adding difference <Movie> into Comparator A #1)
            for (Movie movie : savedWatchList) {
                if (!(redisMoviesIds.contains(movie.getId()))) {
                    redisMovies.add(movie);
                }
            }

            // Clearing "saved in redis" in Redis Client
            for (Long i = listOp.size(keyName); i > 0; i--) {
                listOp.rightPop(keyName);
            }
            
            // Add updated movie list into redis
            for (Movie movie : redisMovies) {
                listOp.rightPush(keyName, movie.toJson().toString());
            }

        // if nothing in database, just add without checking for repeats
        } else {
            for (Movie movie : savedWatchList) {
                listOp.rightPush(keyName, movie.toJson().toString());
            }
        }
    }

    //-------------------------------------------------- get function --------------------------------------------------

    /*
     * TASK: -
     * FUNCTION: Retrieving ALL SAVED movie(s)
     */

    //Version 1 - get (GET ALL SAVED MOVIES FROM DB (W/O USER))
    // public List<Movie> get () {
    //     String keyName = "savedWatchList";
    //     ListOperations<String, String> listOp = template.opsForList();
    //     List<Movie> redisMovies = new LinkedList<>();

    //     for (int i = 0; i < listOp.size(keyName); i++) {
    //         redisMovies.add(Movie.create2(listOp.index(keyName, i)));
    //     }

    //     return redisMovies;
    // }

    //Version 2 - get (GET ALL SAVED MOVIES FROM DB (W USER))
    public List<Movie> get (String name) {

        // ACCESS REDIS KEY-PAIR: <NAME> , Movies (ListOps)
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();
        
        // Checking if user has movielist in database (first login/setup right after register)
        // weird initial check, but UsernamePassword key-pair + GenreScoreboard key-pair done already before calling Trend API when signup
        if (!template.hasKey(name)) {
            listOp.rightPush(name, "");
            listOp.rightPop(name);
        }
        
        // Empty list of movies for "saved in redis"
        List<Movie> redisMovies = new LinkedList<>();

        // Filled list of movies for "saved in redis"
        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        return redisMovies;
    }

    //-------------------------------------------------- getMovieId --------------------------------------------------

    /*
     * TASK: @PATHVARIABLE
     * FUNCTION: retrieving 1 movie(s)
     */

    //Version 1 - getMovieId (GET PATHVARIABLE MOVIE (W/O USER))
    // public Optional<Movie> getMovieId (String id) {
    //     String keyName = "savedWatchList";
    //     ListOperations<String, String> listOp = template.opsForList();
    //     List<Movie> redisMovies = new LinkedList<>();

    //     for (int i = 0; i < listOp.size(keyName); i++) {
    //         redisMovies.add(Movie.create2(listOp.index(keyName, i)));
    //     }

    //     for (int i = 0; i < redisMovies.size(); i++) {
    //         if (redisMovies.get(i).getId().equals(id)) {
    //             return Optional.of(redisMovies.get(i));
    //         }
    //     }
    //     return Optional.empty();
    // }

    //Version 2 - getMovieId (GET PATHVARIABLE MOVIE (W USER))
    public Optional<Movie> getMovieId (String id, String name) {

        // ACCESS REDIS KEY-PAIR: <NAME> , Movies (ListOps)
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();

        // Empty list of movies for "saved in redis"
        List<Movie> redisMovies = new LinkedList<>();

        // Filled list of movies for "saved in redis"
        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        // Check thru "saved in redis" for matching id and return Movie in a list (redisMovies)
        for (int i = 0; i < redisMovies.size(); i++) {
            if (redisMovies.get(i).getId().equals(id)) {
                return Optional.of(redisMovies.get(i));
            }
        }
        return Optional.empty();
    }

    //-------------------------------------------------- Delete Movie function --------------------------------------------------

    /*
     * Delete Movie function
     */
    public void delete (String id, String name) {

        // ACCESS REDIS KEY-PAIR: <NAME> , Movies (ListOps)
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();

        // Empty list of movies for "saved in redis"
        List<Movie> redisMovies = new LinkedList<>();

        // Filled list of movies for "saved in redis"
        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        // Check thru "saved in redis" for matching id and deleting Movie in the list (redisMovies)
        for (Movie movie : redisMovies) {
            if ((movie.getId()).equals(id)) {
                redisMovies.remove(movie);
                break;
            }
        }

        // Clearing "saved in redis" in Redis Client
        for (Long i = listOp.size(keyName); i > 0; i--) {
            listOp.rightPop(keyName);
        }
        
        // Add updated movie list into redis
        for (Movie movie : redisMovies) {
            listOp.rightPush(keyName, movie.toJson().toString());
        }

    }

 //================================================== Genre ScoreBoard ==================================================
    
    /*
     * User Genre Board when user first create account
     */
    public void setUpGenreScore (List<Genre> genreList, String name) {

        // ACCESS REDIS KEY-VALUE: ><NAME> , Genre Scoreboard (HashOps)
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        // create variable for starting value
        Integer startValue = 0;

        // populate user's genre scoreboard with KEY: Genre API call data, VALUE: 0
        for (Genre genre : genreList) {
            hashOp.put(keyName, genre.getGenreId().toString(), startValue);
        }

        System.out.println("User Genre Chart is set up successfully.");

    }

    /*
     * Add Genre Score when User favourites Movie
     */
    public void addToGenreList (List<Integer> genreIdList, String name) {

        // ACCESS REDIS KEY-VALUE: ><NAME> , Genre Scoreboard (HashOps)
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        // call keys and values in hashmap from redis
        Set<String> keys = hashOp.keys(keyName);
        System.out.println("These are KEYS from the Genre ScoreBoard HashMap: " + keys);
        Object[] keysArray = keys.toArray();
        List<Integer> values = hashOp.values(keyName);
        System.out.println("These are VALUES from the Genre ScoreBoard HashMap: " + values);
        
        // create hashmap 
        Map<String, Integer> redisGenreMap  = new HashMap<String, Integer>();

        // populate hashmap with called keys and values
        for (int i = 0; i < keys.size(); i++) {
            redisGenreMap.put(keysArray[i].toString(), values.get(i));
        }

        //edit the hashmap before returning it back to redis
        // if Id (adding score) matches with Id in hashmap, add the count 
        for (Integer i : genreIdList) {
            if (redisGenreMap.containsKey(i.toString())) {
                Integer intString = redisGenreMap.get(i.toString());
                intString++;
                redisGenreMap.put(i.toString(), intString);
            }
        }

        //return it back to redis
        hashOp.putAll(keyName, redisGenreMap);
    }

 //================================================== Recommendation Page ==================================================
    
    /*
     * get Genre board scores
     */
    public List<String> getRec (String name) {

        // ACCESS REDIS KEY-VALUE: ><NAME> , Genre Scoreboard (HashOps)
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        // call keys and values in hashmap from redis
        Set<String> keys = hashOp.keys(keyName);
        Object[] keysArray = keys.toArray();
        List<Integer> values = hashOp.values(keyName);
            
        //create hashmap 
        Map<String, Integer> redisGenreMap  = new HashMap<String, Integer>();

        // populate hashmap with called keys and values
        for (int i = 0; i < keys.size(); i++) {
            redisGenreMap.put(keysArray[i].toString(), values.get(i));
        }

        // create variable for max value
        Integer max = Collections.max(redisGenreMap.values());

        // empty list to store Top Genre Id
        List<String> recKeys = new ArrayList<>();

        // filled list with Top Genre Id
        for (Entry<String, Integer> entry : redisGenreMap.entrySet()) {
            if (entry.getValue()==max) {
                recKeys.add(entry.getKey());
            }
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>This is recKeys movieRepo278: " + recKeys);

        return recKeys;

    }

    /*
     * Reset Genre Score
     */
    public void resetScore (String name) {

        // ACCESS REDIS KEY-VALUE: ><NAME> , Genre Scoreboard (HashOps)
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        // call keys and values in hashmap from redis
        Set<String> keys = hashOp.keys(keyName);
        Object[] keysArray = keys.toArray();
        // List<Integer> values = hashOp.values(keyName);
         
        // create hashmap 
        Map<String, Integer> redisGenreMap  = new HashMap<String, Integer>();

        // replacing all values of each key in hashmap with 0
        for (int i = 0; i < keys.size(); i++) {
            redisGenreMap.put(keysArray[i].toString(), 0);
        }

        // replace existing hashmap in redis with KEY:<genretype>, VALUE:<score(Integer)> = 0
        hashOp.putAll(keyName, redisGenreMap);

        System.out.println(hashOp.keys(keyName));
        System.out.println(hashOp.values(keyName));
    }
    
 //================================================== 1. Account Stuff ==================================================

    /*
     * Account Validation
     */
    public Boolean[] checkUser (String name, String password) {

        // ACCESS REDIS KEY-VALUE: #<NAME> , Username-Password (ValueOps)
        String redisName = "#" + name;
        ValueOperations<String, String> valueOps = template.opsForValue();

        // create boolean variable to check if user exists
        Boolean isExist = template.hasKey(redisName);

        //create boolean array to return result used for "Authentication" case scenario in Account Controller
        Boolean[] booleanArray = new Boolean[2];
        booleanArray[0] = null;
        booleanArray[1] = null;

        // CASE 1: Username CORRECT, Password CORRECT - true, true
        // CASE 2: Username CORRECT, Password WRONG - true, false
        // CASE 3: Username WRONG, Password WRONG - false, false
        if (isExist) {
            booleanArray[0] = true;
            String redisPassword = valueOps.get(redisName);
            String isPassword = ((Boolean)(redisPassword.equals(password))).toString();
            switch (isPassword) {
                case "true":
                    booleanArray[1] = true; //true, true
                    break;

                case "false":
                    booleanArray[1] = false; //true, false
                    break;
            
                default:
                    break;
            }
            return booleanArray;

        } else if (!isExist) {
            booleanArray[0] = false; //false, false
            booleanArray[1] = false;
        }
        
        return booleanArray;
    }

    /*
     * Account Registration
     */
    public Boolean createAccount (String name, String password) {

        // ACCESS REDIS KEY-VALUE: #<NAME> , Username-Password (ValueOps)
        String redisName = "#" + name;
        ValueOperations<String, String> valueOps = template.opsForValue();
        // valueOps.set(redisName, password);
        
        // create boolean variable to check if registered name is taken
        // Boolean isNameTaken = null;
        
        if (template.hasKey(redisName)) {
            // return isNameTaken = true;
            return true;
        } else {
            // isNameTaken = false; 
            valueOps.set(redisName, password);
            // return isNameTaken = false;
            return false;
        }
    }

}

