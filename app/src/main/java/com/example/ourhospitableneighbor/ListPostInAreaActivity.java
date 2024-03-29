package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.view.PanelItemView;

import java.util.ArrayList;
import java.util.List;

public class ListPostInAreaActivity extends AppCompatActivity {
    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_post_in_area);

        posts = PostService.getInstance().getLastPostsInAreaResult();
        if (posts == null) posts = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.listPostInAreaActivity_RecyclerView);
        recyclerView.setAdapter(new Adapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new PanelItemView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.postItemView.setPost(posts.get(position));
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView postItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.postItemView = (PanelItemView) itemView;
        }
    }

}