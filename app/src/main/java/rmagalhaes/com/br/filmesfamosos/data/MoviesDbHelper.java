package rmagalhaes.com.br.filmesfamosos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rafael Magalhães on 23/01/18.
 */

class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesdb.db";
    private static final int VERSION          = 2;


    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE =
                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                        MoviesContract.MoviesEntry._ID                      + " INTEGER PRIMARY KEY, " +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID          + " INTEGER NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANG     + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_POPULARITY        + " REAL NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_VOTE_AVARAGE      + " REAL NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH     + " TEXT NOT NULL); ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
