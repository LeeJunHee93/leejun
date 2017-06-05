package kr.ac.kumoh.ce.mobile.team25;

/**
 * Created by dlgus on 2017-04-24.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class myfrag4 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my4, container, false);
        Button loginbtn = (Button) rootView.findViewById(R.id.RR);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), myfrag4_1.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}

