package com.android.sentinel.HealthCheck.Buttons;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Display.MulitouchEntry;
import com.android.sentinel.HealthCheck.Display.TestMultitouch;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;
import com.android.sentinel.TestBack;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.HOME;
import static com.android.sentinel.HealthCheck.TestFragment.POWER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.VOLUME;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestVolume extends AppCompatActivity {

    Runnable timerTask;
    Handler handler;
    Thread thread;
    ProgressBar progressBar;
    TextView volumeUp, volumeDown, result, skip, explanation, btn;
    boolean isVolUp, isVolDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_volume);
        progressBar = findViewById(R.id.progress);
        volumeDown = findViewById(R.id.volumeDown);
        volumeUp = findViewById(R.id.volumeUp);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        explanation = findViewById(R.id.explain);
        btn = findViewById(R.id.button);
        isVolUp = false;
        isVolDown = false;
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(VOLUME, UNCHECKED, TestVolume.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(HOME)) {
                        Intent intent = new Intent(TestVolume.this,
                                TestBack.class);
                        intent.putExtra(FROM, VOLUME);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(VOLUME, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setDefaults(VOLUME, UNCHECKED, this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        timer(10000);
        runProgress();

    }

    private void runProgress() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 10; i++) {
                    if (i > 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setMax(10);
                    progressBar.setProgress(i);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                volumeUp.setVisibility(View.VISIBLE);
                isVolUp = true;
                setPass();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                volumeDown.setVisibility(View.VISIBLE);
                isVolDown = true;
                setPass();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setPass() {
        if (isVolUp && isVolDown) {
            setDefaults(VOLUME, SUCCESS, TestVolume.this);
            result.setText("PASS");
            result.setTextColor(getResources().getColor(R.color.green));
            btn.setText("Continue");
            btn.setVisibility(View.VISIBLE);
            skip.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            explanation.setVisibility(View.GONE);
            handler.removeCallbacks(timerTask);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getIntent().getExtras() != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(HOME)) {
                            Intent intent = new Intent(TestVolume.this,
                                    TestBack.class);
                            intent.putExtra(FROM, VOLUME);
                            startActivity(intent);
                        }
                    }else {
                        finish();
                    }
                }
            });


        }
    }

    private void timer(long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setFail();
            }
        };
        handler.postDelayed(timerTask, delayMillis);
    }

    private void setFail() {
        setDefaults(VOLUME, FAILED, TestVolume.this);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        btn.setText("Continue");
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(HOME)) {
                        Intent intent = new Intent(TestVolume.this,
                                TestBack.class);
                        intent.putExtra(FROM, VOLUME);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });


    }

}
