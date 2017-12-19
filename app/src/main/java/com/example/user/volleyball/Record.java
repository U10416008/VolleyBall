package com.example.user.volleyball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by user on 2017/12/4.
 */

public class Record extends Fragment {
    private View rootView;
    private Toolbar toolbar;
    private ImageView image[] = new ImageView[6];
    private Player player[] = new Player[6];
    boolean selectMode = false;
    final int NEXT = 5;
    final int WEAPON = 0;
    final int MIDDLE = 1;
    final int RASE = 2;
    final int FREE = 3;
    float valueXarray[];
    float valueYarray[] ;
    public static Record newInstance() {

        Record fragment = new Record();
        Bundle args = new Bundle();
        // For this to work this method should only be called once ever for each instanceSection.
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.record, container, false);

        setHasOptionsMenu(true);
        initTool();
        initImage();
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

                switch (item.getItemId()){

                    case R.id.three:

                        float screenWidth = rootView.findViewById(R.id.recordRL).getWidth();
                        float screenHeight = rootView.findViewById(R.id.recordRL).getHeight();
                        Log.d("X,Y",screenWidth +","+screenHeight);
                        initXY(screenWidth ,screenHeight);

                        selectMode = true;
                        toolbar.setTitle(R.string.first);
                        for(int i = 0 ; i < player.length ; i++)
                            player[i].start_record();
                        break;
                    case R.id.five:
                        showToast(getResources().getString(R.string.action_settings2));
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
                {screenHeight * 0.6f,screenHeight * 0.4f,screenHeight * 0.2f,
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
        }
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
