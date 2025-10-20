package es.unican.movies.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.movies.R;
import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

@AndroidEntryPoint
public class MainView extends AppCompatActivity implements IMainContract.View {

    private IMainContract.Presenter presenter;

    @Inject
    IMoviesRepository repository;

    private ListView lvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new MainPresenter();
        presenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuItemInfo) {
            presenter.onMenuInfoClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterGenre) {
            presenter.onFilterGenreMenuClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterDecade) {
            presenter.onFilterDecadeMenuClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void init() {
        lvMovies = findViewById(R.id.lvMovies);
        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            presenter.onItemClicked((Movie) parent.getItemAtPosition(position));
        });
    }

    @Override
    public IMoviesRepository getMoviesRepository() {
        return repository;
    }

    @Override
    public void showMovies(List<Movie> movies) {
        MovieAdapter adapter = new MovieAdapter(this, movies);
        lvMovies.setAdapter(adapter);
    }

    @Override
    public void showLoadCorrect(int movies) {
        String text = String.format("Se cargaron %d películas", movies);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMovieDetails(Movie movie) {
        Intent intent = new Intent(this, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie));
        startActivity(intent);
    }

    @Override
    public void showInfoActivity() {
        startActivity(new Intent(this, InfoActivity.class));
    }

    @Override
    public void showFilterByGenreActivity(List<String> genresWithCount, List<String> selectedGenresSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_genre, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerGenros);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarGenero);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarGenero);

        List<String> selectedGenres = new ArrayList<>();

        for (String genre : genresWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(genre);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            if (selectedGenresSaved != null && selectedGenresSaved.contains(genre)) {
                checkBox.setChecked(true);
                selectedGenres.add(genre);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedGenres.add(genre);
                } else {
                    selectedGenres.remove(genre);
                }
            });

            container.addView(checkBox);
        }

        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnAplicar.setOnClickListener(v -> {
            presenter.onGenresFiltered(selectedGenres);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void showFilterByDecadeActivity(List<String> decadesWithCount, List<String> selectedDecadesSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_decade, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerDecadas);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarDecada);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarDecada);

        final List<String> selectedDecades = new ArrayList<>();

        for (String decade : decadesWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(decade);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            if (selectedDecadesSaved != null && selectedDecadesSaved.contains(decade)) {
                checkBox.setChecked(true);
                selectedDecades.add(decade);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDecades.add(decade);
                } else {
                    selectedDecades.remove(decade);
                }
            });
            container.addView(checkBox);
        }

        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnAplicar.setOnClickListener(v -> {
            presenter.onDecadesFiltered(selectedDecades);
            dialog.dismiss();
        });

        dialog.show();
    }
}
