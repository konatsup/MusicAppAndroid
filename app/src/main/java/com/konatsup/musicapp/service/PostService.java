package com.konatsup.musicapp.service;

import com.konatsup.musicapp.ListPost;
import com.konatsup.musicapp.Post;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PostService {
    @GET("posts")
    Call<ListPost> getPosts();
}