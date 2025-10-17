package es.unican.movies.activities.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.movies.R;
import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

@AndroidEntryPoint
public class MainView extends AppCompatActivity implements IMainContract.View {

    // Referencia al presentador (parte del patrón MVP)
    private IMainContract.Presenter presenter;

    // Repositorio de películas inyectado con Hilt (inyección de dependencias)
    @Inject
    IMoviesRepository repository;

    // ListView donde se mostrarán las películas
    private ListView lvMovies;

    // ---------------------------------------------------------------------------------------------
    // MÉTODO onCreate: punto de entrada de la actividad
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura la toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Crea el presentador y lo inicializa con la vista actual
        presenter = new MainPresenter();
        presenter.init(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Crea el menú de opciones (tres puntos o icono de filtro)
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        // Infla el archivo XML del menú (res/menu/menu.xml)
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    // Maneja los clics en los ítems del menú superior
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemInfo) {
            // Abre la pantalla de información
            presenter.onMenuInfoClicked();
            return true;

        } else if (itemId == R.id.menuItemFilterGenre) {
            // Abre el diálogo de filtros por género
            presenter.onFilterMenuClicked();
            return true;

        } else if (itemId == R.id.menuItemFilterDecade) {
            // Muestra un mensaje indicando que la función no está disponible aún
            Toast.makeText(this, "Esta funcionalidad estará disponible próximamente", Toast.LENGTH_SHORT).show();
            return true;

        } else if (itemId == R.id.menuItemFilterLimpiar) {
            // Limpia todos los filtros de género y recarga las películas
            presenter.onGenresFiltered(new ArrayList<>());
            Toast.makeText(this, "Filtros de género limpiados", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------------------------------
    // Inicializa los componentes de la vista
    // ---------------------------------------------------------------------------------------------
    @Override
    public void init() {
        lvMovies = findViewById(R.id.lvMovies);

        // Configura un listener para detectar clics en los ítems del ListView
        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            // Notifica al presentador qué película se seleccionó
            presenter.onItemClicked((Movie) parent.getItemAtPosition(position));
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Retorna el repositorio de películas (inyectado con Hilt)
    // ---------------------------------------------------------------------------------------------
    @Override
    public IMoviesRepository getMoviesRepository() {
        return repository;
    }

    // ---------------------------------------------------------------------------------------------
    // Muestra la lista de películas en pantalla
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showMovies(List<Movie> movies) {
        MovieAdapter adapter = new MovieAdapter(this, movies);
        lvMovies.setAdapter(adapter);
    }

    // ---------------------------------------------------------------------------------------------
    // Muestra un mensaje cuando las películas se cargan correctamente
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showLoadCorrect(int movies) {
        String text = String.format("Se cargaron %d películas", movies);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // ---------------------------------------------------------------------------------------------
    // Muestra un mensaje cuando ocurre un error al cargar las películas
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
    }

    // ---------------------------------------------------------------------------------------------
    // Abre la vista de detalles de una película seleccionada
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showMovieDetails(Movie movie) {
        Intent intent = new Intent(this, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie)); // Se pasa la película como objeto parcelable
        startActivity(intent);
    }

    // ---------------------------------------------------------------------------------------------
    // Abre la actividad de información (InfoActivity)
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showInfoActivity() {
        startActivity(new Intent(this, InfoActivity.class));
    }

    // ---------------------------------------------------------------------------------------------
    // Muestra un diálogo para filtrar las películas por género
    // ---------------------------------------------------------------------------------------------
    @Override
    public void showGenreFilterDialog(List<String> allGenres, List<String> selectedGenres) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar por Género");

        // Convierte la lista de géneros a un arreglo de CharSequence para usarlo en el diálogo
        final CharSequence[] genreNames = allGenres.toArray(new CharSequence[0]);
        final boolean[] checkedItems = new boolean[genreNames.length];

        // Usa un Set temporal para almacenar los géneros seleccionados
        final Set<String> tempSelected = new HashSet<>(selectedGenres);

        // Marca los checkboxes según los géneros actualmente seleccionados
        for (int i = 0; i < genreNames.length; i++) {
            checkedItems[i] = tempSelected.contains(genreNames[i].toString());
        }

        // Listener para los checkboxes del diálogo
        builder.setMultiChoiceItems(genreNames, checkedItems, (dialog, which, isChecked) -> {
            String genre = genreNames[which].toString();

            // Agrega o quita el género de la lista temporal según el estado del checkbox
            if (isChecked) {
                tempSelected.add(genre);
            } else {
                tempSelected.remove(genre);
            }

            // Habilita el botón "Aplicar" solo si hubo cambios
            AlertDialog d = (AlertDialog) dialog;
            boolean changed = !tempSelected.equals(new HashSet<>(selectedGenres));
            d.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(changed);
        });

        // Botón "Aplicar" → notifica al presentador con los géneros seleccionados
        builder.setPositiveButton("APLICAR", (dialog, which) -> {
            presenter.onGenresFiltered(new ArrayList<>(tempSelected));
        });

        // Botón "Cancelar" → cierra el diálogo sin aplicar cambios
        builder.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());

        // Crea el diálogo
        AlertDialog dialog = builder.create();

        // Cuando se muestra el diálogo, el botón "Aplicar" empieza deshabilitado
        dialog.setOnShowListener(d -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });

        dialog.show();
    }
}

