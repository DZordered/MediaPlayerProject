package com.example.denis.mediaplayerproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Class what provide editing special directory
 * for find music in
 * <p>
 */
public class EditDirActivity extends AppCompatActivity {
    /**
     * Text what user edit
     */
    private EditText dirFinder;
    /**
     * Converted dirFinder in string
     */
    private String dirPathFromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_of_dir);


        dirFinder = (EditText) findViewById(R.id.editDirFinder);
        Button getDirButton = (Button) findViewById(R.id.getDirButton);
        getDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // convert our text what user input
                dirPathFromUser = dirFinder.getText().toString();
                Intent intent = new Intent(EditDirActivity.this, ListOfDirActivity.class);
                intent.putExtra("dirPathFromUser", dirPathFromUser);
                startActivity(intent);
            }
        });
    }


}

