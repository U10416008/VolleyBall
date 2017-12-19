package com.example.user.volleyball;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by user on 2017/12/4.
 */

public class TeamerInfo extends Fragment {
    private SharedPreferences settings;
    private static final String LAST_POSITION = "POSITION";
    private final int PLAYER_ADD = 1;
    private final int PLAYER_INFO = 2;
    private Spinner spinner;
    private ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<CharSequence> locadapter;
    ArrayAdapter<String> adapter;
    private String name = "";
    private View rootView;
    private MySql helper;
    private ListView lv;
    private SimpleCursorAdapter cursorAdapter;
    public static TeamerInfo newInstance() {

        TeamerInfo fragment = new TeamerInfo();
        Bundle args = new Bundle();
        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        settings = getContext().getSharedPreferences("POSITION",getContext().MODE_PRIVATE);
        settings.edit()
                .putInt(LAST_POSITION, spinner.getSelectedItemPosition())
                .commit();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.teamer_info, container, false);
        helper = new MySql(getContext(), "volleyball.db", null, 1);

        initSpinner();
        initList();
        if(list.size()>1) {
            createList();
        }
        return rootView;
    }
    public void initSpinner(){
        spinner = (Spinner)rootView.findViewById(R.id.spTeamName);
        settings = getContext().getSharedPreferences("POSITION",getContext().MODE_PRIVATE);
        int position = settings.getInt(LAST_POSITION,0);

        read();
        list.add("新增");


        locadapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.location, android.R.layout.simple_list_item_1);
        locadapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                //Toast.makeText(getContext(), list.get(position), Toast.LENGTH_SHORT).show();
                if(position == adapter.getCount()-1) {

                    LayoutInflater inflater = getLayoutInflater();
                    final View mView = inflater.inflate(R.layout.team_name, null);

                    new AlertDialog.Builder(getContext())
                            .setView(mView)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    EditText setTv = (EditText) mView.findViewById(R.id.etname);

                                    String team = setTv.getText().toString();
                                    if(!fileContain(team)) {
                                        list.add(list.size() - 1, team);
                                        save(team);
                                        adapter.notifyDataSetChanged();
                                        spinner.setSelection(list.size() - 2);
                                        if(cursorAdapter != null)
                                            refresh();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.back, null).show();
                }else{
                    if(cursorAdapter == null){
                        createList();
                    }else {
                        refresh();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });
    }
    public void refresh(){
        Cursor cursor = helper.getReadableDatabase().query(
                "main.person", null, "team=?", new String[]{spinner.getSelectedItem().toString()},
                null, null, null);

        cursorAdapter.changeCursor(cursor);
    }
    public void createList(){
        Log.d("SPINNER SELECTED",spinner.getSelectedItem().toString());
        String select = "SELECT * FROM main.person WHERE team=? ";
        Cursor cursor= helper.getReadableDatabase().rawQuery(select,new String[]{spinner.getSelectedItem().toString()});
        cursorAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[] {"name", "location"},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        lv.setAdapter(cursorAdapter);
    }
    public void initList(){
        lv = (ListView)rootView.findViewById(R.id.teamerInfo);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                playerInfo(PLAYER_INFO,((Cursor)cursorAdapter.getItem(position)));

            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playerInfo(PLAYER_ADD,null);
            }
        });
    }
    public void playerInfo(final int type ,Cursor cursor){
        LayoutInflater inflater = getLayoutInflater();
        final View mView = inflater.inflate(R.layout.player_name, null);
        final EditText playname = (EditText)mView.findViewById(R.id.etplayername);
        final EditText playHei = (EditText)mView.findViewById(R.id.etheight);
        final EditText playMiss = (EditText)mView.findViewById(R.id.etmiss);
        final Spinner spLoc = (Spinner) mView.findViewById(R.id.etlocation);
        final TextView tvTeam = (TextView)mView.findViewById(R.id.tvteamName);
        tvTeam.setText(spinner.getSelectedItem().toString());
        spLoc.setAdapter(locadapter);
        if(type == PLAYER_INFO){


            playname.setText(cursor.getString(1));
            playHei.setText(String.valueOf(cursor.getDouble(4)));
            playMiss.setText(String.valueOf(cursor.getDouble(5)));
            spLoc.setSelection(cursor.getInt(6));
            Log.d("LOC",""+cursor.getInt(6));
        }

        new AlertDialog.Builder(getContext())
                .setView(mView)
                .setPositiveButton(type == PLAYER_ADD ? R.string.add:R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {


                            if(!playname.getText().toString().equals("") &&
                                    !playHei.getText().toString().equals("") ) {
                                if(type == PLAYER_ADD)
                                 helper.add(playname.getText().toString(),
                                        spinner.getSelectedItem().toString(),
                                        spLoc.getSelectedItemPosition(),
                                        Double.valueOf(playHei.getText().toString()),
                                        ((playMiss.getText().toString().equals(""))? 50.0:Double.valueOf(playMiss.getText().toString())));
                                else
                                        helper.update(playname.getText().toString(),
                                                spinner.getSelectedItem().toString(),
                                                spLoc.getSelectedItemPosition(),
                                                Double.valueOf(playHei.getText().toString()),
                                                ((playMiss.getText().toString().equals(""))? 50.0:Double.valueOf(playMiss.getText().toString())));
                                if (cursorAdapter == null) {
                                    createList();
                                } else {
                                        refresh();
                                }
                            }else{
                                Toast.makeText(getContext(), "Please Completed Name And Height", Toast.LENGTH_SHORT).show();
                            }
                        }
                })
                .setNegativeButton(type == PLAYER_ADD ? R.string.back : R.string.delete, type == PLAYER_ADD ?null:new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        helper.delete(playname.getText().toString());
                        refresh();
                    }
                }).show();
    }
    public void save(String team){
        try{

                FileOutputStream outputfile = getContext().openFileOutput("team.txt", Context.MODE_APPEND);
                DataOutputStream out = new DataOutputStream(outputfile);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                bw.write(team+"\n");
                bw.close();
                Log.d("Success", team);
                Toast.makeText(getContext(), "Save" + team, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Fail",team);
            Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
        }
    }
    public void read(){
        String line = "";
        try{
            FileInputStream inputfile = getContext().openFileInput("team.txt");
            DataInputStream in = new DataInputStream(inputfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                Log.d("LOAD",line);
                list.add(line);
            }
            inputfile.close();
            //Toast.makeText(getContext(),"Read",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Fail","");
            //Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT);
        }
    }
    public boolean fileContain(String team){
        String line = "";
        try{
            FileInputStream inputfile = getContext().openFileInput("team.txt");
            DataInputStream in = new DataInputStream(inputfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {

                if(line.equals(team)){
                    inputfile.close();
                    Log.d("BEEN USED",team);
                    Toast.makeText(getContext(),team+" Has Been Used",Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

            inputfile.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Fail","");
            Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
