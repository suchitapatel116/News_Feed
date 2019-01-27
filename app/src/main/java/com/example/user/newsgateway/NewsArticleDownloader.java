package com.example.user.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsArticleDownloader";
    private static final String articleURL = "https://newsapi.org/v1/articles";
    private static final String apiKey = "21532e58bb5a4352ada161ca15c2288b";
    NewsService newsService = new NewsService();
    String source;
    private ArrayList<ArticlesData> articlesData_list;

    public NewsArticleDownloader(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");

        String jsonString = "";

        Uri.Builder buildUri = Uri.parse(articleURL).buildUpon();
        buildUri.appendQueryParameter("source", source);
        //Here use the apiKey because it is not able to decode the key param to apiKey
        buildUri.appendQueryParameter("apiKey", apiKey);

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
            Log.d(TAG, "doInBackground: Exception while fetching the article");
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
            //--setArticles in news service
            newsService.setArticles(articlesData_list);
        }
        else
            newsService.setArticles(null);
    }


    private boolean parseJSONString(String str) {
        if(str == null || str.equals("")) {
            Log.d(TAG, "parseJSONString: No article data is available for specified source");
            //Toast.makeText(mainActivity, "No data is available for specified category", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d(TAG, "parseJSONString: ");
        articlesData_list = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(str);
            JSONArray jarticlesArr = jObjMain.getJSONArray("articles");

            for(int i=0; i<jarticlesArr.length(); i++)
            {
                JSONObject jArtObj = (JSONObject) jarticlesArr.get(i);

                String author = jArtObj.getString("author");
                String title = jArtObj.getString("title");
                String desc = jArtObj.getString("description");
                String urlToImage = jArtObj.getString("urlToImage");
                String publishedAt = jArtObj.getString("publishedAt");
                String web_url = jArtObj.getString("url");

                ArticlesData article_obj = new ArticlesData();
                article_obj.setArticle_author(author);
                article_obj.setArticle_title(title);
                article_obj.setArticle_description(desc);
                article_obj.setArticle_urlToImage(urlToImage);
                article_obj.setArticle_publishedAt(publishedAt);
                article_obj.setArticle_webUrl(web_url);

                articlesData_list.add(article_obj);
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
