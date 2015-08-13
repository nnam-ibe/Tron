package show.com.tron;

import android.app.Application;

import java.util.List;

public class TronApplication extends Application {

    private static TronApplication singleton;
    //    private List<Show> showList;
    private DBHelper dbHelper;
    private boolean isNewShow = false;
//    private AdapterShow adapterShow;

    public TronApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        dbHelper = new DBHelper(this);
        isNewShow = true;
    }

    public void setNewShow() {
        isNewShow = true;
    }

    public List<Show> getShowList() {
        if (isNewShow) {
            isNewShow = false;
            return dbHelper.getAllShows();
        } else {
            return null;
        }
    }

    public void updateShow(Show show) {
        isNewShow = true;
        dbHelper.updateShow(show);
    }
}
