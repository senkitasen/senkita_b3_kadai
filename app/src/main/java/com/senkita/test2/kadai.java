package com.senkita.test2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class kadai extends Activity implements /*Runnable,*/SensorEventListener {
//        private String fileName0 = "testfileA.txt";
//        private String fileName1 = "testfileB.txt";
//        private String fileName2 = "testfileC.txt";
//        private String arff = "/storage/sdcard0/Download/kyoushi.arff";
        SensorManager sm;
        TextView tv;
//        Handler h;
        TextView kyo;
        float gx, gy, gz;
        J48 j48 = null;
        Instances trainInstances = null;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                LinearLayout ll = new LinearLayout(this);
                setContentView(ll);

                tv = new TextView(this);
                ll.addView(tv);

                kyo = new TextView(this);
                ll.addView(kyo);

//                h = new Handler();
//                h.postDelayed(this, 500);



                try {
                        ArffLoader al = new ArffLoader();
                        al.setFile(new File("/storage/sdcard0/Download/kyoushi.arff"));
//                        al.setFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/kyoushi.arff"));
//                        al.setFile(new File("/sdcard/Download/kyoushi.arff"));
                        trainInstances = new Instances(al.getDataSet());
                        trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                        j48 = new J48();
                        j48.buildClassifier(trainInstances);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

//        @Override
//        public void run() {
//                tv.setText("X-axis : " + gx + "\n"
//                        + "Y-axis : " + gy + "\n"
//                        + "Z-axis : " + gz + "\n");
//                h.postDelayed(this, 500);
//        }

        @Override
        protected void onResume() {
                super.onResume();
                sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                List<Sensor> sensors =
                        sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
                if (0 < sensors.size()) {
                        sm.registerListener(this, sensors.get(0),
                                SensorManager.SENSOR_DELAY_NORMAL);
                }
        }

        @Override
        protected void onPause() {
                super.onPause();
                sm.unregisterListener(this);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
//                h.removeCallbacks(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
                gx = event.values[0];
                gy = event.values[1];
                gz = event.values[2];

//                saveFile(fileName0, String.valueOf(gx));
//                saveFile(fileName0, ",");
//                saveFile(fileName0, String.valueOf(gy));
//                saveFile(fileName0, ",");
//                saveFile(fileName0, String.valueOf(gz));
//                saveFile(fileName0, ",");

                Attribute x_axis = new Attribute("x", 0);
                Attribute y_axis = new Attribute("y", 1);
                Attribute z_axis = new Attribute("z", 2);

                Instance instance = new DenseInstance(3);
                instance.setValue(x_axis, gx);
                instance.setValue(y_axis, gy);
                instance.setValue(z_axis, gz);
//
//                instance.setValue(x_axis, 0.42135254);
//                instance.setValue(y_axis, 0.34474298);
                instance.setValue(z_axis, 9.490008);

                instance.setDataset(trainInstances);

//                kyo.setText(String.valueOf("hello"));
//                Toast.makeText(this,"hello", Toast.LENGTH_SHORT);
                try {
//                        kyo.setText("hello");

                        double result = j48.classifyInstance(instance);
//                        Log.d("test", String.valueOf(result));
//                        System.out.println(result);
//                        kyo.setText(String.valueOf(result));


                        if(result == 0.0){
                                kyo.setText("Current state : stop" + "\n");
//                                h.postDelayed(this, 500);
                        }
                        else if(result == 1.0){
                                kyo.setText("Current state : walk" + "\n");
//                                h.postDelayed(this, 500);
                        }
                        else if(result == 2.0){
                                kyo.setText("Current state : run" + "\n");
//                                h.postDelayed(this, 500);
                        }
                        else {
                                kyo.setText("Unexpected state" + "\n");
//                                h.postDelayed(this, 500);
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }



        }

        public void saveFile(String file, String str) {
                FileOutputStream fileOutputstream = null;

                try {
                        fileOutputstream = openFileOutput(file, Context.MODE_APPEND);
                        fileOutputstream.write(str.getBytes());
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
}