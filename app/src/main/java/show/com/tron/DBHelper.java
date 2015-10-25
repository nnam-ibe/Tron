package show.com.tron;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String SHOW_TABLE = "shows";
    public static final String SHOW_ID = "shows";
    public static final String SHOW_NAME = "name";
    public static final String SHOW_AIR_DAY = "air_date";
    public static final String SHOW_NO_EPISODES = "no_episodes";
    public static final String SHOW_SEASON = "season";
    public static final String SHOW_EPISODE = "episode";
    public static final String SHOW_LAST_UPDATED = "last_updated";
    public static final String DATABASE_NAME = "App.db";
    private static final String SHOW_TABLE_CREATE = "create table "
            + SHOW_TABLE + "(" + SHOW_ID + " integer primary key autoincrement unique, "
            + SHOW_NAME + " text not null, " + SHOW_AIR_DAY + " text not null, " + SHOW_NO_EPISODES
            + " integer not null, " + SHOW_SEASON + " integer not null, " + SHOW_EPISODE
            + " integer not null, " + SHOW_LAST_UPDATED + " long default 0);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SHOW_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("ALTER TABLE " + SHOW_TABLE + " ADD COLUMN " +
                SHOW_LAST_UPDATED + " long default 0");
    }

    public void insertShow(Show show) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SHOW_NAME, show.getName());
        values.put(SHOW_AIR_DAY, show.getWeekDay().toString());
        values.put(SHOW_NO_EPISODES, show.getNoOfEpisodes());
        values.put(SHOW_SEASON, show.getSeason());
        values.put(SHOW_EPISODE, show.getEpisode());
        values.put(SHOW_LAST_UPDATED, show.getLastUpdatedMillis());

        db.insert(SHOW_TABLE, null, values);
    }

    /**
     * Used to retrieve all the shows in the database.
     * @return ArrayList containing all shows.
     */
    public List<Show> getAllShows() {
        SQLiteDatabase db = getReadableDatabase();
        List<Show> showList = new ArrayList<>();
        Cursor cursor = db.query(SHOW_TABLE,
                new String[]{SHOW_NAME, SHOW_AIR_DAY, SHOW_NO_EPISODES, SHOW_SEASON, SHOW_EPISODE, SHOW_ID, SHOW_LAST_UPDATED},
                null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Show show = new Show(cursor.getString(cursor.getColumnIndex(SHOW_NAME)), cursor.getString(cursor
                    .getColumnIndex(SHOW_AIR_DAY)), cursor.getInt(cursor.getColumnIndex(SHOW_NO_EPISODES)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_SEASON)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_EPISODE)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_ID)),
                    cursor.getLong(cursor.getColumnIndex(SHOW_LAST_UPDATED)));
            showList.add(show);
        }
        if (cursor != null) {
            cursor.close();
        }
        return showList;
    }

    /**
     * Used to update all Show fields in the database
     * @param show the show with new fields to update
     */
    public boolean updateShow(Show show) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SHOW_NAME, show.getName());
        cv.put(SHOW_AIR_DAY, show.getWeekDay().toString());
        cv.put(SHOW_NO_EPISODES, show.getNoOfEpisodes());
        cv.put(SHOW_SEASON, show.getSeason());
        cv.put(SHOW_EPISODE, show.getEpisode());
        cv.put(SHOW_LAST_UPDATED, show.getLastUpdatedMillis());
        String[] whereArgs = new String[]{show.getId() + ""};

        try {
            db.update(SHOW_TABLE, cv, SHOW_ID + " = ?", whereArgs);
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "Exception updating field");
        }
        return false;
    }

    public Show getShow(int show_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SHOW_TABLE,
                new String[]{SHOW_NAME, SHOW_AIR_DAY, SHOW_NO_EPISODES, SHOW_SEASON, SHOW_EPISODE, SHOW_ID, SHOW_LAST_UPDATED},
                SHOW_ID + "=?", new String[]{String.valueOf(show_id)}, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            return new Show(cursor.getString(cursor.getColumnIndex(SHOW_NAME)), cursor.getString(cursor
                    .getColumnIndex(SHOW_AIR_DAY)), cursor.getInt(cursor.getColumnIndex(SHOW_NO_EPISODES)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_SEASON)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_EPISODE)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_ID)),
                    cursor.getLong(cursor.getColumnIndex(SHOW_LAST_UPDATED)));
        }
        return null;
    }

    public List<Show> getTodayShows(String today) {
        SQLiteDatabase db = getReadableDatabase();
        List<Show> showList = new ArrayList<>();

        Cursor cursor = db.query(SHOW_TABLE,
                new String[]{SHOW_NAME, SHOW_AIR_DAY, SHOW_NO_EPISODES, SHOW_SEASON, SHOW_EPISODE, SHOW_ID, SHOW_LAST_UPDATED},
                SHOW_AIR_DAY + "=?", new String[]{today}, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Show show = new Show(cursor.getString(cursor.getColumnIndex(SHOW_NAME)), cursor.getString(cursor
                    .getColumnIndex(SHOW_AIR_DAY)), cursor.getInt(cursor.getColumnIndex(SHOW_NO_EPISODES)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_SEASON)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_EPISODE)),
                    cursor.getInt(cursor.getColumnIndex(SHOW_ID)),
                    cursor.getLong(cursor.getColumnIndex(SHOW_LAST_UPDATED)));
            showList.add(show);
        }
        if (cursor != null) cursor.close();
        return showList;
    }

    public boolean deleteShow(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(SHOW_TABLE, SHOW_ID + "=" + id, null) > 0;
    }

    public void deleteAllShow() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SHOW_TABLE, null, null);
    }

    public int getNumberOfShow() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery("select count(*) from " + SHOW_TABLE, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }

}
