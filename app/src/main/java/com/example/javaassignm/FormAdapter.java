package com.example.javaassignm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.javaassignm.databinding.ItemFormBinding;

import java.util.List;

public class FormAdapter extends ArrayAdapter<FormItem> {
    private Context context;
    private List<FormItem> formItemList;

    public FormAdapter(Context context, List<FormItem> formItemList) {
        super(context, 0, formItemList);
        this.context = context;
        this.formItemList = formItemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemFormBinding binding;

        if (convertView == null) {
            binding = ItemFormBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemFormBinding) convertView.getTag();
        }

        FormItem item = formItemList.get(position);

        binding.tvName.setText(item.getName());
        binding.tvDate.setText(item.getDate());
        binding.imageView.setImageURI(item.getImageUri());

        return convertView;
    }
}
