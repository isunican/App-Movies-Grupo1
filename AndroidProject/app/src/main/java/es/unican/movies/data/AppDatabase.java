package es.unican.movies.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;

/**
 * Clase de la base de datos Room.
 */
@Database(entities = {MovieInList.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieInListDao movieInListDao();
}
