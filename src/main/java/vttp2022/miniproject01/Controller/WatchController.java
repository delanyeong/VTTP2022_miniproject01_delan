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
import vttp2022.miniproject01.Service.WatchService;

@Controller
@RequestMapping (path="watchlist")
public class WatchController {

    @Autowired
    private WatchService watchSvc;
    
    @GetMapping
    public String watchlistpage (Model model) {

        List<Movie> watchlistmovies = watchSvc.get();

        model.addAttribute("moviesToSave", watchlistmovies);
        return "watchlistpage";
    }

    @PostMapping (path="recentsave")
    public String postMovies
    (@RequestBody MultiValueMap<String, String> form,
    Model model,
    HttpSession sess) {
        
        //takes list of movies saved from Session
        List<Movie> savedMovieList = (List<Movie>)sess.getAttribute("movies");

        //gets list of Ids from form 
        List<String> saveIds = form.get("movieId");

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + saveIds);

        //create new list of movies from list of movies based on list of Ids
        List<Movie> moviesToSave = savedMovieList.stream()
            .filter(movieObj -> {
                for (String i: saveIds) {
                    if (i.equals(movieObj.getId())) {
                        return true;
                    }
                }
                return false;
            })
            .toList();

        //save new list of movies to Repo if it's not empty
        if (moviesToSave.size() > 0) {
            watchSvc.save(moviesToSave);
        }

        //return new list of movies to MVC model
        model.addAttribute("moviesToSave", moviesToSave);
        return "recentsave";

    }

}
