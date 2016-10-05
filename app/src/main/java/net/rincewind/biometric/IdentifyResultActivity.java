package net.rincewind.biometric;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class IdentifyResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_result);

//        PicassoFaceDetector.initialize(this);

        TextView txtResult = (TextView) findViewById(R.id.txtResult);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnBack = (Button)findViewById(R.id.btnBack);

        Bundle extras = getIntent().getExtras();
        boolean succeed = false;
        if (extras != null) {
            succeed = extras.getBoolean("succeed");
        }

        if(succeed) {
            if(imageView == null)
                System.out.println("EMPTY!");
            File file = new File(getFilesDir() + "/" + "0-photo.png");
            Picasso.with(this).invalidate(file);
            Picasso.with(this)
                    .load(file)
//                    .rotate(-90)
                    .fit()
                    .centerInside()
                    .into(imageView);
            SharedPreferences settings = getSharedPreferences("Biometric", 0);
            txtResult.setText(settings.getString("name", "???"));
            txtResult.setTextColor(Color.BLACK);

            btnBack.setVisibility(View.GONE);

        } else {

            imageView.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));

            txtResult.setText("Ej igenkänd");
            txtResult.setTextColor(Color.RED);

            btnBack.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(view.getContext(), IdentifyActivity.class));
                        }
                    }
            );

        }

        Button btnContinue = (Button)findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), MainActivity.class));
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        PicassoFaceDetector.releaseDetector();
    }

}
