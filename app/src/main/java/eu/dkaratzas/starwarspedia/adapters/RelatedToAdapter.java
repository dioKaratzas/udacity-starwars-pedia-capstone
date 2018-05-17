package eu.dkaratzas.starwarspedia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.holders.RelatedToViewHolder;
import eu.dkaratzas.starwarspedia.libs.GlideApp;
import eu.dkaratzas.starwarspedia.models.SimpleQueryData;

public class RelatedToAdapter extends RecyclerView.Adapter<RelatedToViewHolder> {
    private Context mContext;
    private List<SimpleQueryData> mDataList;
    private OnItemClickListener mClickListener;

    public RelatedToAdapter(Context context, List<SimpleQueryData> dataList, OnItemClickListener itemClickListener) {
        mContext = context;
        mDataList = dataList;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RelatedToViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_to, parent, false);
        return new RelatedToViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedToViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SimpleQueryData queryData = mDataList.get(position);
        holder.mTitle.setText(queryData.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(mDataList.get(position));
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
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(SimpleQueryData queryData);
    }
}
