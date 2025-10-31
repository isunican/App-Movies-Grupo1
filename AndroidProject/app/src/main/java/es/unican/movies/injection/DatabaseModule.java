package es.unican.movies.injection;

import android.app.Application;
import androidx.room.Room;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import es.unican.movies.data.AppDatabase;
import es.unican.movies.model.MovieInListDao;

/**
 * Módulo de Hilt para proveer la instancia de la base de datos Room y el DAO.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "movies-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public MovieInListDao provideMovieInListDao(AppDatabase database) {
        return database.movieInListDao();
    }

}
