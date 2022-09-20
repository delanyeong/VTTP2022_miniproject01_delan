package vttp2022.miniproject01.Service;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Repository.MovieRepository;

@Service
public class RecService {

    @Autowired
    private MovieRepository movieRepo;

//================================================== Discover (Recommend) API Call ==================================================
    
    @Value("${API_KEY}")
    private String key;
    
    private static final String URL = "https://api.themoviedb.org/3/discover/movie";

    public List<Movie> getRecMovies (String name) {
        //Create the url with query string with ucbuilder
        String url = UriComponentsBuilder.fromUriString(URL)
        // .queryParam("language", "en-US")
        // .queryParam("sort_by", "popularity.desc")
        // .queryParam("include_adult", "false")
        // .queryParam("include_video", "false")
        // .queryParam("page", "1")
        // .queryParam("with_watch_monetization_types", "flatrate")
        .queryParam("with_genres", getGenreIdsAPI(name))
        .queryParam("api_key", key)
        .toUriString();
        
        //Create the GET request - GET <url>
        RequestEntity<Void> req = RequestEntity.get(url).build();
        
        //Make the call to imdb API
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        
        //Check the status code
        if (resp.getStatusCodeValue() != 200) {
            System.err.println("Error status code is not 200");
            return Collections.emptyList();
        }
        
        //Get the String payload from the payload
        String payload = resp.getBody();
        // System.out.println("payload:" + payload);
        
        Reader strReader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(strReader);
        JsonObject recMovieResult = jsonReader.readObject();
        JsonArray recMovieData = recMovieResult.getJsonArray("results");
        
        List<Movie> recMovieList = new LinkedList<>();
        for (int i = 0; i < recMovieData.size(); i++) {
            //data[i]
            JsonObject recDataElem = recMovieData.getJsonObject(i);
            
            recMovieList.add(Movie.create(recDataElem));
        }

        return recMovieList;
    }

//====================================================================================================

    // Top Genre Id
    public String getGenreIdsAPI (String name) {
        List<String> recKeys = movieRepo.getRec(name);
        
        return recKeys.get(0);
    }

    // get your WatchList for indicating already favourited in Recommend page
    public List<Movie> get (String name) {
        return movieRepo.get(name);
    }

    // reset scoreboard
    public void resetScore (String name) {
        movieRepo.resetScore(name);
    }

}
