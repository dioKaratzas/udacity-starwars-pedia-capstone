package eu.dkaratzas.starwarspedia.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eu.dkaratzas.starwarspedia.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView mTitle;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.tvTitle);
    }
}
