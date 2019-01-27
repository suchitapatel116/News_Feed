package com.example.user.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//2) Create a service class
public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean isRunning = true;
    private final String ACTION_MSG_TO_SVC = "2_Msg_to_service";
    private final String ACTION_NEWS_STORY = "2_News_Story";
    private final String SOURCE_OBJ = "source_obj";
    private final String STORY_LIST = "storyList";

    ServiceReceiver serviceReceiver;

    private ArrayList<ArticlesData> articlesData_ArrayList = new ArrayList<>();
    private ArrayList<ArticlesData> storyList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG, "Service Binder");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started!!", Toast.LENGTH_SHORT).show();

        //New service receiver object
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SVC);
        //Register the serviceReceiver broadcast object withe the intent filter
        registerReceiver(serviceReceiver, filter);

        //ALWAYS write your long running tasks in a separate thread, to avoid ANR
        //Create new thread for NewsService
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Here long running tasks are performed like playing music or getting data from internet
                while (isRunning)
                {
                    //Perform the internet download task here
                    while(storyList.isEmpty())
                    {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //Now the list is filled
                    //The storylist is filled when the user selects a new source
                    Intent it = new Intent();
                    it.setAction(ACTION_NEWS_STORY);
                    it.putParcelableArrayListExtra(STORY_LIST, storyList); //.
                    sendBroadcast(it);
                    //Goto NewsReceiver in MainActivity
                    storyList.clear();
                }
            Log.i(TAG, "NewsService was properly implemented");
            }

        }).start();

        //return super.onStartCommand(intent, flags, startId);
        //START_STICKY: if something wrong then the program needs to start the service again
        //START_NON_STICKY: if something wrong then user needs to start the service like user needs to start playing the music again
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        //Unregister the service receiver object
        unregisterReceiver(serviceReceiver);
        isRunning = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<ArticlesData> data_list)
    {
        if(data_list == null)
            return;

        articlesData_ArrayList.clear();
        //Add storylist
        articlesData_ArrayList.addAll(data_list);
        storyList.addAll(data_list); //.

        //The NewsService Thread from Page 1 will pick up the new content in the list and publish it to the app
    }

    //3) Create a Service Broadcast Receiver class
    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(intent.getAction())
            {
                case ACTION_MSG_TO_SVC:
                    //Get the source id string from the intent's extra
                    String source_id = "";
                    SourcesData obj;
                    if(intent.hasExtra(SOURCE_OBJ)) {
                        obj = (SourcesData) intent.getSerializableExtra(SOURCE_OBJ);
                        source_id = obj.getSource_id();

                        NewsArticleDownloader article_asyncTask = new NewsArticleDownloader(NewsService.this, source_id);
                        article_asyncTask.execute();
                    }
                    break;
            }
        }
    }

}
