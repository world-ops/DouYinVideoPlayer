package com.example.douyinvideoplayer;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    // https://beiyou.bytedance.com/api/invoke/video/invoke/video
    @GET("api/invoke/video/invoke/video")
    Call<List<videoInfo>> getVideoInfos();
}
