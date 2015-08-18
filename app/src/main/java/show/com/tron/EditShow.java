package show.com.tron;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditShow extends AppCompatActivity {

    private EditText showName;
    private EditText epiSeason;
    private EditText currSeason;
    private EditText currEpisode;
    private SwitchCompat aSwitch;
    private Spinner spinner;
    private DBHelper dbHelper;
    private Show show;
    private TronApplication tron;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_show);
        tron = (TronApplication) getApplicationContext();
        dbHelper = new DBHelper(this);

        int show_id = getIntent().getIntExtra("ID", 0);
        show = dbHelper.getShow(show_id);
        showName = (EditText) findViewById(R.id.edit_show_name);
        epiSeason = (EditText) findViewById(R.id.edit_episode_season_editText);
        currSeason = (EditText) findViewById(R.id.edit_season_editText);
        currEpisode = (EditText) findViewById(R.id.edit_episode_editText);
        aSwitch = (SwitchCompat) findViewById(R.id.edit_airing_switch);
        spinner = (Spinner) findViewById(R.id.edit_days_spinner);

        showName.setText(show.getName());
        epiSeason.setText(String.valueOf(show.getNoOfEpisodes()));
        currSeason.setText(String.valueOf(show.getSeason()));
        currEpisode.setText(String.valueOf(show.getEpisode()));

        switch (show.getWeekDay()) {
            case OFFAIR:
                spinner.setSelection(0);
                spinner.setEnabled(false);
                aSwitch.setChecked(false);
                break;
            case MONDAY:
                spinner.setSelection(1);
                break;
            case TUESDAY:
                spinner.setSelection(2);
                break;
            case WEDNESDAY:
                spinner.setSelection(3);
                break;
            case THURSDAY:
                spinner.setSelection(4);
                break;
            case FRIDAY:
                spinner.setSelection(5);
                break;
            case SATURDAY:
                spinner.setSelection(6);
                break;
            case SUNDAY:
                spinner.setSelection(7);
                break;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    spinner.setEnabled(false);
                    spinner.setSelection(0);
                } else {
                    spinner.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelOnclick(View view) {
        finish();
    }

    public void doneOnClick(View view) {
        if (showName.getText().toString().trim().isEmpty()) {
            toast("Enter show name");
        } else if (spinner.getSelectedItemPosition() == 0 && aSwitch.isChecked()) {
            toast("Select show air day.");
        } else if (epiSeason.getText().toString().trim().isEmpty()) {
            toast("Enter number of episodes in a season.");
        } else if (currSeason.getText().toString().trim().isEmpty()) {
            toast("Enter the season you're on.");
        } else if (currEpisode.getText().toString().trim().isEmpty()) {
            toast("Enter the episode you're on");
        } else if (Integer.valueOf(currEpisode.getText().toString()) > Integer.valueOf(epiSeason.getText().toString())) {
            toast("Current episode cannot be greater than episodes in a season");
        } else {
            show.setName(showName.getText().toString().trim());
            show.setWeekDay(Day.value(spinner.getSelectedItemPosition()));
            show.setNoOfEpisodes(Integer.valueOf(epiSeason.getText().toString()));
            show.setSeason(Integer.valueOf(currSeason.getText().toString()));
            show.setEpisode(Integer.valueOf(currEpisode.getText().toString()));
            DBHelper dbHelper = new DBHelper(this);
            dbHelper.updateShow(show);
            toast("Show was successfully updated");
            finish();
        }
    }

    private void toast(String messgae) {
        Toast.makeText(this, messgae, Toast.LENGTH_SHORT).show();
    }
}
