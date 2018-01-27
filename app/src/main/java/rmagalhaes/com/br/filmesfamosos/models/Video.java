package rmagalhaes.com.br.filmesfamosos.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafael Magalhães on 27/01/18.
 */

public class Video implements Parcelable {

    @SerializedName("id")
    private
    String id;

    @SerializedName("key")
    private
    String key;

    @SerializedName("name")
    private
    String name;

    @SerializedName("site")
    private
    String site;

    @SerializedName("size")
    private
    long size;

    @SerializedName("type")
    private
    String type;


    private Video(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readLong();
        type = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeLong(size);
        dest.writeString(type);
    }
}
