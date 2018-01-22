package com.example.paulinho.wantedcars.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.paulinho.wantedcars.R;
import com.example.paulinho.wantedcars.util.ExecutorSingleton;
import com.example.paulinho.wantedcars.util.SQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

/**
 * Created by paulinho on 11/24/2017.
 */

public class MainActivity extends AppCompatActivity {

    private EditText edtName, edtYear, edtDesc, edtCat;
    private Button btnChoose, btnAdd, btnList, btnSelect;
    private ImageView imageView;
    private NotificationManager mNotifyMgr;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    final int REQUEST_CODE_GALLERY = 999;
    private ExecutorService executor = ExecutorSingleton.executor;
    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init();
        executor.submit(initrunnable);

        sqLiteHelper = new SQLiteHelper(this, "CARS.sqlite", null, 1);

        //sqLiteHelper.queryData("Drop Table CARS");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CARS(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, year VARCHAR, description  VARCHAR, category VARCHAR, image BLOB)");

        mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sqLiteHelper.insertData(
                            edtName.getText().toString().trim(),
                            edtYear.getText().toString().trim(),
                            edtDesc.getText().toString().trim(),
                            edtCat.getText().toString().trim(),
                            imageViewToByte(imageView)
                    );
                    //Toast.makeText(getApplicationContext(), "Car added to database!", Toast.LENGTH_SHORT).show();
                    showNotification(view,edtName.getText().toString());
                    edtName.setText("");
                    edtYear.setText("");
                    edtDesc.setText("");
                    edtCat.setText("");
                    imageView.setImageResource(R.drawable.basic_car);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CarListActivity.class);
                startActivity(intent);
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date =  year+"";
                edtYear.setText(date);
            }
        };
    }
    private void showNotification(View v,String name){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setContentTitle("Car aded to database");
        mBuilder.setContentText("Car name: "+name);
        int mNotificationId = 001;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }




    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    Runnable initrunnable = new Runnable() {
        @Override
        public void run() {
            edtName = (EditText) findViewById(R.id.edtName);
            edtYear = (EditText) findViewById(R.id.edtYear);
            edtCat = (EditText) findViewById(R.id.edtCat);
            edtDesc =(EditText) findViewById(R.id.edtDesc);
            btnChoose = (Button) findViewById(R.id.btnChoose);
            btnAdd = (Button) findViewById(R.id.btnAdd);
            btnList = (Button) findViewById(R.id.btnList);
            btnSelect =(Button) findViewById(R.id.btnSelect);
            imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.basic_car);
        }
    };

    private void init(){
        edtName = (EditText) findViewById(R.id.edtName);
        edtYear = (EditText) findViewById(R.id.edtYear);
        edtCat = (EditText) findViewById(R.id.edtCat);
        edtDesc =(EditText) findViewById(R.id.edtDesc);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnList = (Button) findViewById(R.id.btnList);
        btnSelect =(Button) findViewById(R.id.btnSelect);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.basic_car);
    }


}
