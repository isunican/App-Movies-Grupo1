package es.unican.movies.activities.details;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import es.unican.movies.R;
import es.unican.movies.model.Movie;
import es.unican.movies.service.EImageSize;
import es.unican.movies.service.ITmdbApi;

public class DetailsView extends AppCompatActivity implements IDetailsContract.View {

    public static final String INTENT_MOVIE = "INTENT_MOVIE";

    private IDetailsContract.Presenter presenter;

    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvReleaseYear;
    private TextView tvDuration;
    private TextView tvGenres;
    private TextView tvVoteAverage;
    private TextView tvSummaryScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Link UI elements
        ivPoster = findViewById(R.id.imPoster);
        tvTitle = findViewById(R.id.tvTituloDecadas);
        tvReleaseYear = findViewById(R.id.tvEstreno);
        tvDuration = findViewById(R.id.tvDuracion);
        tvGenres = findViewById(R.id.tvGenero);
        tvVoteAverage = findViewById(R.id.tvPuntuacionMedia);
        tvSummaryScore = findViewById(R.id.tvPuntuacionSumaria);

        // Create presenter and init
        presenter = new DetailsPresenter(this);
        presenter.init();
    }

    @Override
    public Movie getMovie() {
        // Get movie from intent
        return Parcels.unwrap(getIntent().getParcelableExtra(INTENT_MOVIE));
    }

    @Override
    public void showMovieInfo(String title, String releaseDate, String duration, String genres, String voteAverage, String summaryScore, String posterPath) {
        tvTitle.setText(title);
        tvReleaseYear.setText(releaseDate);
        tvDuration.setText(duration);
        tvGenres.setText(genres);
        tvVoteAverage.setText(voteAverage);
        tvSummaryScore.setText(summaryScore);

        if (posterPath != null && !"-".equals(posterPath)) {
            String imageUrl = ITmdbApi.getFullImagePath(posterPath, EImageSize.W342);
            Picasso.get().load(imageUrl).into(ivPoster);
        } else {
            // Set a placeholder image if the poster is not available
            ivPoster.setImageResource(R.drawable.ic_launcher_background); // Asegúrate de tener un drawable para esto
        }
    }
}
