package show.com.tron;

import android.app.Application;

import java.util.Collections;
import java.util.List;

public class TronApplication extends Application {

    private static TronApplication singleton;
    private DBHelper dbHelper;
    private FragmentShow fragmentShow;
    private FragmentToday fragmentToday;
    private static final String TAG_SHOW = "FRAGMENT SHOW";
    private static final String TAG_TODAY = "FRAGMENT TODAY";

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
        List<Show> list = dbHelper.getAllShows();
        Collections.reverse(list);
        return list;
    }

    private boolean deleteShow(int id) {
        return dbHelper.deleteShow(id);
    }

    public boolean deleteShow(final String TAG, int id) {
        if ( !deleteShow(id) ) {
            return false;
        }
        if ( TAG.equals(TAG_SHOW) ) {
            fragmentToday.onResume();
        } else if ( TAG.equals(TAG_TODAY) ) {
            fragmentShow.onResume();
        } else {
            fragmentToday.onResume();
            fragmentShow.onResume();
        }
        return true;
    }

    private void updateShow(Show show) {
        dbHelper.updateShow(show);
    }

    public void updateShow(final String TAG, Show show) {
        updateShow(show);
        if ( TAG.equals(TAG_SHOW) ) {
            fragmentToday.onResume();
        } else if ( TAG.equals(TAG_TODAY) ) {
            fragmentShow.onResume();
        } else {
            fragmentToday.onResume();
            fragmentShow.onResume();
        }
    }

    public FragmentShow getFragmentShow() {
        return fragmentShow;
    }

    public void setFragmentShow(FragmentShow fragmentShow) {
        this.fragmentShow = fragmentShow;
    }

    public FragmentToday getFragmentToday() {
        return fragmentToday;
    }

    public void setFragmentToday(FragmentToday fragmentToday) {
        this.fragmentToday = fragmentToday;
    }

    public void removeShow(String TAG, int id) {
        if ( TAG.equals(TAG_SHOW) ) {
            fragmentToday.removeShow(id);
        } else if ( TAG.equals(TAG_TODAY) ) {
            fragmentShow.removeShow(id);
        }
    }

    public void putBack(String TAG) {
        if ( TAG.equals(TAG_SHOW) ) {
            fragmentToday.onResume();
        } else if ( TAG.equals(TAG_TODAY) ) {
            fragmentShow.onResume();
        }
    }

}
