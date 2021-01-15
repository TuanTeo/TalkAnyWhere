package com.developer.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.myapplication.R;
import com.developer.myapplication.adapter.ListDeviceAdapter;

public class ListDeviceFragment extends Fragment {

    private RecyclerView mListDeviceRecyclerView;
    private ListDeviceAdapter mListDeviceAdapter;

    public ListDeviceFragment(ListDeviceAdapter listDeviceAdapter){
        mListDeviceAdapter = listDeviceAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_device, container, false);
        mListDeviceRecyclerView = view.findViewById(R.id.recycle_list_divice);
        mListDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDeviceRecyclerView.setAdapter(mListDeviceAdapter);
        return view;
    }
}
