package com.example.user.volleyball;



import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout mToolbar;
    private Pager adapter;
    public AppBarLayout abl;
    private int height = 900;
    ArrayList<Fragment> fr_list = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        abl =(AppBarLayout)findViewById(R.id.abl) ;
        height = abl.getHeight();
        initTool();
        home();
        //Adding onTabSelectedListener to swipe views
    }

    public void home(){
        fr_list.add(new TeamerInfo());
        fr_list.add(new Schedule());
        fr_list.add(new Record());
        fr_list.add(new Team());
        tabLayout = (TabLayout) findViewById(R.id.tabs); // get the reference of TabLayout
        viewPager = (ViewPager) findViewById(R.id.pager);
        //Creating our pager adapter
        adapter = new Pager(getSupportFragmentManager(), 4);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setTab();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
    }
    public void setTab(){
        TabLayout.Tab volleyTab = tabLayout.getTabAt(0); // Create a new Tab names
        volleyTab.setText(R.string.volleyball); // set the Text for the first Tab
        volleyTab.setIcon(R.drawable.volleyball);

        TabLayout.Tab schTab = tabLayout.getTabAt(1);; // Create a new Tab names
        schTab.setText(R.string.schedule); // set the Text for the first Tab
        schTab.setIcon(R.drawable.calendar);

        TabLayout.Tab record = tabLayout.getTabAt(2);; // Create a new Tab names
        record.setText(R.string.record); // set the Text for the first Tab
        record.setIcon(R.drawable.record);

        TabLayout.Tab teamModule = tabLayout.getTabAt(3);; // Create a new Tab names
        teamModule.setText(R.string.module); // set the Text for the first Tab
        teamModule.setIcon(R.drawable.group);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position =tab.getPosition();
                Log.d("position:",""+position);
                switch (position) {
                    case 0:
                        mToolbar.setTitle(getString(R.string.volleyball));

                        break;
                    case 1:
                        mToolbar.setTitle(getString(R.string.schedule));
                        //abl.setExpanded(false);
                        break;
                    case 2:
                        mToolbar.setTitle(getString(R.string.record));
                        break;
                    case 3:
                        mToolbar.setTitle(getString(R.string.module));
                        abl.setExpanded(false,true);
                        break;
                    default:
                        break;
                }
                viewPager.setCurrentItem(position);
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();//取x軸
        int y = (int) event.getY();//取y軸
        int action = event.getAction();//取整數
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("按下:",x + "," + y);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("平移:" , x + "," + y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("彈起:"+x , "," + y);
                break;
        }
        return true;// super.onTouchEvent(event);
    }
    public void initTool(){
        mToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Toolbar cToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.volleyball));
        cToolbar.setNavigationIcon(R.drawable.ic_action_name);
        cToolbar.inflateMenu(R.menu.menu_main);
        cToolbar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) abl.getLayoutParams();

                abl.setLayoutParams(params);
                abl.setExpanded(true);
            }
        });
        cToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()){

                    case R.id.exit:
                        Toast.makeText(MainActivity.this, "Exit is clicked!", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    public class Pager extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;
        FragmentManager fm;
        //Constructor to the class
        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.fm = fm;
            this.tabCount= tabCount;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            Log.d("GetPosition",""+position);
            switch (position) {
                case 0:
                    // First Fragment of First Tab
                    return TeamerInfo.newInstance();

                case 1:
                    // First Fragment of Second Tab
                    return Schedule.newInstance();

                case 2:
                    // First Fragment of Third Tab
                    return Record.newInstance();
                case 3:
                    // First Fragment of Third Tab
                    return Team.newInstance();
                default:
                    return null;
            }

        }


        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }

}

