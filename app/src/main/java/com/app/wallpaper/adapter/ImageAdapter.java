package com.app.wallpaper.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wallpaper.R;
import com.app.wallpaper.interfaces.ItemClickedListner;
import com.app.wallpaper.model.ImageModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<ImageModel> mImageList;
    public ItemClickedListner itemClickedListner;

    public ImageAdapter(Context context, ItemClickedListner itemClickedListner) {
        mContext = context;
        mImageList = new ArrayList<>();
        this.itemClickedListner = itemClickedListner;
        loadImagesFromAssets();
    }

    private void loadImagesFromAssets() {
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] images = assetManager.list("");
            for (String fileName : images) {
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                    InputStream inputStream = assetManager.open(fileName);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ImageModel imageModel = new ImageModel();
                    imageModel.setImage(bitmap);
                    imageModel.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                    imageModel.setFilePath(fileName);
                    mImageList.add(imageModel);
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel imageModel = mImageList.get(position);
        holder.mImageView.setImageBitmap(imageModel.getImage());
        holder.mImageView.setOnClickListener(view -> {

            itemClickedListner.onItemClicked(position,imageModel.getTitle(),imageModel.getFilePath());

        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }
}
