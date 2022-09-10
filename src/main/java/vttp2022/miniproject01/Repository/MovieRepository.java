package vttp2022.miniproject01.Repository;

import java.time.Duration;
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

    // saveList1 IS ADDING W/O CHECKING FOR REPEATS
    // public void saveList1 (List<Movie> savedWatchList) {
    //     String keyName = "savedWatchList";
    //     ListOperations<String, String> listOp = template.opsForList();
        
    //     for (Movie movie : savedWatchList) {
    //         listOp.rightPush(keyName, movie.toJson().toString());
    //     }
    // }

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
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();

        long l = listOp.size(keyName);
        if (l > 0) {
            
            List<Movie> redisMovies = new LinkedList<>();

            for (int i = 0; i < listOp.size(keyName); i++) {
                redisMovies.add(Movie.create2(listOp.index(keyName, i)));
            }

            List<String> redisMoviesIds = new LinkedList<>();
            for (Movie movie : redisMovies) {
                redisMoviesIds.add(movie.getId());
            }

            for (Movie movie : savedWatchList) {
                if (!(redisMoviesIds.contains(movie.getId()))) {
                    redisMovies.add(movie);
                }
            }

            for (Long i = listOp.size(keyName); i > 0; i--) {
                listOp.rightPop(keyName);
            }
            
            for (Movie movie : redisMovies) {
                listOp.rightPush(keyName, movie.toJson().toString());
            }
        } else {
            for (Movie movie : savedWatchList) {
                listOp.rightPush(keyName, movie.toJson().toString());
            }
        }
    }

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
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();
        
        if (!template.hasKey(name)) {
            listOp.rightPush(name, "");
            listOp.rightPop(name);
        }
        
        List<Movie> redisMovies = new LinkedList<>();
        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        return redisMovies;
    }

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
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();
        List<Movie> redisMovies = new LinkedList<>();

        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        for (int i = 0; i < redisMovies.size(); i++) {
            if (redisMovies.get(i).getId().equals(id)) {
                return Optional.of(redisMovies.get(i));
            }
        }
        return Optional.empty();
    }

    //delete function
    public void delete (String id, String name) {
        String keyName = name;
        ListOperations<String, String> listOp = template.opsForList();
        List<Movie> redisMovies = new LinkedList<>();

        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        for (Movie movie : redisMovies) {
            if ((movie.getId()).equals(id)) {
                redisMovies.remove(movie);
                break;
            }
        }

        for (Long i = listOp.size(keyName); i > 0; i--) {
            listOp.rightPop(keyName);
        }
        
        for (Movie movie : redisMovies) {
            listOp.rightPush(keyName, movie.toJson().toString());
        }

    }

    // =============================================================================
    // GenreList
    
    //User Genre Board when user first create account
    public void setUpGenreScore (List<Genre> genreList, String name) {
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        Integer startValue = 0;

        for (Genre genre : genreList) {
            hashOp.put(keyName, genre.getGenreId().toString(), startValue);
        }

        System.out.println("User Genre Chart is set up successfully.");

    }

    //Add Genre Score when User favourites Movie
    public void addToGenreList (List<Integer> genreIdList, String name) {
        String keyName = ">" + name;
        HashOperations<String, String, Integer> hashOp = template.opsForHash();

        //call hashmap from redis
        Set<String> keys = hashOp.keys(keyName);
        System.out.println("These are KEYS from the Genre ScoreBoard HashMap: " + keys);
        Object[] keysArray = keys.toArray();
        List<Integer> values = hashOp.values(keyName);
        System.out.println("These are VALUES from the Genre ScoreBoard HashMap: " + values);
        
        //create hashmap 
        Map<String, Integer> redisGenreMap  = new HashMap<String, Integer>();

        for (int i = 0; i < keys.size(); i++) {
            redisGenreMap.put(keysArray[i].toString(), values.get(i));
        }

        //edit the hashmap before returning it back to redis
        // if id matches with id in hashmap, add the count 

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

//============================Recommended
    //get Genre board scores
    public List<String> getRec (String name) {
            String keyName = ">" + name;
            HashOperations<String, String, Integer> hashOp = template.opsForHash();

            //call hashmap from redis
            Set<String> keys = hashOp.keys(keyName);
            Object[] keysArray = keys.toArray();
            List<Integer> values = hashOp.values(keyName);
            
            //create hashmap 
            Map<String, Integer> redisGenreMap  = new HashMap<String, Integer>();

            for (int i = 0; i < keys.size(); i++) {
                redisGenreMap.put(keysArray[i].toString(), values.get(i));
            }

            Integer max = Collections.max(redisGenreMap.values());

            List<String> recKeys = new ArrayList<>();
            for (Entry<String, Integer> entry : redisGenreMap.entrySet()) {
                if (entry.getValue()==max) {
                recKeys.add(entry.getKey());
            }
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>This is recKeys movieRepo278: " + recKeys);

        return recKeys;

    }
    


// =============================================================================
// User Account and Validation

    public Boolean[] checkUser (String name, String password) {
        String redisName = "#" + name;
        ValueOperations<String, String> valueOps = template.opsForValue();
        Boolean isExist = template.hasKey(redisName);
        Boolean[] booleanArray = new Boolean[2];
        booleanArray[0] = null;
        booleanArray[1] = null;

        //have name but wrong password

        //have name and correct password

        //dont have name 

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

    public Boolean createAccount (String name, String password) {
        String redisName = "#" + name;
        ValueOperations<String, String> valueOps = template.opsForValue();
        // valueOps.set(redisName, password);
        Boolean isNameTaken = null;
        
        if (template.hasKey(redisName)) {
            return isNameTaken = true;
        } else {
            isNameTaken = false; 
            valueOps.set(redisName, password);
        }
        return isNameTaken;
    }

    

}

