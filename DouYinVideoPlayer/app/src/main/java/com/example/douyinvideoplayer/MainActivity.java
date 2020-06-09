package com.example.douyinvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewpage2;
    Adapter adapter;
    SmartRefreshLayout smartRefreshLayout;
    List<String> list = new ArrayList<>();
    private List<videoInfo> mVideoInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpage2 = findViewById(R.id.page2);
        smartRefreshLayout = findViewById(R.id.refresh);
        smartRefreshLayout.setEnableLoadMore(true);
        mVideoInfos = new ArrayList<>();
        adapter = new Adapter(this);

        viewpage2.setAdapter(adapter);

        getData();
        viewpage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == list.size() - 1) {
                    Toast.makeText(getApplicationContext(), position + "触发loadmore", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getVideoInfos().enqueue(new Callback<List<videoInfo>>() {

            @Override
            public void onResponse(Call<List<videoInfo>> call, Response<List<videoInfo>> response) {
                if (response.body() != null){
                    List<videoInfo> videoInfos = response.body();
                    Log.d("retrofit", String.valueOf(videoInfos.size()));
                    if (videoInfos.size() != 0){
                        adapter.setData(videoInfos);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<videoInfo>> call, Throwable t) {
                Log.d("retrofit fail", t.getMessage());
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        List<String> list = new ArrayList<>();
        private List<videoInfo> mDataset;
        private Context mContext;


        public void setList(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }
        public Adapter(Context context){
            mContext = context;
        }

        public void setData(List<videoInfo> myDataset) { mDataset = myDataset; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.nickname.setText(mDataset.get(position).nickname);
            holder.descriptiom.setText(mDataset.get(position).description);
            holder.videoView.setVideoPath(mDataset.get(position).feedurl);
            holder.like.setText(String.valueOf(mDataset.get(position).likecount));
            holder.commit.setText(String.valueOf(mDataset.get(position).likecount));
            holder.count.setText(String.valueOf(mDataset.get(position).likecount));
            holder.videoView.setMediaController(new MediaController(mContext));
            //Glide.with(mContext).load(mDataset.get(position).avatar).into(holder.vedioViewHead);
            //holder.videoView.start();

            holder.videoView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (holder.videoView.isPlaying()){
                        holder.videoView.pause();
                        //holder.vedioViewHead.setVisibility(View.GONE);
                        //holder.btn_play.setVisibility(View.VISIBLE);
                    }else {
                        holder.videoView.start();
                        //holder.vedioViewHead.setVisibility(View.GONE);
                        //holder.btn_play.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });

            holder.videoView.start();
            //holder.vedioViewHead.setVisibility(View.GONE);
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//循环播放视频

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset == null ? 0 : mDataset.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        public TextView nickname;
        public TextView descriptiom;
        public ImageButton btn_like;
        public TextView like;
        public TextView count;
        public TextView commit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video);
            nickname=itemView.findViewById(R.id.nickname);
            descriptiom=itemView.findViewById(R.id.description);
            like=itemView.findViewById(R.id.like);
            count=itemView.findViewById(R.id.count);
            commit=itemView.findViewById(R.id.commit);
            btn_like=itemView.findViewById(R.id.btn_heart);
        }

    }
}
