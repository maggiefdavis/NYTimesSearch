package com.codepath.nytimessearch.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nytimessearch.DatePickerFragment;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.SearchFilters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity implements OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    SearchFilters filters;
    ArrayList<String> newsDeskItems;
    //String newsDesk = "";
    String sort = "";
    String beginDate = "";
    int spinnerPos;
    String dateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filters = (SearchFilters) getIntent().getSerializableExtra("filters");
        if (filters != null) {
            newsDeskItems = filters.getNewsDeskItems();
        } else {
            newsDeskItems = new ArrayList<String>();
        }
        spinnerPos = getIntent().getIntExtra("spinnerPos", 0);

        Spinner spinner = (Spinner) findViewById(R.id.spSort);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(spinnerPos);

        CheckBox cbBusiness = (CheckBox) findViewById(R.id.cbBusiness);
        CheckBox cbCulture = (CheckBox) findViewById(R.id.cbCulture);
        CheckBox cbPolitics = (CheckBox) findViewById(R.id.cbPolitics);
        CheckBox cbScience = (CheckBox) findViewById(R.id.cbScience);

        if(newsDeskItems.contains("\"Business\"")) {
            cbBusiness.setChecked(true);
        } else {
            cbBusiness.setChecked(false);
        }

        if(newsDeskItems.contains("\"Culture\"")) {
            cbCulture.setChecked(true);
        } else {
            cbCulture.setChecked(false);
        }

        if(newsDeskItems.contains("\"Politics\"")) {
            cbPolitics.setChecked(true);
        } else {
            cbPolitics.setChecked(false);
        }

        if(newsDeskItems.contains("\"Science\"")) {
            cbScience.setChecked(true);
        } else {
            cbScience.setChecked(false);
        }

        dateStr = getIntent().getStringExtra("dateStr");
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(dateStr);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();


        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbBusiness:
                if (checked) {
                    newsDeskItems.add("\"Business\"");
                } else if (newsDeskItems.contains("\"Business\"")){
                    newsDeskItems.remove("\"Business\"");
                }
                break;
            case R.id.cbCulture:
                if (checked) {
                    newsDeskItems.add("\"Culture\"");
                } else if (newsDeskItems.contains("\"Culture\"")){
                    newsDeskItems.remove("\"Culture\"");
                }
                break;
            case R.id.cbPolitics:
                if (checked) {
                    newsDeskItems.add("\"Politics\"");
                } else if (newsDeskItems.contains("\"Politics\"")){
                    newsDeskItems.remove("\"Politics\"");
                }
                break;
            case R.id.cbScience:
                if (checked) {
                    newsDeskItems.add("\"Science\"");
                } else if (newsDeskItems.contains("\"Science\"")){
                    newsDeskItems.remove("\"Science\"");
                }
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        sort = (String) parent.getItemAtPosition(pos);
        spinnerPos = pos;

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateStr = monthOfYear + "/" + dayOfMonth + "/" + year;
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(dateStr);
        // Construct the desired format (YYYYMMDD)
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
// Format the calendar object into the desired format
        beginDate = format.format(c.getTime());
// This returns => "20160405"
    }

    public void onSubmit(View v) {
        Intent i = new Intent();
        if (filters == null) {
            filters = new SearchFilters();
        }
        if (!newsDeskItems.isEmpty()) {
            filters.setNewsDeskItems(newsDeskItems);
        }
        if (!sort.equals("")) {
            filters.setSort(sort);
        }
        if(!beginDate.equals("")) {
            filters.setBeginDate(beginDate);
        }

        i.putExtra("filters", filters);
        i.putExtra("spinnerPos", spinnerPos);
        i.putExtra("dateStr", dateStr);
        setResult(RESULT_OK, i);
        finish();
    }
}
