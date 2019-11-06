package com.example.xyzreader.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xyzreader.model.Article;
import com.example.xyzreader.remote.ArticleRepository;

import java.util.List;

public class ArticlesViewModel extends ViewModel {

    private MutableLiveData<List<Article>> mutableLiveData;
    private ArticleRepository articleRepository;

    public void init() {
        if (mutableLiveData != null) {
            return;
        }
        articleRepository = ArticleRepository.getInstance();
        mutableLiveData = articleRepository.getArticleList();

    }

    public LiveData<List<Article>> getArticleRepository() {
        return mutableLiveData;
    }
}
