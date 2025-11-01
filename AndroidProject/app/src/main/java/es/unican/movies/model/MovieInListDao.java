package es.unican.movies.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * Data Access Object (DAO) para las películas en la lista del usuario.
 */
@Dao
public interface MovieInListDao {

    /**
     * Inserta una película en la base de datos. Si la película ya existe, la reemplaza.
     * @param movie La película a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieInList movie);

    /**
     * Obtiene todas las películas de la lista del usuario.
     * @return Una lista con todas las películas.
     */
    @Query("SELECT * FROM movies_in_list")
    List<MovieInList> getAll();

    /**
     * Obtiene una película de la base de datos por su ID.
     * @param movieId El ID de la película a buscar.
     * @return La película si se encuentra, o null si no.
     */
    @Query("SELECT * FROM movies_in_list WHERE id = :movieId")
    MovieInList getMovieById(int movieId);

    /**
     * Elimina una película de la base de datos por su ID.
     * @param movieId El ID de la película a eliminar.
     */
    @Query("DELETE FROM movies_in_list WHERE id = :movieId")
    void deleteMovieById(int movieId);

    /**
     * Elimina todas las películas de la tabla.
     */
    @Query("DELETE FROM movies_in_list")
    void deleteAll();
}
