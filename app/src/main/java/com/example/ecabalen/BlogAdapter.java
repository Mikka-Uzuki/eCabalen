package com.example.ecabalen;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>
{
    ImageView cardViewChangePicture;

    public ImageView getCardViewChangePicture() {
        return cardViewChangePicture;
    }

    BlogAdapter blogAdapter;

    public BlogAdapter getInstance() {
        return blogAdapter;
    }

    Context context;
    private List<BlogHelper> mBlog;

    public BlogAdapter(List<BlogHelper> Blogs, Context context2)
    {
        mBlog = Blogs;
        context = context2;
        blogAdapter = this;
    }

    String Email;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(BlogAdapter.ViewHolder holder, int position)
    {
        BlogHelper BlogHelper = mBlog.get(position);

        SharedPreferences sh = context.getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        holder.tvFullName.setText(BlogHelper.getFullName());
        holder.tvDatePosted.setText(BlogHelper.getDatePosted());
        holder.tvCaption.setText(BlogHelper.getCaption());

        byte[] decodedString = Base64.decode(BlogHelper.getPicture(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imgView.setImageBitmap(decodedByte);

        holder.imgViewEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Dialog dialog = new Dialog(context);

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
                EditText txtCaption;

                linearLayoutBack = dialog.findViewById(R.id.linearLayoutBack);
                cardViewChangePicture = dialog.findViewById(R.id.cardViewChangePicture);
                txtCaption = dialog.findViewById(R.id.txtCaption);
                cardViewAdd = dialog.findViewById(R.id.cardViewAdd);


                cardViewChangePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
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

                txtCaption.setText(BlogHelper.getCaption());

                byte[] decodedString = Base64.decode(BlogHelper.getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                cardViewChangePicture.setImageBitmap(decodedByte);

                cardViewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = URLDatabase.URL_BLOG_EDIT;

                        RequestQueue queue = Volley.newRequestQueue(context);

                        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                dialog.dismiss();

                                ((Activity)context).finish();
                                context.startActivity(((Activity) context).getIntent());
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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
                                params.put("blog_id", BlogHelper.getBlogID());
                                params.put("caption", txtCaption.getText().toString());
                                params.put("picture", encodeTobase64(((BitmapDrawable)cardViewChangePicture.getDrawable()).getBitmap()));
                                return params;
                            }
                        };
                        queue.add(request);
                    }
                });
                dialog.show();
            }
        });

        holder.imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.delete_confirmation_layout);
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

                        String url = URLDatabase.URL_BLOG_DELETE;

                        RequestQueue queue = Volley.newRequestQueue(context);

                        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                dialog.dismiss();

                                mBlog.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeRemoved(position, mBlog.size());
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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
                                params.put("blog_id", BlogHelper.getBlogID());
                                return params;
                            }
                        };
                        queue.add(request);
                    }
                });
                dialog.show();
            }
        });


        if(Email.equals(BlogHelper.getEmail()))
        {
            holder.imgViewDelete.setVisibility(View.VISIBLE);
            holder.imgViewEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imgViewDelete.setVisibility(View.GONE);
            holder.imgViewEdit.setVisibility(View.GONE);
        }
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        return imageEncoded;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mBlog.size();
    }

    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.blog_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgView, imgViewEdit, imgViewDelete;
        TextView tvFullName, tvDatePosted, tvCaption;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFullName = itemView.findViewById(R.id.tvFullName);
            imgView = itemView.findViewById(R.id.imgView);
            tvDatePosted = itemView.findViewById(R.id.tvDatePosted);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            imgViewEdit = itemView.findViewById(R.id.imgViewEdit);
            imgViewDelete = itemView.findViewById(R.id.imgViewDelete);
        }
    }
}
