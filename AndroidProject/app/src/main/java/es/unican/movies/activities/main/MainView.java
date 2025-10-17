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

/**
 * Clase que representa la vista principal de la aplicación.
 * Muestra la lista de películas y gestiona la interacción del usuario.
 * Esta clase sigue el patrón de arquitectura MVP (Model-View-Presenter), donde
 * actúa como la Vista (View).
 */
@AndroidEntryPoint
public class MainView extends AppCompatActivity implements IMainContract.View {

    // Contrato para la comunicación con el Presenter
    private IMainContract.Presenter presenter;

    // Repositorio de películas inyectado por Hilt
    @Inject
    IMoviesRepository repository;

    // Elemento de la UI para mostrar la lista de películas
    private ListView lvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración de la barra de herramientas (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicialización del presenter y enlace con esta vista
        presenter = new MainPresenter();
        presenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú de opciones en la barra de herramientas
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Maneja las selecciones de los ítems del menú
        int itemId = item.getItemId();
        if (itemId == R.id.menuItemInfo) {
            // El usuario ha pulsado el ítem de información
            presenter.onMenuInfoClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterGenre) {
            // El usuario ha pulsado el ítem de filtrar por género
            presenter.onFilterGenreMenuClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterDecade) {
<<<<<<< HEAD
            // Funcionalidad no implementada todavía
            Toast.makeText(this, "Esta funcionalidad estará disponible próximamente", Toast.LENGTH_SHORT).show();
=======
            presenter.onFilterMenuClicked();
>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void init() {
        // Inicializa la ListView y configura el listener para los clics en sus elementos
        lvMovies = findViewById(R.id.lvMovies);
        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            presenter.onItemClicked((Movie) parent.getItemAtPosition(position));
        });
    }

    @Override
    public IMoviesRepository getMoviesRepository() {
        // Proporciona al presenter el repositorio de datos
        return repository;
    }

    @Override
    public void showMovies(List<Movie> movies) {
        // Muestra la lista de películas en la ListView usando un adaptador personalizado
        MovieAdapter adapter = new MovieAdapter(this, movies);
        lvMovies.setAdapter(adapter);
    }

    @Override
    public void showLoadCorrect(int movies) {
        // Muestra un mensaje Toast de éxito indicando cuántas películas se han cargado
        String text = String.format("Se cargaron %d películas", movies);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadError() {
        // Muestra un mensaje Toast de error si falla la carga de películas
        Toast.makeText(this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMovieDetails(Movie movie) {
        // Abre la vista de detalles para la película seleccionada
        Intent intent = new Intent(this, DetailsView.class);
        // Envuelve el objeto Movie para pasarlo a través del Intent
        intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie));
        startActivity(intent);
    }

    @Override
    public void showInfoActivity() {
        // Abre la actividad de información de la aplicación
        startActivity(new Intent(this, InfoActivity.class));
    }
    
    /**
     * Muestra un diálogo de alerta para que el usuario pueda filtrar las películas por género.
     * @param genresWithCount Lista de géneros disponibles, con el conteo de películas para cada uno.
     * @param selectedGenresSaved Lista de géneros que el usuario había seleccionado previamente.
     */
    @Override
<<<<<<< HEAD
    public void showFilterByGenreActivity(List<String> genresWithCount, List<String> selectedGenresSaved) {
=======
    public void showFilterGenresDialog(List<String> genresWithCount, List<String> selectedGenresSaved) {
        //Cargar el layout del diálogo
>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_genre, null);
        builder.setView(dialogView);

        //Obtener los elementos del layout
        LinearLayout container = dialogView.findViewById(R.id.containerGenros);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarGenero);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarGenero);

<<<<<<< HEAD
        // Lista para guardar los géneros que el usuario selecciona en este diálogo
        List<String> selectedGenres = new ArrayList<>();

        // Crea un CheckBox para cada género disponible
=======
        //Guardar lo que el usuario selecciona ahora
        List<String> selectedGenres = new ArrayList<>();

        //Recorre todos los géneros disponibles
>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8
        for (String genre : genresWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(genre);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            // Si el género ya estaba seleccionado, lo marca en la UI
            if (selectedGenresSaved != null && selectedGenresSaved.contains(genre)) {
                checkBox.setChecked(true);
                selectedGenres.add(genre);
            }

            // Listener para actualizar la lista de géneros seleccionados cuando el usuario
            // marca o desmarca una casilla
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

        // Configura el botón de cancelar para cerrar el diálogo
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // Configura el botón de aplicar para notificar al presenter con los filtros seleccionados
        btnAplicar.setOnClickListener(v -> {
            presenter.onGenresFiltered(selectedGenres);
            dialog.dismiss();
        });

        dialog.show();
    }
<<<<<<< HEAD
=======

    @Override
    public void showFilterDecadesDialog(List<String> decadesWithCount, List<String> selectedDecadesSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_decade, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerDecadas);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarDecada);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarDecada);

        //Aquí guardamos lo que el usuario selecciona ahora
        List<String> selectedGenres = new ArrayList<>();

        for (String genre : decadesWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(genre);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            //Marcamos los que ya estaban seleccionados
            if (selectedDecadesSaved != null && selectedDecadesSaved.contains(genre)) {
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

>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8
}
