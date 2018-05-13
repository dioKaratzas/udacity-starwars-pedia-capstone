package eu.dkaratzas.starwarspedia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.holders.CategoryViewHolder;
import eu.dkaratzas.starwarspedia.libs.GlideApp;
import eu.dkaratzas.starwarspedia.models.CategoryItems;
import eu.dkaratzas.starwarspedia.models.QueryData;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context mContext;
    private CategoryItems mCategoryItems;
    private OnItemClickListener mClickListener;

    public CategoryAdapter(Context context, CategoryItems categoryItems, OnItemClickListener itemClickListener) {
        mContext = context;
        mCategoryItems = categoryItems;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final QueryData queryData = mCategoryItems.getQueryDataList().get(position);
        holder.mTitle.setText(queryData.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(mCategoryItems.getQueryDataList().get(position));
                }
            }
        });
        GlideApp.with(mContext)
                .load(queryData.getImageStorageReference())
                .placeholder(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.mIvThumb);
    }

    @Override
    public int getItemCount() {
        return mCategoryItems.getQueryDataList().size();
    }

    public interface OnItemClickListener {
        void onItemClick(QueryData queryData);
    }
}
