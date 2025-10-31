package es.unican.movies.activities.userlist;

import android.os.Bundle;
import android.widget.Button;
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

        Button btnDeleteAll = findViewById(R.id.btn_delete_all);
        btnDeleteAll.setOnClickListener(v -> presenter.onDeleteAllClicked());

        presenter.init(this);
        presenter.loadMovies();
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
