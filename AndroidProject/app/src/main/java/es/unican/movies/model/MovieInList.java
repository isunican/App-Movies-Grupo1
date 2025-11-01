package es.unican.movies.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "movies_in_list")
public class MovieInList {

    @PrimaryKey
    private int id;

    private String title;

    private String posterPath;

    private String status;

    private String rating;

}
