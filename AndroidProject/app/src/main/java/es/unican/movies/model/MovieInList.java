package es.unican.movies.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase sencilla para representar una película en la lista de un usuario.
 * Usa Lombok para generar getters y setters.
 */
@Getter
@Setter
public class MovieInList {

    private String title;
    private String status; // "Terminado", "En proceso", "Sin empezar"

}
