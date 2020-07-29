package com.zukron.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/27/2020
 */
public class Library implements Parcelable {
    private String title;
    private String path;

    public Library(String title, String path) {
        this.title = title;
        this.path = path;
    }

    protected Library(Parcel in) {
        title = in.readString();
        path = in.readString();
    }

    public static final Creator<Library> CREATOR = new Creator<Library>() {
        @Override
        public Library createFromParcel(Parcel in) {
            return new Library(in);
        }

        @Override
        public Library[] newArray(int size) {
            return new Library[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(path);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Library) {
            Library temp = (Library) o;
            return this.title.equals(temp.title) && this.path.equals(temp.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.title.hashCode() + this.path.hashCode());
    }
}
