package com.example.ecabalen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    LinearLayout linearLayoutBack;
    String Email;

    ImageView imgViewPicture;
    TextView tvFullName;
    CardView cardViewChangePicture;

    LinearLayout linearLayoutMyAccount, linearLayoutLogout, linearLayoutAbout;
    LinearLayout linearLayoutFavorite, linearLayoutPhoto;

    public static final int PICK_IMAGE = 1;

    boolean isChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        SharedPreferences sh = getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        imgViewPicture = findViewById(R.id.imgViewPicture);
        tvFullName = findViewById(R.id.tvFullName);
        cardViewChangePicture = findViewById(R.id.cardViewChangePicture);
        linearLayoutMyAccount = findViewById(R.id.linearLayoutMyAccount);
        linearLayoutLogout = findViewById(R.id.linearLayoutLogout);
        linearLayoutAbout = findViewById(R.id.linearLayoutAbout);
        linearLayoutFavorite = findViewById(R.id.linearLayoutFavorite);
        linearLayoutPhoto = findViewById(R.id.linearLayoutPhoto);

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChanged)
                {
                    finish();
                }
                else
                {
                    Toast.makeText(SettingActivity.this, "Your Profile Picture is uploading", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardViewChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChanged = false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        linearLayoutMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, AccountSettingActivity.class);
                Bundle bundle = new Bundle();

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        linearLayoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, PhotoActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.logout_confirmation_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;

                window.setAttributes(wlp);

                CardView cardViewNo, cardViewYes;

                cardViewNo = dialog.findViewById(R.id.cardViewNo);
                cardViewYes = dialog.findViewById(R.id.cardViewYes);

                cardViewNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                cardViewYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        SharedPreferences settings = getSharedPreferences("ECabalen", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();

                        Intent intent= new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
                dialog.show();
            }
        });

        LoadAccount();
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        return imageEncoded;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == PICK_IMAGE)
            {
                InputStream imageStream = null;
                try {
                    imageStream = this.getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                imgViewPicture.setImageBitmap(yourSelectedImage);

                String url = URLDatabase.URL_ACCOUNT_PICTURE_UPDATE;

                RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        isChanged = true;
                        Toast.makeText(SettingActivity.this, "Your Profile Picture is saved", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(SettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                        params.put("email", Email);
                        params.put("picture", encodeTobase64(yourSelectedImage));
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                );
                queue.add(request);
            }
        }
        else
        {
            isChanged = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(isChanged)
        {
            super.onBackPressed();
        }
        else
        {
            Toast.makeText(SettingActivity.this, "Your Profile Picture is uploading", Toast.LENGTH_SHORT).show();
        }
    }

    void LoadAccount()
    {
        String url = URLDatabase.URL_HOME;

        RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);

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
                            JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                            String fullName = jsonObjectData.getString("full_name");
                            String picture = jsonObjectData.getString("picture");

                            if(!picture.equals("null"))
                            {
                                byte[] decodedString = Base64.decode(picture, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgViewPicture.setImageBitmap(decodedByte);
                            }

                            tvFullName.setText(fullName);
                        }
                    }

                } catch (Exception e) {

                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(SettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("email", Email);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        queue.add(request);
    }
}