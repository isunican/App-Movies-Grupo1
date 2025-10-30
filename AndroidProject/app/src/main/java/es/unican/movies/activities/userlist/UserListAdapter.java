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

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<MovieInList> movies;

    public UserListAdapter(List<MovieInList> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_in_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPoster;
        private final TextView tvTitle;
        private final TextView tvStatus;
        private final TextView tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

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
                        ratingDisplay = ratingValue; // Muestra el valor original si no es uno de los esperados
                }
            } else {
                ratingDisplay = "N/A";
            }

            tvRating.setText(ratingDisplay);

            if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                Picasso.get().load(movie.getPosterPath()).into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.ic_launcher_background); // Default image
            }
        }
    }
}
