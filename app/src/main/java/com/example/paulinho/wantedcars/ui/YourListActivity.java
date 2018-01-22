package com.example.paulinho.wantedcars.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paulinho.wantedcars.R;
import com.example.paulinho.wantedcars.model.Car;
import com.example.paulinho.wantedcars.util.CarListAdapter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by paulinho on 11/24/2017.
 */

public class YourListActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<Car> list;
    CarListAdapter adapter = null;
    String s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
         s = getIntent().getStringExtra("SESSION_USER");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list_activity);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new CarListAdapter(this, R.layout.car_items, list);
        gridView.setAdapter(adapter);
        updateCarList();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Show details", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(YourListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        //get id from database
                        Cursor c = LogInActivity.sqLiteHelper.getData("SELECT id FROM CARS");
                        ArrayList<Integer> arrID = new ArrayList<Integer>();
                        while (c.moveToNext()){
                            arrID.add(c.getInt(0));
                        }
                        if (item == 0) {
                            //details
                            showDialogDetails(YourListActivity.this, arrID.get(position));
                        }
                        else
                            if (item == 1) {

                                    // update
                                    showDialogUpdate(YourListActivity.this, arrID.get(position));

                        } else
                            {
                            // delete

                                if(s.equals("m7paul29@gmail.com")) {
                                    showDialogDelete(arrID.get(position));
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "You are not authorized", Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    ImageView imageViewCar;
    private void showDialogUpdate(Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_car_activity);
        dialog.setTitle("Update");
        Car thiscar = null;
        for (Car car : list)
            if (car.getId() == position)
                thiscar=car;
        imageViewCar = (ImageView) dialog.findViewById(R.id.imageViewCar);
        byte[] carImage = thiscar.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(carImage, 0, carImage.length);
        imageViewCar.setImageBitmap(bitmap);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtName.setText(thiscar.getName());
        final EditText edtYear = (EditText) dialog.findViewById(R.id.edtYear);
        edtYear.setText(thiscar.getYear());
        final EditText edtDesc = (EditText) dialog.findViewById(R.id.edtDescr);
        edtDesc.setText(thiscar.getDescription());
        final EditText edtCat = (EditText) dialog.findViewById(R.id.edtCategory);
        edtCat.setText(thiscar.getCategory());
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);
        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        YourListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LogInActivity.sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtYear.getText().toString().trim(),
                            edtDesc.getText().toString().trim(),
                            edtCat.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewCar),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                    Toast.makeText(getApplicationContext(), "Please fill the spaces with good data", Toast.LENGTH_SHORT).show();
                }
                updateCarList();
            }
        });
    }
    private void showDialogDetails(Activity activity ,final int position){final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.show_car_activity);
        dialog.setTitle("Update");
        Car thiscar = null;
        for (Car car : list)
            if (car.getId() == position)
                thiscar=car;
        imageViewCar = (ImageView) dialog.findViewById(R.id.imageViewCar);
        byte[] carImage = thiscar.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(carImage, 0, carImage.length);
        imageViewCar.setImageBitmap(bitmap);
        final TextView edtName = (TextView) dialog.findViewById(R.id.edtName);
        edtName.setText("Vehicle: " + thiscar.getName());
        final TextView edtYear = (TextView) dialog.findViewById(R.id.edtYear);
        edtYear.setText("Year: "+ thiscar.getYear());
        final TextView edtDesc = (TextView) dialog.findViewById(R.id.edtDescr);
        edtDesc.setText("INFO: "+thiscar.getDescription());
        final TextView edtCat = (TextView) dialog.findViewById(R.id.edtCategory);
        edtCat.setText("Category: "+thiscar.getCategory());
        Button btnAddList = (Button) dialog.findViewById(R.id.btnAddList);
        Button btnShare = (Button) dialog.findViewById(R.id.btnShare);
        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.8);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO
            }
            });

        final Car finalThiscar = thiscar;
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(finalThiscar);
            }
        });
    }

    private void showDialogDelete(final int idCar){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(YourListActivity.this);

        dialogDelete.setTitle("Warning:");
        dialogDelete.setMessage("Are you sure you want to delete this car?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {LogInActivity.sqLiteHelper.deleteData(idCar);
                    Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateCarList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
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

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewCar.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
         s = getIntent().getStringExtra("SESSION_USER");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateCarList(){
        // get all data from sqlite
        Cursor cursor = LogInActivity.sqLiteHelper.getData("SELECT * FROM CARS");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String year = cursor.getString(2);
            String description =cursor.getString(3);
            String category= cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            list.add(new Car(id,name, year, description,category, image));
        }
        adapter.notifyDataSetChanged();
    }


    private void sendEmail(Car car){

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Found this awesome car"+car.getName());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I found it on Wanted Cars app:\n "+car.toString());
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(YourListActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}