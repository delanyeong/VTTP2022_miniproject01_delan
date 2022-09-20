package vttp2022.miniproject01.Controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Service.MovieService;

@RestController
public class MovieRestController {

    @Autowired
    private MovieService movieSvc;

    /*
     * @PathVariable page (JUST JSON)
     */
    @GetMapping (path="home/mywatchlist/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWatchlistId
    (@PathVariable String id, Model model, HttpSession sess) {
        Optional<Movie> opt = movieSvc.getMovieId(id, (String)sess.getAttribute("name"));

        if (opt.isEmpty()) {
            JsonObject err = Json.createObjectBuilder()
                .add("error", "Cannot find news article %s".formatted(id))
                .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err.toString());
        }

        Movie movie = opt.get();
        return ResponseEntity.ok(movie.toJson().toString());
    }
    
}
