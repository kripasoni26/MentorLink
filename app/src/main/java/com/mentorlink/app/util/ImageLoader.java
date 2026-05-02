package com.mentorlink.app.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mentorlink.app.R;

public final class ImageLoader {

    private ImageLoader() {
    }

    public static void load(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.bg_avatar_placeholder)
                .error(R.drawable.bg_avatar_placeholder)
                .into(imageView);
    }
}
