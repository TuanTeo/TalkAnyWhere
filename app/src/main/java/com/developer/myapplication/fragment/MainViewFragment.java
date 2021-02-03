package com.developer.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.myapplication.R;
import com.developer.myapplication.listener.IMainViewListener;

public class MainViewFragment extends Fragment {

    private Button mListenButton;
    private Button mCallButton;
    private IMainViewListener mIMainViewListener;

    public MainViewFragment(IMainViewListener iMainViewListener){
        mIMainViewListener = iMainViewListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_view, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        mCallButton = view.findViewById(R.id.call_button);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainViewListener.connectToDivice();
            }
        });

        mListenButton = view.findViewById(R.id.listen_button);
        mListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainViewListener.enableScanDevice();
            }
        });
    }
}
