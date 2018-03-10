package com.example.user.volleyball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.ArrayList;


/**
 * Created by user on 2017/12/4.
 */

public class Record extends Fragment {
    private SharedPreferences settings;
    private MySql helper;
    private View rootView;
    private Toolbar toolbar;
    private ImageView image[] = new ImageView[6];
    private AppCompatCheckBox check[] = new AppCompatCheckBox[13];
    private Player player[] = new Player[6];
    private LinearLayout parentLayout;
    private LinearLayout parentLayout2;
    private TextView tvScore1;
    private TextView tvScore2;
    private View mView;
    boolean selectMode = false;
    boolean allocate = false;
    String arrow = "->";
    final int NEXT = 5;
    final int WEAPON = 1;
    final int MIDDLE = 2;
    final int RASE = 3;
    final int FREE = 0;
    private int currentPlayer = 0;
    float valueXarray[];
    float valueYarray[] ;
    boolean onCreate = false;
    boolean check_all_get = false;
    boolean check_all_lose = false;
    boolean finish = false;
    public ArrayList<TextView> tvPointlist ;
    String team_name= "cs";
    String team_name2= "";
    int sum[] = new int[6];
    int position = 0;
    int currentBoard = 0;
    Board board[]  = new Board[5];
    int run = 0;
    int item[] = new int[13];
    int total[][] = new int[6][13];
    private int itemNum = 0;
    private int totalMiss=0;
    private int totalGet=0;
    private int winBoard = 0;
    private int miss[] = new int[6];
    private int get[] = new int[6];
    private float height = 0;
    private float width = 0;
    public static Record newInstance() {

        Record fragment = new Record();
        Bundle args = new Bundle();

        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }
    //save values in shared_pref
    @Override
    public void onStop() {
        Log.d("stop","Stop");
        if(selectMode)
            finishBoard(currentPlayer);
        settings.edit()
                .putInt("POSITION", position)
                .putString("MINE",team_name)
                .putString("ENEMY",team_name2)
                .putBoolean("selectMode",selectMode)
                .putBoolean("allocate",allocate)
                .putBoolean("get",check_all_get)
                .putBoolean("lose",check_all_lose)
                .putBoolean("finish",finish)
                .putInt("score1",board[currentBoard] != null ?board[currentBoard].getName1Score():0)
                .putInt("score2",board[currentBoard] != null ?board[currentBoard].getName2Score():0)
                .putInt("currentBoard",currentBoard)
                .putInt("run",run)
                .putInt("height",(int)height)
                .putInt("width",(int)width)
                .commit();
        super.onStop();
    }
    // get shared_pref values when fragment do onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        Log.d("create","create");

        settings = getContext().getSharedPreferences("TEAM_ATTACK",getContext().MODE_PRIVATE);

        position = settings.getInt("POSITION",0);
        selectMode = settings.getBoolean("selectMode",false);
        allocate = settings.getBoolean("allocate",false);
        currentBoard = settings.getInt("currentBoard",0);
        run = settings.getInt("run",0);
        team_name = settings.getString("MINE","cs");
        team_name2 = settings.getString("ENEMY","");
        check_all_get = settings.getBoolean("get",false);
        check_all_lose = settings.getBoolean("lose",false);
        finish = settings.getBoolean("finish",false);

        setRetainInstance(true);
    }


    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {


        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(!onCreate){
            tvPointlist = new ArrayList<>();
            rootView = inflater.inflate(R.layout.record, container, false);
            mView = inflater.inflate(R.layout.update_record, null);
            parentLayout = (LinearLayout) mView.findViewById(R.id.inflaterLN);
            parentLayout2 = (LinearLayout) mView.findViewById(R.id.inflaterLN2);
            tvScore1 = (TextView)rootView.findViewById(R.id.textScore1);
            tvScore2 = (TextView)rootView.findViewById(R.id.textScore2);
            helper = new MySql(getContext(), "volleyball.db", null, 1);

            setHasOptionsMenu(true);
            initTool();
            initImage();
            //如果上次離開app時比賽未結束
            if(!finish) {
                if (selectMode)
                    initSetting();
                if (allocate) {
                    initAllocate();

                    float screenHeight = settings.getInt("height", 1344);
                    float screenWidth = settings.getInt("width", 555);
                    Log.d("X,Y", screenWidth + "," + screenHeight);
                    initXY(screenWidth, screenHeight);
                    for (int i = 0; i < player.length; i++) {
                        player[i].start_record();
                        for(int j = 0 ; j < currentBoard ; j++){
                            player[i].readScore(j);
                            for(int k = 0 ; k < total[i].length ; k++){
                                total[i][k] += player[i].recordScore[k];
                            }
                        }
                    }


                }
            }else{
                //如果上次離開app時已經結束比賽

                selectMode = false;
                allocate = false;
                check_all_get = false;
                check_all_lose = false;
                run = 0;

                currentBoard = 0;
                finish = false;
                for (int i = 0; i < player.length; i++) {
                    for (int j = 0; j< total[i].length; j++) {
                        total[i][j] = 0;
                    }
                }

            }
            //createView will be executed once
            onCreate = true;
        }

        return rootView;
    }
    //初始化上次離開app時比分
    public void initSetting(){
        Log.d("setting","setting");

        for(int i = 0 ; i < run ; i++){
            board[i] = new Board(team_name,team_name2,i+1);
        }
        board[currentBoard].setName1Score(settings.getInt("score1",0));
        tvScore1.setText(String.valueOf(board[currentBoard].getName1Score()));
        board[currentBoard].setName2Score(settings.getInt("score2",0));
        tvScore2.setText(String.valueOf(board[currentBoard].getName2Score()));

        switch(currentBoard) {
            case 0:toolbar.setTitle(R.string.first);break;
            case 1:toolbar.setTitle(R.string.second);break;
            case 2:toolbar.setTitle(R.string.third);break;
            case 3:toolbar.setTitle(R.string.forth);break;
            case 4:toolbar.setTitle(R.string.fifth);break;
            default:

        }
        initRecordItem();
    }
    //初始化上次離開app時的隊員分配
    public void initAllocate(){
        for(int i = 0 ; i < player.length ; i++){
            allocate = player[i].nameList();
            if(!allocate){
                showToast(getString(R.string.not_alloc));
                break;
            }
        }
        if(allocate) {
            showToast(getString(R.string.allocated));

        }


    }
    public void initTool(){
        toolbar = (Toolbar)rootView.findViewById(R.id.recordToolbar);
        toolbar.inflateMenu(R.menu.record_menu);
        toolbar.setTitle(R.string.record);
        toolbar.setNavigationIcon(R.drawable.ic_action_name2);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    //三局
                    case R.id.three:
                        if (!selectMode) {

                            float screenWidth = rootView.findViewById(R.id.recordRL).getWidth();
                            float screenHeight = rootView.findViewById(R.id.recordRL).getHeight();
                            Log.d("X,Y", screenWidth + "," + screenHeight);
                            initXY(screenWidth, screenHeight);
                            run = 3;
                            for (int i = 0; i < player.length; i++) {
                                //分配對員位置
                                player[i].start_record();
                            }
                            for(int i = 0 ; i < run ; i++){
                                board[i] = new Board(team_name,team_name2,i+1);
                            }

                            toolbar.setTitle(R.string.first);
                            //選擇要記錄項目
                            initRecordItem();
                        }

                        break;
                    case R.id.five:
                        if (!selectMode) {
                            float screenWidth = rootView.findViewById(R.id.recordRL).getWidth();
                            float screenHeight = rootView.findViewById(R.id.recordRL).getHeight();
                            Log.d("X,Y", screenWidth + "," + screenHeight);
                            initXY(screenWidth, screenHeight);
                            run = 5;
                            for (int i = 0; i < player.length; i++)
                                player[i].start_record();
                            for(int i = 0 ; i < run ; i++){
                                board[i] = new Board(team_name,team_name2,i+1);
                            }

                            toolbar.setTitle(R.string.first);
                            initRecordItem();
                        }
                        break;

                    case R.id.recordturn :
                        //位置輪轉
                        if(selectMode) {
                            showToast(getResources().getString(R.string.turn));
                            for (int i = 0; i < player.length; i++)
                                player[i].turn();
                        }
                        else
                            showToast(getResources().getString(R.string.please_select_mode));
                        break;

                        //紀錄敵隊與選擇自己隊伍
                    case R.id.recordName:

                        final View nameView = getLayoutInflater().inflate(R.layout.set_team_name,null);
                        final EditText editName = (EditText)nameView.findViewById(R.id.etTeam);
                        final Spinner spinnerName = (Spinner)nameView.findViewById(R.id.spMyTeam);
                        ArrayList<String> list = new ArrayList<>();
                        for(int i = 0 ; i < ((MainActivity)getActivity()).list.size()-1;i++){
                            list.add(((MainActivity)getActivity()).list.get(i));
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, list);
                        adapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        spinnerName.setAdapter(adapter);
                        spinnerName.setSelection(position);
                        team_name = spinnerName.getSelectedItem().toString();
                        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(!allocate) {

                                }else{
                                    spinnerName.setSelection(spinnerName.getSelectedItemPosition());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        editName.setText(team_name2);
                        new AlertDialog.Builder(getContext()).setView(nameView)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int w) {
                                        team_name2 = editName.getText().toString();
                                        if(!allocate) {
                                            team_name = spinnerName.getSelectedItem().toString();


                                            position = spinnerName.getSelectedItemPosition();
                                        }else{
                                            showToast(getString(R.string.allocated));
                                        }
                                        toolbar.setTitle(team_name
                                                + arrow + team_name2);
                                        Log.d("teamName",team_name);
                                    }
                                }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                            }).show();
                        break;
                        //分配隊員
                    case R.id.allocate_player:
                        if(selectMode&& !allocate){
                            for(int i = 0 ; i < player.length ; i++){

                                allocate = player[i].nameList();
                                player[i].restart(i);
                                if(!allocate){
                                    showToast(getString(R.string.not_alloc));
                                    break;
                                }
                            }
                            if(allocate) {
                                showToast(getString(R.string.allocated));

                            }

                        }else if(!selectMode){
                            showToast(getString(R.string.please_select_mode));
                        }else{
                            showToast(getString(R.string.allocated));
                        }
                        break;
                    case R.id.restart:
                        restart();
                        break;
                }
                return false;
            }
        });


    }
    public void restart(){

            for(int j = 0 ;  j< player.length ; j++) {
                for (int i = 0; i < total[j].length; i++) {
                    total[j][i] = 0;
                }
            }
            for(int i = 0 ; i < player.length ; i++){
                if(image[i]!= null)
                    image[i].setVisibility(View.INVISIBLE);
            }
            for(int i = 0 ; i < run ; i++){
                if(board[i]!=null)
                    board[i].restart();
            }
            if(mView!=null) {
                clearLayoutView((ViewGroup)parentLayout);
                clearLayoutView((ViewGroup)parentLayout2);
                //clearLayoutView((ViewGroup)((ViewGroup) (((ViewGroup) (((ViewGroup) mView).getChildAt(1))).getChildAt(0))).getChildAt(1));
                //clearLayoutView((ViewGroup) ((ViewGroup)((ViewGroup) (((ViewGroup) (((ViewGroup) mView).getChildAt(1))).getChildAt(1))).getChildAt(1)).getChildAt(0));
            }
            tvPointlist.clear();
            itemNum = 0;

            tvScore1.setText("0");
            tvScore2.setText("0");
            currentBoard = 0;
            selectMode = false;
            allocate = false;
            check_all_get = false;
            check_all_lose = false;
            finish = false;
            showToast(getString(R.string.restart));


    }
    //清除原本的紀錄項目
    private void clearLayoutView(ViewGroup v) {
        boolean doBreak = false;
        while (!doBreak) {
            int childCount = v.getChildCount();
            Log.d("count",childCount+"");
            int i;
            for(i=0; i<childCount; i++) {
                View currentChild = v.getChildAt(i);
                // Change ImageView with your desired type view
                if (currentChild instanceof LinearLayout) {
                    v.removeView(currentChild);
                    break;
                }
            }

            if (i == childCount) {
                doBreak = true;
            }
        }
    }
    public void initXY(float screenWidth,float screenHeight){
        height = screenHeight;
        width = screenWidth;
        valueXarray = new float[]
                {screenWidth * 0.35f ,screenWidth * 0.4f,screenWidth * 0.35f,
                        screenWidth * 0.15f,screenWidth * 0.25f,screenWidth * 0.15f};
        valueYarray = new float[]
                {screenHeight * 0.2f,screenHeight * 0.4f,screenHeight * 0.6f,
                        screenHeight * 0.6f,screenHeight * 0.4f,screenHeight * 0.2f
                };

    }
    public void showToast(String toast){
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
    //對應image 與player
    public void initImage(){
        image[0] = (ImageView)rootView.findViewById(R.id.red3);
        image[1] = (ImageView)rootView.findViewById(R.id.black3);
        image[2] = (ImageView)rootView.findViewById(R.id.blue3);
        image[3] = (ImageView)rootView.findViewById(R.id.red4);
        image[4] = (ImageView)rootView.findViewById(R.id.black4);
        image[5] = (ImageView)rootView.findViewById(R.id.blue4);

        for(int i = 0 ; i < player.length ; i++){
            final int i2 = i;
            player[i] = new Player(image[i],i%3 + 1,i,i);

            image[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!selectMode ){
                        showToast(getString(R.string.please_select_mode));
                    }else if(!allocate){
                        showToast(getString(R.string.please_allocate_player));

                    }else{
                        currentPlayer = i2;
                        if(!finish) {
                            updateRate(currentPlayer);
                            renewPoint();
                        }else{
                            drawCanvas(currentPlayer);

                        }


                    }
                }
            });
            image[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    return false;
                }
            });
        }
    }
    //紀錄得分率
    public void recordMiss(){
        int sumGet = 0;
        for(int lplayer = 0 ; lplayer < player.length;lplayer++){
            for(int i = 5 ; i < total[lplayer].length ; i++){
                sum[lplayer] += total[lplayer][i];
                Log.d("total",total[lplayer][i]+"");
            }
            for(int i = 1 ; i < 4 ; i++) {
                sumGet += total[lplayer][i];
                helper.addTotal(player[lplayer].getName(), team_name, sumGet, totalGet - get[lplayer]);
            }
            sumGet = 0;
        }
    }
    //畫比賽結束後隊員表現的圓餅圖
    public void drawCanvas(int lplayer){
        final View pieChart = getLayoutInflater().inflate(R.layout.canvas,null);
        PieChart pie = (PieChart)pieChart.findViewById(R.id.piechart);
        SecureRandom sr = new SecureRandom();
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<>();


        for(int i = 5 ; i < total[lplayer].length; i++){
            if(total[lplayer][i] != 0 ) {
                yvalues.add(new Entry((((float) total[lplayer][i] / (float) sum[lplayer]) * 100.f), i - 5));
                Log.d("entry", ((float) total[lplayer][i] / (float) sum[lplayer]) * 100.f + "");
                xVals.add(check[i].getText().toString());
                colors.add(Color.rgb(sr.nextInt(256), sr.nextInt(256), sr.nextInt(256)));
            }

        }
        PieDataSet dataSet = new PieDataSet(yvalues, " ");
        dataSet.setColors(colors);
        dataSet.setHighlightEnabled(true);
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        PieData data = new PieData(xVals, dataSet);

        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        pie.setData(data);
        pie.setDrawHoleEnabled(true);
        pie.setTransparentCircleRadius(35f);
        pie.setHoleRadius(25f);
        pie.setDescription("");
        Legend legend = pie.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(3);
        //legend.setTextSize(12f);
        pie.setDescription("Total MissBalls : "+String.valueOf(sum[lplayer]));
        pie.setDescriptionTextSize(16f);
        new AlertDialog.Builder(getContext()).setView(pieChart).setTitle(player[lplayer].getName())
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {


                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        for(int i = 0; i < player.length; i++){
                            for(int k = 0 ; k < currentBoard ; k++)
                                player[i].writeScore(k);
                        }
                    }
                }).show();

    }
    public void initRecordItem(){
        final View chView = getLayoutInflater().inflate(R.layout.check_record_item,null);
        check[0] = (AppCompatCheckBox)chView.findViewById(R.id.all_get);
        check[1] = (AppCompatCheckBox)chView.findViewById(R.id.startBallSuccess);
        check[2] = (AppCompatCheckBox)chView.findViewById(R.id.attackSuccess);
        check[3] = (AppCompatCheckBox)chView.findViewById(R.id.middleSuccess);

        check[4] = (AppCompatCheckBox)chView.findViewById(R.id.all_lose);
        check[5] = (AppCompatCheckBox)chView.findViewById(R.id.startBallLose);
        check[6] = (AppCompatCheckBox)chView.findViewById(R.id.attackLose);
        check[7] = (AppCompatCheckBox)chView.findViewById(R.id.defense);
        check[8] = (AppCompatCheckBox)chView.findViewById(R.id.touch);
        check[9] = (AppCompatCheckBox)chView.findViewById(R.id.over);
        check[10] = (AppCompatCheckBox)chView.findViewById(R.id.middleLose);
        check[11] = (AppCompatCheckBox)chView.findViewById(R.id.be_attacked);
        check[12] = (AppCompatCheckBox)chView.findViewById(R.id.over_att);
        check[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!check_all_get) {
                    for (int i = 1; i < 4; i++)
                        check[i].setChecked(check[0].isChecked());
                    check_all_get = true;
                }
            }
        });
        check[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!check_all_lose) {
                    for (int i = 5; i < check.length; i++)
                        check[i].setChecked(check[4].isChecked());
                    check_all_lose = true;
                }
            }
        });
        for(int i  = 1 ; i  < check.length;i++){
            if(i!=4 ){
                final int index = i ;
                check[index].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if(!check[index].isChecked()){
                            //得分項目全選
                            if(check_all_get) {
                                if (index < 4) {
                                    check[0].setChecked(false);
                                    check_all_get = false;

                                }

                            }
                            //失分項目全選
                            if(check_all_lose){
                                if (index > 4) {
                                    check[4].setChecked(false);
                                    check_all_lose = false;

                                }
                            }

                        }
                    }
                });
            }
        }
        if(!selectMode) {
            new AlertDialog.Builder(getContext()).setView(chView)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int w) {
                            int time = 0;
                            for (int i = 0; i < check.length; i++) {
                                if (check[i].isChecked()) {
                                    if (i != 0 && i != 4) {
                                        //增加紀錄項目
                                        addItem(i, check[i].getText().toString(), i <= 3 ? GET : LOSE);
                                        time++;
                                    }

                                }
                            }
                            if (time > 0) {
                                selectMode = true;
                            }
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                }
            }).show();
        }else{


            for (int i = 0; i < check.length; i++) {
                check[i].setChecked(true);
                if (check[i].isChecked()) {
                    if (i != 0 && i != 4) {
                        addItem(i, check[i].getText().toString(), i <= 3 ? GET : LOSE);
                    }

                }
            }
        }
    }
    int viewId = 0;
    public void updateRate(final int i){
        //判斷mview是否有parent
        if(mView.getParent()!=null){
            ViewGroup vGroup = (ViewGroup) mView.getParent();
            vGroup.removeView(mView);
        }
        //可選擇隊員的選單
        final Spinner myPlayer = (Spinner)mView.findViewById(R.id.myTeamPlayer);
        ArrayAdapter adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_list_item_1, player[i].list);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        myPlayer.setAdapter(adapter);
        myPlayer.setSelection(player[i].play);
        myPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //如果要換的隊員不在場上
                if(player[(i+3)%6].play != position ){

                    int sum = 0 ;
                    for(int k = 1 ; k < 4;k++){
                        sum += player[i].recordScore[k];
                    }

                    helper.addTotal(player[i].getName(),team_name,sum,totalGet-get[i]);
                    miss[i] = totalMiss;
                    get[i] = totalGet;
                    myPlayer.setSelection(position);
                    player[i].play = position;
                    player[i].turnPlayer(position);
                    player[i].readScore(currentBoard);

                    renewPoint();

                }else{
                    myPlayer.setSelection(player[i].play);
                    showToast(getString(R.string.player_used));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        new AlertDialog.Builder(getContext()).setView(mView)
                .setPositiveButton(R.string.finish_record, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int w) {
                        dialogInterface.dismiss();
                    }
                })

                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finishBoard(i);
                    }
                }).show();
    }
    final int GET = 10;
    final int LOSE = 9;
    //結束一局
    public void finishBoard(int i){
        player[i].writeScore(currentBoard);
        //這場結束
        if(currentBoard < run && board[currentBoard].getFinish()) {
            for(int k = 0;k< player.length ; k++) {
                if(k!=i)
                    player[k].writeScore(currentBoard);
                player[k].finishRecord();
                renewPoint();

            }

            currentBoard++;
            if(currentBoard <run)
                showToast(getString(R.string.finish));
            toolbar.setTitle(
                    currentBoard == 1 ? R.string.second:
                            currentBoard == 2 ?R.string.third:
                                    (run > 3 ?( currentBoard == 3  ?R.string.forth: R.string.fifth):R.string.totalfinish));
            tvScore1.setText("0");
            tvScore2.setText("0");
            if(currentBoard >= run) {
                //計算得分率
                recordMiss();
                finish = true;
            }
        }
    }
    //新增紀錄項目
    public void addItem(final int item_number , final String item , int type){
        viewId++;
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout linear = (LinearLayout) mView.findViewById(R.id.inflaterLN);
        LinearLayout linear2 = (LinearLayout)mView.findViewById(R.id.inflaterLN2);
        final View mViewChild = inflater.inflate(R.layout.one_item_record,null,false);


        mView.setId(viewId);
        //動態新增
        if(type == GET)
            parentLayout.addView(mViewChild);
        else
            parentLayout2.addView(mViewChild);
        //linear.addView(mViewChild);

        final TextView tvItem = (TextView)mViewChild.findViewById(R.id.tvItem);
        final TextView tvPoint = (TextView)mViewChild.findViewById(R.id.tvPoint);
        tvPointlist.add(tvPoint);
        this.item[itemNum] = item_number;
        itemNum++;
        setPoint(tvPoint,0,item_number);
        ImageButton plus = (ImageButton)mViewChild.findViewById(R.id.button_ADD);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPoint(tvPoint,1,item_number);
            }
        });
        ImageButton minus = (ImageButton)mViewChild.findViewById(R.id.button_MINUS);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPoint(tvPoint,-1,item_number);
            }
        });
        tvItem.setText(item);
    }
    //更新每個人的狀況
    public void renewPoint(){
        for(int i = 0 ; i <tvPointlist.size();i++){
            setPoint(tvPointlist.get(i),0,item[i]);
        }
    }
    //紀錄雙方分數 紀錄每個人的狀況
    public void setPoint(TextView tvPoint,int type,int item_number){
        if(player[currentPlayer].recordScore[item_number] + type >=0) {
            if (currentBoard < run && !board[currentBoard].getFinish()) {
                if (item_number < 4) {

                    board[currentBoard].setName1Score(board[currentBoard].getName1Score() + type);
                    tvScore1.setText(String.valueOf(board[currentBoard].getName1Score()));
                    totalGet += type;
                } else {
                    board[currentBoard].setName2Score(board[currentBoard].getName2Score() + type);
                    tvScore2.setText(String.valueOf(board[currentBoard].getName2Score()));
                    totalMiss += type;
                }

                tvPoint.setText(String.valueOf(player[currentPlayer].recordScore[item_number] += type));

            }
        }



    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    class Player{
        int loc = WEAPON;
        int order = 7;
        long duration = 1000;
        private String name = "";
        String list[] ;
        ImageView location = image[0];
        //get for i < 4 fail for i >4
        public int recordScore[] = new int[13];



        public int play = 0;
        private int player = 0;
        String select = "SELECT * FROM main.person WHERE team=? and locationint=?";
        Cursor cursor= helper.getReadableDatabase().rawQuery(select,new String[]{"cs","0"});

        Player(ImageView location , int loc,int order,int player){
            this.location = location;
            this.loc = loc;
            this.order = order;
            this.player = player;
            for(int i = 0 ; i < recordScore.length;i++){
                recordScore[i]=0;
            }

        }
        public void finishRecord(){

            for(int i = 0 ; i < recordScore.length ; i++){
                total[player][i] += recordScore[i];
                recordScore[i] = 0;

            }
        }
        public boolean nameList(){
            String sloc = String.valueOf(loc);
            //從資料庫中拉對應的資料出來當作可選隊員的名單
            select = "SELECT * FROM main.person WHERE team=? and locationint=?";
            cursor= helper.getReadableDatabase().rawQuery(
                    select,new String[]{team_name,sloc});

            ArrayList<String> nameList = new ArrayList<>();

            if(cursor.getCount()>=2) {
                cursor.moveToFirst();
                do {

                        nameList.add(cursor.getString(1));
                        Log.d("teamName", cursor.getString(2));
                        Log.d("nameCursor", cursor.getString(1));

                } while (cursor.moveToNext());


                    list = nameList.toArray(new String[nameList.size()]);
                    if (list.length >= 2) {
                        switch (loc) {
                            case FREE:
                                name = list[0];
                                break;
                            case WEAPON:
                                name = list[play = order >= 3 ? 1 : 0];
                                break;
                            case MIDDLE:
                                name = list[play = order >= 3 ? 1 : 0];
                                break;
                            case RASE:
                                name = list[play = order >= 3 ? 1 : 0];
                                break;
                            default:
                                break;
                        }
                        Log.d("name", name);
                    }
                return true;

            }
            return  false;
        }
        public String getName(){
            return name;
        }
        //換隊員
        public void turnPlayer(int player){
            name = list[player];
        }
        public void anim(int backOrNext){
            float valueX = 0f;
            float valueY = 0f;

            if(backOrNext == NEXT) {
                valueX = valueXarray[order];
                valueY = valueYarray[order];
            }else{
                valueX = valueXarray[--order];
                valueY = valueYarray[--order];
            }
            start(valueX,valueY);
        }
        //轉一個
        public void turn(){
            order = (++order)%6;
            float valueX = valueXarray[order];
            float valueY = valueYarray[order];
            start(valueX,valueY);
        }

        public void start_record(){
            location.setVisibility(View.VISIBLE);
            float valueX = valueXarray[order];
            float valueY = valueYarray[order];
            start(valueX,valueY);
        }
        public void restart(int order){
            finishRecord();
            this.order = order;

            float valueX = valueXarray[order];
            float valueY = valueYarray[order];
            start(valueX,valueY);
            for(int j = 0 ; j <= 4 ; j++)
                writeScore(j);

        }
        public void start(float valueX,float valueY){
            ObjectAnimator animX = ObjectAnimator.ofFloat(location,"x",valueX);

            ObjectAnimator animY = ObjectAnimator.ofFloat(location,"y",valueY);
            animX.setDuration(duration);
            animY.setDuration(duration);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animX,animY);
            animSet.start();
        }
        //讀取每個人的分數檔
        public void readScore(int currentBoard){
            String line = "";
            try{
                FileInputStream inputfile = getContext().openFileInput(team_name + "_"+name + "_" + String.valueOf(currentBoard) +".txt");
                DataInputStream in = new DataInputStream(inputfile);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                int i = 0 ;
                while ((line = br.readLine()) != null) {
                    Log.d("LOAD",line);
                    if(i<13)
                        recordScore[i] = Integer.valueOf(line);
                    i++;

                }
                inputfile.close();
                //Toast.makeText(getContext(),"Read",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                for(int i = 0 ; i < recordScore.length ; i++){
                    recordScore[i] = 0;
                }
                Log.d("Fail",name);
                Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT);
            }
        }
        //寫入每個人的分數檔
        public void writeScore(int currentBoard){
            try{

                FileOutputStream outputfile = getContext().openFileOutput(team_name + "_"+name + "_" + String.valueOf(currentBoard)+".txt", Context.MODE_PRIVATE);
                DataOutputStream out = new DataOutputStream(outputfile);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                for(int i = 0 ; i < recordScore.length ; i++){
                    bw.write(String.valueOf(recordScore[i])+"\n");
                }

                bw.close();
                Log.d("Success", name);
                //Toast.makeText(getContext(), "Save" + name, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Fail",name);
                Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
            }
        }

    }
    //局
    class Board{
        int run = 0;
        String name1= "";
        String name2= "";
        int name1Score = 0;
        int name2Score = 0;
        private int win = 0;
        Board(String name1,String name2,int run){
            this.name1 = name1;
            this.name2 = name2;
            this.run = run;
        }
        public int getName1Score(){
            return name1Score;
        }
        public void setName1Score(int name1Score){
            this.name1Score = name1Score;
        }
        public int getName2Score(){
            return name2Score;
        }
        public void setName2Score(int name2Score){
            this.name2Score = name2Score;
        }
        public String getName1(){
            return name1;
        }
        public void setName1(String name1){
            this.name1 = name1;
        }
        public String getName2(){
            return name2;
        }
        public void setName2(String name2){
            this.name2 = name2;
        }
        public void restart(){
            name1Score = 0;
            name2Score = 0;
        }
        //判斷這局是否結束
        public boolean getFinish(){

            return (name1Score >=25 || name2Score >= 25 )&& getScoreMinus() >=2? true : false;
        }
        public int getScoreMinus(){
            return name1Score > name2Score? name1Score-name2Score : name2Score - name1Score;
        }
        public int getWin(){
            if(getFinish()){
                if(name2Score > name1Score){
                    win = 1;//lose
                }else{
                    win = 2;//win
                }
            }else{
                win = 0;//still
            }
            return win;
        }


    }


}
