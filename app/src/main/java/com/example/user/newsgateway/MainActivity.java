package com.example.user.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.user.newsgateway.ArticlePlaceHolderFragment.newInstance;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String ACTION_NEWS_STORY = "2_News_Story";
    private final String ACTION_MSG_TO_SVC = "2_Msg_to_service";
    private final String SOURCE_OBJ = "source_obj";
    private final String STORY_LIST = "storyList";


    NewsReceiver newsReceiver;
    private Menu option_menu;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayList<SourcesData> sourcesData_ArrayList = new ArrayList<>();
    private ArrayList<String> category_ArrayList;
    private HashMap<String, SourcesData> sourceData_map = new HashMap<>();
    private ArrayList<String> sourceNames_list = new ArrayList<>();

    private android.support.v4.view.ViewPager pager;
    private List<Fragment> fragment_list;
    private SectionPagerAdapter pagerAdapter;

    private ArrayList<ArticlesData> articlesDataArrayList = new ArrayList<>();
    private String current_NewsSource = "";
    private String current_Category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1) Start the service
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);

        //6) Create options menu xml and add the overridden methods in Main Activity

        //7) Create drawer
        //7.1) Main Activity xml
        //DrawerLayout is not available in the panel so in main activity xml replace the layout code with DrawerLayout code
        //Add Constraint Layout if want to add other item views on the screen
        //Add ListView under drawer layout, with some width, choiceMode and layout_gravity
        //layout_gravity = start actually sets the drawer outside the screen area

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer_list);

        //7.2) Create Adapter
        //mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, items));

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item_view, sourceNames_list));

        //The list cannot be filled as we don't have the list of items currently
        //Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Set up the pager here
                pager = (ViewPager) findViewById(R.id.viewPager_container);
                pager.setBackground(null);
                current_NewsSource = sourceNames_list.get(position);

                Intent intent = new Intent();
                intent.setAction(ACTION_MSG_TO_SVC);

                if(sourceData_map.containsKey(current_NewsSource))
                {
                    SourcesData obj = sourceData_map.get(current_NewsSource);
                    intent.putExtra(SOURCE_OBJ, obj);
                    sendBroadcast(intent);
                    selectItem(position);
                    //Goto ServiceReceiver in NewsService
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        //7.3) Add Drawer Toggle: Create new ActionBarDrawerToggle object, set below two features to true, add onPostCreate() and onConfigurationChanged()
        //ActionBarDrawerToggle works like options menu, so implements like options menu, code will be written in onOptionsItemSelected()
        //Below three statements: To display the hamburger icon for viewing the drawer
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //8) view pager: fragment: Setup the viewPager
        fragment_list = new ArrayList<Fragment>();

        pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewPager_container);
        pager.setAdapter(pagerAdapter);

        //9) Load the data
        //Call the async tasks to download the data from API
        if(current_Category.equals(""))
            new NewsSourceDownloader(this, "").execute();
        else
            new NewsSourceDownloader(this, current_Category).execute();

        /*//Perform this task after loading the data so the lists are filled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        */
    }

    private void selectItem(int position)
    {
        //Toast.makeText(this, sourceNames_list.get(position), Toast.LENGTH_SHORT).show();
        setTitle(sourceNames_list.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void reDoFragments(ArrayList<ArticlesData> data)
    {
        for(int i=0; i<pagerAdapter.getCount(); i++)
            pagerAdapter.notifyChangeInPosition(i);

        fragment_list.clear();
        //fragments.clear();

        for(int i=0; i<data.size(); i++)
        {
            ArticlesData obj = data.get(i);
            //fragments.add(ArticlePlaceHolderFragment.newInstance(i, obj));
            fragment_list.add(ArticlePlaceHolderFragment.newInstance(i, obj, data.size()));

            pagerAdapter.notifyChangeInPosition(i);
        }

        pagerAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    //Below 2 methods are required to make the drawer toggle work
    //Called after onCreate()
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    //Called when device is rotated
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //This is called after onStart() and before onResume().
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        current_Category = savedInstanceState.getString("curr_category");
        current_NewsSource = savedInstanceState.getString("curr_source");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("curr_category", current_Category);
        outState.putString("curr_source", current_NewsSource);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent in = new Intent(this, NewsReceiver.class);
        stopService(in);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        //It just a reference to the menu, it can also be defined as findViewById, but as we have direct access in onCreateOptionsMenu we use this
        //It is used for dynamically adding menu items, initially the menu is empty
        option_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //7.3: for items selected from the drawer: Check if drawer menu item selected
        if(mDrawerToggle.onOptionsItemSelected(item))
        {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle "+item);
            return true;
        }

        String sel_item_name = (String) item.getTitle();
        current_Category = sel_item_name;
        new NewsSourceDownloader(this, sel_item_name).execute();

        mDrawerLayout.openDrawer(mDrawerList);
        //return true;
        return super.onOptionsItemSelected(item);
    }

    //5) Create NewsSourceDownloader and NewsArticleDownloader Async Task
    public void setSources(ArrayList<SourcesData> data_list, ArrayList<String> categories_list)
    {
        Log.d(TAG, "setSources: ");
        if(data_list == null || categories_list == null)
            return;

        //Clear the source map (HashMap of source names to Source objects)
        sourceData_map.clear();
        //Clear the list of source names (used to populate the drawer list)
        sourceNames_list.clear();

        //Fill the list of sources (used to populate the drawer list) using the names of sources passed in
        sourcesData_ArrayList.addAll(data_list);
        for(SourcesData item : data_list)
            sourceNames_list.add(item.getSource_name());

        //Fill the source map with each news source name (key) and the source object (value)
        for(SourcesData item : data_list) {
            sourceData_map.put(item.getSource_name(), item);
        }

        //Fill the options menu
        if(category_ArrayList == null) {
            category_ArrayList = new ArrayList<>();

            category_ArrayList.add("All");
            category_ArrayList.addAll(categories_list);

            Collections.sort(category_ArrayList);

            if(option_menu != null)
                for(String str: category_ArrayList)
                    option_menu.add(str);
        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
    }

    //4) Create News receiver class and create intentFilter in oncreate()
    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(intent.getAction())
            {
                case ACTION_NEWS_STORY:
                    //Get the Article list from the intent's extras
                    if(intent.hasExtra(STORY_LIST))
                    {
                        //set the article list from the extras
                        articlesDataArrayList.clear();
                        ArrayList<ArticlesData> data, obj;
                        data = (ArrayList<ArticlesData>) intent.getSerializableExtra(STORY_LIST);
                        articlesDataArrayList.addAll(data);

                        //call reDoFragments(list)
                        reDoFragments(data);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //Add the FragmentPagerAdapter class
    public class SectionPagerAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragment_list.get(position);
            //return (Fragment) fragments.get(position);
            //return null;
        }

        @Override
        public int getCount() {
            //Show total pages. This is hard-coded but often is the size of a collection.
            return fragment_list.size();
            //return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }
}
