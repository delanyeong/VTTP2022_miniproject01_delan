package vttp2022.miniproject01.Model;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Genre {
    
    private Integer genreId;
    private String genreName;
    
    public Integer getGenreId() {return genreId;}
    public void setGenreId(Integer genreId) {this.genreId = genreId;}
    
    public String getGenreName() {return genreName;}
    public void setGenreName(String genreName) {this.genreName = genreName;}

    //create function for backend
    public static Genre createGenre(String jsonStr) {
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);
        return createGenre(reader.readObject());
    }
    
    //create function for backend
    public static Genre createGenre(JsonObject genreDataElem) {
        Genre gen = new Genre();
        gen.setGenreId(genreDataElem.getInt("id"));
        gen.setGenreName(genreDataElem.getString("name", "Genre Name"));
        return gen;
    }

    public JsonObject toJson () {
        return Json.createObjectBuilder()
			.add("id", this.genreId)
			.add("name", this.genreName)
			.build();
    }
}
