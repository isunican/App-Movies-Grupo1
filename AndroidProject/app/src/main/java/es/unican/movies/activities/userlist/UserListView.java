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

/**
 * Vista para la lista de películas de un usuario.
 * Esta clase implementa la interfaz IUserListContract.View y se encarga de la
 * interacción con el usuario y la presentación de los datos.
 */
public class UserListView extends AppCompatActivity implements IUserListContract.View {

    private IUserListContract.Presenter presenter;
    private RecyclerView rvMovies;
    private List<String> selectedDialogItems;

    /**
     * Método llamado al crear la actividad.
     * Se encarga de inicializar la vista, el presentador y la UI.
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
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

    /**
     * Infla el menú de opciones en la barra de herramientas.
     * @param menu El menú en el que se inflarán los elementos.
     * @return true para que el menú sea mostrado.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    /**
     * Gestiona la selección de un elemento en el menú de opciones.
     * @param item El elemento del menú que fue seleccionado.
     * @return true si el evento fue gestionado, false en caso contrario.
     */
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

    /**
     * Muestra la lista de películas en el RecyclerView.
     * @param movies La lista de películas a mostrar.
     */
    @Override
    public void showMovies(List<MovieInList> movies) {
        UserListAdapter adapter = new UserListAdapter(movies);
        rvMovies.setAdapter(adapter);
    }

    /**
     * Muestra un mensaje Toast informando que la carga de películas fue correcta.
     * @param movieCount El número de películas cargadas.
     */
    @Override
    public void showLoadCorrect(int movieCount) {
        Toast.makeText(this, "Se han cargado " + movieCount + " películas.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra un mensaje Toast informando de un error en la carga de películas.
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error al cargar la lista", Toast.LENGTH_SHORT).show();
    }

    /**
     * Abre la actividad de información de la aplicación.
     */
    @Override
    public void showInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    /**
     * Muestra un diálogo de selección múltiple para filtrar por estado.
     * @param statusesWithCount La lista de estados con el conteo de películas.
     * @param selectedStatuses Los estados actualmente seleccionados.
     */
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
