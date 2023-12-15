package com.example.ecabalen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class LandingActivity extends AppCompatActivity {


    CardView cardViewRegister;
    MaterialCardView materialCardViewLogin;

    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        cardViewRegister = findViewById(R.id.cardViewRegister);
        materialCardViewLogin = findViewById(R.id.materialCardViewLogin);

        SharedPreferences sh = getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        if(!Email.isEmpty())
        {
            Intent intent= new Intent(LandingActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        cardViewRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        materialCardViewLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}