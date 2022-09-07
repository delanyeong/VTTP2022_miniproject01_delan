package vttp2022.miniproject01.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.RecService;

@Controller
@RequestMapping
public class RecController {
    
    @Autowired
    private RecService recSvc;

    @GetMapping(path="recommended")
    public String getRec (Model model, HttpSession sess) {
        List<Movie> recMovieList = recSvc.getRecMovies((String)sess.getAttribute("name"));
        
        
        
        model.addAttribute("trendMovieList", recMovieList);
        return "recommend";
    }

    }




