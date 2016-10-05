package net.rincewind.biometric;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IdentifyActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private EasyCamera camera;
    private EasyCamera.CameraActions actions;

    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    }
                }
        );

        Button btnCapture = (Button)findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        final Button btnSucceed = (Button)findViewById(R.id.btnSucceed);


                        EasyCamera.PictureCallback callback = new EasyCamera.PictureCallback() {
                            public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
                                System.out.println("Meow2!");
                                String filename = "identifyphoto.png";
                                Bitmap cropped = Utils.cropFace(data, view.getContext());
                                Utils.saveImage(cropped, filename, view.getContext());

                                File file = new File(getFilesDir() + "/" + filename);

                                int prediction = Utils.predict(getFilesDir(), file);

                                Intent result = new Intent(IdentifyActivity.this, IdentifyResultActivity.class);
                                result.putExtra("succeed", prediction > 0);
                                startActivity(result);
                            }
                        };

                        actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
                        System.out.println("Meow!");
                    }
                }
        );

        System.out.println("MOCKUP: " + State.getInstance().mockup);
        switch(State.getInstance().mockup) {
            case 1:
                break;
            case 2:
                TextView prompt = (TextView) findViewById(R.id.prompt);
                prompt.setVisibility(View.VISIBLE);
                break;
            case 3:
                String phrase = "nyponsoppa är nyttigt";
                TextView txtPrompt = (TextView) findViewById(R.id.prompt);
                txtPrompt.setText("Säg frasen \"" + phrase + "\", och tryck sedan på IDENTIFIERA.");
                txtPrompt.setVisibility(View.VISIBLE);
                break;
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
        setupCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
}
