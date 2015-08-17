package show.com.tron;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TronApplication tron;
    private List<String> searchList; //List of show names to search against.
    private List<String> searchFiltered; //List of filtered show names
    private boolean mSearchOpened; //Search filed opened flag
    private String mSearchQuery; //Searched text.
    private DBHelper db;
    private Drawable mIconOpenSearch; //Search bar open icon
    private Drawable mIconCloseSearch; //Search bar close icon
    private EditText mSearchEt;
    private MenuItem mSearchAction;
    private ListView mSearchLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tron = (TronApplication) getApplicationContext();
        db = new DBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        mSearchOpened = false;
        mSearchQuery = "";
        mIconOpenSearch = ContextCompat.getDrawable(this, R.drawable.ic_action_search);
        mIconCloseSearch = ContextCompat.getDrawable(this, R.drawable.ic_action_search_close );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchList = db.getShowNames();
        searchFiltered = searchList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            if (mSearchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
            return true;
        } else if (id == R.id.action_export_shows) {
            try {
                DataXmlExporter dxe = new DataXmlExporter(db.getReadableDatabase());
                Log.e("MainActivity", "Starting to export file");
                dxe.export("Show_");
            } catch (IOException e) {
                Log.e("MainActivity", "Error trying to export file");
                e.printStackTrace();
            }
        }  else if (id == R.id.action_import_shows) {

        } else if (id == R.id.action_settings) {
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    //OnClick for floating action button
    public void fabOnClick(View view) {
        startActivity(new Intent(this, NewShowActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentShow(), "SHOWS");
        adapter.addFrag(new FragmentToday(), "TODAY");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void openSearchBar(String queryText) {

        // Set custom view on action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);

        // Search edit text field setup.
        mSearchEt = (EditText) actionBar.getCustomView()
                .findViewById(R.id.etSearch);
        mSearchEt.addTextChangedListener(new SearchWatcher());
        mSearchEt.setText(queryText);
        mSearchEt.requestFocus();

        // Change search icon accordingly.
        mSearchAction.setIcon(mIconCloseSearch);
        mSearchOpened = true;
    }

    private void closeSearchBar() {
        // Remove custom view.
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        // Change search icon accordingly.
        mSearchAction.setIcon(mIconOpenSearch);
        mSearchOpened = false;
    }

    /**
     * Responsible for handling changes in search edit text.
     */
    private class SearchWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
//            mSearchQuery = mSearchEt.getText().toString();
//            searchFiltered = performSearch(searchList, mSearchQuery);
//            getListAdapter().update(mMoviesFiltered);
        }

    }

//    private MoviesListAdapter getListAdapter() {
//        return (MoviesListAdapter) mMoviesLv.getAdapter();
//    }
}
