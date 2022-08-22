package vttp2022.miniproject01.Controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.MovieService;

@Controller
@RequestMapping 
public class MovieController {

    @Autowired
    private MovieService movieSvc;

    @GetMapping
    public String getTrendMovies (Model model, HttpSession sess) {
        List<Movie> trendMovieList = movieSvc.getTrendMovies();
        sess.setAttribute("movies", trendMovieList);
        model.addAttribute("trendMovieList", trendMovieList);
        return "trendpage";
    }
    
    @GetMapping (path="mywatchlist")
    public String watchlistpage (Model model) {
        List<Movie> watchlistmovies = movieSvc.get();
        model.addAttribute("moviesToSave", watchlistmovies);
        return "watchlistpage";
    }

    @PostMapping (path="savetrend")
    public String postMovies
    (@RequestBody MultiValueMap<String, String> form,
    Model model,
    HttpSession sess) {
        List<Movie> savedMovieList = (List<Movie>)sess.getAttribute("movies"); //takes list of movies saved from Session
        List<String> saveIds = form.get("movieId");                             //gets list of Ids from form 
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + saveIds);
        List<Movie> moviesToSave = savedMovieList.stream()                          //create new list of movies from list of movies based on list of Ids
            .filter(movieObj -> {
                for (String i: saveIds) {
                    if (i.equals(movieObj.getId())) {
                        return true;
                    }
                }
                return false;
            })
            .toList();
        if (moviesToSave.size() > 0) {      //save new list of movies to Repo if it's not empty
            movieSvc.save(moviesToSave);
        }
        return "redirect:/mywatchlist";
    }

}
