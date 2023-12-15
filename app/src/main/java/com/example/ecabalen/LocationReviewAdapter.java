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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationReviewAdapter extends RecyclerView.Adapter<LocationReviewAdapter.ViewHolder>
{
    Context context;
    private List<LocationReviewHelper> mLocationReview;

    public LocationReviewAdapter(List<LocationReviewHelper> LocationReviews, Context context2)
    {
        mLocationReview = LocationReviews;
        context = context2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(LocationReviewAdapter.ViewHolder holder, int position)
    {
        LocationReviewHelper LocationReviewHelper = mLocationReview.get(position);

        byte[] decodedString = Base64.decode(LocationReviewHelper.getUserPicture(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imgViewPicture.setImageBitmap(decodedByte);

        byte[] decodedString2 = Base64.decode(LocationReviewHelper.getReviewPicture(), Base64.DEFAULT);
        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
        holder.imgViewReviewPicture.setImageBitmap(decodedByte2);

        holder.tvFullName.setText(LocationReviewHelper.getFullName());
        holder.tvDescription.setText(LocationReviewHelper.getDescription());
        holder.tvDatePosted.setText(LocationReviewHelper.getDatePosted());
        holder.ratingBar.setRating(Float.parseFloat(LocationReviewHelper.getRating()));
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
        return mLocationReview.size();
    }

    @Override
    public LocationReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.location_review_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgViewPicture;
        TextView tvFullName;
        RatingBar ratingBar;
        TextView tvDescription;
        ImageView imgViewReviewPicture;
        TextView tvDatePosted;

        public ViewHolder(View itemView) {
            super(itemView);

            imgViewPicture = itemView.findViewById(R.id.imgViewPicture);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgViewReviewPicture = itemView.findViewById(R.id.imgViewReviewPicture);
            tvDatePosted = itemView.findViewById(R.id.tvDatePosted);
        }
    }
}