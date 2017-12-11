package com.example.r30_a.testlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.r30_a.testlayout.explosion.explosionwidget.ExplosionField;

public class explosionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explosion);

        ExplosionField explosionField = new ExplosionField(this);

        explosionField.addListener(findViewById(R.id.root));

    }
}
