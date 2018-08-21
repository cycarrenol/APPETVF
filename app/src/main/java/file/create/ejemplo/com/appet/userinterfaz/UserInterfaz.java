package file.create.ejemplo.com.appet.userinterfaz;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import android.os.Handler;


import file.create.ejemplo.com.appet.Help;
import file.create.ejemplo.com.appet.R;
import file.create.ejemplo.com.appet.dispositivosbt.DispositivosBT;
import file.create.ejemplo.com.appet.MainActivity;

public class UserInterfaz extends AppCompatActivity  {


    Spinner numeleMySpinnner;

    private boolean repetirEnvio = true;//U

    private String datoEnvioSeparacionAlterno;//U
    private String DatoSpinner;//U
    private String aleatorioString;//U



    private int ElContador=0;//U
    private int mCounter;//U
    private int mInterval = 1000;//U
    private int mMaxRepeat = 60;//U
    private int contadorSeparacion = 0;//U
    private int permitirIterar = 0;//U

    private Handler mHandler;//U
    private Runnable mRunnable;//U

    ImageButton BatrasDispositivos;//U
    ImageButton BguardarTXT;//U
    ImageButton BiniciarEnvioRecepcion;//U
    ImageButton BenviarSepNumele;//U
    ImageButton BAyuda;//U
    ImageButton IBcheckSep;
    ImageButton IBcheckNumele;
    EditText ETSeparacion;//U
    TextView TVresistividades;//U
    TextView TVAvanceResistividades;

    //-------------------------------------------
    //USED
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interfaz);


        //------------------------------Enlaza  los elementos del Layout con las variables de UserInterfaz

        BenviarSepNumele = (ImageButton) findViewById(file.create.ejemplo.com.appet.R.id.BenviarSepNumele);
        BAyuda = (ImageButton) findViewById(R.id.BAyuda);
        BiniciarEnvioRecepcion = (ImageButton) findViewById(R.id.BiniciarEnvioRecepcion);
        BatrasDispositivos = (ImageButton) findViewById(R.id.BatrasDispositivos);
        BguardarTXT = (ImageButton) findViewById(R.id.BguardarTXT);
        IBcheckSep = findViewById(R.id.IBcheckSep);
        IBcheckNumele = findViewById(R.id.IBcheckNumele);
        ETSeparacion = (EditText) findViewById(file.create.ejemplo.com.appet.R.id.ETSeparacion);
        TVresistividades = (TextView) findViewById(R.id.TVresistividades);
         numeleMySpinnner = findViewById(R.id.IDSPINNER);
        TVAvanceResistividades = findViewById(R.id.TVAvanceResistividades);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ComboNumele,android.R.layout.simple_spinner_item);

        numeleMySpinnner.setAdapter(adapter);

        TVresistividades.setMovementMethod(new ScrollingMovementMethod());

        //------------------------------Enlaza  los elementos del Layout con las variables de UserInterfaz



        ///-----------------------------------------------------------------Metodo para crear el TXT

        BguardarTXT.setOnClickListener(new View.OnClickListener() {// METODO PARA CREAR EL ARCHIVO EN UNA CARPETA
            @Override
            public void onClick(View view) {

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("HH mm ss(dd MM  yyyy)");
                String archivo = df.format(c) + ".txt";

                BguardarTXT.setBackgroundColor(Color.GRAY);

                FileOutputStream fileOutputStream = null;


                File myDirectoty1 = new File(Environment.getExternalStorageDirectory(), "RESISTIVIDADESAPPET");

                  myDirectoty1.mkdir();

                File file = new File(myDirectoty1, archivo);

                Context context = getApplicationContext();

                String ruta = "Archivo guardado en: " + myDirectoty1.getName() + "/" + archivo;

                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, ruta, duration);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);// CHANGE POSITION OF TOAST
                toast.show();

                try {

                    FileOutputStream os = fileOutputStream = new FileOutputStream(file);
                    String data = TVresistividades.getText().toString();
                    TVresistividades.setText(TVresistividades.getText());
                    os.write(data.getBytes());
                    os.close();
                } catch (Exception e) {// Con exception  se maneja cualquier excepción
                    e.printStackTrace();
                }
            }//FIN ONCLICK
        });

        //-----------------------------------------------------------------Fin método para crear TXT



        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);


                        if (ElContador ==0){
                            if (dataInPrint.equals(ETSeparacion.getText().toString())) {
                                IBcheckSep.setVisibility(View.VISIBLE);
                                ElContador++;
                                contadorSeparacion++;
                            }
                        }

                       if (ElContador==1) {

                           if (dataInPrint.equals(DatoSpinner)) {

                               IBcheckNumele.setVisibility(View.VISIBLE);
                               ElContador++;
                               BenviarSepNumele.setBackgroundColor(Color.rgb(255, 191, 0));
                               ETSeparacion.setTextColor(Color.BLACK);

                           }
                       }



                        if (permitirIterar >= 1) {

                            aleatorioString = dataInPrint;
                        }

                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click

        BenviarSepNumele.setOnClickListener(new View.OnClickListener() {// ENCENDER LED
            public void onClick(View v)

            {
                String datoEnvioSeparacion = ETSeparacion.getText().toString();
                String datoEnvioNumele =  DatoSpinner;

                if(datoEnvioNumele.equals("8")){

                    mMaxRepeat = 8;

                    }else if(datoEnvioNumele.equals("16")){

                    mMaxRepeat = 36;

                }else if(datoEnvioNumele.equals("32")){

                    mMaxRepeat = 156;

                }else if(datoEnvioNumele.equals("48")){

                    mMaxRepeat = 361;

                }

                 datoEnvioSeparacionAlterno = datoEnvioSeparacion;

                if (datoEnvioSeparacion.matches("") || ETSeparacion.getText().toString().equals(".")) {

                    ETSeparacion.setText("");
                    ETSeparacion.setHint("Ingresar");
                    ETSeparacion.setHintTextColor(Color.RED);
                    ETSeparacion.setTextColor(Color.BLACK);


                } else if (DatoSpinner.equals("seleccionar")) {

                    numeleMySpinnner.setBackgroundColor(Color.parseColor("#ffcccc"));

                } else {
                    if(contadorSeparacion ==0){
                        datoEnvioSeparacionAlterno = datoEnvioSeparacionAlterno+"#";
                        MyConexionBT.write(datoEnvioSeparacionAlterno);
                        numeleMySpinnner.setBackgroundColor(Color.parseColor("#f5f5dc"));
                    }
                    if(contadorSeparacion == 1 ){
                        datoEnvioNumele =  datoEnvioNumele+"$";
                        MyConexionBT.write(datoEnvioNumele);

                    }

                }
            }
        });
        mHandler = new Handler();

        BiniciarEnvioRecepcion.setOnClickListener(new View.OnClickListener() {// APAGAR LED
            public void onClick(View v) {

                if (contadorSeparacion >= 1 && repetirEnvio == true ) {

                    BiniciarEnvioRecepcion.setBackgroundColor(Color.rgb(191, 191, 191));

                     repetirEnvio = false;

                    permitirIterar = permitirIterar + 1;
                    mCounter = 0;

                    mRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // Método que marca la iteración
                            doTask();
                        }
                    };
                    mHandler.postDelayed(mRunnable, (mInterval));
                }
            }
        });

        BAyuda.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                openActivity4();

            }
        });

        numeleMySpinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DatoSpinner = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        BatrasDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    @Override
    public void onResume() {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {

                Context context = getApplicationContext();
                finish();
            }
        }
    }


    protected void doTask() {
        mCounter++;

        MyConexionBT.write("GO*");

        //TVresistividades.setText(aleatorioString);
        if(mCounter==1){
            TVresistividades.setText("");
        }else{
            TVresistividades.append(String.valueOf(aleatorioString));
            TVresistividades.append("\n"); // for new line
            TVAvanceResistividades.setText(mCounter-1+" de "+(mMaxRepeat-1));
         }

         // Schedule the task to do again after an interval
        mHandler.postDelayed(mRunnable, mInterval);

        // If the task reach the maximum repeat count then stop it here
        if (mCounter == mMaxRepeat) {
            /*
                public final void removeCallbacks (Runnable r)
                    Remove any pending posts of Runnable r that are in the message queue.
            */


            mHandler.removeCallbacks(mRunnable);
        }
    }


    public void openActivity2() {
        Intent intent = new Intent(this, DispositivosBT.class);
        startActivity(intent);
    }

    public void openActivity4() {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }


}