package com.stephenakanniolu.lab1011;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class N01725208Fragment extends Fragment {

    private RecyclerView rvVideos;
    private WebView webViewYoutube;

    // Updated IDs: Google Gemini 1.5, NVIDIA AI, and IBM Generative AI
    private final String[] videoIds = {"v5mP9Kx1tE8", "p7Y_S_9k52E", "hfIUstzHs9A"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_n01725208, container, false);

        rvVideos = view.findViewById(R.id.rv_videos);
        webViewYoutube = view.findViewById(R.id.webview_youtube);

        // Setup WebView
        WebSettings webSettings = webViewYoutube.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Using a modern User Agent to ensure smooth embedding
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        webViewYoutube.setWebViewClient(new WebViewClient());
        webViewYoutube.setWebChromeClient(new WebChromeClient());

        // Setup RecyclerView
        List<String> descriptions = Arrays.asList(getResources().getStringArray(R.array.video_descriptions));
        VideoAdapter adapter = new VideoAdapter(descriptions, position -> {
            loadYouTubeVideo(videoIds[position]);
        });
        rvVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVideos.setAdapter(adapter);

        return view;
    }

    private void loadYouTubeVideo(String videoId) {
        String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0";
        webViewYoutube.loadUrl(embedUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webViewYoutube != null) {
            webViewYoutube.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webViewYoutube != null) {
            webViewYoutube.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if (webViewYoutube != null) {
            webViewYoutube.destroy();
        }
        super.onDestroy();
    }

    private static class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
        private final List<String> descriptions;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public VideoAdapter(List<String> descriptions, OnItemClickListener listener) {
            this.descriptions = descriptions;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvDescription.setText(descriptions.get(position));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        }

        @Override
        public int getItemCount() {
            return descriptions.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDescription;

            public ViewHolder(View itemView) {
                super(itemView);
                tvDescription = itemView.findViewById(R.id.tv_video_description);
            }
        }
    }
}