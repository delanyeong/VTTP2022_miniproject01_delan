package vttp2022.miniproject01.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import vttp2022.miniproject01.Model.Movie;

@Service
public class WatchService {

    public void save (List<Movie> savedMovieList) {
        System.out.println("saved");
    }
    
}
