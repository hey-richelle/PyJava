package com.sys.system.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.sys.system.Adapter.ImageAdapter;
import com.sys.system.R;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.List;

public class Admin_ui extends AppCompatActivity {
    private Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ui);

        banner = findViewById(R.id.banner);

        // Create a list of image URLs or resources
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.javadev);
        images.add(R.drawable.python234);

        // Set up the Banner with image data
        banner.setAdapter(new ImageAdapter(images))
                .setIndicator(new CircleIndicator(this))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        // Handle banner item click events here
                    }
                })
                .start();

    }
}
