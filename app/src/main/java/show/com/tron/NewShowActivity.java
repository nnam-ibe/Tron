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


public class NewShowActivity extends AppCompatActivity {

    private EditText showName;
    private EditText epiSeason;
    private EditText currSeason;
    private EditText currEpisode;
    private SwitchCompat aSwitch;
    private Spinner spinner;
    private TronApplication tron;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_show);
        tron = (TronApplication) getApplicationContext();

        showName = (EditText) findViewById(R.id.new_show_name);
        epiSeason = (EditText) findViewById(R.id.new_episode_season_editText);
        currSeason = (EditText) findViewById(R.id.new_season_editText);
        currEpisode = (EditText) findViewById(R.id.new_episode_editText);
        aSwitch = (SwitchCompat) findViewById(R.id.new_airing_switch);
        spinner = (Spinner) findViewById(R.id.days_spinner);

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
            Show show = new Show(showName.getText().toString().trim(), Day.value(spinner.getSelectedItemPosition()),
                    Integer.valueOf(epiSeason.getText().toString()), Integer.valueOf(currSeason.getText().toString()),
                    Integer.valueOf(currEpisode.getText().toString()));
            DBHelper dbHelper = new DBHelper(this);
            dbHelper.insertShow(show);
            toast("Show was successfully added");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String messgae) {
        Toast.makeText(this, messgae, Toast.LENGTH_SHORT).show();
    }
}
