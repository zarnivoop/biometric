package net.rincewind.biometric;

/**
 * Created by stefan on 2016-09-12.
 */

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        formUri = "http://rincewind.net:5984/acra-biometric/_design/acra-storage/_update/report",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUriBasicAuthLogin = "biometric",
        formUriBasicAuthPassword = "smurfsalot"
)
public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);


        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
