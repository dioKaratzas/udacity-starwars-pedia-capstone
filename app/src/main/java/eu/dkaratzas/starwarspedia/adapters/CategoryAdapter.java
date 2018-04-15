package eu.dkaratzas.starwarspedia.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import eu.dkaratzas.starwarspedia.holders.CategoryViewHolder;
import eu.dkaratzas.starwarspedia.models.SwapiModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private SwapiModel mCategory;

    public CategoryAdapter(SwapiModel category) {
        mCategory = category;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
