package rmagalhaes.com.br.filmesfamosos.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Movies {

    @SerializedName("results")
    private ArrayList<Movie> movies;

    public Movies() {
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

}
