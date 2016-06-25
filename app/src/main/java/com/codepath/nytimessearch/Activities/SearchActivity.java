package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.nytimessearch.Article;
import com.codepath.nytimessearch.ArticleArrayAdapter;
import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.ItemClickSupport;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;

    //EditText etQuery;
    //GridView gvResults;
    RecyclerView rvResults;
    SearchView searchView;
   // Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    String currentQuery;
    SearchFilters filters;
    int spinnerPos;
    String dateStr;
    StaggeredGridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpViews();
    }

    public void setUpViews() {
        //etQuery = (EditText) findViewById(R.id.etQuery);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        rvResults.setAdapter(adapter);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
       gridLayoutManager =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvResults.setLayoutManager(gridLayoutManager);

        ItemClickSupport.addTo(rvResults).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //create an intent to display article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = articles.get(position);
                //pass in that article to intent
                i.putExtra("article", article);
                //launch the activity
                startActivity(i);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                currentQuery = query;
                articles.clear();
                adapter.notifyDataSetChanged();
                loadDataFromApi(0, currentQuery);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            launchFilterView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadDataFromApi (int page, String query) {
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadDataFromApi(page, currentQuery);
            }
        });
        //String query = etQuery.getText().toString();
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "0be89842a4dc4f99ba0d5aa314659d4d");
        params.put("page", page);
        params.put("q", query);
        params.put("sort", "Newest");
        if (filters != null) {
            ArrayList<String> newsDeskItems = filters.getNewsDeskItems();
            if (!newsDeskItems.isEmpty()) {
                String newsDeskItemsStr =
                        android.text.TextUtils.join(" ", newsDeskItems);
                String newsDeskParamValue =
                        String.format("news_desk:(%s)", newsDeskItemsStr);
                params.put("fq", newsDeskParamValue);
            }
            String sort = filters.getSort();
            if (!sort.equals("")) {
                params.put("sort", sort);
            }
            String beginDate = filters.getBeginDate();
            if (!beginDate.equals("")) {
                params.put("begin_date", beginDate);
            }
        }

        Log.d("SearchActivity", url+"?"+params);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    //Log.d("DEBUG", articleJsonResults.toString());
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    //Log.d("DEBUG", articles.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void launchFilterView () {
        Intent i = new Intent(SearchActivity.this, FilterActivity.class);
        i.putExtra("filters", filters);
        i.putExtra("spinnerPos", spinnerPos);
        i.putExtra("dateStr", dateStr);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && i!=null) {
            // Extract name value from result extras
            filters = (SearchFilters) i.getSerializableExtra("filters");
            spinnerPos = i.getIntExtra("spinnerPos", 0);
            dateStr = i.getStringExtra("dateStr");
            articles.clear();
            adapter.notifyDataSetChanged();
            loadDataFromApi(0, currentQuery);
        }
    }
}
