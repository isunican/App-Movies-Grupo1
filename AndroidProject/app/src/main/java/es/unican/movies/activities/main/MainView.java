package es.unican.movies.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
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
            presenter.onFilterMenuClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterDecade) {
            Toast.makeText(this, "Esta funcionalidad estará disponible próximamente", Toast.LENGTH_SHORT).show();
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
    public void showFilterActivity(List<String> genresWithCount, List<String> selectedGenresSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_genre, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerGenros);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarGenero);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarGenero);

        // 🔹 Aquí guardamos lo que el usuario selecciona ahora
        List<String> selectedGenres = new ArrayList<>();

        for (String genre : genresWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(genre);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            // 🔹 Marcamos los que ya estaban seleccionados
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
            // 🔹 Enviamos al presenter los géneros seleccionados
            presenter.onGenresFiltered(selectedGenres);
            dialog.dismiss();
        });

        dialog.show();
    }




    /*
    @Override
    public void showGenreFilterDialog(List<String> allGenres, List<String> selectedGenres) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar por Género");

        final CharSequence[] genreNames = allGenres.toArray(new CharSequence[0]);
        final boolean[] checkedItems = new boolean[genreNames.length];
        final Set<String> tempSelected = new HashSet<>(selectedGenres);

        for (int i = 0; i < genreNames.length; i++) {
            checkedItems[i] = tempSelected.contains(genreNames[i].toString());
        }

        builder.setMultiChoiceItems(genreNames, checkedItems, (dialog, which, isChecked) -> {
            String genre = genreNames[which].toString();
            if (isChecked) {
                tempSelected.add(genre);
            } else {
                tempSelected.remove(genre);
            }
            AlertDialog d = (AlertDialog) dialog;
            boolean changed = !tempSelected.equals(new HashSet<>(selectedGenres));
            d.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(changed);
        });

        builder.setPositiveButton("APLICAR", (dialog, which) -> {
            presenter.onGenresFiltered(new ArrayList<>(tempSelected));
        });

        builder.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("LIMPIAR", (dialog, which) -> {
            if (!selectedGenres.isEmpty()) {
                presenter.onGenresFiltered(new ArrayList<>());
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(!selectedGenres.isEmpty());
        });

        dialog.show();
    }*/
}
