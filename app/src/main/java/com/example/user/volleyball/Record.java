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
    private View mView;
    boolean selectMode = false;
    boolean allocate = false;
    String arrow = "->";
    final int NEXT = 5;
    final int WEAPON = 1;
    final int MIDDLE = 2;
    final int RASE = 3;
    final int FREE = 0;
    private int weaponSize=0;
    private int middleSize=0;
    private int raseSize=0;
    private int freeSize=0;
    private int currentPlayer = 0;
    int setCheck = 1;
    float valueXarray[];
    float valueYarray[] ;
    boolean onCreate = false;
    boolean check_all_get = false;
    boolean check_all_lose = false;
    public ArrayList<TextView> tvPointlist ;
    String team_name= "cs";
    String team_name2= "";
    int position = 0;
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
                .commit();
        super.onStop();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment

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
            helper = new MySql(getContext(), "volleyball.db", null, 1);
            settings = getContext().getSharedPreferences("TEAM_ATTACK",getContext().MODE_PRIVATE);
            position = settings.getInt("POSITION",0);
            team_name2 = settings.getString("ENEMY","");
            setHasOptionsMenu(true);
            initTool();
            initImage();
            onCreate = true;
        }

        return rootView;
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
                            for (int i = 0; i < player.length; i++)
                                player[i].start_record();
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
                            if(allocate)
                            showToast(getString(R.string.allocated));

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
    public void setTeam(){

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
                        renewPoint();
                        updateRate(i2);

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
        new AlertDialog.Builder(getContext()).setView(chView)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int w) {
                int time =0;
                for(int i = 0 ; i < check.length; i ++){
                    if(check[i].isChecked()){
                        if(i!= 0 && i!= 4) {
                            addItem(i,check[i].getText().toString(), i <= 3 ? GET : LOSE);
                            time++;
                        }

                    }
                }
                if(time > 0 ) {
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
            setPoint(tvPointlist.get(i),0,i<=2?i+1:i+2);
        }
    }
    public void setPoint(TextView tvPoint,int type,int item_number){
        switch(item_number) {
            case 1 :
                tvPoint.setText(String.valueOf(player[currentPlayer].startBall_get + type <0?
                        0:(player[currentPlayer].startBall_get +=type)));
                break;
            case 2 :
                tvPoint.setText(String.valueOf(player[currentPlayer].attack_get + type<0?
                        0:(player[currentPlayer].attack_get +=type)));
                break;
            case 3 :
                tvPoint.setText(String.valueOf(player[currentPlayer].middle_get + type<0?
                        0:(player[currentPlayer].middle_get +=type)));
                break;
            case 5 :
                tvPoint.setText(String.valueOf(player[currentPlayer].startBall_fail + type<0?
                        0:(player[currentPlayer].startBall_fail +=type)));
                break;
            case 6 :
                tvPoint.setText(String.valueOf(player[currentPlayer].attack_fail + type<0?
                        0:(player[currentPlayer].attack_fail +=type)));
                break;
            case 7 :
                tvPoint.setText(String.valueOf(player[currentPlayer].defense + type<0?
                        0:(player[currentPlayer].defense +=type)));
                break;
            case 8 :
                tvPoint.setText(String.valueOf(player[currentPlayer].touch + type<0?
                        0:(player[currentPlayer].touch +=type)));
                break;
            case 9 :
                tvPoint.setText(String.valueOf(player[currentPlayer].over + type<0?
                        0:(player[currentPlayer].over +=type)));
                break;
            case 10 :
                tvPoint.setText(String.valueOf(player[currentPlayer].middle_fail + type<0?
                        0:(player[currentPlayer].middle_fail +=type)));
                break;
            case 11 :
                tvPoint.setText(String.valueOf(player[currentPlayer].attacked + type<0?
                        0:(player[currentPlayer].attacked +=type)));
                break;
            case 12 :
                tvPoint.setText(String.valueOf(player[currentPlayer].over_attack + type<0?
                        0:(player[currentPlayer].over_attack +=type)));
                break;
            default:
                break;
        }
    }
    class Player{
        int loc = WEAPON;
        int order = 7;
        long duration = 1000;
        private String name = "";
        String list[] ;
        ImageView location = image[0];
        //get
        public int startBall_get = 0,attack_get = 0,middle_get = 0;
        //fail
        public int startBall_fail = 0,attack_fail =0,defense = 0,touch = 0,
                over = 0,middle_fail = 0,attacked = 0,over_attack = 0;
        public int play = 0;
        String select = "SELECT * FROM main.person WHERE team=? and locationint=?";
        Cursor cursor= helper.getReadableDatabase().rawQuery(select,new String[]{"cs","0"});

        Player(ImageView location , int loc,int order){
            this.location = location;
            this.loc = loc;
            this.order = order;

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
        public void setName(){
            cursor.moveToFirst();
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
        public void start(float valueX,float valueY){
            ObjectAnimator animX = ObjectAnimator.ofFloat(location,"x",valueX);

            ObjectAnimator animY = ObjectAnimator.ofFloat(location,"y",valueY);
            animX.setDuration(duration);
            animY.setDuration(duration);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animX,animY);
            animSet.start();
        }

    }

}
