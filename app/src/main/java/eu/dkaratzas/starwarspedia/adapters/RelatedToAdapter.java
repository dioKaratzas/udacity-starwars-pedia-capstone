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
import eu.dkaratzas.starwarspedia.models.SwapiModel;

public class RelatedToAdapter extends RecyclerView.Adapter<RelatedToViewHolder> {
    private Context mContext;
    private List<SwapiModel> mSwapiList;
    private OnItemClickListener mClickListener;

    public RelatedToAdapter(Context context, List<SwapiModel> List, OnItemClickListener itemClickListener) {
        mContext = context;
        mSwapiList = List;
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
        final SwapiModel swapiModel = mSwapiList.get(position);
        holder.mTitle.setText(swapiModel.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(mSwapiList.get(position));
                }
            }
        });
        GlideApp.with(mContext)
                .load(swapiModel.getImageStorageReference())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvThumb);
    }

    @Override
    public int getItemCount() {
        return mSwapiList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(SwapiModel swapiModel);
    }
}
