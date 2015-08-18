package show.com.tron;

import android.app.Application;

import java.util.List;

public class TronApplication extends Application {

    private static TronApplication singleton;
    private DBHelper dbHelper;

    public TronApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        dbHelper = new DBHelper(this);
    }

    public List<Show> getShowList() {
        return dbHelper.getAllShows();
    }

    public void updateShow(Show show) {
        dbHelper.updateShow(show);
    }
}
