package com.example.user.newsgateway;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 22-04-2018.
 */

public class ArticlesData implements Serializable, Parcelable {

    private String article_author;
    private String article_title;
    private String article_description;
    private String article_urlToImage;
    private String article_publishedAt;
    private String article_webUrl;

    public ArticlesData(){}

    public ArticlesData(Parcel data)
    {
        article_author = data.readString();
        article_title = data.readString();
        article_description = data.readString();
        article_urlToImage = data.readString();
        article_publishedAt = data.readString();
        article_webUrl = data.readString();
    }

    public String getArticle_author() {
        return article_author;
    }

    public void setArticle_author(String article_author) {
        this.article_author = article_author;
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_description() {
        return article_description;
    }

    public void setArticle_description(String article_description) {
        this.article_description = article_description;
    }

    public String getArticle_urlToImage() {
        return article_urlToImage;
    }

    public void setArticle_urlToImage(String article_urlToImage) {
        this.article_urlToImage = article_urlToImage;
    }

    public String getArticle_publishedAt() {
        return article_publishedAt;
    }

    public void setArticle_publishedAt(String article_publishedAt) {
        this.article_publishedAt = article_publishedAt;
    }

    public String getArticle_webUrl() {
        return article_webUrl;
    }

    public void setArticle_webUrl(String article_webUrl) {
        this.article_webUrl = article_webUrl;
    }

    @Override
    public String toString() {
        return "ArticlesData{" +
                "article_author='" + article_author + '\'' +
                ", article_title='" + article_title + '\'' +
                ", article_description='" + article_description + '\'' +
                ", article_urlToImage='" + article_urlToImage + '\'' +
                ", article_publishedAt='" + article_publishedAt + '\'' +
                ", article_webUrl='" + article_webUrl + '\'' +
                '}';
    }

    public static final Creator<ArticlesData> CREATOR = new Creator<ArticlesData>() {
        @Override
        public ArticlesData createFromParcel(Parcel in) {
            return new ArticlesData(in);
        }

        @Override
        public ArticlesData[] newArray(int size) {
            return new ArticlesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(article_author);
        parcel.writeString(article_title);
        parcel.writeString(article_description);
        parcel.writeString(article_urlToImage);
        parcel.writeString(article_publishedAt);
        parcel.writeString(article_webUrl);
    }
}
