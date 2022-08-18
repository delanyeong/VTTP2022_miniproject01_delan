package vttp2022.miniproject01.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.TrendService;

@Controller
@RequestMapping
public class TrendController {
    
    @Autowired
    private TrendService trendSvc; 

    @GetMapping
    public String getTrendMovies (Model model) {
        List<Movie> trendMovieList = trendSvc.getTrendMovies();
        
        model.addAttribute("trendMovieList", trendMovieList);

        return "trendpage";
    }

}