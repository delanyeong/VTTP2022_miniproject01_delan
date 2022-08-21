package vttp2022.miniproject01.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Repository.MovieRepository;

@Service
public class WatchService {

    @Autowired
    private MovieRepository movieRepo;

    public void save (List<Movie> savedMovieList) {
        movieRepo.save(savedMovieList);
        movieRepo.saveList1(savedMovieList);
        System.out.println("saved");
    }

    public List<Movie> get () {
        
        return movieRepo.get();
        
    }
    
}
