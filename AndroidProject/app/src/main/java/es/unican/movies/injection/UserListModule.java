package es.unican.movies.injection;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import es.unican.movies.activities.userlist.IUserListContract;
import es.unican.movies.activities.userlist.UserListPresenter;

/**
 * Módulo de Hilt para proveer la implementación del presentador de la lista de usuario.
 */
@Module
@InstallIn(ActivityComponent.class)
public abstract class UserListModule {

    @Binds
    public abstract IUserListContract.Presenter bindUserListPresenter(UserListPresenter presenter);

}
