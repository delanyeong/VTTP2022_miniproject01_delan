package vttp2022.miniproject01.Controller;

import java.util.LinkedList;
import java.util.List;
// import java.util.Optional;

import javax.servlet.http.HttpSession;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

// import jakarta.json.Json;
// import jakarta.json.JsonObject;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.AccountService;
import vttp2022.miniproject01.Service.MovieService;

@Controller
@RequestMapping 
public class MovieController {

    @Autowired
    private MovieService movieSvc;

    //GENRE
    @Autowired
    private AccountService accSvc;

    /*
     * /home landing page - first landing page
     * 1. trending movies from Trending API
     * 2. filtering to indicate which movies are already favourited
     * 
     */
    @GetMapping (path="home")
    public String getTrendMovies (Model model, HttpSession sess) {
        
        model.addAttribute("name", (String)sess.getAttribute("name")); //for navbar greeting

//====================================================================================================
        // 2. (RIGHT AFTER LOGGING IN) to check Trending api call with your WatchList - for the favourite icon
        
        if (null != (String)sess.getAttribute("name")) { // if user is logged in
            List<Movie> trendMovieList = movieSvc.getTrendMovies(); // Trending API movies
            List<Movie> watchlistmovies = movieSvc.get((String)sess.getAttribute("name")); // your WatchList movies

            /*
             * Comparing with trending API List<Movie>.getId() for fave icon - PART 1
             */
            List<Movie> moviesFavourited = trendMovieList.stream() 
            .filter(movieObj -> {
                for (Movie i: watchlistmovies) {
                    if ((i.getId()).equals(movieObj.getId())) {
                        return true;
                    }
                }
                return false;
            })
            .toList();
            System.out.println("moviesFavourited: " + moviesFavourited.toString()); //Java Object name of movies favourited

            /*
             * DONT REALLY NEED JUST FOR CHECKING
             */
            List<String> trendMovieListId = new LinkedList<>();
            for (Movie movie : trendMovieList) {
                trendMovieListId.add(movie.getId());
            }
            System.out.println("trendMovieListId: " + trendMovieListId.toString()); //id of movies from Trending API

            /*
             * Comparing with trending API List<Movie>.getId() for fave icon - PART 2
             */
            List<String> moviesFavouritedId = new LinkedList<>();
            for (Movie movie : moviesFavourited) {
                moviesFavouritedId.add(movie.getId()); 
            }
            System.out.println("moviesFavourited2: " + moviesFavouritedId.toString()); //id of movies favourited

//====================================================================================================

            sess.setAttribute("movies", trendMovieList); // MIGHT BE NEEDED DONT DELETE - to be used for filtering articles to show saveArticles from all articles

            model.addAttribute("trendMovieList", trendMovieList); // Populating page with trending movies at Trending page
            model.addAttribute("trendMovieListId", trendMovieListId); // DONT REALLY NEED JUST FOR CHECKING
            model.addAttribute("moviesFavouritedId", moviesFavouritedId); // Comparing with trending API List<Movie>.getId() for icon - PART 3
        }

        return "trendpage";
    }

    /*
     * WatchList page
     */
    @GetMapping (path="home/mywatchlist")
    public String watchlistpage (HttpSession sess, Model model) {
        List<Movie> watchlistmovies = movieSvc.get((String)sess.getAttribute("name"));
        model.addAttribute("moviesToSave", watchlistmovies);
        model.addAttribute("name", (String)sess.getAttribute("name"));
        return "watchlistpage";
    }

    /*
     * Favourite Movie
     * button click will send movie Id to here and save in a List<String> but only contain one element
     */
    @PostMapping (path="home/savetrend")
    public String postMovies
    (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

//====================================================================================================
        // 2. (subsequent saves) to check Trending api call with your WatchList - for the favourite icon
        List<Movie> savedMovieList = (List<Movie>)sess.getAttribute("movies"); //takes list of movies saved from Session
        List<String> saveIds = form.get("movieId");                             //gets list of Ids from form 
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> saveIds: " + saveIds);
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
        if (moviesToSave.size() > 0) {                                              //save new list of movies to Repo if it's not empty
            movieSvc.save(moviesToSave, (String)sess.getAttribute("name"));
        }
//====================================================================================================

        //GENRE - when favouriting, add to scoreboard
        accSvc.addToGenreList(moviesToSave, (String)sess.getAttribute("name"));

        //To determine where to return (Trending/Search/Recommended)
        System.out.println("THIS IS THE QUERY " + sess.getAttribute("query")); //for Search page
        System.out.println("THIS IS THE PAGE " + sess.getAttribute("page"));

        if (sess.getAttribute("page").equals("search")) {

            String URL = "/search/movie"; // CASE 1: Search page - return to it by building the query string url again from the keyword searched
            String url = UriComponentsBuilder.fromUriString(URL)
                .queryParam("query", sess.getAttribute("query"))
                .toUriString();
            sess.setAttribute("page", "trend"); //reset to return to default Trending Page
            System.out.println("THIS IS THE PAGE " + sess.getAttribute("page"));
            return "redirect:" + url;

        } else if (sess.getAttribute("page").equals("recommend")) { //CASE 2: Recommended page
            sess.setAttribute("page", "trend"); //reset to return to default Trending Page
            return "redirect:/recommended";
        } else {

            // return "redirect:/home/mywatchlist";
            return "redirect:/home"; //CASE 3: (default) Trending page
        }

    }

    //version 1 - Changed for returning jsonstring by Rest Controller in MovieRestController
    // @GetMapping (path="home/mywatchlist/{id}")
    // public String getWatchlistId
    // (@PathVariable String id, Model model, HttpSession sess) {
    //     Optional<Movie> opt = movieSvc.getMovieId(id, (String)sess.getAttribute("name"));

    //     if (opt.isEmpty()) {
    //         String errorMsg = "Cannot find article %s".formatted(id);
    //         model.addAttribute("errormsg", errorMsg);
    //     } else {
    //         Movie movieById = opt.get();
    //         model.addAttribute("singlecontent", movieById);
    //     }

    //     return "singlecontent";
    // }


    /*
     * @PathVariable page (NOT just JSON)
     */
    @GetMapping (path="home/{id}")
    public String getMovieId
    (@PathVariable String id, Model model, HttpSession sess) {
        List<Movie> savedMovieList = (List<Movie>)sess.getAttribute("movies"); //takes list of movies saved from Session
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> id: " + id);
        List<Movie> singleMovie = savedMovieList.stream()                          //create new list of movies from list of movies based on list of Ids
            .filter(movieObj -> {
                    if (id.equals(movieObj.getId())) {
                        return true;
                    }
                return false;
            })
            .toList();
        model.addAttribute("singlecontent", singleMovie.get(0));

        model.addAttribute("name", (String)sess.getAttribute("name"));

        return "singlecontent";
    }

    /*
     * Delete
     */
    @GetMapping (path="home/mywatchlist/delete/{id}")
    public String getWatchlistId
    (@PathVariable String id, Model model, HttpSession sess) {

        movieSvc.delete(id, (String)sess.getAttribute("name"));

        // Optional<Movie> opt = movieSvc.getMovieId(id, (String)sess.getAttribute("name"));

        // if (opt.isEmpty()) {
        //     String errorMsg = "Cannot find article %s".formatted(id);
        //     model.addAttribute("errormsg", errorMsg);
        // } else {
        //     Movie movieById = opt.get();
        //     model.addAttribute("singlecontent", movieById);
        // }

        return "redirect:/home/mywatchlist";
    }

    /*
     * Logout
     */
    @GetMapping (path="logout")
    public String logout (HttpSession sess, Model model) {
        model.addAttribute("name", (String)sess.getAttribute("name"));
        sess.invalidate();
        return "logout";
    }

}
