package com.example.mehrb.filesharing.View;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.mehrb.filesharing.R;
import com.example.mehrb.filesharing.ViewModel.controller.ClientHandler;
import com.example.mehrb.filesharing.ViewModel.controller.ServerRunnable;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private EditText ipEditText;
    private EditText portEditText;
    private EditText idEditText;
    private EditText fileEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        ipEditText = (EditText) findViewById(R.id.et_ip);
        portEditText = (EditText) findViewById(R.id.et_port);
        idEditText = (EditText) findViewById(R.id.et_id);
        fileEditText = (EditText) findViewById(R.id.et_file);
    }

    public void onGetButtonClickListener(View view) {
        Executors.newCachedThreadPool().execute(new ClientHandler(fileEditText.getText().toString(),
                ipEditText.getText().toString(),
                Integer.parseInt(portEditText.getText().toString())
                , idEditText.getText().toString()));
        Executors.newCachedThreadPool().execute(new ServerRunnable(ipEditText.getText().toString(), idEditText.getText().toString()));
    }
}
