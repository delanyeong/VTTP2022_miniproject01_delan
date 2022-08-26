package vttp2022.miniproject01.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
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
        valueOps.set(redisName, password);
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

