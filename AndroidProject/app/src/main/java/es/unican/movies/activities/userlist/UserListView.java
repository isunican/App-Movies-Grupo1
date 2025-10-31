package es.unican.movies.activities.userlist;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import es.unican.movies.R;
import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;

/**
 * La UserListView es la actividad que muestra la lista de películas guardadas por el usuario.
 * Implementa la interfaz IUserListContract.View, siguiendo el patrón MVP, y se encarga de
 * la presentación de los datos en la interfaz de usuario. Recibe las actualizaciones del
 * UserListPresenter y las refleja en la pantalla.
 */
@AndroidEntryPoint
public class UserListView extends AppCompatActivity implements IUserListContract.View {

    @Inject
    MovieInListDao movieInListDao; // DAO inyectado para acceder a la BD de la lista de usuario

    @Inject
    IUserListContract.Presenter presenter;

    /**
     * Se llama cuando se crea la actividad. Se encarga de configurar la vista,
     * inyectar las dependencias necesarias y comunicar al presentador que se inicie.
     *
     * @param savedInstanceState Si la actividad se reinicia, este Bundle contiene los datos más recientes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_list_testing);

        // Los datos de ejemplo para pruebas se insertan en un hilo secundario
        new Thread(() -> {
            movieInListDao.deleteAll();

            MovieInList movie1 = new MovieInList();
            movie1.setTitle("Peli Buena");
            movie1.setPosterPath(""); // Ruta vacía para mostrar imagen por defecto
            movie1.setStatus("Visto");
            movie1.setRating("Bueno");
            movieInListDao.insert(movie1);

            MovieInList movie2 = new MovieInList();
            movie2.setTitle("Peli Normal");
            movie2.setPosterPath("");
            movie2.setStatus("En Proceso");
            movie2.setRating("Normal");
            movieInListDao.insert(movie2);

            MovieInList movie3 = new MovieInList();
            movie3.setTitle("Peli Mala");
            movie3.setPosterPath("");
            movie3.setStatus("Pendiente");
            movie3.setRating("Malo");
            movieInListDao.insert(movie3);

            // Inicializa el presentador en el hilo de UI después de insertar los datos
            runOnUiThread(() -> presenter.init(this));
        }).start();
    }

    /**
     * Muestra las películas en un RecyclerView. Este método es llamado por el presentador
     * cuando las películas han sido cargadas exitosamente.
     *
     * @param movies La lista de películas para mostrar.
     */
    @Override
    public void showMovies(List<MovieInList> movies) {
        runOnUiThread(() -> {
            RecyclerView rvMovies = findViewById(R.id.rvMoviesInList);
            rvMovies.setLayoutManager(new LinearLayoutManager(this));
            rvMovies.setAdapter(new UserListAdapter(movies));
        });
    }

    /**
     * Muestra un mensaje de error (Toast) cuando la carga de la lista de películas falla.
     */
    @Override
    public void showLoadError() {
        runOnUiThread(() -> 
            Toast.makeText(this, "Error al cargar la lista", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Muestra un mensaje (Toast) indicando que la lista de películas del usuario está vacía.
     */
    @Override
    public void showEmptyList() {
        runOnUiThread(() ->
            Toast.makeText(this, "Tu lista está vacía", Toast.LENGTH_SHORT).show()
        );
    }
}
