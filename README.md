# Kalpnik
# Procedeure to create a project in android with opencv Support
1. Extract the downloaded zip file.( https://sourceforge.net/projects/opencvlibrary/files/opencv-android/3.1.0/OpenCV-3.1.0-android-sdk.zip/download)
2. Open Android Studio and create a new project with package of your choice. Here I have created a new project with com.learn2crack.opencvdemo
3. Then select File ->New -> Import Module
4. You need to select the OpenCV SDK location. Select OpenCV-android-sdk/sdk/java. Then select Next and Finish. OpenCV sdk is imported as a module.
5. In the project explorer change the project view from Android to Project. Open Project -> openCVLibrary -> build.gradle
6. Change the compileSdkVersion, targetSdkVersion and buildToolsVersion value to the latest version you use. Here use 23, 23 and 23.0.2. Then sync the project. The errors will be gone.
7. Switch back to Android view in Project explorer. Right click on the app module and select Open Module Settings.
8. For the app module in the Dependencies tab, select Add -> Module Dependency -> openCVLibrary
Add native llib
1. Now add native JNI libraries in our project. These libraries should be added in jniLibs directory. Create a new jniLibs directory in app-> src -> main.
2. Open the extracted OpenCV SDK directory. Switch to OpenCV-android-sdk/sdk/native/libs directory.
3. You will find directories for many CPU architectures. Copy the required architecture directory to the jniLibs directory. Here I copied x86_64 and armeabi-v7a because my Android emulator has x86_64 architecture and my OnePlus One has armeabi-v7a architecture. Delete all files except libopencv_java3.so.
4. Open your gradle.properties file and enter the following code.
android.useDeprecatedNdk=true
Now we have setup SDK in our Android Studio project. Lets test whether OpenCV is loaded.

package com.learn2crack.opencvdemo;
 
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
 
import org.opencv.android.OpenCVLoader;
 
public class MainActivity extends AppCompatActivity {
 
    private static final String TAG = "MainActivity";
 
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
    }
}
