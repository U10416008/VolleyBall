package com.example.user.volleyball;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by user on 2017/12/4.
 */

public class Schedule extends Fragment {
    RecyclerView recyclerView;
    DateAdapter adapter;
    private ArrayList<DateSchedule> datelist = new ArrayList<>();
    CompactCalendarView calendarView ;
    private AppBarLayout appBarLayout;
    View rootView;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private int hour = 6;
    private int min = 0;
    private String AM_PM = "PM";
    private int scheduleType = 0;
    public final int ADD = 1;
    public final int UPDATE =2;
    private boolean isExpanded = false;
    private RemindSql remindSql;
    public static Schedule newInstance() {

        Schedule fragment = new Schedule();
        Bundle args = new Bundle();
        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.schedule, container, false);
        remindSql = new RemindSql(getContext(), "date_alert.db", null, 1);
        initRecy();
        appBarLayout = rootView.findViewById(R.id.app_bar_layout);
        initCal();
        setHasOptionsMenu(true);
        initTool();
        initFab();
        initList();
        return rootView;
    }
    public void initList(){
        String select = "SELECT * FROM alert";
        Cursor cursor= remindSql.getReadableDatabase().rawQuery(select,null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                DateSchedule DS = new DateSchedule(getContext(),cursor.getString(1),cursor.getInt(2),
                        cursor.getString(6),cursor.getInt(4),cursor.getInt(3),cursor.getString(5));
                updateList(DS);

            } while (cursor.moveToNext());
        }
    }
    public void initRecy(){
        recyclerView = rootView.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DateAdapter(datelist);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewListener(new DateAdapter.OnRecyclerViewListener(){
            @Override
            public void onItemClick(View view , int position){
                DateSchedule clickDS = datelist.get(position);
                updateSchedule(clickDS,UPDATE);
                Toast.makeText(getContext(),clickDS.getNote()+clickDS.getSchedule()+clickDS.getHour() + ":" + clickDS.getMin() +clickDS.getAM_PM() ,Toast.LENGTH_LONG ).show();
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
    }
    public void initFab() {
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateSchedule(null, ADD);

            }
        });
    }
    public void initTool(){
        Toolbar toolbar = rootView.findViewById(R.id.cltoolbar);

    }
    public void initCal(){
        calendarView = (CompactCalendarView)rootView.findViewById(R.id.calendar);
        calendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);
        calendarView.setShouldDrawDaysHeader(true);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });
        setCurrentDate(new Date());

        final ImageView arrow = rootView.findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = rootView.findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                float rotation = isExpanded ? 0 : 180;
                ViewCompat.animate(arrow).rotation(rotation).start();
                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);

            }

        });

    }
    public void updateSchedule(final DateSchedule cDS,final int type){

        LayoutInflater inflater = getLayoutInflater();
        final View mView = inflater.inflate(R.layout.schedule_add, null);
        final TextView date = (TextView) mView.findViewById(R.id.tvDate);
        final TimePicker time = (TimePicker) mView.findViewById(R.id.timePicker);
        final RadioGroup rGroup = (RadioGroup)mView.findViewById(R.id.radioGroup);
        final EditText note = (EditText)mView.findViewById(R.id.etNote) ;
        if(type == UPDATE) {
            date.setText(cDS.getDate());
            time.setHour(cDS.get24Hour());
            time.setMinute(cDS.getMin());
            rGroup.check(cDS.getSchedule());
            note.setText(cDS.getNote());
        }
        else
            date.setText(getSubtitle());

        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if(hourOfDay < 12) {
                    AM_PM = " AM";

                } else {
                    AM_PM = " PM";
                    hourOfDay=hourOfDay-12;
                }
                hour = hourOfDay;
                min = minute;
            }

        });

        scheduleType = R.id.radioRace;
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioRace:
                        scheduleType = R.id.radioRace;
                        break;
                    case R.id.radioPratice:
                        scheduleType = R.id.radioPratice;
                        break;
                    case R.id.radioOther:
                        scheduleType = R.id.radioOther;
                        break;

                }

            }
        });

        new AlertDialog.Builder(getContext())
                .setView(mView)
                .setPositiveButton(type == ADD?R.string.add :R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(!date.getText().toString().equals("")) {
                            if(type == ADD) {
                                Log.d("ScID",""+scheduleType);
                                DateSchedule DS = new DateSchedule(getContext(),date.getText().toString(),
                                        scheduleType, note.getText().toString(), hour, min, AM_PM);
                                remindSql.add(date.getText().toString(),scheduleType,
                                        min,hour,AM_PM,note.getText().toString());
                                updateList(DS);
                            }else{
                                cDS.setAM_PM(AM_PM);
                                cDS.setMin(min);
                                cDS.setHour(hour);
                                cDS.setNote(note.getText().toString());
                                cDS.setSchedule(scheduleType);
                                remindSql.update(date.getText().toString(),scheduleType,
                                        min,hour,AM_PM,note.getText().toString());
                            }

                        }



                    }
                })
                .setNegativeButton(type == ADD? R.string.back:R.string.delete, type == ADD? null:new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        remindSql.delete(cDS.getDate(),cDS.getSchedule());
                        datelist.remove(cDS);
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    public void updateList(DateSchedule date){
        datelist.add(date);
        adapter.notifyDataSetChanged();
    }
    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (calendarView != null) {
            calendarView.setCurrentDate(date);
        }
    }


    public void setTitle(CharSequence title) {
        TextView tvTitle = rootView.findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    private void setSubtitle(String subtitle) {
        TextView datePickerTextView = rootView.findViewById(R.id.date_picker_text_view);
        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }
    @NonNull
    private String getSubtitle() {
        TextView datePickerTextView = rootView.findViewById(R.id.date_picker_text_view);
        if (datePickerTextView != null) {
            return datePickerTextView.getText().toString();
        }
        return "";
    }
}
