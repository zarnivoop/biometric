package net.rincewind.biometric;

import android.content.Context;
import android.graphics.Bitmap;
import static android.graphics.Bitmap.*;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_face;

import static org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

public class Utils {

    //target to save
    public static Target getPicassoTarget(final String url, final Context context){
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            FileOutputStream fos = context.openFileOutput(url, Context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public static Bitmap rawBytesToBitmap(byte[] data, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(data));
        return bm;
    }

    public static Bitmap cropFace(final byte[] data, final Context context) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(data);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        FaceDetector detector = new FaceDetector.Builder( context )
                .setTrackingEnabled(false)
                .setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if (!detector.isOperational()) {
            //Handle contingency
            System.out.println("detector not operational");
            return null;
        } else {
            Frame frame = new Frame.Builder().setBitmap(rotated).build();
            SparseArray<Face> faces = detector.detect(frame);
            detector.release();

            System.out.println("faces: " + faces.size());
            if(faces.size() != 1)
                return null;
            Face face = faces.valueAt(0);
            if(face.getPosition() == null || face.getPosition().x < 0 || face.getPosition().y < 0)
                return null;
            System.out.println((int) face.getPosition().x + "," + (int) face.getPosition().y + "     " + (int) face.getWidth() + "," + (int) face.getHeight());
            Bitmap cropped = Bitmap.createBitmap(rotated, (int) face.getPosition().x, (int) face.getPosition().y, (int) face.getWidth(), (int) face.getHeight());
            return cropped;
        }
    }

    public static void saveImage(Bitmap bitmap, String filename, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int predict(File root, File testFile) {
//        String trainingDir = dir.getAbsolutePath();
        Mat testImage = imread(testFile.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

//        File root = new File(trainingDir);

        System.out.println("MEOW!");
        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                boolean match = name.contains("-") && name.contains("photo") && (name.endsWith(".png"));
                System.out.println("file " + name + " - " + match);
                return match;
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        opencv_core.MatVector images = new opencv_core.MatVector(imageFiles.length);

        opencv_core.Mat labels = new opencv_core.Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            System.out.println(image.getAbsolutePath());
            opencv_core.Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }

        //opencv_face.FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
        //FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

        faceRecognizer.train(images, labels);

        int predictedLabel = faceRecognizer.predict(testImage);

        System.out.println("Predicted label: " + predictedLabel);

        return predictedLabel;
    }

}