package com.zukron.musicplayer.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zukron.musicplayer.Preferences;
import com.zukron.musicplayer.R;
import com.zukron.musicplayer.adapter.LibraryAdapter;
import com.zukron.musicplayer.model.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, StorageChooser.OnSelectListener, LibraryAdapter.OnSelected {
    private RecyclerView rvMain;
    private FloatingActionButton fabMain;
    private StorageChooser storageChooser;
    private ArrayList<Library> libraries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMain = findViewById(R.id.rv_main);
        fabMain = findViewById(R.id.fab_main);

        loadPreferences();
        permission();
        init();
    }

    private void loadPreferences() {
        libraries = Preferences.loadPreferences(getSharedPreferences(Preferences.NAME, Preferences.MODE_PRIVATE));

        if (libraries == null) {
            libraries = new ArrayList<>();
        } else {
            setRecyclerView();
        }
    }

    private void permission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        }
    }

    private void init() {
        // theme storage chooser
        StorageChooser.Theme theme = new StorageChooser.Theme(this);
        theme.setScheme(getResources().getIntArray(R.array.paranoid_theme));

        // storage chooser builder
        storageChooser = new StorageChooser.Builder()
                .withActivity(this)
                .setTheme(theme)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        fabMain.setOnClickListener(this);
        storageChooser.setOnSelectListener(this);
    }

    private void setRecyclerView() {
        LibraryAdapter libraryAdapter = new LibraryAdapter(this, libraries);
        libraryAdapter.setOnSelected(this);
        rvMain.setAdapter(libraryAdapter);
    }

    @Override
    public void onClick(View view) {
        storageChooser.show();
    }

    @Override
    public void onSelect(String path) {
        File file = new File(path);

        if (file.exists() || file.canRead()) {
            if (isFolderContainsMp3(file)) {
                libraries.add(new Library(file.getName(), file.getAbsolutePath()));
                removeDuplicate(libraries);

                setRecyclerView();
                Preferences.savePreferences(libraries, getSharedPreferences(Preferences.NAME, Preferences.MODE_PRIVATE));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Failed");
                builder.setMessage("File .mp3 tidak terdeteksi");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        }
    }

    private boolean isFolderContainsMp3(File file) {
        boolean valid = false;
        File[] files = file.listFiles();

        if (files != null) {
            for (File value : files) {
                valid = value.getAbsolutePath().contains(".mp3");
                break;
            }
        }

        return valid;
    }

    private void removeDuplicate(ArrayList<Library> libraries) {
        Set<Library> set = new HashSet<>(libraries);
        libraries.clear();
        libraries.addAll(set);
    }

    @Override
    public void onSelectedItem(Library library) {
        Intent intent = new Intent(this, DetailMusicActivity.class);
        intent.putExtra("Library", library);
        startActivity(intent);
    }
}