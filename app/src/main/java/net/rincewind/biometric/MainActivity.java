package net.rincewind.biometric;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PicassoFaceDetector.initialize(this);

        Button concept1 = (Button)findViewById(R.id.concept1);
        Button concept2 = (Button)findViewById(R.id.concept2);
        Button concept3 = (Button)findViewById(R.id.concept3);
        Button btnRegister = (Button)findViewById(R.id.btnRegister);

        concept1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("MEOW!");
                        State.getInstance().mockup = 1;
                        startActivity(new Intent(view.getContext(), IdentifyActivity.class));
                    }
                }
        );

        concept2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        State.getInstance().mockup = 2;
                        startActivity(new Intent(view.getContext(), IdentifyActivity.class));
                    }
                }
        );

        concept3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        State.getInstance().mockup = 3;
                        startActivity(new Intent(view.getContext(), IdentifyActivity.class));
                    }
                }
        );

        btnRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), RegisterActivity.class));
                    }
                }
        );

        // Request permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // User granted camera access
        } else {
            // User denied camera access
        }
    }
}
