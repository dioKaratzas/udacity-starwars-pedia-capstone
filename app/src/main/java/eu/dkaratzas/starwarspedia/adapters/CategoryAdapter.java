package eu.dkaratzas.starwarspedia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.holders.CategoryViewHolder;
import eu.dkaratzas.starwarspedia.libs.GlideApp;
import eu.dkaratzas.starwarspedia.models.SwapiModel;
import eu.dkaratzas.starwarspedia.models.SwapiModelList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context mContext;
    private SwapiModelList<SwapiModel> mSwapiModelList;
    private OnItemClickListener mClickListener;

    public CategoryAdapter(Context context, SwapiModelList<SwapiModel> swapiModelList, OnItemClickListener itemClickListener) {
        mContext = context;
        mSwapiModelList = swapiModelList;
        mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SwapiModel swapiModel = mSwapiModelList.results.get(position);
        holder.mTitle.setText(swapiModel.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(mSwapiModelList.results.get(position));
                }
            }
        });
        GlideApp.with(mContext)
                .load(swapiModel.getImageStorageReference())
                .into(holder.mIvPoster);
    }

    @Override
    public int getItemCount() {
        return mSwapiModelList.results.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(SwapiModel swapiModel);
    }
}
