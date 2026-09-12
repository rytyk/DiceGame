package com.codecat.dicegame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Button btnShake;
    TextView tvDice1, tvDice2, tvSum, tvStatus, tvX, tvY, tvZ;

    private Random random = new Random();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime;

    private boolean isShaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDice1 = findViewById(R.id.tvDice1);
        tvDice2 = findViewById(R.id.tvDice2);
        tvSum = findViewById(R.id.tvSum);
        tvStatus = findViewById(R.id.tvStatus);

        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);

        btnShake = findViewById(R.id.btnShake);
        btnShake.setOnClickListener(v -> rollDice());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null){
            sensorManager.registerListener( this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    private void rollDice() {
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        int sum = dice1 + dice2;

        tvDice1.setText("Кубик 1 - " + dice1);
        tvDice2.setText("Кубик 2 - " + dice2);
        tvSum.setText("Сума - " + sum);
        tvStatus.setText("Кидок завершено");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
            return;
        }

        double X = event.values[0];
        double Y = event.values[1];
        double Z = event.values[2];

        double acceleration = Math.sqrt(X*X + Y*Y + Z*Z);

        if (acceleration > 15){
            lastShakeTime = System.currentTimeMillis();

            if (!isShaking){
                isShaking = true;
                tvStatus.setText("ТРЯСІТЬ");

                tvDice1.setText("?");
                tvDice2.setText("?");
                tvSum.setText("?");
            }
        }
        if (isShaking){
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 500){
                isShaking = false;
                rollDice();
            }
        }
    }
}