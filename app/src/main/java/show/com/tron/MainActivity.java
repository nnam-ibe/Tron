package show.com.tron;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private ViewPager viewPager;
    private TronApplication tron;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DBHelper.getInstance(this);
        tron = (TronApplication)getApplicationContext();
        tron.setFragmentShow(new FragmentShow());
        tron.setFragmentToday(new FragmentToday());

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(mItem,new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                tron.getFragmentShow().showAdapter.getFilter().filter("");
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String cs) {
                tron.getFragmentShow().showAdapter.getFilter().filter(cs);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                tron.getFragmentShow().showAdapter.getFilter().filter(query);
                return false;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_export_shows:
                new ExportFileTask().execute();
                return true;
            case R.id.action_import_shows:
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                FileDialog fileDialog = new FileDialog(this, mPath);
                fileDialog.setFileEndsWith(".db");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        new ImportFileTask().execute(file);
                    }
                });
                fileDialog.showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //OnClick for floating action button
    public void fabOnClick(View view) {
        startActivity(new Intent(this, NewShowActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(tron.getFragmentShow(), "SHOWS");
        adapter.addFrag(tron.getFragmentToday(), "TODAY");
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

    private class ImportFileTask extends AsyncTask<File, Void, Boolean> {

        protected Boolean doInBackground(File... file) {
            try {
                db.importDB(file[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                tron.getFragmentShow().onResume();
                tron.getFragmentToday().onResume();
                toast("Import successful!");
            } else {
                toast("Failed to import");
            }
        }
    }

    private class ExportFileTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... v) {
            try {
                db.exportDB();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            if ( result ) {
                toast("File exported to directory /storage/emulated/0/Tron/");
            } else {
                toast("Export Failes");
            }

        }
    }

}
