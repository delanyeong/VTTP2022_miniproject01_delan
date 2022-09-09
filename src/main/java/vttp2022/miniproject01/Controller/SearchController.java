package vttp2022.miniproject01.Controller;

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
import vttp2022.miniproject01.Service.SearchService;

@Controller
@RequestMapping (path="search")
public class SearchController {

    @Autowired
    private SearchService searchSvc;
    
    @GetMapping
    public String searchPage () {
        return "searchresult";
    }

    @GetMapping (path="movie")
    public String searchMovies
    (@RequestParam String query,
    Model model,
    HttpSession sess) {
        List<Movie> searchMovieList = searchSvc.searchMovies(query);
        sess.setAttribute("movies", searchMovieList);
        model.addAttribute("searchMovieList", searchMovieList);
        model.addAttribute("query", query);

        return "searchresult";
    }
    
    @PostMapping (path="savesearch")
    public String forwardToWatchlist () {
        return "forward:/home/savetrend";
    }

    @GetMapping (path="{id}")
    public String getSearchId (@PathVariable String id) {
        return "forward:/home/{id}";
    }

}
