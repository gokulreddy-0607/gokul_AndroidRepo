package com.example.dapp_b;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final List<Uri> imageUris;

    public ImageAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);

        Glide.with(holder.imageView.getContext())
                .load(imageUri)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
            intent.putExtra("imageUri", imageUri);
            v.getContext().startActivity(intent);
        });

        holder.removeImageButton.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                imageUris.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView removeImageButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            removeImageButton = itemView.findViewById(R.id.removeImageButton);
        }
    }
}
