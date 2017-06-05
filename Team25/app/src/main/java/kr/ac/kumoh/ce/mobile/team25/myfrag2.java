package kr.ac.kumoh.ce.mobile.team25;

/**
 * Created by dlgus on 2017-04-24.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class myfrag2 extends Fragment {
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA=0;
    Button camerabtn;
    Button Openbtn;
    Button Lockbtn;
    Button Bannapbtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my2, container, false);
        camerabtn=(Button)rootView.findViewById(R.id.camera);
        Openbtn=(Button)rootView.findViewById(R.id.open);
        Lockbtn =(Button)rootView.findViewById(R.id.lock);
        Bannapbtn=(Button)rootView.findViewById(R.id.bannap);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTakePhotoAction();
            }
        });
        Bannapbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        Openbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        Lockbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        return rootView;
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url="tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        mImageCaptureUri=Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));
        Log.i("사진파일",""+mImageCaptureUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
        startActivityForResult(intent,PICK_FROM_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.actoin.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                break;
            }
        }
    }
}
