package com.example.xyzreader.remote;

import androidx.lifecycle.MutableLiveData;

import com.example.xyzreader.model.Article;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository {

    private static ArticleRepository articleRepository;

    public static ArticleRepository getInstance() {
        if (articleRepository == null) {
            articleRepository = new ArticleRepository();
        }
        return articleRepository;
    }

    private ArticleApi articleApi;

    public ArticleRepository() {
        articleApi = RetrofitService.cteateService(ArticleApi.class);
    }


    public MutableLiveData<List<Article>> getArticleList() {
        MutableLiveData<List<Article>> data = new MutableLiveData<>();
        articleApi.getArticleList().enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
