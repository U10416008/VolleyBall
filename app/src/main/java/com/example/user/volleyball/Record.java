package com.example.user.volleyball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by user on 2017/12/4.
 */

public class Record extends Fragment {
    private View rootView;
    private Toolbar toolbar;
    private ImageView image[] = new ImageView[6];
    private AppCompatCheckBox check[] = new AppCompatCheckBox[13];
    private Player player[] = new Player[6];
    private LinearLayout parentLayout;
    private LinearLayout parentLayout2;
    private View mView;
    boolean selectMode = false;
    final int NEXT = 5;
    final int WEAPON = 0;
    final int MIDDLE = 1;
    final int RASE = 2;
    final int FREE = 3;
    int setCheck = 1;
    float valueXarray[];
    float valueYarray[] ;
    boolean onCreate = false;
    boolean check_all_get = false;
    boolean check_all_lose = false;
    public static Record newInstance() {

        Record fragment = new Record();
        Bundle args = new Bundle();

        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
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
            rootView = inflater.inflate(R.layout.record, container, false);
            mView = inflater.inflate(R.layout.update_record, null);
            parentLayout = (LinearLayout) mView.findViewById(R.id.inflaterLN);
            parentLayout2 = (LinearLayout) mView.findViewById(R.id.inflaterLN2);

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
                            selectMode = true;
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
                }
                return false;
            }
        });


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
            player[i] = new Player(image[i],i%3,i);
            image[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!selectMode){
                        showToast(getString(R.string.please_select_mode));
                    }else{
                        updateRate();
                    }
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

                            }else if(check_all_lose){
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
                for(int i = 0 ; i < check.length; i ++){
                    if(check[i].isChecked()){
                        if(i!= 0 && i!= 4)
                            addItem(check[i].getText().toString(),i<=3? GET:LOSE);
                    }
                }
                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
    int viewId = 0;
    public void updateRate(){
        //addItem("",LOSE);
        if(mView.getParent()!=null){
            ViewGroup vGroup = (ViewGroup) mView.getParent();
            vGroup.removeView(mView);
        }
        new AlertDialog.Builder(getContext()).setView(mView)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int w) {

                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


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
    public void addItem(String item , int type){
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
        ImageButton plus = (ImageButton)mViewChild.findViewById(R.id.button_ADD);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ImageButton minus = (ImageButton)mViewChild.findViewById(R.id.button_MINUS);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvItem.setText(item);
    }
    class Player{
        int loc = WEAPON;
        int order = 7;
        long duration = 1000;
        ImageView location = image[0];
        Player(ImageView location , int loc,int order){
            this.location = location;
            this.loc = loc;
            this.order = order;
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
