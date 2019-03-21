package com.example.fawwazazrin.cleanmyriver_test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.fawwazazrin.cleanmyriver_test.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements Runnable {

    Camera camera;
    Camera.PictureCallback mPicture;
    SurfaceView oriHolder, transparentView;
    SurfaceHolder holder, holderTransparent;
    FloatingActionButton CameraButton;
    Thread thread = null;
    boolean isRunning = true;
    private float RectLeft, RectTop, RectRight, RectBottom;
    int deviceHeight, deviceWidth;
    public static Bitmap bitmap;
    Canvas canvas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        CameraButton = (FloatingActionButton) findViewById(R.id.cameraButton);

        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            camera.takePicture(null,null,mPicture);


            }

        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        }
        oriHolder = (SurfaceView) findViewById(R.id.oriHolder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            oriHolder.setSecure(true);
        }
        holder = oriHolder.getHolder();
        setupHolder();

        // Create second surface with another holder (holderTransparent)
        transparentView = (SurfaceView) findViewById(R.id.transparentView);
        holderTransparent = transparentView.getHolder();
        setupTransparentHolder();
        transparentView.setZOrderMediaOverlay(true);

        //get width and height of screen
        deviceWidth = getScreenWidth();
        deviceHeight = getScreenHeight();

    }

    private void setupHolder() {
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //synchronized (holder){Draw();} //call a draw method
                    camera = Camera.open(); //open a camera
                } catch (Exception e) {
                    Log.i("Exception", e.toString());
                    return;
                }
                Camera.Parameters params = camera.getParameters();

                //params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                // adjust the orientation of the camera
                Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                if (display.getRotation() == Surface.ROTATION_0) {
                    camera.setDisplayOrientation(90);
                }

                // display the camera preview
                try {
                    camera.stopPreview();
                    camera.setPreviewDisplay(holder);
                    Thread.sleep(100);
                    camera.startPreview();

                } catch (InterruptedException | IOException e) {
                    Log.i("Exception", e.toString());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                refreshCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.release();
            }
        });
    }

    public void setupTransparentHolder() {
        holderTransparent.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                thread = new Thread(CameraActivity.this);
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
//                holderTransparent.unlockCanvasAndPost(canvas);
//                isRunning = false;
            }
        });
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;

    }


    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;

    }


    public void run() {
        while(isRunning) {
            if (holder.getSurface().isValid()) {
                // Draw the rectangle on transparent SurfaceView
                canvas = holderTransparent.lockCanvas();
                Point centerOfCanvas = new Point(deviceWidth / 2, deviceHeight / 2);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(3);
                RectLeft = centerOfCanvas.x - (centerOfCanvas.x/2);
                RectTop = centerOfCanvas.y - (centerOfCanvas.x/2);
                RectRight = centerOfCanvas.x + (centerOfCanvas.x/2);
                RectBottom = centerOfCanvas.y + (centerOfCanvas.x/2);
                Rect rec = new Rect((int) RectLeft, (int) RectTop, (int) RectRight, (int) RectBottom);
                canvas.drawRect(rec, paint);
                holderTransparent.unlockCanvasAndPost(canvas);
            }
        }
    }


    public void refreshCamera() {
        if (holder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
            camera.setPreviewDisplay(holder);
            Thread.sleep(100);
            camera.startPreview();

        } catch (InterruptedException | IOException e) {
            Log.i("Exception", e.toString());
            return;
        }
    }

    private Camera.PictureCallback getPictureCallBack() {
        Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                Intent intent = new Intent(CameraActivity.this, ImageViewerActivity.class);
                startActivity(intent);

            }

        };
        return mPicture;
    }


}
