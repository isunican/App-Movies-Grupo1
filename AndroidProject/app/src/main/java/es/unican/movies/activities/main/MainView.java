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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.movies.R;
import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

@AndroidEntryPoint // Permite la inyección de dependencias con Hilt
public class MainView extends AppCompatActivity implements IMainContract.View {

    private IMainContract.Presenter presenter; // Referencia al presentador (MVP)

    // Listas para guardar los filtros seleccionados
    List<String> selectedGenres = new ArrayList<>();
    final List<String> selectedDecades = new ArrayList<>();

    @Inject
    IMoviesRepository repository; // Repositorio inyectado por Hilt (fuente de datos)

    private ListView lvMovies; // Lista donde se mostrarán las películas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Carga el layout principal

        // Configuración de la toolbar (barra superior)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializa el presentador y vincula la vista
        presenter = new MainPresenter();
        presenter.init(this);
    }

    // Crea el menú superior (los tres puntos)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu); // Infla el archivo XML del menú
        return true;
    }

    // Maneja los clics en los elementos del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Opción de información
        if (itemId == R.id.menuItemInfo) {
            presenter.onMenuInfoClicked();
            return true;

            // Filtro por género
        } else if (itemId == R.id.menuItemFilterGenre) {
            presenter.onFilterGenreMenuClicked();
            return true;

            // Filtro por década
        } else if (itemId == R.id.menuItemFilterDecade) {
            presenter.onFilterDecadeMenuClicked();
            return true;

            // Botón para limpiar filtros
        } else if (itemId == R.id.menuItemFilterLimpiar) {
            presenter.onLimpiarFiltroMenuClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Inicializa los elementos de la interfaz
    @Override
    public void init() {
        lvMovies = findViewById(R.id.lvMovies);

        // Acción al pulsar una película: mostrar detalles
        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            presenter.onItemClicked((Movie) parent.getItemAtPosition(position));
        });
    }

    // Devuelve el repositorio de películas al presentador
    @Override
    public IMoviesRepository getMoviesRepository() {
        return repository;
    }

    // Muestra la lista de películas en pantalla
    @Override
    public void showMovies(List<Movie> movies) {
        MovieAdapter adapter = new MovieAdapter(this, movies);
        lvMovies.setAdapter(adapter);
    }

    // Muestra un mensaje cuando la carga de películas es correcta
    @Override
    public void showLoadCorrect(int movies) {
        String text = String.format("Se cargaron %d películas", movies);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // Muestra un diálogo de error si falla la carga
    @Override
    public void showLoadError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);

        TextView tvMensajeError = dialogView.findViewById(R.id.tvMensajeError);
        tvMensajeError.setText("Error al cargar las peliculas");

        AlertDialog dialog = builder.create();
        dialog.show();

        // Botón para cerrar el diálogo
        Button btnAceptarError = dialogView.findViewById(R.id.btnAceptarError);
        btnAceptarError.setOnClickListener(v -> dialog.dismiss());
    }

    // Abre la pantalla de detalles de una película
    @Override
    public void showMovieDetails(Movie movie) {
        Intent intent = new Intent(this, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie));
        startActivity(intent);
    }

    // Abre la pantalla de información de la app
    @Override
    public void showInfoActivity() {
        startActivity(new Intent(this, InfoActivity.class));
    }

    /**
     * Este método se encarga de inicializar y desplegar la vista de filtrado por géneros,
     * presentando la lista de géneros disponibles junto con el número de elementos
     * asociados a cada uno. Además, restaura la selección previa del usuario si existiera.
     *
     * @param genresWithCount          Lista de cadenas que representan los géneros disponibles
     *                                 para el filtrado, usualmente en el formato
     *                                 "Género (cantidad)".
     * @param selectedGenresSaved      Lista de géneros que el usuario había seleccionado
     *                                 anteriormente, usada para restaurar el estado del filtro.
     */
    @Override
    public void showFilterByGenreActivity(List<String> genresWithCount, List<String> selectedGenresSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_genre, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerGenros);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarGenero);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarGenero);

        // Usamos una lista temporal para esta sesión del diálogo
        List<String> tempSelected = new ArrayList<>();
        if (selectedGenresSaved != null) {
            tempSelected.addAll(selectedGenresSaved);
        }

        // Guardamos la selección inicial para detectar cambios
        final List<String> initialSelection = new ArrayList<>(tempSelected);

        btnAplicar.setEnabled(false);

        for (String genre : genresWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(genre);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            // Marca los géneros seleccionados
            if (tempSelected.contains(genre)) {
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    tempSelected.add(genre);
                } else {
                    tempSelected.remove(genre);
                }

                // Activamos el botón solo si hubo cambios
                boolean changed = !new HashSet<>(tempSelected).equals(new HashSet<>(initialSelection));
                btnAplicar.setEnabled(changed);
            });

            container.addView(checkBox);
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnAplicar.setOnClickListener(v -> {
            // Actualizamos la lista global
            selectedGenres.clear();
            selectedGenres.addAll(tempSelected);

            presenter.onGenresFiltered(selectedGenres);
            dialog.dismiss();
        });

        dialog.show();
    }


    /**
     * Muestra la actividad o interfaz que permite al usuario filtrar elementos por década
     * Esta función se encarga de inicializar y desplegar la vista que presenta las décadas
     * disponibles para filtrado, junto con el número de elementos asociados a cada una.
     * También marca las décadas que ya han sido seleccionadas previamente por el usuario.
     *
     * @param decadesWithCount         Lista de cadenas que representan las décadas disponibles
     *                                 para filtrado, usualmente en el formato "Década (cantidad)".
     * @param selectedDecadesSaved     Lista de décadas que el usuario había seleccionado
     *                                 anteriormente, usada para restaurar el estado del filtro.
     */
    @Override
    public void showFilterByDecadeActivity(List<String> decadesWithCount, List<String> selectedDecadesSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_decade, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.containerDecadas);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarDecada);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarDecada);

        // Usamos una lista temporal para esta sesión del diálogo
        List<String> tempSelected = new ArrayList<>();
        if (selectedDecadesSaved != null) {
            tempSelected.addAll(selectedDecadesSaved);
        }

        // Guardamos la selección inicial para detectar cambios
        final List<String> initialSelection = new ArrayList<>(tempSelected);

        btnAplicar.setEnabled(false); // Desactivado hasta que haya cambios

        // Crea dinámicamente los CheckBox de cada década
        for (String decade : decadesWithCount) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(decade);
            checkBox.setTextSize(16);
            checkBox.setPadding(8, 8, 8, 8);

            // Marca las décadas ya seleccionadas
            if (tempSelected.contains(decade)) {
                checkBox.setChecked(true);
            }

            // Listener para detectar cambios
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    tempSelected.add(decade);
                } else {
                    tempSelected.remove(decade);
                }

                // Activa "Aplicar" solo si hay cambios reales
                boolean changed = !new HashSet<>(tempSelected).equals(new HashSet<>(initialSelection));
                btnAplicar.setEnabled(changed);
            });

            container.addView(checkBox);
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        // Botón "Cancelar"
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // Botón "Aplicar"
        btnAplicar.setOnClickListener(v -> {
            // Actualizamos la lista global
            selectedDecades.clear();
            selectedDecades.addAll(tempSelected);

            presenter.onDecadesFiltered(selectedDecades);
            dialog.dismiss();
        });

        dialog.show();
    }
}
