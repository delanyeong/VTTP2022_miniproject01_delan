package vttp2022.miniproject01.Controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.miniproject01.Model.Genre;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.AccountService;
import vttp2022.miniproject01.Service.RecService;

@Controller
@RequestMapping
public class RecController {
    
    @Autowired
    private RecService recSvc;

    @Autowired
    private AccountService accSvc;

    /*
     * /recommended landing page
     * 1. recommended movies from Discover API
     * 2. filtering to indicate which movies are already favourited
     * 3. showing your genre profile
     */
    @GetMapping(path="recommended")
    public String getRec (Model model, HttpSession sess) {
        List<Movie> recMovieList = recSvc.getRecMovies((String)sess.getAttribute("name"));
        sess.setAttribute("movies", recMovieList);

//====================================================================================================
        // 2. to check Discover api call with your WatchList - for the favourite icon
        List<Movie> watchlistmovies = recSvc.get((String)sess.getAttribute("name"));
        List<Movie> moviesFavourited = recMovieList.stream()                //create new list of movies from list of movies based on list of Ids
            .filter(movieObj -> {
                for (Movie i: watchlistmovies) {
                    if ((i.getId()).equals(movieObj.getId())) {
                        return true;
                    }
                }
                return false;
            })
            .toList();

        List<String> moviesFavouritedId = new LinkedList<>();
        for (Movie movie : moviesFavourited) {
            moviesFavouritedId.add(movie.getId());
        }

        model.addAttribute("moviesFavouritedId", moviesFavouritedId);
//====================================================================================================
        
        // 3. Showing your genre profile
        // receive top genre id
        String topGenreId = recSvc.getGenreIdsAPI((String)sess.getAttribute("name"));
        // reusing this function for the purpose to get genre type names and match with top genre id
        List<Genre> genreList = accSvc.setUpGenreScore((String)sess.getAttribute("name"));
        // matching part then send to MVC model
        for (Genre genre : genreList) {
            if ((genre.getGenreId().toString()).equals(topGenreId)) {
                model.addAttribute("genreType", genre.getGenreName());
            }
        }
        
        
        model.addAttribute("trendMovieList", recMovieList);                     //displaying Discover API movies
        model.addAttribute("name", (String)sess.getAttribute("name"));    //for navbar greeting
        return "recommend";
    }

    /*
     * Favourite Movie
     * endpoint used to forward to original endpoint for saving movies (from searchController to trendController)
     */
    @PostMapping (path="/recommended/savesearch")
    public String forwardToWatchlist (HttpSession sess) {
        sess.setAttribute("page", "recommend"); // for original savemovie endpoint to know where the save is coming from (trend/search)
        return "forward:/home/savetrend";
    }

    /*
     * Reset
     * reset the scoreboard
     */
    @GetMapping (path="/recommended/reset")
    public String resetScore (HttpSession sess) {
        recSvc.resetScore((String)sess.getAttribute("name"));
        return "redirect:/recommended";
    }

}




