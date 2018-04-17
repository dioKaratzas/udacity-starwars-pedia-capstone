package eu.dkaratzas.starwarspedia.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SwapiModelList<T> implements Parcelable {
    @JsonProperty("next")
    private String next;
    @JsonProperty("previous")
    private String previous;
    @JsonProperty("count")
    private int count;
    @JsonProperty("results")
    public List<T> results;

    public SwapiModelList() {
        this.next = "";
        this.previous = "";
        this.count = 0;
        this.results = new ArrayList<>();
    }

    public SwapiModelList(String next, String previous, int count, List<T> results) {
        this.next = next;
        this.previous = previous;
        this.count = count;
        this.results = results;
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

    protected SwapiModelList(Parcel in) {
        this.next = in.readString();
        this.previous = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<SwapiModelList> CREATOR = new Creator<SwapiModelList>() {
        @Override
        public SwapiModelList createFromParcel(Parcel source) {
            return new SwapiModelList(source);
        }

        @Override
        public SwapiModelList[] newArray(int size) {
            return new SwapiModelList[size];
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

    public int getPageCount() {
        return (int) Math.max(1, (long) Math.ceil((double) (count) / 10));
    }

    public boolean gotAnotherPage() {
        return next != null;
    }
    //endregion
}