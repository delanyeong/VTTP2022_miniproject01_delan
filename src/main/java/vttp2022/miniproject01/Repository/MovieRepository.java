package vttp2022.miniproject01.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vttp2022.miniproject01.Model.Movie;

@Repository
public class MovieRepository {

    @Autowired
    @Qualifier("redislab")
    private RedisTemplate<String, String> template;
    
    public void save (List<Movie> savedWatchlist) {
        Map<String, String> map = new HashMap<>();
        for (Movie mo : savedWatchlist) {
            map.put(mo.getId(), mo.toJson().toString());
        }
        template.opsForValue().multiSet(map);

        for (String id : map.keySet()) {
            template.expire(id, Duration.ofMinutes(5));
        }
    }

    public void saveList1 (List<Movie> savedWatchList) {
        String keyName = "savedWatchList";
        ListOperations<String, String> listOp = template.opsForList();
        
        for (Movie movie : savedWatchList) {
            listOp.rightPush(keyName, movie.toJson().toString());
        }
    }

    public void saveList2 (List<Movie> savedWatchList) {
        String keyName = "savedWatchList";
        ListOperations<String, String> listOp = template.opsForList();

        List<Movie> redisMovies = new LinkedList<>();

        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create(listOp.index(keyName, i)));
        }

        for (Movie movie : savedWatchList) {
            for (int i = 0; i < redisMovies.size(); i++) {
                if (movie.getId() != redisMovies.get(i).getId()) {
                    redisMovies.add(movie);
                } else {
                    continue;
                }
            }
        }

        long l = listOp.size(keyName);
        if (l > 0) {
            listOp.trim(keyName, 0, l);
        }

        for (Movie movie : redisMovies) {
            listOp.rightPush(keyName, movie.toJson().toString());
        }
    }

    public List<Movie> get () {
        String keyName = "savedWatchList";
        ListOperations<String, String> listOp = template.opsForList();
        List<Movie> redisMovies = new LinkedList<>();

        for (int i = 0; i < listOp.size(keyName); i++) {
            redisMovies.add(Movie.create2(listOp.index(keyName, i)));
        }

        return redisMovies;
    }

}

