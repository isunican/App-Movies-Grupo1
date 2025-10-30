package es.unican.movies.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.NonNull;

/**
 * Clase que representa una película guardada en la lista personal del usuario en la base de datos.
 */
@Data
@Entity(tableName = "movies_in_list")
public class MovieInList {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String posterPath;

    /**
     * Estado de la película (Visto, En Proceso, Pendiente).
     */
    private String status;

    /**
     * Valoración de la película (Bien, Normal, Mal).
     */
    private String rating;

}
