package es.unican.movies.activities.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import es.unican.movies.R;
import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.model.Movie;
import es.unican.movies.model.MovieInListDao;
import es.unican.movies.service.EImageSize;
import es.unican.movies.service.ITmdbApi;

/**
 * El MovieAdapter es un ArrayAdapter personalizado para mostrar una lista de objetos Movie
 * en un ListView. Se encarga de inflar el layout de cada elemento de la lista y de vincular
 * los datos de la película (póster, título) a las vistas correspondientes.
 * También gestiona la interacción del usuario con cada película, como hacer clic para ver
 * detalles o añadir la película a una lista personal.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> movieList;
    private final MovieInListDao movieInListDao;
    private final IMainContract.View mainView;

    /**
     * Constructor para el MovieAdapter.
     *
     * @param context        El contexto actual.
     * @param movieList      La lista de películas a mostrar.
     * @param movieInListDao El DAO para acceder a la lista de películas del usuario y comprobar si una película ya ha sido añadida.
     * @param mainView       La vista principal (MainView) para invocar callbacks, como mostrar el diálogo para añadir a la lista.
     */
    protected MovieAdapter(@NonNull Context context, @NonNull List<Movie> movieList, @NonNull MovieInListDao movieInListDao, @NonNull IMainContract.View mainView) {
        super(context, R.layout.activity_main_movie_item, movieList);
        this.movieList = movieList;
        this.movieInListDao = movieInListDao;
        this.mainView = mainView;
    }

    /**
     * Obtiene una vista que muestra los datos en la posición especificada en el conjunto de datos.
     *
     * @param position    La posición del elemento dentro del conjunto de datos del adaptador.
     * @param convertView La vista antigua a reutilizar, si es posible.
     * @param parent      El padre al que esta vista se adjuntará eventualmente.
     * @return Una vista correspondiente a los datos en la posición especificada.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_main_movie_item, parent, false);
        }

        // Gestiona el clic para mostrar los detalles de la película
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DetailsView.class);
            intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie));
            getContext().startActivity(intent);
        });

        // Carga la imagen del póster
        ImageView ivPoster = convertView.findViewById(R.id.ivPoster);
        String imageUrl = ITmdbApi.getFullImagePath(movie.getPosterPath(), EImageSize.W92);
        Picasso.get().load(imageUrl).fit().centerInside().into(ivPoster);

        // Establece el título de la película
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(movie.getTitle());

        // Configura el botón para añadir a la lista
        ImageButton btnAddToList = convertView.findViewById(R.id.btnAddToList);

        // Comprueba en un hilo secundario si la película ya está en la lista del usuario
        new Thread(() -> {
            boolean isMovieInList = movieInListDao.getMovieById(movie.getId()) != null;
            // Actualiza la UI en el hilo principal
            ((MainView) mainView).runOnUiThread(() -> {
                btnAddToList.setEnabled(!isMovieInList);
                if (isMovieInList) {
                    // Si la película ya está en la lista, deshabilita el botón y lo atenúa
                    btnAddToList.setOnClickListener(null);
                    btnAddToList.setColorFilter(Color.GRAY);
                } else {
                    // Si no, configura el botón para mostrar el diálogo de añadir
                    btnAddToList.setOnClickListener(v -> ((MainView) mainView).showAddToListDialog(movie));
                    btnAddToList.clearColorFilter();
                }
            });
        }).start();

        return convertView;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        return movieList.get(position);
    }
}
