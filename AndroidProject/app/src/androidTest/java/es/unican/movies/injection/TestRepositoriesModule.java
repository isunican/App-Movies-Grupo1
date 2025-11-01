package es.unican.movies.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
import es.unican.movies.service.FakeMoviesRepository;
import es.unican.movies.service.IMoviesRepository;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = RepositoriesModule.class
)
public class TestRepositoriesModule {
    // Clase comentada temporalmente
    // Esta clase se comenta porque inyecta un FakeMoviesRepository de forma global a través de Hilt,
    // pero en los tests instrumentados ya se está inyectando un IMoviesRepository mediante @BindValue
    // de forma individual en cada test (con datos específicos de distintos archivos JSON).
    // Mantener ambas inyecciones provoca conflictos de bindings duplicados en Dagger Hilt.
    // Si en el futuro se decide centralizar la inyección de repositorios de test, se puede reactivar esta clase.

    /*
    @Provides
    @Singleton
    public IMoviesRepository provideMoviesRepository() {
        return new FakeMoviesRepository();
    }

     */
}
