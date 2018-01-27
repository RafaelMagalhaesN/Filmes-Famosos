package rmagalhaes.com.br.filmesfamosos.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rafael Magalhães on 27/01/18.
 */

public class Videos implements Parcelable {

    @SerializedName("results")
    private ArrayList<Video> videos;

    public Videos() {
    }


    public ArrayList<Video> getVideos() {
        return videos;
    }

    @SuppressWarnings("unused")
    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos();
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
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
