package vttp2022.miniproject01.Model;

import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private Integer vote_average;
    private Integer vote_count;
    private List<Integer> genre_ids; //genre_ids
    
    public List<Integer> getGenre_ids() {return genre_ids;} //genre_ids
    public void setGenre_ids(List<Integer> genre_ids) {this.genre_ids = genre_ids;} //genre_ids

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

    public Integer getVote_average() {return vote_average;}
    public void setVote_average(Integer vote_average) {this.vote_average = vote_average;}

    public Integer getVote_count() {return vote_count;}
    public void setVote_count(Integer vote_count) {this.vote_count = vote_count;}

    
    //create function for frontend
    public static Movie create(String jsonStr) {
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);
        return create(reader.readObject());
    }
    
    //create function for frontend
    public static Movie create(JsonObject trendDataElem) {
        Movie mo = new Movie();
        mo.setId(Integer.toString(trendDataElem.getInt("id")));
        mo.setTitle(trendDataElem.getString("title", "Movie Title"));
        mo.setOverview(trendDataElem.getString("overview"));
        mo.setPoster_path(trendDataElem.getString("poster_path", "Movie Poster"));
        mo.setRelease_date(trendDataElem.getString("release_date", "Release Date"));
        mo.setVote_average(trendDataElem.getInt("vote_average"));
        mo.setVote_count(trendDataElem.getInt("vote_count"));
        mo.setGenre_ids(getIntList(trendDataElem.getJsonArray("genre_ids"))); //genre_ids
        // System.out.println("this is" + trendDataElem.getJsonArray("genre_ids"));
        return mo;
    }

    //genre_ids
    public static List<Integer> getIntList(JsonArray intJsonArray) {
        List<Integer> genreIntList = new LinkedList<>();
        if (intJsonArray != null) {
            for (int i = 0; i < intJsonArray.size(); i++) {
                // System.out.println("here it is " + intJsonArray.getInt(i));    
                genreIntList.add(intJsonArray.getInt(i));
            }
        } else {
            return Collections.emptyList();
        }
        // System.out.println(genreIntList);
        return genreIntList;
    }

    public static List<Integer> getIntList2(String intJsonArray) {
        List<Integer> genreIntList = new LinkedList<>();
        String[] stringArray = intJsonArray.replaceAll("\\[", "")
                                .replaceAll("]", "")
                                .replaceAll("\\s", "")
                                .split(",");
        int[] arr = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            arr[i] = Integer.valueOf(stringArray[i]);
        }
        for (int i = 0; i < arr.length; i++) {
            genreIntList.add(arr[i]);
        }
        return genreIntList;
    }

    //create function for backend
    public static Movie create2(String jsonStr) {
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);
        return create2(reader.readObject());
    }
    
    //create function for backend
    public static Movie create2(JsonObject trendDataElem) {
        Movie mo = new Movie();
        mo.setId(trendDataElem.getString("id"));
        mo.setTitle(trendDataElem.getString("title", "Movie Title"));
        mo.setOverview(trendDataElem.getString("overview"));
        mo.setPoster_path(trendDataElem.getString("poster_path"));
        mo.setRelease_date(trendDataElem.getString("release_date", "Release Date"));
        mo.setVote_average(trendDataElem.getInt("vote_average"));
        mo.setVote_count(trendDataElem.getInt("vote_count"));
        System.out.println("this is it " + (trendDataElem.getString("genre_ids")));
        mo.setGenre_ids(getIntList2(trendDataElem.getString("genre_ids"))); //genre_ids
        return mo;
    }

    public JsonObject toJson () {
        return Json.createObjectBuilder()
			.add("id", this.id)
			.add("title", this.title)
			.add("overview", this.overview)
			.add("poster_path", this.poster_path)
			.add("release_date", this.release_date)
            .add("vote_average", this.vote_average)
            .add("vote_count", this.vote_count)
            .add("genre_ids", (this.genre_ids).toString()) //genre_ids
			.build();
    }

    

    



}
