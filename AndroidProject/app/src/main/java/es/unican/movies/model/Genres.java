package es.unican.movies.model;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Parcel
public class Genres {
    private Integer id;
    private String name;
}
