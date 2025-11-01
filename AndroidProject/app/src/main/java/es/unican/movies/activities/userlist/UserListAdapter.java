package es.unican.movies.activities.userlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.List;
import es.unican.movies.R;
import es.unican.movies.model.MovieInList;
import es.unican.movies.service.EImageSize;

/**
 * El UserListAdapter es un adaptador para un RecyclerView que muestra la lista de películas
 * guardadas por el usuario. Se encarga de vincular los datos de cada película (póster, título,
 * estado y valoración) con la vista de elemento de lista correspondiente.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final List<MovieInList> movies;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    /**
     * Constructor para el UserListAdapter.
     *
     * @param movies La lista de películas a mostrar.
     */
    public UserListAdapter(List<MovieInList> movies) {
        this.movies = movies;
    }

    /**
     * Se llama cuando RecyclerView necesita un nuevo ViewHolder para representar un elemento.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de que se vincule a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene una vista para el elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_in_list_testing, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Se llama por RecyclerView para mostrar los datos en la posición especificada.
     *
     * @param holder   El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    /**
     * Devuelve el número total de elementos en la lista.
     * @return El tamaño de la lista de películas.
     */
    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    /**
     * Clase ViewHolder que representa cada elemento individual de la lista.
     * Contiene las referencias a las vistas (TextViews) para un solo item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPoster;
        private final TextView tvTitle;
        private final TextView tvStatus;
        private final TextView tvRating;

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista raíz del elemento de la lista.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        /**
         * Vincula los datos de una película a las vistas dentro del ViewHolder.
         *
         * @param movie La película a mostrar.
         */
        public void bind(MovieInList movie) {
            tvTitle.setText(movie.getTitle());
            tvStatus.setText("Estado: " + movie.getStatus());

            String ratingValue = movie.getRating();
            String ratingDisplay;

            if (ratingValue != null) {
                switch (ratingValue) {
                    case "Bueno":
                        ratingDisplay = "😊";
                        break;
                    case "Normal":
                        ratingDisplay = "😐";
                        break;
                    case "Malo":
                        ratingDisplay = "😞";
                        break;
                    default:
                        ratingDisplay = ratingValue; // Muestra el valor original si no coincide
                }
            } else {
                ratingDisplay = "N/A";
            }

            tvRating.setText(ratingDisplay);

            if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                String imageUrl = IMAGE_BASE_URL + EImageSize.W185.value + movie.getPosterPath();
                Picasso.get().load(imageUrl).into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.ic_launcher_background); // Imagen por defecto
            }
        }
    }
}
