package rmagalhaes.com.br.filmesfamosos.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Movies implements Parcelable {

    @SerializedName("results")
    private ArrayList<Movie> movies;

    public Movies() {
    }

    protected Movies(Parcel in) {
        movies = in.createTypedArrayList(Movie.CREATOR);
    }

    @SuppressWarnings("unused")
    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(movies);
    }
}
