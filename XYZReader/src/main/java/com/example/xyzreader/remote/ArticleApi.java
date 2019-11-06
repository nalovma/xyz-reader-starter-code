package com.example.xyzreader.remote;

import com.example.xyzreader.model.Article;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ArticleApi {

    @GET("xyz-reader-json")
    Call<List<Article>> getArticleList();
}
