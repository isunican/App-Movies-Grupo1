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

@AndroidEntryPoint
public class UserListView extends AppCompatActivity implements IUserListContract.View {

    @Inject
    MovieInListDao movieInListDao; // DAO para acceder a la BD de la lista de usuario

    @Inject
    IUserListContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_list_testing);

        // Add sample data for testing in a background thread
        new Thread(() -> {
            movieInListDao.deleteAll();

            MovieInList movie1 = new MovieInList();
            movie1.setTitle("Peli Buena");
            movie1.setPosterPath(""); // Empty path to show default image
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

            // Initialize presenter on the UI thread after inserting data
            runOnUiThread(() -> presenter.init(this));
        }).start();
    }

    @Override
    public void showMovies(List<MovieInList> movies) {
        runOnUiThread(() -> {
            RecyclerView rvMovies = findViewById(R.id.rvMoviesInList);
            rvMovies.setLayoutManager(new LinearLayoutManager(this));
            rvMovies.setAdapter(new UserListAdapter(movies));
        });
    }

    @Override
    public void showLoadError() {
        runOnUiThread(() -> 
            Toast.makeText(this, "Error al cargar la lista", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void showEmptyList() {
        runOnUiThread(() ->
            Toast.makeText(this, "Tu lista está vacía", Toast.LENGTH_SHORT).show()
        );
    }
}
