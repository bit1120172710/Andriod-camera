package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private  File imgfile;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_IMAGE_CAPTURE);
             //   ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
                //todo 在这里申请相机、存储的权限
            } else {
                takePicture();
            }
        });

    }

    private void takePicture() {
        Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgfile= Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if(imgfile!=null)
        {
            Uri fileUri =FileProvider.getUriForFile(this,"com.bytedance.camera.demo",imgfile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        }
        startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        //todo 打开相机
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }
    private void setPic() {
        int targetWidth=imageView.getWidth();
        int targetHeight=imageView.getHeight();
        BitmapFactory.Options bmOptions=new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imgfile.getAbsolutePath(),bmOptions);
        int photoW=bmOptions.outWidth;
        int photoH=bmOptions.outHeight;
        int scaleFactor=Math.min(photoW/targetWidth,photoH/targetHeight);
        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inPurgeable=true;
        Bitmap bitmap=BitmapFactory.decodeFile(imgfile.getAbsolutePath(),bmOptions);
        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap
        bitmap=Utils.rotateImage(bitmap,imgfile.getAbsolutePath());
        //todo 如果存在预览方向改变，进行图片旋转
        //Bundle extras=data.getExtras();
       // Bitmap imageBitmap=(Bitmap)extras.get("data");
        imageView.setImageBitmap(bitmap);
        //todo 如果存在预览方向改变，进行图片旋转
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { ;
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //todo 判断权限是否已经授予
                    //Log.d("aaaa", "onRequestPermissionsResult: "+grantResults.length);
                    takePicture();
                break;
            }

        }
    }
}
