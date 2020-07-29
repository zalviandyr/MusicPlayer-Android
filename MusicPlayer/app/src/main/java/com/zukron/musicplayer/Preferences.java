package com.zukron.musicplayer;

import android.content.SharedPreferences;

import com.zukron.musicplayer.model.Library;

import java.util.ArrayList;

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/28/2020
 */
public class Preferences {
    public static final String NAME = "MusicPlayerPref";
    public static final int MODE_PRIVATE = 0;

    public static ArrayList<Library> loadPreferences(SharedPreferences sharedPreferences) {
        ArrayList<Library> libraries = null;
        int size = sharedPreferences.getInt("Musics_Size", 0);

        if (size != 0) {
            libraries = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String title = sharedPreferences.getString("Musics_Title_" + i, null);
                String path = sharedPreferences.getString("Musics_Path_" + i, null);
                libraries.add(new Library(title, path));
            }
        }

        return libraries;
    }

    public static void savePreferences(ArrayList<Library> libraries, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Musics_Size", libraries.size());

        for (int i = 0; i < libraries.size(); i++) {
            editor.putString("Musics_Title_" + i, libraries.get(i).getTitle());
            editor.putString("Musics_Path_" + i, libraries.get(i).getPath());
        }

        editor.apply();
    }
}
