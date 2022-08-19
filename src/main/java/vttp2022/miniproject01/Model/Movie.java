package vttp2022.miniproject01.Model;

import java.math.BigDecimal;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class Movie {
    
    // private Boolean adult;
    // private String backdrop_path;
    private String id;
    private String title;
    // private String original_language;
    // private String original_title;
    private String overview;
    private String poster_path;
    // private String media_type;
    // private Integer[] genre_ids;
    // private BigDecimal popularity;
    private String release_date;
    // private Boolean video;
    // private BigDecimal vote_average;
    // private Integer vote_count;

    // public Boolean getAdult() {return adult;}
    // public void setAdult(Boolean adult) {this.adult = adult;}

    // public String getBackdrop_path() {return backdrop_path;}
    // public void setBackdrop_path(String backdrop_path) {this.backdrop_path = backdrop_path;}
    
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    // public String getOriginal_language() {return original_language;}
    // public void setOriginal_language(String original_language) {this.original_language = original_language;}

    // public String getOriginal_title() {return original_title;}
    // public void setOriginal_title(String original_title) {this.original_title = original_title;}

    public String getOverview() {return overview;}
    public void setOverview(String overview) {this.overview = overview;}

    public String getPoster_path() {return poster_path;}
    public void setPoster_path(String poster_path) {this.poster_path = poster_path;}

    // public String getMedia_type() {return media_type;}
    // public void setMedia_type(String media_type) {this.media_type = media_type;}

    // public Integer[] getGenre_ids() {return genre_ids;}
    // public void setGenre_ids(Integer[] genre_ids) {this.genre_ids = genre_ids;}

    // public BigDecimal getPopularity() {return popularity;}
    // public void setPopularity(BigDecimal bigDecimal) {this.popularity = bigDecimal;}

    public String getRelease_date() {return release_date;}
    public void setRelease_date(String release_date) {this.release_date = release_date;}

    // public Boolean getVideo() {return video;}
    // public void setVideo(Boolean video) {this.video = video;}

    // public BigDecimal getVote_average() {return vote_average;}
    // public void setVote_average(BigDecimal bigDecimal) {this.vote_average = bigDecimal;}

    // public Integer getVote_count() {return vote_count;}
    // public void setVote_count(Integer vote_count) {this.vote_count = vote_count;}

    public static Movie create(JsonObject trendDataElem) {

        // Integer[] genreIds = new Integer[5];
        // for (int i = 0; i < genre_ids.size(); ++i) {
        //     genreIds[i] = genre_ids.getInt(i);
        // }

        Movie mo = new Movie();
        // mo.setAdult(trendDataElem.getBoolean("adult"));
        // mo.setBackdrop_path(trendDataElem.getString("backdrop_path"));
        mo.setId(Integer.toString(trendDataElem.getInt("id")));
        mo.setTitle(trendDataElem.getString("title", "Movie Title"));
        // mo.setOriginal_language(trendDataElem.getString("original_language"));
        // mo.setOriginal_title(trendDataElem.getString("original_title"));
        mo.setOverview(trendDataElem.getString("overview"));
        mo.setPoster_path(trendDataElem.getString("poster_path"));
        // mo.setMedia_type(trendDataElem.getString("media_type"));
        // mo.setGenre_ids(genreIds); 

        // mo.setPopularity(Double.parseDouble(trendDataElem.getString("popularity")));

        // mo.setPopularity(trendDataElem.getJsonNumber("popularity").bigDecimalValue());

        mo.setRelease_date(trendDataElem.getString("release_date", "Release Date"));
        // mo.setVideo(trendDataElem.getBoolean("video"));

        // mo.setVote_average(Double.parseDouble(trendDataElem.getString("vote_average")));

        // mo.setVote_average(trendDataElem.getJsonNumber("vote_average").bigDecimalValue());


        // mo.setVote_count(trendDataElem.getInt("vote_count"));



        return mo;
    }
    @Override
    public String toString() {
        return "Movie [id=" + id + ", overview=" + overview + ", poster_path=" + poster_path + ", release_date="
                + release_date + ", title=" + title + "]";
    }

    

    



}
