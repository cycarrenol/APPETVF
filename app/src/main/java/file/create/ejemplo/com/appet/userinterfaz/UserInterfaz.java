package file.create.ejemplo.com.appet.userinterfaz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import file.create.ejemplo.com.appet.R;
import file.create.ejemplo.com.appet.dispositivosbt.DispositivosBT;
import file.create.ejemplo.com.appet.MainActivity;

public class UserInterfaz extends AppCompatActivity {


    public String StringAleatorio;
    public int i = 0;
    Button createq, BConfig, Begin;
    TextView textTXT;
    Button EnviarNumele, EnviarSeparacion,IdDesconectar;
    TextView IdBufferIn, IdBufferIn2, TEXTTEST;
    EditText ETSeparacion;
    EditText NUMELE;


    //-------------------------------------------
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
        //2)
        //Enlaza los controles con sus respectivas vistas
        EnviarNumele = (Button) findViewById(file.create.ejemplo.com.appet.R.id.EnviarNumele);
        EnviarSeparacion = (Button) findViewById(file.create.ejemplo.com.appet.R.id.EnviarSeparacion);
        IdDesconectar = (Button) findViewById(file.create.ejemplo.com.appet.R.id.IdDesconectar);
        Begin = (Button) findViewById(R.id.Begin);
        //IdBufferIn = (TextView) findViewById(file.create.ejemplo.com.appet.R.id.IdBufferIn);
        //IdBufferIn2 = (TextView) findViewById(file.create.ejemplo.com.appet.R.id.IdBufferIn2);
        textTXT = (TextView) findViewById(R.id.textoTXT);

        // TEXTTEST = (TextView) findViewById(file.create.ejemplo.com.controlbt.R.id.TEXTTEST);


        createq = (Button) findViewById(R.id.createq);// asigno a la variable create (de tipo botton) al elemento Button del layout

        textTXT = (TextView) findViewById(R.id.textoTXT);// /Contenido del  archivo/ tenga presente que lo que está después del  R.id es el id del elemento del layout
        // textRuta = (TextView) findViewById(R.id.textoRUTA);// muestra la ruta de almacenamiento del archivo


        ETSeparacion =   findViewById(file.create.ejemplo.com.appet.R.id.ETSeparacion);
        NUMELE =   findViewById(file.create.ejemplo.com.appet.R.id.NUMELE);


        ///MET CREAR TXT

        createq.setOnClickListener(new View.OnClickListener() {// METODO PARA CREAR EL ARCHIVO EN UNA CARPETA
            @Override
            public void onClick(View view) {

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss(dd-MM-yyyy)");
                String archivo = df.format(c)+".txt";

                //String archivo ="Miarchivo.txt";

                createq.setBackgroundColor(Color.YELLOW);
                createq.setTextColor(Color.BLACK);

                FileOutputStream fileOutputStream = null;

                File myDirectoty1 =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"RESISTIVIDADES");


                myDirectoty1.mkdir();
                File file = new File(myDirectoty1, archivo);

                Context context = getApplicationContext();

                String ruta ="Archivo guardado en: "+myDirectoty1.getName()+"/"+archivo;

                //String ruta = myDirectoty1.getAbsolutePath()+"/"+archivo;
                //  textRuta.setText(ruta);// Imprime la ruta del almacenamiento  del archivo

                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, ruta, duration);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);// CHANGE POSITION OF TOAST
                toast.show();

                try {

                    FileOutputStream os = fileOutputStream = new FileOutputStream(file);
                    String data ="HOLA MUNDO";
                    textTXT.setText(data);
                    os.write(data.getBytes());
                    os.close();
                } catch ( Exception e) {// Con exception  se maneja cualquier excepción
                    e.printStackTrace();
                }
            }
        });

        /// MET CREAR TXT

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);

                        //TEXTTEST.setText(dataInPrint);


                        if(dataInPrint.equals("SEP_OK")){


                           // IdBufferIn.setText(" " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                            EnviarSeparacion.setBackgroundColor(Color.GREEN);
                            EnviarSeparacion.setText("OK");
                        }else if(dataInPrint.equals("NUMEL_OK")){

                            //IdBufferIn2.setText(" " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                            EnviarNumele.setBackgroundColor(Color.GREEN);
                            EnviarNumele.setText("OK");
                        }else if(dataInPrint.length() == 1){

                            textTXT.setText(" " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
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
        EnviarSeparacion.setOnClickListener(new View.OnClickListener() {// ENCENDER LED
            public void onClick(View v)

            {


                String SEPA  =  ETSeparacion.getText().toString()+"#";

                MyConexionBT.write(SEPA);
                // MyConexionBT.write(NUMEROELEC);


            }
        });

        EnviarNumele.setOnClickListener(new View.OnClickListener() {// APAGAR LED
            public void onClick(View v) {

                String NUMEROELEC  =  NUMELE.getText().toString() +"$";

                MyConexionBT.write(NUMEROELEC);
            }
        });


        Begin.setOnClickListener(new View.OnClickListener() {// ENCENDER LED

            Random r = new Random();

            public void onClick(View v)

            {



                for( i = 0; i < 60; i++){

                    int randomNumber = 10 + r.nextInt(89);

                    StringAleatorio = String.valueOf(randomNumber);


                    StringAleatorio  =  ETSeparacion.getText().toString()+"*";

                    MyConexionBT.write(StringAleatorio);



                }

            }
        });




        IdDesconectar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
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
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
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
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}