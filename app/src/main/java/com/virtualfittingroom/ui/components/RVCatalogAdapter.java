package com.virtualfittingroom.ui.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.models.CatalogModel;

import java.util.List;

public class RVCatalogAdapter extends RecyclerView.Adapter<RVCatalogAdapter.CatalogViewHolder> {
    public static final String TAG = "RVCatalogAdapter";
    private List<CatalogModel> catalogModels;

    private CatalogItemCallback catalogItemCallback;

    public RVCatalogAdapter(List<CatalogModel> catalogModels, CatalogItemCallback catalogItemCallback) {
        this.catalogModels = catalogModels;
        this.catalogItemCallback = catalogItemCallback;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.katalog_recyclerview_item, parent, false);
        return  new CatalogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        holder.tvCatalogName.setText(this.catalogModels.get(position).getName());
        holder.tvCatalogDescription.setText(this.catalogModels.get(position).getDescription());
        holder.tvCatalogColor.setText(this.catalogModels.get(position).getColor());

        Picasso.get()
                .load(this.catalogModels.get(position).getImageUrl())
                .placeholder(R.drawable.placehold_catalog)
                .into(holder.ivCatalogImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catalogItemCallback.onClick(catalogModels.get(holder.getAdapterPosition()));
            }
        });

        Log.i(TAG, "onBindViewHolder: bind: " + position);
    }



    @Override
    public int getItemCount() {
        return this.catalogModels.size();
    }

    public class CatalogViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivCatalogImage;
        public TextView tvCatalogName, tvCatalogDescription, tvCatalogColor;

        public View itemView;
        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivCatalogImage = itemView.findViewById(R.id.itemImage);
            this.tvCatalogName = itemView.findViewById(R.id.itemName);
            this.tvCatalogDescription = itemView.findViewById(R.id.itemDescription);
            this.tvCatalogColor = itemView.findViewById(R.id.itemColor);
            this.itemView = itemView;
        }

    }

    public interface CatalogItemCallback{
        void onClick(CatalogModel catalog);
    }
}
