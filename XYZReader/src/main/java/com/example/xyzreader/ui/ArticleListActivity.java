package com.example.xyzreader.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.xyzreader.adapter.ArticleAdapter;
import com.example.xyzreader.R;
import com.example.xyzreader.model.Article;
import com.example.xyzreader.utils.ConnectivityReceiver;
import com.example.xyzreader.viewmodels.ArticlesViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class ArticleListActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener, ArticleAdapter.ArticleItemClickListener {

    private static final String TAG = ArticleListActivity.class.toString();

    public static final String KEY_RECYCLE_STATE = "recycleState";
    Parcelable recycleSavedState;
    ConnectivityReceiver connectivityReceiver;
    StaggeredGridLayoutManager sglm;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    List<Article> articleList;
    ArticleAdapter articleAdapter;
    ArticlesViewModel articlesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);
        connectivityReceiver = new ConnectivityReceiver(this);

        articleAdapter = new ArticleAdapter(this, this);
        articlesViewModel = ViewModelProviders.of(this).get(ArticlesViewModel.class);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        if (savedInstanceState == null) {
            refresh();
        } else {
            recycleSavedState = savedInstanceState.getParcelable(KEY_RECYCLE_STATE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_RECYCLE_STATE, sglm.onSaveInstanceState());

    }

    private void refresh() {
        articlesViewModel.init();
        articlesViewModel.getArticleRepository().observe(this, articles -> {
            articleList = articles;
            articleAdapter.setArticleList(articles);
            mSwipeRefreshLayout.setRefreshing(false);
            initData();
        });
    }

    private void initData() {
        mRecyclerView.setAdapter(articleAdapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        sglm = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        if (recycleSavedState != null) {
            sglm.onRestoreInstanceState(recycleSavedState);
        }
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showSnakBarMessage(getString(R.string.no_internet));
        }
    }

    private void showSnakBarMessage(String text) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }
}
