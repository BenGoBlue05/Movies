package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bplewis5 on 7/11/16.
 */
public class Movie implements Parcelable{

    String mPosterUrlStr;
    String mReleaseDate;
    String mTitle;
    double mVoteAvg;
    String mSynopsis;

    public Movie(String mPosterUri, String mReleaseDate, String mTitle, double mVoteAvg, String mSynopsis) {
        this.mPosterUrlStr = mPosterUri;
        this.mReleaseDate = mReleaseDate;
        this.mTitle = mTitle;
        this.mVoteAvg = mVoteAvg;
        this.mSynopsis = mSynopsis;
    }

    public String getmPosterUrlStr() {
        return mPosterUrlStr;
    }

    public void setmPosterUrlStr(String mPosterUrlStr) {
        this.mPosterUrlStr = mPosterUrlStr;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public double getmVoteAvg() {
        return mVoteAvg;
    }

    public void setmVoteAvg(double mVoteAvg) {
        this.mVoteAvg = mVoteAvg;
    }

    public String getmSynopsis() {
        return mSynopsis;
    }

    public void setmSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    protected Movie(Parcel in) {
        mPosterUrlStr = in.readString();
        mReleaseDate = in.readString();
        mTitle = in.readString();
        mVoteAvg = in.readDouble();
        mSynopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterUrlStr);
        dest.writeString(mReleaseDate);
        dest.writeString(mTitle);
        dest.writeDouble(mVoteAvg);
        dest.writeString(mSynopsis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
