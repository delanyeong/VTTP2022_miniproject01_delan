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
    public String watchlistpage () {
        return "watchlistpage";
    }

    @PostMapping
    public String postMovies
    (@RequestBody MultiValueMap<String, String> form,
    Model model,
    HttpSession sess) {
        
        List<Movie> savedMovieList = (List<Movie>)sess.getAttribute("movies");

        List<String> saveIds = form.get("movieId");

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + saveIds);

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

        if (moviesToSave.size() > 0) {
            watchSvc.save(moviesToSave);
        }

        model.addAttribute("savedMovieList", savedMovieList);
        return "watchlistpage";

    }

}
