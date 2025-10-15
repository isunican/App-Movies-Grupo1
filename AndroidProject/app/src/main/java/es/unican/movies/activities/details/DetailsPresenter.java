package es.unican.movies.activities.details;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.model.Genres;

public class DetailsPresenter implements IDetailsContract.Presenter {

    private final IDetailsContract.View view;

    public DetailsPresenter(IDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void init() {
        Movie movie = view.getMovie();
        if (movie != null) {
            String title = formatString(movie.getTitle());
            String releaseYear = formatReleaseYear(movie.getReleaseDate());
            String duration = formatDuration(movie.getRuntime());
            String genres = formatGenres(movie.getGenres());
            String voteAverage = formatDouble(movie.getVoteAverage(), 2);
            String summaryScore = calculateSummaryScore(movie.getVoteAverage(), movie.getVoteCount());
            String posterPath = movie.getPosterPath();

            view.showMovieInfo(title, releaseYear, duration, genres, voteAverage, summaryScore, posterPath);
        }
    }

    private String formatString(String text) {
        return (text == null || text.isEmpty()) ? "-" : text;
    }

    private String formatReleaseYear(String date) {
        if (date != null && date.length() >= 4) {
            return date.substring(0, 4);
        }
        return "-";
    }

    private String formatDuration(int minutes) {
        if (minutes <= 0) {
            return "-";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format(Locale.getDefault(), "%dh %dm", hours, mins);
    }

    private String formatGenres(List<Genres> genres) {
        if (genres == null || genres.isEmpty()) {
            return "-";
        }
        return genres.stream().map(Genres::getName).collect(Collectors.joining(", "));
    }

    private String formatDouble(double value, int decimals) {
        if (value < 0) {
            return "-";
        }
        return String.format(Locale.US, "%." + decimals + "f", value);
    }

    private String calculateSummaryScore(double voteAverage, int voteCount) {
        if (voteAverage < 0 || voteCount < 0) {
            return "-";
        }
        double normalizedCount = 2 * Math.log10(1 + voteCount);
        double summaryScoreValue = (voteAverage + normalizedCount) / 2;
        return formatDouble(summaryScoreValue, 2);
    }
}
