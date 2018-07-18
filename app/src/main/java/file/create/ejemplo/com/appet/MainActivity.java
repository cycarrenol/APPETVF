package file.create.ejemplo.com.appet;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import file.create.ejemplo.com.appet.dispositivosbt.DispositivosBT;

public class MainActivity extends AppCompatActivity {

    private Button BconectarBT;
    private Button BabrirArchivos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        BconectarBT = findViewById(R.id.BconectarBT);
        BabrirArchivos = findViewById(R.id.BabrirArchivos);

        BconectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });

        BabrirArchivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/myFolder/");
                intent.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent, "Open folder"));

            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
    public void openActivity2(){
        Intent intent = new Intent(this,DispositivosBT.class);
        startActivity(intent);
    }
}
