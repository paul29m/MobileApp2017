package com.example.paulinho.wantedcars.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paulinho.wantedcars.R;
import com.example.paulinho.wantedcars.model.Car;

import java.util.ArrayList;

/**
 * Created by paulinho on 11/24/2017.
 */
public class CarListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Car> carlist;

    public CarListAdapter(Context context, int layout, ArrayList<Car> carList) {
        this.context = context;
        this.layout = layout;
        this.carlist = carList;
    }

    @Override
    public int getCount() {
        return carlist.size();
    }

    @Override
    public Object getItem(int position) {
        return carlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtYear;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtYear = (TextView) row.findViewById(R.id.txtYear);
            holder.imageView = (ImageView) row.findViewById(R.id.imgCar);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Car car = carlist.get(position);
        holder.txtName.setText(car.getName());
        holder.txtYear.setText(car.getYear());

        byte[] carImage = car.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(carImage, 0, carImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
