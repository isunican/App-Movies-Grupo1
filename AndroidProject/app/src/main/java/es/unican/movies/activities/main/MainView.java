package es.unican.movies.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.movies.R;
import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.activities.userlist.UserListView;
import es.unican.movies.model.Movie;
import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;
import es.unican.movies.service.EImageSize;
import es.unican.movies.service.IMoviesRepository;
import es.unican.movies.service.ITmdbApi;

@AndroidEntryPoint // Permite la inyección de dependencias con Hilt
public class MainView extends AppCompatActivity implements IMainContract.View {

    private IMainContract.Presenter presenter; // Referencia al presentador (MVP)
    private String selectedRating; // used for the dialog

    // Listas para guardar los filtros seleccionados
    List<String> selectedGenres = new ArrayList<>();
    final List<String> selectedDecades = new ArrayList<>();

    @Inject
    IMoviesRepository repository; // Repositorio inyectado por Hilt (fuente de datos)

    @Inject
    MovieInListDao movieInListDao; // DAO para la lista de usuario

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
        } else if (itemId == R.id.btn_lista) {
            startActivity(new Intent(this, UserListView.class));
            return true;
        } else if (itemId == R.id.menuItemFilterGenre) {
            presenter.onFilterGenreMenuClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterDecade) {
            presenter.onFilterDecadeMenuClicked();
            return true;
        } else if (itemId == R.id.menuItemFilterLimpiar) {
            // Limpia ambos filtros
            selectedGenres.clear();
            selectedDecades.clear();
            // Notifica al presenter
            presenter.onGenresFiltered(selectedGenres);
            presenter.onDecadesFiltered(selectedDecades);
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
        MovieAdapter adapter = new MovieAdapter(this, movies, movieInListDao, this);
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
        dialog.setCanceledOnTouchOutside(true);

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
        dialog.setCanceledOnTouchOutside(true);

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

    /**
     * Muestra un diálogo que permite al usuario añadir una película a su lista personal.
     * El diálogo recoge el estado de visualización (ej. "Visto", "Pendiente") y una
     * valoración personal (ej. "Bueno", "Normal", "Malo").
     *
     * Al confirmar, crea un objeto {@link MovieInList} y lo inserta en la base de datos
     * a través del DAO {@link MovieInListDao}. La operación de inserción se realiza en
     * un hilo secundario para no bloquear la interfaz de usuario.
     *
     * @param movie La película que se va a añadir a la lista.
     */
    public void showAddToListDialog(Movie movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_to_list, null);
        builder.setView(dialogView);

        // Get views from dialog
        ImageView ivDialogPoster = dialogView.findViewById(R.id.ivDialogPoster);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        TextView tvEmojiHappy = dialogView.findViewById(R.id.tvEmojiHappy);
        TextView tvEmojiNeutral = dialogView.findViewById(R.id.tvEmojiNeutral);
        TextView tvEmojiSad = dialogView.findViewById(R.id.tvEmojiSad);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnApply = dialogView.findViewById(R.id.btnApply);

        // Set movie data
        tvDialogTitle.setText(movie.getTitle());
        String imageUrl = ITmdbApi.getFullImagePath(movie.getPosterPath(), EImageSize.W154);
        Picasso.get().load(imageUrl).into(ivDialogPoster);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Visto", "A medias", "Pendiente"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Setup Emojis
        selectedRating = null; // Reset rating
        final float selectedAlpha = 1.0f;
        final float unselectedAlpha = 0.3f;

        tvEmojiHappy.setAlpha(unselectedAlpha);
        tvEmojiNeutral.setAlpha(unselectedAlpha);
        tvEmojiSad.setAlpha(unselectedAlpha);

        tvEmojiHappy.setOnClickListener(v -> {
            selectedRating = "Bueno";
            tvEmojiHappy.setAlpha(selectedAlpha);
            tvEmojiNeutral.setAlpha(unselectedAlpha);
            tvEmojiSad.setAlpha(unselectedAlpha);
        });

        tvEmojiNeutral.setOnClickListener(v -> {
            selectedRating = "Normal";
            tvEmojiHappy.setAlpha(unselectedAlpha);
            tvEmojiNeutral.setAlpha(selectedAlpha);
            tvEmojiSad.setAlpha(unselectedAlpha);
        });

        tvEmojiSad.setOnClickListener(v -> {
            selectedRating = "Malo";
            tvEmojiHappy.setAlpha(unselectedAlpha);
            tvEmojiNeutral.setAlpha(unselectedAlpha);
            tvEmojiSad.setAlpha(selectedAlpha);
        });

        AlertDialog dialog = builder.create();

        // Setup Buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnApply.setOnClickListener(v -> {
            if (selectedRating == null) {
                Toast.makeText(this, "Por favor, selecciona una valoración", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = spinnerStatus.getSelectedItem().toString();

            MovieInList movieInList = new MovieInList();
            movieInList.setId(movie.getId()); // IMPORTANT: Set the movie ID
            movieInList.setTitle(movie.getTitle());
            movieInList.setPosterPath(movie.getPosterPath());
            movieInList.setStatus(status);
            movieInList.setRating(selectedRating);

            new Thread(() -> {
                movieInListDao.insert(movieInList);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Película añadida a tu lista", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // Refresh the list to update the button states
                    presenter.onRefreshClicked();
                });
            }).start();
        });

        dialog.show();
    }
}
