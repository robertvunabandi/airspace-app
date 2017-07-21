package com.codepath.rawr.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.rawr.R;
import com.codepath.rawr.SearchResultsActivity;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SendReceiveFragment extends Fragment {

    public SendReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_receive, container, false);



        final EditText et_from = (EditText) v.findViewById(R.id.et_from);
        final EditText et_to = (EditText) v.findViewById(R.id.et_to);
        final EditText et_date = (EditText) v.findViewById(R.id.et_date);
        TextInputLayout dateWrapper = (TextInputLayout) v.findViewById(R.id.dateWrapper);
        final TextInputLayout til_from = (TextInputLayout) v.findViewById(R.id.til_from);
        TextInputLayout til_to = (TextInputLayout) v.findViewById(R.id.til_to);
        Button btSearch = (Button) v.findViewById(R.id.bt_search);
        final ImageView ivToggleFilter = (ImageView) v.findViewById(R.id.iv_toggleFilters);
        final ExpandableRelativeLayout erlFilter = (ExpandableRelativeLayout) v.findViewById(R.id.erl_filters);



        ivToggleFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Toggle the expandable view
                erlFilter.toggle();

                // TODO - Change the drawable to either expanded or collapsed
                // TODO - Add filters in XML
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchResultsActivity.class);

                // inside of SearchResultsActivity we will call the database using the
                // *from* *to* and *by* parameters typed in here, and there in SRA we will get
                // back an array of TravelNotices to put into the recycler view

                i.putExtra("from", et_from.getText());
                i.putExtra("to", et_to.getText());
                i.putExtra("by", et_date.getText());

                getContext().startActivity(i);
            }
        });


        // Calendar
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // updateLabel();
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                et_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return v;
    }
}
