package com.example.newzapp;

import android.os.Bundle;
import android.util.Log;
import com.google.firebase.messaging.*;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface{
    //63c7346bbe774899beb298bf21618ea5
    private RecyclerView newsRV,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryRVModal> categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("new_user_forums");

        newsRV = findViewById(R.id.idRVNews);
        categoryRV = findViewById(R.id.idRVCategories);
        loadingPB = findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        categoryRVModalArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModalArrayList,this,this::OnCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);
        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();
    }
    private void getCategories()
    {
        categoryRVModalArrayList.add(new CategoryRVModal("All","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8OxII7uQ1gl5mZs4bQzM9oo4uJ7YBPDVcVA&s"));
        categoryRVModalArrayList.add(new CategoryRVModal("Technology","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCEEBJ3WTHM6yGg4jCMhDJwv1N0VyanyKyQQ&s"));
        categoryRVModalArrayList.add(new CategoryRVModal("Science","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu3Ajwe9fhbvK_1tHFyu-3LiU3Zm-YRspmrQ&s"));
        categoryRVModalArrayList.add(new CategoryRVModal("Sports","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlWlYZARxT-2PSTgfJKskDhxZa0dNv31GisA&s"));
        categoryRVModalArrayList.add(new CategoryRVModal("General","https://images.unsplash.com/photo-1512314889357-e157c22f938d?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8R2VuZXJhbHxlbnwwfHwwfHx8MA%3D%3D"));
        categoryRVModalArrayList.add(new CategoryRVModal("Business","https://images.unsplash.com/photo-1444653614773-995cb1ef9efa?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YnVzaW5lc3MlMjBpbWFnZXN8ZW58MHx8MHx8fDA%3D"));
        categoryRVModalArrayList.add(new CategoryRVModal("Entertainment","https://images.unsplash.com/photo-1603190287605-e6ade32fa852?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8RW50ZXJ0YWlubWVudHxlbnwwfHwwfHx8MA%3D%3D"));
        categoryRVModalArrayList.add(new CategoryRVModal("Health","https://images.unsplash.com/photo-1477332552946-cfb384aeaf1c?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTB8fHdlbGxuZXNzfGVufDB8fDB8fHww"));
        categoryRVAdapter.notifyDataSetChanged();
    }

    private void getNews(String category)
    {
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryURL = "https://newsapi.org/v2/top-headlines?country=in&category="+category+"&apiKey=63c7346bbe774899beb298bf21618ea5";
        String url = "https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=63c7346bbe774899beb298bf21618ea5";
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        if(category.equals("All"))
        {
            call = retrofitAPI.getAllNews(url);
        }
        else{
            call = retrofitAPI.getNewsByCategory(categoryURL);
        }
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles> articles = newsModal.getArticles();
                for(int i=0;i< articles.size();i++)
                {
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(),articles.get(i).getUrlToImage(),
                            articles.get(i).getUrl(),articles.get(i).getContent()));
                }
                newsRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get news :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void OnCategoryClick(int position) {
        String category = categoryRVModalArrayList.get(position).getCategory();
        getNews(category);
    }
}