package com.example.user.volleyball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by user on 2017/12/4.
 */

public class Team extends Fragment {
    Toolbar mToolbar;
    ImageView weapon1;
    ImageView weapon2;
    ImageView rase1;
    ImageView rase2;
    ImageView middle1;
    ImageView middle2;
    View rootView;
    Player player[] = new Player[6];
    long duration = 1000;
    int location = 0;
    final int NEXT = 1;
    final int BACK = 6;
    final int weapon = 0;
    final int middle = 1;
    final int rase = 2;
    final int free = 3;
    boolean attackMode = false;
    int attack = 0;
    float valueXarray[][];
    float valueYarray[][] ;
    boolean selectMode = false;
    public static Team newInstance() {

        Team fragment = new Team();
        Bundle args = new Bundle();
        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.team, container, false);
        /*rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //float screenWidth = getResources().getDisplayMetrics().widthPixels;
                //float screenHeight = getResources().getDisplayMetrics().heightPixels;
                float screenWidth = rootView.findViewById(R.id.rl).getWidth();
                float screenHeight = rootView.findViewById(R.id.rl).getHeight();
                Log.d("X,Y",screenWidth +","+screenHeight);

                initXY(screenWidth ,screenHeight);
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/

        setHasOptionsMenu(true);
        initTool();
        initImage();
        initFab();
        return rootView;
    }
    public void initXY(float screenWidth , float screenHeight){
        valueXarray = new float[][]{
                //D
                //weaponfrontNext
                {screenWidth*0.2f,screenWidth*0.2f,screenWidth*0.2f,screenWidth*0.25f,screenWidth*0.25f,screenWidth*0.25f,
                        //weaponfrontBack
                        screenWidth*0.15f,screenWidth*0.25f,screenWidth*0.7f,screenWidth*0.75f,screenWidth*0.5f,screenWidth*0.25f},
                //DmiddlefrontNext
                {screenWidth*0.5f,screenWidth*0.5f,screenWidth*0.5f,screenWidth*0.45f,screenWidth*0.45f,screenWidth*0.45f,
                        //middlefrontBack
                        screenWidth*0.15f,screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.65f,screenWidth*0.5f,screenWidth*0.2f},
                //rasefrontNext
                {screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.65f,screenWidth*0.65f,screenWidth*0.65f,
                        //rasefrontBack
                        screenWidth*0.2f,screenWidth*0.5f,screenWidth*0.8f,screenWidth*0.8f,screenWidth*0.5f,screenWidth*0.2f},
                //A
                //weaponbackNext
                {screenWidth*0.2f,screenWidth*0.2f,screenWidth*0.2f,screenWidth*0.25f,screenWidth*0.25f,screenWidth*0.25f,
                        //weaponbackBack
                        screenWidth*0.2f,screenWidth*0.45f,screenWidth*0.5f,screenWidth*0.7f,screenWidth*0.45f,screenWidth*0.25f},
                //middlebackNext
                {screenWidth*0.5f,screenWidth*0.5f,screenWidth*0.5f,screenWidth*0.45f,screenWidth*0.45f,screenWidth*0.45f,
                        //middlebackBack
                        screenWidth*0.4f,screenWidth*0.5f,screenWidth*0.55f,screenWidth*0.7f,screenWidth*0.45f,screenWidth*0.25f},
                //rasebackNext
                {screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.65f,screenWidth*0.65f,screenWidth*0.65f,
                        //rasebackBack
                        screenWidth*0.4f,screenWidth*0.45f,screenWidth*0.7f,screenWidth*0.7f,screenWidth*0.45f,screenWidth*0.25f}};

        valueYarray = new float[][]{
                //D
                //weaponfrontNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.6f,screenHeight*0.6f,screenHeight*0.6f,
                        //weaponfrontBack
                        screenHeight*0.5f,screenHeight*0.5f,screenHeight*0.5f,screenHeight*0.75f,screenHeight*0.6f,screenHeight*0.75f},
                //middlefrontNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.75f,screenHeight*0.75f,screenHeight*0.75f,
                        //weaponfrontBack
                        screenHeight*0.5f,screenHeight*0.5f,screenHeight*0.5f,screenHeight*0.75f,screenHeight*0.6f,screenHeight*0.75f},
                //rasefrontNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.6f,screenHeight*0.6f,screenHeight*0.6f,
                        //weaponfrontBack
                        screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.75f,screenHeight*0.6f,screenHeight*0.75f},
                //A
                //weaponbackNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.6f,screenHeight*0.6f,screenHeight*0.6f,
                        //weaponfrontBack
                        screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.85f,screenHeight*0.6f,screenHeight*0.6f},
                //middlebackNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.75f,screenHeight*0.75f,screenHeight*0.75f,
                        //weaponfrontBack
                        screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.85f,screenHeight*0.75f,screenHeight*0.75f},
                //rasebackNext
                {screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.6f,screenHeight*0.6f,screenHeight*0.6f,
                        //weaponfrontBack
                        screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.35f,screenHeight*0.85f,screenHeight*0.6f,screenHeight*0.6f}};
    }
    public void initTool(){
        mToolbar = (Toolbar) rootView.findViewById(R.id.actionToolbar);
        mToolbar.setTitle(R.string.mode);
        mToolbar.setNavigationIcon(R.drawable.ic_action_name2);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(attackMode){
                    showToast(getResources().getString(R.string.defense));
                    mToolbar.setNavigationIcon(R.drawable.ic_action_name2);
                    attack = 0;
                    attackMode = false;
                }else{
                    showToast(getResources().getString(R.string.attack));
                    mToolbar.setNavigationIcon(R.drawable.ic_action_name3);
                    attack = 3;
                    attackMode = true;
                }
                if(selectMode) {
                    showToast(getResources().getString(R.string.turn));
                    for (int i = 0; i < player.length; i++)
                        player[i].changeMode();
                }
            }
        });
        mToolbar.inflateMenu(R.menu.team_module);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.double_rase:
                        mToolbar.setTitle(R.string.action_settings);
                        showToast(getResources().getString(R.string.action_settings));
                        float screenWidth = rootView.findViewById(R.id.rl).getWidth();
                        float screenHeight = rootView.findViewById(R.id.rl).getHeight();
                        Log.d("X,Y",screenWidth +","+screenHeight);
                        initXY(screenWidth ,screenHeight);
                        selectMode = true;
                        for(int i = 0 ; i < player.length ; i++)
                            player[i].changeMode();
                        break;
                    case R.id.single_rase:
                        mToolbar.setTitle(R.string.action_settings2);
                        showToast(getResources().getString(R.string.action_settings2));
                        break;

                    case R.id.turn :
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
    public void initImage(){
        weapon1 = (ImageView)rootView.findViewById(R.id.red1);
        player[0] = new Player(weapon1,weapon,0);

        weapon2 = (ImageView)rootView.findViewById(R.id.red2);
        player[3] = new Player(weapon2,weapon,3);

        rase1 = (ImageView)rootView.findViewById(R.id.black1);
        player[2] = new Player(rase1,rase,2);

        rase2 = (ImageView)rootView.findViewById(R.id.black2);
        player[5] = new Player(rase2,rase,5);

        middle1 = (ImageView)rootView.findViewById(R.id.blue1);
        player[1] = new Player(middle1,middle,1);

        middle2 = (ImageView)rootView.findViewById(R.id.blue2);
        player[4] = new Player(middle2,middle,4);

    }
    public void initFab(){
        FloatingActionButton fabNext = (FloatingActionButton) rootView.findViewById(R.id.fabNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(selectMode)
                    for(int i = 0 ; i < player.length ; i++)
                        player[i].anim(NEXT);
                else
                    showToast(getResources().getString(R.string.please_select_mode));
            }
        });
        FloatingActionButton fabRef = (FloatingActionButton) rootView.findViewById(R.id.fabRefresh);
        fabRef.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(selectMode)
                    for(int i = 0 ; i < player.length ; i++)
                        player[i].anim(BACK);
                else
                    showToast(getResources().getString(R.string.please_select_mode));
            }
        });
    }
    public void showToast(String toast){
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
    class Player{
        int loc = weapon;
        int order = 7;
        ImageView location = weapon1;
        Player(ImageView location , int loc,int order){
            this.location = location;
            this.loc = loc;
            this.order = order;
        }

        public void anim(int backOrNext){
            float valueX = 0f;
            float valueY = 0f;

            if(backOrNext == NEXT) {
                valueX = valueXarray[loc+attack][order];
                valueY = valueYarray[loc+attack][order];
            }else{
                valueX = valueXarray[loc+attack][order+BACK];
                valueY = valueYarray[loc+attack][order+BACK];
            }
            start(valueX,valueY);
        }
        public void turn(){
            order = (++order)%6;
            float valueX = valueXarray[loc+attack][order+BACK];
            float valueY = valueYarray[loc+attack][order+BACK];
            start(valueX,valueY);
        }
        public void changeMode(){
            float valueX = valueXarray[loc+attack][order+BACK];
            float valueY = valueYarray[loc+attack][order+BACK];
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
