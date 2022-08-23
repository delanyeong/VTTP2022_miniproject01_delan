package vttp2022.miniproject01.Service;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
public class MovieService {

    @Autowired
    private MovieRepository movieRepo;
    
    @Value("${API_KEY}")
    private String key;
    
    private static final String URL = "https://api.themoviedb.org/3/trending/movie/day";

    public List<Movie> getTrendMovies () {
        //Create the url with query string with ucbuilder
        String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("media_type", "movie")
        .queryParam("time_window", "week")
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
        System.out.println("payload:" + payload);
        
        Reader strReader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(strReader);
        JsonObject trendMovieResult = jsonReader.readObject();
        JsonArray trendMovieData = trendMovieResult.getJsonArray("results");
        
        List<Movie> trendMovieList = new LinkedList<>();
        for (int i = 0; i < trendMovieData.size(); i++) {
            //data[i]
            JsonObject trendDataElem = trendMovieData.getJsonObject(i);
            
            trendMovieList.add(Movie.create(trendDataElem));
        }

        return trendMovieList;
    }

    
    public void save (List<Movie> savedMovieList) {
        movieRepo.save(savedMovieList);
        movieRepo.saveList2(savedMovieList);
        System.out.println("saved");
    }
    
    public List<Movie> get () {
        return movieRepo.get();
    }

    public Optional<Movie> getMovieId (String id) {
        return movieRepo.getMovieId(id);
    }
}
