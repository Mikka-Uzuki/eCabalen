package com.example.ecabalen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogActivity extends AppCompatActivity {


    List<BlogHelper> blogHelpers;
    BlogAdapter blogAdapter;
    RecyclerView recyclerView;

    LinearLayout linearLayoutBack;

    String Email;
    CardView cardViewAdd;

    public static final int PICK_IMAGE = 1;

    boolean isChanged = true;
    ImageView cardViewChangePicture;
    EditText txtCaption;
    String Picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);

        SharedPreferences sh = getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        blogHelpers = new ArrayList<>();

        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        recyclerView = findViewById(R.id.recyclerView);
        cardViewAdd = findViewById(R.id.cardViewAdd);

        recyclerView.setHasFixedSize(true);

        blogAdapter = new BlogAdapter(blogHelpers, this);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager2);

        recyclerView.setAdapter(blogAdapter);

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cardViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(BlogActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.blog_add_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;

                window.setAttributes(wlp);

                LinearLayout linearLayoutBack;
                CardView cardViewAdd;

                linearLayoutBack = dialog.findViewById(R.id.linearLayoutBack);
                cardViewChangePicture = dialog.findViewById(R.id.cardViewChangePicture);
                txtCaption = dialog.findViewById(R.id.txtCaption);
                cardViewAdd = dialog.findViewById(R.id.cardViewAdd);

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

                linearLayoutBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                txtCaption.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(txtCaption.getText().toString().isEmpty()
                                || Picture.isEmpty()
                        )
                        {
                            cardViewAdd.setEnabled(false);
                            cardViewAdd.setClickable(false);
                            cardViewAdd.setFocusable(false);
                            cardViewAdd.setAlpha(0.2f);
                        }
                        else
                        {
                            cardViewAdd.setEnabled(true);
                            cardViewAdd.setClickable(true);
                            cardViewAdd.setFocusable(true);
                            cardViewAdd.setAlpha(1.0f);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                cardViewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = URLDatabase.URL_BLOG_ADD;

                        RequestQueue queue = Volley.newRequestQueue(BlogActivity.this);

                        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                dialog.dismiss();

                                finish();
                                startActivity(getIntent());
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(BlogActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                                params.put("caption", txtCaption.getText().toString());
                                params.put("picture", Picture);
                                return params;
                            }
                        };
                        queue.add(request);
                    }
                });
                dialog.show();
            }
        });

        LoadBlog();
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
                cardViewChangePicture.setImageBitmap(yourSelectedImage);

                Picture = encodeTobase64(yourSelectedImage);

                if(txtCaption.getText().toString().isEmpty()
                        || Picture.isEmpty()
                )
                {
                    cardViewAdd.setEnabled(false);
                    cardViewAdd.setClickable(false);
                    cardViewAdd.setFocusable(false);
                    cardViewAdd.setAlpha(0.2f);
                }
                else
                {
                    cardViewAdd.setEnabled(true);
                    cardViewAdd.setClickable(true);
                    cardViewAdd.setFocusable(true);
                    cardViewAdd.setAlpha(1.0f);
                }
            }

            if (requestCode == 3)
            {
                InputStream imageStream = null;
                try {
                    imageStream = this.getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                blogAdapter.getInstance().getCardViewChangePicture().setImageBitmap(yourSelectedImage);
            }
        }
        else
        {
            isChanged = true;
        }
    }

    void LoadBlog()
    {
        blogHelpers.clear();
        recyclerView.setAdapter(blogAdapter);
        String url = URLDatabase.URL_BLOG;

        RequestQueue queue = Volley.newRequestQueue(BlogActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(!response.equals("[]"))
                    {
                        blogHelpers.clear();

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                                String blogID = jsonObjectData.getString("blog_id");
                                String caption = jsonObjectData.getString("caption");
                                String fullName = jsonObjectData.getString("full_name");
                                String email = jsonObjectData.getString("email");
                                String picture = jsonObjectData.getString("picture");
                                String datePosted = jsonObjectData.getString("date_posted");

                                blogHelpers.add(new BlogHelper(blogID,email, fullName,caption,picture,datePosted));
                            }

                            catch (Exception err)
                            {
                                Toast.makeText(BlogActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        recyclerView.setAdapter(blogAdapter);
                    }

                } catch (Exception e) {

                    Toast.makeText(BlogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(BlogActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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