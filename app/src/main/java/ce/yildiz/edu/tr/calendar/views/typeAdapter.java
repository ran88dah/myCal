package ce.yildiz.edu.tr.calendar.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ce.yildiz.edu.tr.calendar.R;


public class typeAdapter extends ArrayAdapter<typeItem>{

    public typeAdapter(Context context, ArrayList<typeItem> typeList) {
        super(context, 0, typeList);
    }

    //@NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.type_dpinner_row, parent, false
            );
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.img_view1);
        TextView textViewName = convertView.findViewById(R.id.text_view1);

        typeItem currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getTypeImage());
            textViewName.setText(currentItem.getTypeName());
        }

        return convertView;
    }
}