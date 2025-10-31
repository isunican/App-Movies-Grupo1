package es.unican.movies.activities.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.unican.movies.R;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.model.MovieInList;

public class UserListView extends AppCompatActivity implements IUserListContract.View {

    private IUserListContract.Presenter presenter;
    private RecyclerView rvMovies;
    private List<String> selectedDialogItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        presenter = new UserListPresenter();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvMovies = findViewById(R.id.rvMoviesInList);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        presenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemFilterStatus) {
            presenter.onFilterStatusMenuClicked();
            return true;
        } else if (itemId == R.id.menu_info) {
            presenter.onMenuInfoClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMovies(List<MovieInList> movies) {
        UserListAdapter adapter = new UserListAdapter(movies);
        rvMovies.setAdapter(adapter);
    }

    @Override
    public void showLoadCorrect(int movieCount) {
        Toast.makeText(this, "Se han cargado " + movieCount + " películas.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error al cargar la lista", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    @Override
    public void showFilterByStatusDialog(List<String> statusesWithCount, List<String> selectedStatuses) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar por Estado");

        final CharSequence[] dialogItems = statusesWithCount.toArray(new CharSequence[0]);
        final boolean[] checkedItems = new boolean[dialogItems.length];
        selectedDialogItems = new ArrayList<>(selectedStatuses);

        for (int i = 0; i < dialogItems.length; i++) {
            if (selectedDialogItems.contains(dialogItems[i].toString())) {
                checkedItems[i] = true;
            }
        }

        builder.setMultiChoiceItems(dialogItems, checkedItems, (dialog, which, isChecked) -> {
            String selected = dialogItems[which].toString();
            if (isChecked) {
                selectedDialogItems.add(selected);
            } else {
                selectedDialogItems.remove(selected);
            }
        });

        builder.setPositiveButton("Aplicar", (dialog, which) -> presenter.onStatusFiltered(selectedDialogItems));
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
