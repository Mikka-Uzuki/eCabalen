package com.example.ecabalen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    CardView cardViewLogin;

    TextView tvRegister;

    LinearLayout linearLayoutBack;

    TextView tvShowHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        cardViewLogin = findViewById(R.id.cardViewLogin);
        tvRegister = findViewById(R.id.tvRegister);
        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        tvShowHide = findViewById(R.id.tvShowHide);

        tvShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvShowHide.getText().toString().equals("SHOW"))
                {
                    tvShowHide.setText("HIDE");
                    txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    tvShowHide.setText("SHOW");
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoginChecker();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoginChecker();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                LoginChecker();
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoginChecker();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoginChecker();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                LoginChecker();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cardViewLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = URLDatabase.URL_LOGIN;

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(!response.equals("[]"))
                            {
                                JSONObject jsonObject = new JSONObject(response);

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                                        //String floodJanMarch = jsonObjectData.getString("flood_jan_march");

                                        SharedPreferences sharedPreferences = getSharedPreferences("ECabalen",MODE_PRIVATE);

                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                                        myEdit.putString("email", txtEmail.getText().toString());

                                        myEdit.commit();

                                        Intent intent= new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }

                                    catch (Exception err)
                                    {
                                        Toast.makeText(LoginActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", txtEmail.getText().toString());
                        params.put("password", txtPassword.getText().toString());
                        return params;
                    }
                };
                queue.add(request);
            }
        });
    }

    void LoginChecker()
    {
        if(txtPassword.getText().toString().isEmpty() || txtEmail.getText().toString().isEmpty())
        {
            cardViewLogin.setAlpha(0.2f);
            cardViewLogin.setClickable(false);
            cardViewLogin.setFocusable(false);
        }
        else
        {
            cardViewLogin.setAlpha(1);
            cardViewLogin.setClickable(true);
            cardViewLogin.setFocusable(true);
        }
    }
}