package vttp2022.miniproject01.Model;

import java.io.StringReader;
import java.math.BigDecimal;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Movie {
    
    private String id;
    private String title;
    private String overview;
    private String poster_path;
    private String release_date;
    
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getOverview() {return overview;}
    public void setOverview(String overview) {this.overview = overview;}

    public String getPoster_path() {return poster_path;}
    public void setPoster_path(String poster_path) {this.poster_path = poster_path;}

    public String getRelease_date() {return release_date;}
    public void setRelease_date(String release_date) {this.release_date = release_date;}


    
    public static Movie create(String jsonStr) {
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);
        return create(reader.readObject());
    }
    
    public static Movie create(JsonObject trendDataElem) {
        Movie mo = new Movie();
        mo.setId(Integer.toString(trendDataElem.getInt("id")));
        mo.setTitle(trendDataElem.getString("title", "Movie Title"));
        mo.setOverview(trendDataElem.getString("overview"));
        mo.setPoster_path(trendDataElem.getString("poster_path"));
        mo.setRelease_date(trendDataElem.getString("release_date", "Release Date"));
        return mo;
    }


    public static Movie create2(String jsonStr) {
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);
        return create2(reader.readObject());
    }
    
    public static Movie create2(JsonObject trendDataElem) {
        Movie mo = new Movie();
        mo.setId(trendDataElem.getString("id"));
        mo.setTitle(trendDataElem.getString("title", "Movie Title"));
        mo.setOverview(trendDataElem.getString("overview"));
        mo.setPoster_path(trendDataElem.getString("poster_path"));
        mo.setRelease_date(trendDataElem.getString("release_date", "Release Date"));
        return mo;
    }

    public JsonObject toJson () {
        return Json.createObjectBuilder()
			.add("id", this.id)
			.add("title", this.title)
			.add("overview", this.overview)
			.add("poster_path", this.poster_path)
			.add("release_date", this.release_date)
			.build();
    }

    

    



}
