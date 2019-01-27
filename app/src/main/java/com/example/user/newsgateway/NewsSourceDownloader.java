package com.example.user.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 22-04-2018.
 */

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsSourceDownloader";
    private static final String sourcesByCategoryURL = "https://newsapi.org/v1/sources?language=en&country=us";
    private static final String apiKey = "21532e58bb5a4352ada161ca15c2288b";
    MainActivity mainActivity;
    String newsCategory;
    private ArrayList<SourcesData> sourcesData_list;
    private ArrayList<String> category_list;

    public NewsSourceDownloader(MainActivity mainActivity, String category) {
        this.mainActivity = mainActivity;
        if(category.equalsIgnoreCase("ALL") || category.equals(""))
            this.newsCategory = "";
        else
            this.newsCategory = category;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");

        String jsonString = "";

        Uri.Builder buildUri = Uri.parse(sourcesByCategoryURL).buildUpon();
        buildUri.appendQueryParameter("category", newsCategory);
        buildUri.appendQueryParameter("key", apiKey);

        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: Generated url is: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            InputStream inpStrm = conn.getInputStream();
            BufferedReader buf_reader = new BufferedReader(new InputStreamReader(inpStrm));

            String line;
            while((line = buf_reader.readLine()) != null)
                sb.append(line).append('\n');
        }
        catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception while fetching the news sources");
            e.printStackTrace();
            return null;
        }

        jsonString = sb.toString();
        Log.d(TAG, "doInBackground: The json string obtained is: "+ jsonString);
        return jsonString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: ");

        boolean isParseSuccessful = parseJSONString(s);
        if(isParseSuccessful) {
            //Create a list of unique news category names taken from the source objects
            category_list = new ArrayList<>();
            for(int i=0; i < sourcesData_list.size(); i++)
            {
                String cat = sourcesData_list.get(i).getSource_category();
                boolean ind = category_list.contains(cat);
                //If already there then don't add
                if(!ind)
                    category_list.add(cat);
            }
            mainActivity.setSources(sourcesData_list, category_list);
        }
        else
            mainActivity.setSources(null, null);
    }

    private boolean parseJSONString(String str) {
        if (str == null) {
            Toast.makeText(mainActivity, "News Service is unavailable", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(str.equals("")) {
            Toast.makeText(mainActivity, "No data is available for specified category", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d(TAG, "parseJSONString: ");
        sourcesData_list = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(str);
            JSONArray jSourcesArr = jObjMain.getJSONArray("sources");

            for(int i=0; i<jSourcesArr.length(); i++)
            {
                JSONObject jSrcObj = (JSONObject) jSourcesArr.get(i);

                String id = jSrcObj.getString("id");
                String name = jSrcObj.getString("name");
                String url = jSrcObj.getString("url");
                String category = jSrcObj.getString("category");

                SourcesData source_obj = new SourcesData();
                source_obj.setSource_id(id);
                source_obj.setSource_name(name);
                source_obj.setSource_url(url);
                source_obj.setSource_category(category);

                sourcesData_list.add(source_obj);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "parseJSONString: Error while parsing the JSON data");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
