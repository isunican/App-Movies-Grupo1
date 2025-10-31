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

public class MovieAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> movieList;
    private final MovieInListDao movieInListDao;
    private final IMainContract.View mainView;

    protected MovieAdapter(@NonNull Context context, @NonNull List<Movie> movieList, @NonNull MovieInListDao movieInListDao, @NonNull IMainContract.View mainView) {
        super(context, R.layout.activity_main_movie_item, movieList);
        this.movieList = movieList;
        this.movieInListDao = movieInListDao;
        this.mainView = mainView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_main_movie_item, parent, false);
        }

        // Handle click to show details
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DetailsView.class);
            intent.putExtra(DetailsView.INTENT_MOVIE, Parcels.wrap(movie));
            getContext().startActivity(intent);
        });

        ImageView ivPoster = convertView.findViewById(R.id.ivPoster);
        String imageUrl = ITmdbApi.getFullImagePath(movie.getPosterPath(), EImageSize.W92);
        Picasso.get().load(imageUrl).fit().centerInside().into(ivPoster);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(movie.getTitle());

        ImageButton btnAddToList = convertView.findViewById(R.id.btnAddToList);

        new Thread(() -> {
            boolean isMovieInList = movieInListDao.getMovieById(movie.getId()) != null;
            ((MainView) mainView).runOnUiThread(() -> {
                btnAddToList.setEnabled(!isMovieInList);
                if (isMovieInList) {
                    btnAddToList.setOnClickListener(null);
                    btnAddToList.setColorFilter(Color.GRAY);
                } else {
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
