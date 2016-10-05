package net.rincewind.biometric;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ConfirmRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_registration);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        File file = new File(getFilesDir() + "/" + "0-photo.png");
        Picasso.with(this).invalidate(file);
        Picasso.with(this)
            .load(file)
//            .rotate(-90)
            .fit()
            .centerInside()
//            .transform(new FaceCenterCrop(188, 188))
            .into(imageView);

        /*
        File imgFile = new File(getFilesDir() + "/" + "photo0.jpg");
        if(imgFile.exists())
        {
            System.out.println("GOT FILE!");
            imageView.setImageURI(Uri.fromFile(imgFile));
        } else
            System.out.println("NO FILE!");
        */

        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), RegisterActivity.class));
                    }
                }
        );

        final TextView txtName = (TextView) findViewById(R.id.txtName);

        /*
        txtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return false;
                }
                return false;
            }
        });
        */

        Button btnContinue = (Button)findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                SharedPreferences settings = getSharedPreferences("Biometric", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("name", txtName.getText().toString());
                editor.commit();

                startActivity(new Intent(view.getContext(), MainActivity.class));
                Toast toast = Toast.makeText(view.getContext(), "Registrering slutförd!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PicassoFaceDetector.releaseDetector();
    }
}
