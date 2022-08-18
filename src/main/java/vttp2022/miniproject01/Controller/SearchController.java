package vttp2022.miniproject01.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.SearchService;

@Controller
@RequestMapping (path="search")
public class SearchController {

    @Autowired
    private SearchService searchSvc;
    
    @GetMapping
    public String searchPage () {
        return "searchpage";
    }

    @GetMapping (path="movie")
    public String searchMovies
    (@RequestParam String query,
    // @RequestParam String language,
    // @RequestParam Boolean include_adult,
    // @RequestParam Integer year,
    Model model) {

        // List<Movie> searchMovieList = searchSvc.searchMovies(language, query, include_adult, year);
        List<Movie> searchMovieList = searchSvc.searchMovies(query);

        model.addAttribute("searchMovieList", searchMovieList);

        return "searchresult";
    }

}
