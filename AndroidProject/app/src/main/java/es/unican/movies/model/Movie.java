package es.unican.movies.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * A movie using the TMDB data model.
 */
@Getter
@Setter
@Parcel
public class Movie {

    @SerializedName("id")
    protected int id;

    @SerializedName("original_title")
    protected String title;

    @SerializedName("poster_path")
    protected String posterPath;

    @SerializedName("runtime")
    protected int runtime;

    @SerializedName("vote_average")
    protected double voteAverage;

    @SerializedName("vote_count")
    protected int voteCount;

    @SerializedName("release_date")
    protected String releaseDate;

    @SerializedName("genres")
    protected List<Genres> genres;

}
