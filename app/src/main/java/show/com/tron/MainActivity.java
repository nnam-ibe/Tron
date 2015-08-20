package show.com.tron;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private FragmentShow fragmentShow;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);
        fragmentShow = new FragmentShow();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

//        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
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
                fragmentShow.showAdapter.getFilter().filter("");
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String cs) {
                fragmentShow.showAdapter.getFilter().filter(cs);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                fragmentShow.showAdapter.getFilter().filter(query);
                return false;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_export_shows:
                try {
                    DataXmlExporter dxe = new DataXmlExporter(db.getReadableDatabase());
                    toast("File exported to directory /storage/emulated/0/Tron/");
                    dxe.export("Show_");
                } catch (IOException e) {
                    toast("Couldn't export file");
                    Log.e("MainActivity", "Error trying to export file");
                    e.printStackTrace();
                }
                return true;
            case R.id.action_import_shows:
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                FileDialog fileDialog = new FileDialog(this, mPath);
                fileDialog.setFileEndsWith(".xml");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        try {
                            List<Show> list = DataXmlImporter.importer(file);
                            for (Show show : list) {
                                db.insertShow(show);
                            }
                            fragmentShow.onResume();
                            toast("Import successful!!");
                        } catch (Exception e) {
                            toast("Import failed, File format bad!!");
                            Log.e("MainActivity", "Exception trying to import file");
                            e.printStackTrace();
                        }
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
        adapter.addFrag(fragmentShow, "SHOWS");
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

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
