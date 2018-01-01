package com.example.user.volleyball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    public ArrayList<TextView> tvPointlist ;
    String team_name= "cs";
    String team_name2= "";
    int position = 0;
    int currentBoard = 0;
    Board board[]  = new Board[5];
    int run = 0;
    int item[] = new int[13];
    int itemNum = 0;
    public static Record newInstance() {

        Record fragment = new Record();
        Bundle args = new Bundle();

        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onStop() {

        settings.edit()
                .putInt("POSITION", position)
                .putString("ENEMY",team_name2)
                .putBoolean("selectMode",selectMode)
                .putBoolean("allocate",allocate)
                .putBoolean("get",check_all_get)
                .putBoolean("lose",check_all_lose)
                .putInt("score1",board[currentBoard] != null ?board[currentBoard].getName1Score():0)
                .putInt("score2",board[currentBoard] != null ?board[currentBoard].getName2Score():0)
                .putInt("currentBoard",currentBoard)
                .putInt("run",run)
                .putInt("height",rootView.findViewById(R.id.recordRL).getHeight())
                .putInt("width",rootView.findViewById(R.id.recordRL).getWidth())
                .commit();
        super.onStop();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        settings = getContext().getSharedPreferences("TEAM_ATTACK",getContext().MODE_PRIVATE);
        position = settings.getInt("POSITION",0);
        selectMode = settings.getBoolean("selectMode",false);
        allocate = settings.getBoolean("allocate",false);
        currentBoard = settings.getInt("currentBoard",0);
        run = settings.getInt("run",0);
        team_name2 = settings.getString("ENEMY","");
        check_all_get = settings.getBoolean("get",false);
        check_all_lose = settings.getBoolean("lose",false);

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
            if(selectMode)
                initSetting();
            if(allocate) {
                initAllocate();

                float screenHeight = settings.getInt("height",1344);
                float screenWidth = settings.getInt("width",555);
                Log.d("X,Y", screenWidth + "," + screenHeight);
                initXY(screenWidth, screenHeight);
                for (int i = 0; i < player.length; i++)
                    player[i].start_record();
                            //save height here and do whatever you want with it

            }
            onCreate = true;
        }

        return rootView;
    }
    public void initSetting(){

        for(int i = 0 ; i < run ; i++){
            board[i] = new Board(team_name,team_name2,i+1);
        }
        board[currentBoard].setName1Score(settings.getInt("score1",0));
        board[currentBoard].setName2Score(settings.getInt("score2",0));
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

                    case R.id.three:
                        if (!selectMode) {
                            float screenWidth = rootView.findViewById(R.id.recordRL).getWidth();
                            float screenHeight = rootView.findViewById(R.id.recordRL).getHeight();
                            Log.d("X,Y", screenWidth + "," + screenHeight);
                            initXY(screenWidth, screenHeight);
                            run = 3;
                            for (int i = 0; i < player.length; i++)
                                player[i].start_record();
                            for(int i = 0 ; i < run ; i++){
                                board[i] = new Board(team_name,team_name2,i+1);
                            }

                            toolbar.setTitle(R.string.first);
                            initRecordItem();
                        }

                        break;
                    case R.id.five:

                        break;

                    case R.id.recordturn :
                        if(selectMode) {
                            showToast(getResources().getString(R.string.turn));
                            for (int i = 0; i < player.length; i++)
                                player[i].turn();
                        }
                        else
                            showToast(getResources().getString(R.string.please_select_mode));
                        break;
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
                    case R.id.allocate_player:
                        if(selectMode&& !allocate){
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

                        }else if(!selectMode){
                            showToast(getString(R.string.please_select_mode));
                        }else{
                            showToast(getString(R.string.allocated));
                        }
                        break;
                    case R.id.restart:
                        if(selectMode&& allocate){
                            for(int i = 0 ; i < player.length ; i++){
                                player[i].restart(i);
                                image[i].setVisibility(View.INVISIBLE);
                            }
                            for(int i = 0 ; i < run ; i++){
                                board[i].restart();
                            }
                            clearLayoutView((ViewGroup) (((ViewGroup) (((ViewGroup)mView).getChildAt(2))).getChildAt(0)));
                            clearLayoutView((ViewGroup) (((ViewGroup) (((ViewGroup)mView).getChildAt(4))).getChildAt(0)));

                            tvPointlist.clear();
                            itemNum = 0;

                            tvScore1.setText("0");
                            tvScore2.setText("0");
                            currentBoard = 0;
                            selectMode = false;
                            allocate = false;
                            check_all_get = false;
                            check_all_lose = false;
                            showToast(getString(R.string.restart));
                        }else if(!selectMode){
                            showToast(getString(R.string.please_select_mode));
                        }else{
                            showToast(getString(R.string.allocated));
                        }
                }
                return false;
            }
        });


    }

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
    public void initImage(){
        image[0] = (ImageView)rootView.findViewById(R.id.red3);
        image[1] = (ImageView)rootView.findViewById(R.id.black3);
        image[2] = (ImageView)rootView.findViewById(R.id.blue3);
        image[3] = (ImageView)rootView.findViewById(R.id.red4);
        image[4] = (ImageView)rootView.findViewById(R.id.black4);
        image[5] = (ImageView)rootView.findViewById(R.id.blue4);

        for(int i = 0 ; i < player.length ; i++){
            final int i2 = i;
            player[i] = new Player(image[i],i%3 + 1,i);

            image[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!selectMode ){
                        showToast(getString(R.string.please_select_mode));
                    }else if(!allocate){
                        showToast(getString(R.string.please_allocate_player));

                    }else{
                        currentPlayer = i2;
                        updateRate(i2);
                        renewPoint();


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
                            if(check_all_get) {
                                if (index < 4) {
                                    check[0].setChecked(false);
                                    check_all_get = false;

                                }

                            }
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
            check[0].setChecked(true);
            check[4].setChecked(true);

            for (int i = 0; i < check.length; i++) {
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
        //addItem("",LOSE);
        if(mView.getParent()!=null){
            ViewGroup vGroup = (ViewGroup) mView.getParent();
            vGroup.removeView(mView);
        }
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
                if(player[(i+3)%6].play != position ){
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
                        player[i].writeScore(currentBoard);
                        if(board[currentBoard].getFinish() && currentBoard <=5) {
                            for(int k = 0;k< player.length ; k++) {
                                if(k!=i)
                                    player[i].writeScore(currentBoard);
                                player[i].finishRecord();
                            }
                            currentBoard++;
                            toolbar.setTitle(
                                    currentBoard == 1 ? R.string.second:
                                            currentBoard == 2 ?R.string.third:
                                                    (run > 3 ?( currentBoard == 3  ?R.string.forth: R.string.fifth):R.string.totalfinish));
                        }
                        dialogInterface.dismiss();
                    }
                })

                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                }).show();
    }
    final int GET = 10;
    final int LOSE = 9;
    public void addItem(final int item_number , final String item , int type){
        viewId++;
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout linear = (LinearLayout) mView.findViewById(R.id.inflaterLN);
        LinearLayout linear2 = (LinearLayout)mView.findViewById(R.id.inflaterLN2);
        final View mViewChild = inflater.inflate(R.layout.one_item_record,null,false);


        mView.setId(viewId);
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
    public void renewPoint(){
        for(int i = 0 ; i <tvPointlist.size();i++){
            setPoint(tvPointlist.get(i),0,item[i]);
        }
    }
    public void setPoint(TextView tvPoint,int type,int item_number){

        if( !board[currentBoard].getFinish() ) {
            if (item_number < 4) {
                board[currentBoard].setName1Score(board[currentBoard].getName1Score() + type);
                tvScore1.setText(String.valueOf(board[currentBoard].getName1Score()));
            }
            else {
                board[currentBoard].setName2Score(board[currentBoard].getName2Score() + type);
                tvScore2.setText(String.valueOf(board[currentBoard].getName2Score()));

            }

            tvPoint.setText(String.valueOf(player[currentPlayer].recordScore[item_number] + type <0?
                    0:(player[currentPlayer].recordScore[item_number] +=type)));

        }
        if(board[currentBoard].getFinish())
            showToast(getString(R.string.finish));


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
        String select = "SELECT * FROM main.person WHERE team=? and locationint=?";
        Cursor cursor= helper.getReadableDatabase().rawQuery(select,new String[]{"cs","0"});

        Player(ImageView location , int loc,int order){
            this.location = location;
            this.loc = loc;
            this.order = order;
            for(int i = 0 ; i < recordScore.length;i++){
                recordScore[i]=0;
            }

        }
        public void finishRecord(){

            for(int i = 0 ; i < recordScore.length ; i++){
                recordScore[i] = 0;
            }
        }
        public boolean nameList(){
            String sloc = String.valueOf(loc);
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
            for(int j = 0 ; j <= currentBoard ; j++)
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
    class Board{
        int run = 0;
        String name1= "";
        String name2= "";
        int name1Score = 0;
        int name2Score = 0;
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
        public boolean getFinish(){

            return (name1Score >=25 || name2Score >= 25 )&& getScoreMinus() >=2? true : false;
        }
        public int getScoreMinus(){
            return name1Score > name2Score? name1Score-name2Score : name2Score - name1Score;
        }

    }

}
