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
import org.osmdroid.views.MapView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>
{
    String Email;
    HomeActivity homeActivity;
    ImageView cardViewChangePicture;

    public ImageView getCardViewChangePicture() {
        return cardViewChangePicture;
    }

    Context context;
    private List<LocationHelper> mLocation;

    LocationAdapter locationAdapter;

    public LocationAdapter getInstance() {
        return locationAdapter;
    }

    public LocationAdapter(List<LocationHelper> Locations, Context context2)
    {
        mLocation = Locations;
        context = context2;
        locationAdapter = this;
    }

    String MyLocation;

    double lat1, lon1, lat2, lon2;

    private double distance(double lat1, double lon1, double lat2, double
            lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist); }
    public static final int PICK_IMAGE = 1;
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0); }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI); }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(LocationAdapter.ViewHolder holder, int position)
    {
        LocationHelper LocationHelper = mLocation.get(position);

        homeActivity = new HomeActivity().getInstance();

        holder.tvLocationName.setText(LocationHelper.getName());

        holder.ratingBar.setRating(Float.parseFloat(LocationHelper.getRating()));

        SharedPreferences sh = context.getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        MyLocation = sh.getString("my_location", "");
        Email = sh.getString("email", "");

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        List<Address> address2;
        Address location;
        Address location2;
        try {
            address = coder.getFromLocationName(MyLocation, 5);
            location = address.get(0);

            lat1 = location.getLatitude();
            lon1 = location.getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            address2 = coder.getFromLocationName(LocationHelper.getAddress(), 5);
            location2 = address2.get(0);

            lat2 = location2.getLatitude();
            lon2 = location2.getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        }

        double currentDistance = distance(lat1, lon1, lat2, lon2);

        holder.tvLocationDistance.setText(String.format("%.2f", currentDistance) + "km");

        List<LocationImageHelper> locationImageHelpers;
        LocationImageAdapter locationImageAdapter;

        locationImageHelpers = new ArrayList<>();

        holder.recyclerView.setHasFixedSize(true);

        locationImageAdapter = new LocationImageAdapter(locationImageHelpers, context);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(linearLayoutManager2);

        holder.recyclerView.setAdapter(locationImageAdapter);

        holder.tvType.setText(LocationHelper.getType());

        //GET LOCATION IMAGE
        {
            String url = URLDatabase.URL_LOCATION_IMAGE;

            RequestQueue queue = Volley.newRequestQueue(context);

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

                                    String locationImageID = jsonObjectData.getString("location_image_id");
                                    String locationID = jsonObjectData.getString("location_id");
                                    String picture = jsonObjectData.getString("picture");;
                                    String datePosted = jsonObjectData.getString("date_posted");

                                    locationImageHelpers.add(new LocationImageHelper(locationImageID,locationID,picture,datePosted));
                                }

                                catch (Exception err)
                                {
                                    Toast.makeText(context, err.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            holder.recyclerView.setAdapter(locationImageAdapter);
                        }

                    } catch (Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
                    params.put("location_id", LocationHelper.getLocationID());
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Dialog dialog = new Dialog(context);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.location_view_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;

                window.setAttributes(wlp);

                CardView cardViewWrite = dialog.findViewById(R.id.cardViewWrite);
                TextView tvPlaceName = dialog.findViewById(R.id.tvPlaceName);
                TextView tvLocationDistance = dialog.findViewById(R.id.tvLocationDistance);
                LinearLayout linearLayoutBack = dialog.findViewById(R.id.linearLayoutBack);
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                RecyclerView recyclerViewReview = dialog.findViewById(R.id.recyclerViewReview);
                CardView cardViewMap = dialog.findViewById(R.id.cardViewMap);

                RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                tvPlaceName.setText(LocationHelper.getName());

                ratingBar.setRating(Float.parseFloat(LocationHelper.getRating()));

                tvLocationDistance.setText(holder.tvLocationDistance.getText().toString());

                linearLayoutBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                List<LocationImageHelper> locationImageHelpers2;
                LocationImageAdapter locationImageAdapter2;

                locationImageHelpers2 = new ArrayList<>();

                recyclerView.setHasFixedSize(true);

                locationImageAdapter2 = new LocationImageAdapter(locationImageHelpers2, context);

                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager2);

                recyclerView.setAdapter(locationImageAdapter2);

                for(LocationImageHelper locationImageHelper : locationImageHelpers)
                {
                    locationImageHelpers2.add(new LocationImageHelper(locationImageHelper.getLocationImageID(),locationImageHelper.getLocationID(),locationImageHelper.getPicture(),locationImageHelper.getDatePosted()));
                }

                recyclerView.setAdapter(locationImageAdapter2);

                cardViewMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(context, MapViewActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("Name", LocationHelper.getName());
                        bundle.putString("Location", LocationHelper.getAddress());

                        intent.putExtras(bundle);

                        context.startActivity(intent);
                    }
                });


                //REVIEWS
                {
                    List<LocationReviewHelper> locationReviewHelpers;
                    LocationReviewAdapter locationReviewAdapter;

                    locationReviewHelpers = new ArrayList<>();

                    recyclerViewReview.setHasFixedSize(true);

                    locationReviewAdapter = new LocationReviewAdapter(locationReviewHelpers, context);

                    LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    recyclerViewReview.setLayoutManager(linearLayoutManager3);

                    //GET REVIEWS
                    {
                        {
                            String url = URLDatabase.URL_LOCATION_REVIEW;

                            RequestQueue queue = Volley.newRequestQueue(context);

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

                                                    String locationReviewID = jsonObjectData.getString("location_review_id");
                                                    String fullName = jsonObjectData.getString("full_name");
                                                    String userPicture = jsonObjectData.getString("user_picture");;
                                                    String reviewPicture = jsonObjectData.getString("review_picture");
                                                    String description = jsonObjectData.getString("description");
                                                    String rating = jsonObjectData.getString("rating");
                                                    String datePosted = jsonObjectData.getString("date_posted");

                                                    locationReviewHelpers.add(new LocationReviewHelper(locationReviewID,fullName,userPicture,reviewPicture,description,rating,datePosted));
                                                }

                                                catch (Exception err)
                                                {
                                                    Toast.makeText(context, err.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            recyclerViewReview.setAdapter(locationReviewAdapter);
                                        }

                                    } catch (Exception e) {

                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
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
                                    params.put("location_id", LocationHelper.getLocationID());
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

                    recyclerViewReview.setAdapter(locationReviewAdapter);
                }

                cardViewWrite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog2 = new Dialog(context);

                        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog2.setCancelable(true);
                        dialog2.setCanceledOnTouchOutside(true);
                        dialog2.setContentView(R.layout.location_review_add_layout);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog2.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        Window window = dialog2.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;

                        window.setAttributes(wlp);

                        RatingBar ratingBar = dialog2.findViewById(R.id.ratingBar);
                        EditText txtDescription = dialog2.findViewById(R.id.txtDescription);

                        cardViewChangePicture = dialog2.findViewById(R.id.cardViewChangePicture);
                        CardView cardViewPost = dialog2.findViewById(R.id.cardViewPost);

                        cardViewChangePicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                            }
                        });

                        txtDescription.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if(txtDescription.getText().toString().isEmpty())
                                {
                                    cardViewPost.setEnabled(false);
                                    cardViewPost.setFocusable(false);
                                    cardViewPost.setClickable(false);
                                    cardViewPost.setAlpha(0.2f);
                                }
                                else
                                {
                                    cardViewPost.setEnabled(true);
                                    cardViewPost.setFocusable(true);
                                    cardViewPost.setClickable(true);
                                    cardViewPost.setAlpha(1.0f);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        cardViewPost.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                String url = URLDatabase.URL_REVIEW_POST;

                                RequestQueue queue = Volley.newRequestQueue(context);

                                StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        dialog2.dismiss();
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
                                        params.put("email", Email);
                                        params.put("location_id", LocationHelper.getLocationID());
                                        params.put("picture",encodeTobase64(((BitmapDrawable)cardViewChangePicture.getDrawable()).getBitmap()));
                                        params.put("description", txtDescription.getText().toString());
                                        params.put("rating", String.valueOf(ratingBar.getRating()));
                                        return params;
                                    }
                                };
                                queue.add(request);
                            }
                        });
                        dialog2.show();
                    }
                });
                dialog.show();
            }
        });
    }

    public static String encodeTobase64(Bitmap image) {
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
        return mLocation.size();
    }

    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.location_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RatingBar ratingBar;
        TextView tvLocationName, tvLocationDistance, tvType;
        RecyclerView recyclerView;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            cardView = itemView.findViewById(R.id.cardView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationDistance = itemView.findViewById(R.id.tvLocationDistance);
            tvType = itemView.findViewById(R.id.tvType);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
