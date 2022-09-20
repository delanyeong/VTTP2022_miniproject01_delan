package vttp2022.miniproject01.Controller;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.MovieService;
import vttp2022.miniproject01.Service.SearchService;

@Controller
@RequestMapping (path="search")
public class SearchController {

    @Autowired
    private SearchService searchSvc;

    @Autowired
    private MovieService movieSvc;
    
    // @GetMapping
    // public String searchPage () {
    //     return "searchresult";
    // }

    /*
     * /search/movie landing page (after input in search bar from anywhere)
     */
    @GetMapping (path="movie")
    public String searchMovies (@RequestParam String query, Model model, HttpSession sess) {
        List<Movie> searchMovieList = searchSvc.searchMovies(query);
        sess.setAttribute("movies", searchMovieList);
        
        model.addAttribute("searchMovieList", searchMovieList);
        model.addAttribute("query", query); //for UI to show what was searched and use for returning back to the same search results page

//====================================================================================================
        // 2. to check Search api call with your WatchList - for the favourite icon
        List<Movie> watchlistmovies = movieSvc.get((String)sess.getAttribute("name"));
        List<Movie> moviesFavourited = searchMovieList.stream()                //create new list of movies from list of movies based on list of Ids
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
        
        model.addAttribute("name", (String)sess.getAttribute("name")); // for navbar greeting
        return "searchresult";
    }
    
    /*
     * Favourite Movie
     * endpoint used to forward to original endpoint for saving movies (from SearchController to MovieController)
     */
    @PostMapping (path="savesearch")
    public String forwardToWatchlist (@RequestParam String query, HttpSession sess) {
        sess.setAttribute("query", query);          // use for returning back to search result page after saving movie
        sess.setAttribute("page", "search"); // for original savemovie endpoint to know where the save is coming from (trend/search)
        return "forward:/home/savetrend";
    }

    /*
     * @PathVariable
     * endpoint used to forward to original endpoint for @PathVariable (from SearchController to MovieController)
     */
    @GetMapping (path="{id}")
    public String getSearchId (@PathVariable String id) {
        return "forward:/home/{id}";
    }

}
