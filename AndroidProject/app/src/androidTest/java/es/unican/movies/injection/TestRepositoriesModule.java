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

    @Provides
    @Singleton
    public IMoviesRepository provideMoviesRepository() {
        return new FakeMoviesRepository();
    }
}
