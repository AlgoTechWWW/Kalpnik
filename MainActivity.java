package detect.handdetect;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Method;

import static org.opencv.core.Core.findNonZero;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.resize;

//import android.graphics.Rect;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mRgba, hsv, imgOut, mRgba1;
    Mat capfrm;
    Mat new_object;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);

        javaCameraView.setVisibility(SurfaceView.VISIBLE);
//        setDisplayOrientation(javaCameraView , 90);

        javaCameraView.setCvCameraViewListener(this);

    }

    protected void setDisplayOrientation(JavaCameraView camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[]{angle});
        } catch (Exception e1) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "Opencv Loaded successfully");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "Opencv not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //mRgba1 = new Mat(width, height, CvType.CV_8UC3);
        mRgba = new Mat(320, 240, CvType.CV_8UC3);
        hsv = new Mat(width, height, CvType.CV_8UC3);
        imgOut = new Mat(width, height, CvType.CV_8UC1);

        capfrm = new Mat(width, height, CvType.CV_8UC3);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        String path = "android.resource://detect.handdetect/" + R.raw.hand1;
        Uri newuri = Uri.parse("path");

        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(0);
        boolean s = videoCapture.grab();
        videoCapture.read(capfrm);

        Drawable myDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.plate);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

        new_object = new Mat(myLogo.getWidth(), myLogo.getHeight(), CvType.CV_8UC3);

        Utils.bitmapToMat(myLogo, new_object);

        mRgba1 = inputFrame.rgba();
        //Size snew = new Size(320,  240);
        //Size val = mRgba.size();
        //resize(src, dst, dst.size(), 0, 0, interpolation);
        resize(mRgba1, mRgba, new Size(320, 240), 0, 0, INTER_NEAREST);
        //Size s1 = new Size(3,3);


        //Imgproc.blur(mRgba, mRgba, s1);
        Imgproc.cvtColor(mRgba, hsv, Imgproc.COLOR_RGB2HSV);
        Scalar R1 = new Scalar(0, 10, 60);
        Scalar R2 = new Scalar(20, 100, 255);

        Core.inRange(hsv, R1, R2, imgOut);

        Mat nonZeroCoordinates = new Mat(imgOut.rows(), imgOut.cols(), CvType.CV_8UC1);
        findNonZero(imgOut, nonZeroCoordinates);

        double meanx = 0, meany = 0;

        for (int idx = 0; idx < nonZeroCoordinates.rows(); idx++) {
            double[] pos_val = nonZeroCoordinates.get(idx, 0);
            meanx = meanx + pos_val[1];
            meany = meany + pos_val[0];
        }

        meanx /= nonZeroCoordinates.rows();
        meany /= nonZeroCoordinates.rows();

        Mat mRgbaT = new Mat(mRgba1.size(), CvType.CV_8UC3);
        //Core.flip(mRgba.t(), mRgbaT, 1);

        resize(mRgba1, mRgbaT, mRgba1.size());
        double scale = mRgba1.rows() / 240;

        meanx *= scale;
        meany *= scale;


        if (meanx > 0 && meanx + new_object.width() <= mRgbaT.width() && meany > 0 && meany + new_object.height() <= mRgbaT.height()) {
            //new_object.copyTo(mRgbaT.submat(new Rect((int)meanx, (int)meany, 50, 50)));
            new_object.copyTo(mRgbaT.submat(new Rect((int) meanx, (int) meany, new_object.width(), new_object.height())));
        }

        Mat mRgbaT1 = mRgbaT.t();
        Core.flip(mRgbaT.t(), mRgbaT1, 1);

        resize(mRgbaT1, mRgbaT1, mRgbaT.size());
        //resize(capfrm, capfrm, mRgbaT.size());
        return capfrm;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}