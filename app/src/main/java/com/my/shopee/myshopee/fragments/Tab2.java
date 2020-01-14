package com.my.shopee.myshopee.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.my.shopee.myshopee.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tab2 extends Fragment {

    List<String> your_array_list;
    View view;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    public Tab2() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_tab2, container, false);
        listView = view.findViewById(R.id.otp_list_view);
        your_array_list = new ArrayList<String>();
        your_array_list.add("2542");
        your_array_list.add("1243");
        your_array_list.add("3545");
        your_array_list.add("3435");
        your_array_list.add("3245");
        arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, your_array_list );
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
