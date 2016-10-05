package net.rincewind.biometric;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import cat.lafosca.facecropper.FaceCropper;

public class RegisterActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private EasyCamera camera;
    private EasyCamera.CameraActions actions;

    private final String TAG = "Register";

    private int photocount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView2);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

//        PicassoFaceDetector.initialize(this);

        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    }
                }
        );

        final Button btnCapture = (Button)findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        /*
                        final int[] num = {0};
                        final EasyCamera.PictureCallback callback = new EasyCamera.PictureCallback() {
                            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                                cropAndSaveImage(data, "photo" + num[0] + ".jpg");
                                num[0]++;
                                startActivity(new Intent(RegisterActivity.this, ConfirmRegistrationActivity.class));
                            }
                        };
                        */

                        btnCapture.setEnabled(false);

                        final EasyCamera.PictureCallback callback = new EasyCamera.PictureCallback() {
                            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                                System.out.println("Meow2!");
                                String filename = photocount + "-photo.png";
//                                try {
                                    Bitmap cropped = Utils.cropFace(data, view.getContext());
                                    if(cropped != null) {
                                        Utils.saveImage(cropped, filename, view.getContext());

                                        System.out.println("SAVED " + filename);

                                        if (photocount > 1) {
                                            Intent result = new Intent(RegisterActivity.this, ConfirmRegistrationActivity.class);
                                            startActivity(result);
                                        } else {
                                            photocount++;
                                            btnCapture.setEnabled(true);
                                            Toast toast = Toast.makeText(view.getContext(), "Ta ett foto till.", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    } else {
                                        btnCapture.setEnabled(true);
                                        Toast toast = Toast.makeText(view.getContext(), "Fotot kunde inte användas. Ta ett foto till.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
/*
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    btnCapture.setEnabled(true);
                                    Toast toast = Toast.makeText(view.getContext(), "Fotot kunde inte användas. Ta ett foto till.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
*/

                            }
                        };

                        actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
                        System.out.println("Meow!");

                        /*
                        final EasyCamera.PictureCallback callback = new EasyCamera.PictureCallback() {
                            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                                System.out.println("Meow2!");
                                FileOutputStream fos = null;
                                String filename = photocount + "-photo.jpg";
                                try {
                                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
                                    fos.write(data);
                                    fos.close();

                                    System.out.println("SAVED " + filename);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Utils.cropFace(filename, view.getContext());

                                if(photocount > 1) {
                                    Intent result = new Intent(RegisterActivity.this, ConfirmRegistrationActivity.class);
                                    startActivity(result);
                                } else {
                                    photocount++;
                                    btnCapture.setEnabled(true);
                                    Toast toast = Toast.makeText(view.getContext(), "Ta ett foto till", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }
                        };

                        actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
                        System.out.println("Meow!");
*/
                        /*
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
//                                    Thread.sleep(500);
//                                    actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
//                                    Thread.sleep(500);
//                                    actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        */
                    }
                }
        );

    }

    private void cropAndSaveImage(byte[] data, String path) {
        System.out.println("Meow2!");
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(path, Context.MODE_PRIVATE);
            fos.write(data);
            fos.close();
/*
            Picasso.with(this)
                .load("file:" + getFilesDir() + "/" + path)
                .rotate(-90)
                .resize(surfaceView.getMeasuredWidth(), surfaceView.getMeasuredHeight())
                .centerInside()
                .transform(new FaceCenterCrop(188, 188))
                .into(Utils.getPicassoTarget(path, this));
*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    private void setupCamera() {
        camera = DefaultEasyCamera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera.alignCameraAndDisplayOrientation(this.getWindowManager());
        camera.enableShutterSound(true);
        try {
            actions = camera.startPreview(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        if(camera != null) {
            camera.stopPreview();
            camera.close();
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        System.out.println("resume");
        if(camera != null) {
            try {
                camera.startPreview(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        //setupCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        System.out.println("pause");
        if (camera != null) {
            camera.stopPreview();
        }
        */
    }
}
