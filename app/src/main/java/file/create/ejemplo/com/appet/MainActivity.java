package file.create.ejemplo.com.appet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import file.create.ejemplo.com.appet.dispositivosbt.DispositivosBT;

public class MainActivity extends AppCompatActivity {

    private Button BconectarBT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        BconectarBT = findViewById(R.id.BconectarBT);

        BconectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });
    }


    public void openActivity2(){
        Intent intent = new Intent(this,DispositivosBT.class);
        startActivity(intent);
    }
}
