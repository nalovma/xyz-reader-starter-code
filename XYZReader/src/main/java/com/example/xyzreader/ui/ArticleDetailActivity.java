package com.example.xyzreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import com.example.xyzreader.R;
import com.example.xyzreader.model.Article;
import com.example.xyzreader.utils.DynamicHeightNetworkImageView;
import com.example.xyzreader.utils.ImageLoaderHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private static final String TAG = ArticleDetailActivity.class.toString();

    public static final String KEY_ARTICLE = "article_key";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.ENGLISH);
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    @BindView(R.id.thumbnail)
    DynamicHeightNetworkImageView thumbnailView;

    @BindView(R.id.article_title)
    TextView titleView;

    @BindView(R.id.article_subtitle)
    TextView subtitleView;

    @BindView(R.id.article_details)
    TextView detailsView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        Article article = (Article) getIntent().getSerializableExtra(KEY_ARTICLE);
        if (article != null) {
            publishUi(article);
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void publishUi(Article article) {
        titleView.setText(article.getTitle());


        collapsingToolbarLayout.setTitle(article.getTitle());
        detailsView.setText(Html.fromHtml(article.getBody()));
        Date publishedDate = parsePublishedDate(article.getPublishedDate());

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + article.getAuthor()));
        } else {
            subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + " by "
                            + article.getAuthor()));
        }
        thumbnailView.setImageUrl(
                article.getThumb(),
                ImageLoaderHelper.getInstance(this).getImageLoader());
        thumbnailView.setAspectRatio(article.getAspectRatio());
    }

    @OnClick(R.id.fb_share)
    public void onShareButtonClick(View view) {
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("Some sample text")
                .getIntent(), getString(R.string.action_share)));

    }
}
