package vttp2022.miniproject01.Controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.AccountService;
import vttp2022.miniproject01.Service.MovieService;

@Controller
@RequestMapping 
public class MovieController {

    @Autowired
    private MovieService movieSvc;

    //genre
    @Autowired
    private AccountService accSvc;

    @GetMapping (path="home")
    public String getTrendMovies (Model model, HttpSession sess) {
        
        model.addAttribute("name", (String)sess.getAttribute("name"));
        
        if (null != (String) sess.getAttribute("name")) {
            List<Movie> trendMovieList = movieSvc.getTrendMovies();
            List<Movie> watchlistmovies = movieSvc.get((String)sess.getAttribute("name"));



            List<Movie> moviesFavourited = trendMovieList.stream()                          //create new list of movies from list of movies based on list of Ids
            .filter(movieObj -> {
                for (Movie i: watchlistmovies) {
                    if ((i.getId()).equals(movieObj.getId())) {
                        return true;
                    }
                }
                return false;
            })
            .toList();

            System.out.println("moviesFavourited: " + moviesFavourited.toString());

            List<String> trendMovieListId = new LinkedList<>();
            for (Movie movie : trendMovieList) {
                trendMovieListId.add(movie.getId());
            }

            System.out.println("trendMovieListId: " + trendMovieListId.toString());

            List<String> moviesFavouritedId = new LinkedList<>();
            for (Movie movie : moviesFavourited) {
                moviesFavouritedId.add(movie.getId());
            }

            System.out.println("moviesFavourited2: " + moviesFavouritedId.toString());

            sess.setAttribute("movies", trendMovieList);
            model.addAttribute("trendMovieList", trendMovieList);
            

            model.addAttribute("trendMovieListId", trendMovieListId);
            model.addAttribute("moviesFavouritedId", moviesFavouritedId);
        }



        return "trendpage";
    }

    @GetMapping (path="home/mywatchlist")
    public String watchlistpage (HttpSession sess, Model model) {
        List<Movie> watchlistmovies = movieSvc.get((String)sess.getAttribute("name"));
        model.addAttribute("moviesToSave", watchlistmovies);
        return "watchlistpage";
    }

    @PostMapping (path="home/savetrend")
    public String postMovies
    (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
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

        //GENRE
        accSvc.addToGenreList(moviesToSave, (String)sess.getAttribute("name"));

        System.out.println("THIS IS THE QUERY " + sess.getAttribute("query"));
        System.out.println("THIS IS THE PAGE " + sess.getAttribute("page"));

        if (sess.getAttribute("page").equals("search")) {
            String URL = "/search/movie";
            String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("query", sess.getAttribute("query"))
        .toUriString();
        sess.setAttribute("page", "trend");
        System.out.println("THIS IS THE PAGE " + sess.getAttribute("page"));
        return "redirect:" + url;
        } else {

            // return "redirect:/home/mywatchlist";
            return "redirect:/home";
        }

    }

    //version 1
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

    @GetMapping (path="logout")
    public String logout (HttpSession sess) {
        sess.invalidate();
        return "logout";
    }

}
