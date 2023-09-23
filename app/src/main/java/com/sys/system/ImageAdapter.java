package com.sys.system;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;
import java.util.List;

public class ImageAdapter extends BannerAdapter<Integer, ImageAdapter.ImageViewHolder> {

    public ImageAdapter(List<Integer> data) {
        super(data);
    }

    @Override
    public ImageViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindView(ImageViewHolder holder, Integer data, int position, int size) {
        holder.imageView.setImageResource(data); // Load the image resource
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
