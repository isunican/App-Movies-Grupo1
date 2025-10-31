package es.unican.movies.activities.userlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.unican.movies.R;
import es.unican.movies.model.MovieInList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final List<MovieInList> movies;



    public UserListAdapter(List<MovieInList> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos un layout de item simple proporcionado por Android
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;
        private final TextView text2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }

        public void bind(final MovieInList movie) {
            text1.setText(movie.getTitle());
            String status = movie.getStatus();
            text2.setText(status != null && !status.isEmpty() ? status : "Sin empezar");
        }
    }
}
