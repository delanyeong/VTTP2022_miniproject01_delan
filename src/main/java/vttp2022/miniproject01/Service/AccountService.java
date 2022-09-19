package vttp2022.miniproject01.Service;

import java.io.Reader;
import java.io.StringReader;
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
import vttp2022.miniproject01.Model.Genre;
import vttp2022.miniproject01.Model.Movie;
import vttp2022.miniproject01.Repository.MovieRepository;

@Service
public class AccountService {
    
    @Autowired
    private MovieRepository movieRepo;

//================================================== 2. Genre API Call ==================================================

    /*
     * API Call from TMDB API for Movie Genre (Genre ID + Genre Desc)
     */

    @Value("${API_KEY}")
    private String key;
    
    private static final String URL = "https://api.themoviedb.org/3/genre/movie/list";

    public List<Genre> setUpGenreScore (String name) {

        //Create the url with query string with ucbuilder
        String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("language", "en-US")
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
        }
        
        //Get the String payload from the payload
        String payload = resp.getBody();
        // System.out.println("payload:" + payload);
        
        Reader strReader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(strReader);
        JsonObject genreListResult = jsonReader.readObject();
        JsonArray genreListData = genreListResult.getJsonArray("genres");
        
        List<Genre> genreList = new LinkedList<>();
        for (int i = 0; i < genreListData.size(); i++) {
            //data[i]
            JsonObject genreDataElem = genreListData.getJsonObject(i);
            
            genreList.add(Genre.createGenre(genreDataElem));
        }

        //call repo to create User Genre Chart when account is created
        // movieRepo.setUpGenreScore(genreList, name);
        return genreList;
    }

 //================================================== 3. Add to Genre Scoreboard ==================================================

    /*
     * Record Genre from Saved Movie for Recommendations
     */
    public void addToGenreList (List<Movie> moviesToSaveList, String name) {
        
        //Create new list to hold the genre Ids extracted from the saved movie
        List<Integer> genreIdList = new LinkedList<>();
        
        //extract genre Ids from each movie in moviesToSaveList
        for (int i = 0; i < moviesToSaveList.size(); i++) {
            for (int j = 0; j < moviesToSaveList.get(i).getGenre_ids().size(); j++) {
                genreIdList.add(moviesToSaveList.get(i).getGenre_ids().get(j));
            }
        }

        //pass extracted list of genre Ids to take score in redis in repo
        movieRepo.addToGenreList(genreIdList, name);
    }

 //================================================== 1. Account Stuff ==================================================

    public Boolean[] checkUser (String name, String password) {
        Boolean[] isExist = movieRepo.checkUser(name, password);
        return isExist;
    }

    public Boolean createAccount (String name, String password) {

        //GENRE
        if (!movieRepo.createAccount(name, password)) {      //if register user is available
            List<Genre> genreList = setUpGenreScore(name);  //get latest Genre List from API
            movieRepo.setUpGenreScore(genreList, name);     //use latest Genre List to create User Score Board
        }
        
        return movieRepo.createAccount(name, password);
    }

}
