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
 * Módulo de inyección de dependencias para la base de datos de Room.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    /**
     * Provee una instancia única de la base de datos de la aplicación.
     *
     * @param application La aplicación.
     * @return La instancia de la base de datos.
     */
    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(Application application) {
        // Changing the database name to force a recreation
        return Room.databaseBuilder(application, AppDatabase.class, "movies-db-v2")
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Provee una instancia del DAO para las películas en la lista del usuario.
     *
     * @param appDatabase La base de datos de la aplicación.
     * @return El DAO de películas en la lista.
     */
    @Provides
    @Singleton
    public MovieInListDao provideMovieInListDao(AppDatabase appDatabase) {
        return appDatabase.movieInListDao();
    }
}
