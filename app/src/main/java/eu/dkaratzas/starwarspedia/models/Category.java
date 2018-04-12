package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Category<T> implements Parcelable {
    @JsonProperty("next")
    private String next;
    @JsonProperty("previous")
    private String previous;
    @JsonProperty("count")
    private int count;
    @JsonProperty("results")
    public List<T> results;

    public Category() {
        this.next = "";
        this.previous = "";
        this.count = 0;
        this.results = new ArrayList<>();
    }

    public boolean hasMore() {
        return !TextUtils.isEmpty(next);
    }

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.next);
        dest.writeString(this.previous);
        dest.writeInt(this.count);
    }

    protected Category(Parcel in) {
        this.next = in.readString();
        this.previous = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    //endregion

    //region Getters

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public int getCount() {
        return count;
    }

    public List<T> getResults() {
        return results;
    }

    //endregion
}