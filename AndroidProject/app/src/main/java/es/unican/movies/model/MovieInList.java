package es.unican.movies.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity(tableName = "movies_in_list")
/**
 * Clase sencilla para representar una película en la lista de un usuario.
 * Usa Lombok para generar getters y setters.
 */
@Getter
@Setter
public class MovieInList {

    @PrimaryKey
    private int id;

    private String title;

    private String posterPath;

    private String status;

    private String rating;

}
