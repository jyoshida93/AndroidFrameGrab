package com.example.android.androidframegrab;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        Button b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = Environment.getExternalStorageDirectory();
                File vidFile = new File(path, "/vid/short.ts");
                File imageFile = new File(path, "/images");
                Log.d(TAG,vidFile.toString());
                grabFrames(vidFile, imageFile);
            }
        });
    }

    private void grabFrames(File vidFile, File imageDirectory) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(vidFile);
        AndroidFrameConverter converter = new AndroidFrameConverter();
        try {
            grabber.start();
            Frame frame;
            int imageNumber = 1;
            // save every 20th frame
            while((frame = grabber.grabImage()) != null) {
                if (imageNumber % 20 == 1) {
                    Bitmap bitMap = converter.convert(frame);
                    File outputFile = new File(imageDirectory.toString()+"/image"+imageNumber+".jpg");
                    saveImage(bitMap, outputFile);
                }
                imageNumber++;
            }
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap bitMap, File outputFile) {
        try {
            FileOutputStream fout = new FileOutputStream(outputFile);
            bitMap.compress(Bitmap.CompressFormat.JPEG, 85, fout); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                    // permission was granted.
                }
                return;
            }
        }
    }
}
