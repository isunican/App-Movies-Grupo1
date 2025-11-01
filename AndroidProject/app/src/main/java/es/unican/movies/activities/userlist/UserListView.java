package es.unican.movies.activities.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

import es.unican.movies.R;
import es.unican.movies.activities.info.InfoActivity;
import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;

/**
 * La UserListView es la actividad que muestra la lista de películas guardadas por el usuario.
 * Implementa la interfaz IUserListContract.View, siguiendo el patrón MVP, y se encarga de
 * la presentación de los datos en la interfaz de usuario. Recibe las actualizaciones del
 * UserListPresenter y las refleja en la pantalla.
 */
@AndroidEntryPoint
public class UserListView extends AppCompatActivity implements IUserListContract.View {

    @Inject
    MovieInListDao movieInListDao; // DAO inyectado para acceder a la BD de la lista de usuario

    @Inject
    IUserListContract.Presenter presenter;

    private RecyclerView rvMovies;
    private List<String> selectedDialogItems;

    /**
     * Se llama cuando se crea la actividad. Se encarga de configurar la vista,
     * inyectar las dependencias necesarias y comunicar al presentador que se inicie.
     *
     * @param savedInstanceState Si la actividad se reinicia, este Bundle contiene los datos más recientes.
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
        inflater.inflate(R.menu.menu, menu);
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
/*
        if (itemId == R.id.menuItemFilterStatus) {
            presenter.onFilterStatusMenuClicked();
            return true;
        } else if (itemId == R.id.menu_info) {
            presenter.onMenuInfoClicked();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    /**
     * Muestra las películas en un RecyclerView. Este método es llamado por el presentador
     * cuando las películas han sido cargadas exitosamente.
     *
     * @param movies La lista de películas para mostrar.
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
     * Muestra un mensaje (Toast) indicando que la lista de películas del usuario está vacía.
     */
    @Override
    public void showEmptyList() {
        runOnUiThread(() ->
            Toast.makeText(this, "Tu lista está vacía", Toast.LENGTH_SHORT).show()
        );
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
