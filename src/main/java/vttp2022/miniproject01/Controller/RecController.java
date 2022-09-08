package vttp2022.miniproject01.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(path="recommended")
    public String getRec (Model model, HttpSession sess) {
        List<Movie> recMovieList = recSvc.getRecMovies((String)sess.getAttribute("name"));
        
        String topGenreId = recSvc.getGenreIdsAPI((String)sess.getAttribute("name"));

        List<Genre> genreList = accSvc.setUpGenreScore((String)sess.getAttribute("name"));

        for (Genre genre : genreList) {
            if ((genre.getGenreId().toString()).equals(topGenreId)) {
                model.addAttribute("genreType", genre.getGenreName());
            }
        }
        
        
        model.addAttribute("trendMovieList", recMovieList);
        return "recommend";
    }

    }




