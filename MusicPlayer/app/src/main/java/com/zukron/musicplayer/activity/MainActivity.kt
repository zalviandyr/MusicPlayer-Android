package com.zukron.musicplayer.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.codekidlabs.storagechooser.StorageChooser
import com.zukron.musicplayer.MusicPreferences
import com.zukron.musicplayer.R
import com.zukron.musicplayer.adapter.LibraryAdapter
import com.zukron.musicplayer.model.Library
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private var libraries = mutableListOf<Library>()
    private val libraryAdapter = LibraryAdapter(this)
    private var storageChooser: StorageChooser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check permission
        if (checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
        }

        // load preferences
        val librariesPref = MusicPreferences.load(this)
        librariesPref.let {
            libraries = it as MutableList<Library>
            libraryAdapter.libraries = libraries

            recycler_main.adapter = libraryAdapter
        }

        // storage chooser builder
        val storageChooserTheme = StorageChooser.Theme(this)
        storageChooserTheme.scheme = resources.getIntArray(R.array.paranoid_theme)

        storageChooser = StorageChooser.Builder()
                .withActivity(this)
                .setTheme(storageChooserTheme)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build()
    }

    override fun onStart() {
        super.onStart()

        libraryAdapter.onSelected = object : LibraryAdapter.OnSelected {
            override fun onSelectedItem(library: Library) {
                val intent = Intent(this@MainActivity, DetailMusicActivity::class.java)
                intent.putExtra(DetailMusicActivity.EXTRA_LIBRARY, library)
                startActivity(intent)
            }
        }

        btn_floating_action.setOnClickListener {
            storageChooser?.show()
        }

        storageChooser?.setOnSelectListener {
            val file = File(it)
            val isContainsMp3: () -> Boolean = {
                var valid = false
                val fileList: Array<File>? = file.listFiles()
                if (fileList != null) {
                    for (fileVal in fileList) {
                        val isMp3 = fileVal.absolutePath.contains(".mp3").also { exist ->
                            valid = exist
                        }
                        if (isMp3) break
                    }
                }
                valid
            }

            if (file.exists() || file.canRead()) {
                val exist = isContainsMp3()
                if (exist) {
                    libraries.add(Library(file.name, file.absolutePath))
                    libraries = libraries.distinct().toMutableList()

                    libraryAdapter.libraries = libraries
                    libraryAdapter.notifyDataSetChanged()

                    MusicPreferences.save(libraries, this)
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Failed")
                    alertDialogBuilder.setMessage("File .mp3 tidak terdeteksi")
                    alertDialogBuilder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                            dialogInterface?.dismiss()
                        }
                    })

                    alertDialogBuilder.show()
                }
            }
        }
    }
}
