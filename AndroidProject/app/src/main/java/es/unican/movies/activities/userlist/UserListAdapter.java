package es.unican.movies.activities.userlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.unican.movies.model.MovieInList;

/**
 * Adaptador para el RecyclerView que muestra la lista de películas de un usuario.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final List<MovieInList> movies;

    /**
     * Constructor del adaptador.
     * @param movies La lista de películas a mostrar.
     */
    public UserListAdapter(List<MovieInList> movies) {
        this.movies = movies;
    }

    /**
     * Crea una nueva vista para un elemento de la lista (invocado por el layout manager).
     * @param parent El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista.
     * @return Un nuevo ViewHolder que contiene la vista del elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos un layout de item simple proporcionado por Android
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos de una película a una vista (invocado por el layout manager).
     * @param holder El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento en la lista.
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
        private final TextView text1;
        private final TextView text2;

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista raíz del elemento de la lista.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }

        /**
         * Vincula los datos de una película a las vistas del ViewHolder.
         * @param movie La película a mostrar.
         */
        public void bind(final MovieInList movie) {
            text1.setText(movie.getTitle());
            String status = movie.getStatus();
            text2.setText(status != null && !status.isEmpty() ? status : "Sin empezar");
        }
    }
}
