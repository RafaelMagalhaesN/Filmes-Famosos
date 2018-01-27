package rmagalhaes.com.br.filmesfamosos.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rafael Magalhães on 27/01/18.
 */

public class Reviews implements Parcelable{
    @SerializedName("results")
    private ArrayList<Review> reviews;

    public Reviews() {
    }


    public ArrayList<Review> getReviews() {
        return reviews;
    }

    @SuppressWarnings("unused")
    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews();
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
