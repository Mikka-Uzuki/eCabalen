package com.example.ecabalen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView tvFullName;
    HomeActivity homeActivity;

    public HomeActivity getInstance() {
        return homeActivity;
    }

    List<LocationHelper> locationHelpers;
    LocationAdapter locationAdapter;

    RecyclerView recyclerView;

    List<LocationHelper> locationHelpers2;
    LocationAdapter locationAdapter2;

    RecyclerView recyclerViewRestaurant;

    private FusedLocationProviderClient fusedLocationProviderClient;
    LinearLayout linearLayoutSetting, linearLayoutExplore, linearLayoutVoiceTranslator, linearLayoutBlog;
    String Email;

    TextView tvAdd;

    ImageView cardViewChangePicture;
    public static final int PICK_IMAGE = 1;

    String Picture;
    CardView cardViewAdd;
    EditText txtLocationName, txtLocationAddress;

    String[] categories = { "Spots", "Restaurant" };
    String Category;

    String LocationID;
    TextView tvSearch;

    ArrayList<String> arrayList;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        tvSearch = findViewById(R.id.tvSearch);

        arrayList=new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");


        SharedPreferences sh = getSharedPreferences("ECabalen", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        try {
            MapView map = (MapView) findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);

            {
                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocationName("Porac Municipal Hall", 1);
                    Address obj = addresses.get(0);

                    GeoPoint startPoint = new GeoPoint(obj.getLatitude(), obj.getLongitude());

                    IMapController mapController = map.getController();
                    mapController.setZoom(14);
                    mapController.setCenter(startPoint);


                    Marker startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                    //startMarker.setIcon(getResources().getDrawable(R.drawable.location_red_icon));
                    //startMarker.setTitle("Tylo's Parking Lot");
                    map.getOverlays().add(startMarker);


                    map.invalidate();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception err)
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        }

        homeActivity = this;

        locationHelpers = new ArrayList<>();

        tvAdd = findViewById(R.id.tvAdd);
        recyclerView = findViewById(R.id.recyclerView);
        tvFullName = findViewById(R.id.tvFullName);

        locationHelpers2 = new ArrayList<>();

        recyclerViewRestaurant = findViewById(R.id.recyclerViewRestaurant);
        linearLayoutBlog = findViewById(R.id.linearLayoutBlog);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager lm = (LocationManager)HomeActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(HomeActivity.this)
                    .setMessage("Enable your location service")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
        }

        linearLayoutSetting = findViewById(R.id.linearLayoutSetting);
        linearLayoutExplore = findViewById(R.id.linearLayoutExplore);
        linearLayoutVoiceTranslator = findViewById(R.id.linearLayoutVoiceTranslator);

        linearLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, ExploreActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutVoiceTranslator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, VoiceTranslatorActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HomeActivity.this, BlogActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setHasFixedSize(true);

        locationAdapter = new LocationAdapter(locationHelpers, this);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(locationAdapter);

        //RESTAURANT

        recyclerViewRestaurant.setHasFixedSize(true);

        locationAdapter2 = new LocationAdapter(locationHelpers2, this);

        GridLayoutManager gridLayoutManager2=new GridLayoutManager(this,2);
        recyclerViewRestaurant.setLayoutManager(gridLayoutManager2);

        recyclerViewRestaurant.setAdapter(locationAdapter2);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.location_add_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;

                window.setAttributes(wlp);

                LinearLayout linearLayoutBack;
                Spinner spinner;

                linearLayoutBack = dialog.findViewById(R.id.linearLayoutBack);
                cardViewChangePicture = dialog.findViewById(R.id.cardViewChangePicture);
                txtLocationAddress = dialog.findViewById(R.id.txtLocationAddress);
                txtLocationName = dialog.findViewById(R.id.txtLocationName);
                cardViewAdd = dialog.findViewById(R.id.cardViewAdd);
                spinner = dialog.findViewById(R.id.spinner);

                LocationID = getRandomNumberString();

                linearLayoutBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                spinner.setOnItemSelectedListener(HomeActivity.this);

                ArrayAdapter ad = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item, categories);

                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(ad);

                cardViewChangePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

                txtLocationName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(txtLocationName.getText().toString().isEmpty()
                                || txtLocationAddress.getText().toString().isEmpty()
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
                txtLocationAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(txtLocationName.getText().toString().isEmpty()
                                || txtLocationAddress.getText().toString().isEmpty()
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

                        String url = URLDatabase.URL_LOCATION_ADD;

                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

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
                                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                                if(Category.equals("Spots"))
                                {
                                    params.put("location_category_id", "1");
                                }

                                if(Category.equals("Restaurant"))
                                {
                                    params.put("location_category_id", "2");
                                }

                                params.put("location_id", LocationID);
                                params.put("name", txtLocationName.getText().toString());
                                params.put("address", txtLocationAddress.getText().toString());
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

        LoadAccount();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
    {
        Category = categories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        return imageEncoded;
    }

    void LoadAccount()
    {

        String url = URLDatabase.URL_HOME;

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

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
                            //String picture = jsonObjectData.getString("picture");

                            /*if(!picture.equals("null"))
                            {
                                byte[] decodedString = Base64.decode(picture, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgViewPicture.setImageBitmap(decodedByte);
                            }*/

                            tvFullName.setText("Hey " + fullName + "!");
                        }
                    }

                } catch (Exception e) {

                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
    void LoadLocationsSpots()
    {
        locationHelpers.clear();
        recyclerView.setAdapter(locationAdapter);
        String url = URLDatabase.URL_LOCATION_SPOTS;

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(!response.equals("[]"))
                    {
                        locationHelpers.clear();

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                                String locationID = jsonObjectData.getString("location_id");
                                String locationCategoryID = jsonObjectData.getString("location_category_id");
                                String name = jsonObjectData.getString("name");
                                String address = jsonObjectData.getString("address");
                                String datePosted = jsonObjectData.getString("date_posted");
                                String rating = jsonObjectData.getString("rating");

                                arrayList.add(name);

                                locationHelpers.add(new LocationHelper("SPOTS",locationID,locationCategoryID,name,address,datePosted, rating));
                            }

                            catch (Exception err)
                            {
                                Toast.makeText(HomeActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        recyclerView.setAdapter(locationAdapter);

                        LoadLocationsRestaurants();
                    }

                } catch (Exception e) {

                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
    void LoadLocationsRestaurants()
    {
        locationHelpers2.clear();
        recyclerViewRestaurant.setAdapter(locationAdapter2);
        String url = URLDatabase.URL_LOCATION_RESTAURANT;

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(!response.equals("[]"))
                    {
                        locationHelpers2.clear();

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                                String locationID = jsonObjectData.getString("location_id");
                                String locationCategoryID = jsonObjectData.getString("location_category_id");
                                String name = jsonObjectData.getString("name");
                                String address = jsonObjectData.getString("address");
                                String datePosted = jsonObjectData.getString("date_posted");
                                String rating = jsonObjectData.getString("rating");

                                arrayList.add(name);

                                locationHelpers2.add(new LocationHelper("RESTAURANT",locationID,locationCategoryID,name,address,datePosted, rating));
                            }

                            catch (Exception err)
                            {
                                Toast.makeText(HomeActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        recyclerViewRestaurant.setAdapter(locationAdapter2);

                        tvSearch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Initialize dialog
                                dialog=new Dialog(HomeActivity.this);

                                // set custom dialog
                                dialog.setContentView(R.layout.dialog_searchable_spinner);

                                // set custom height and width
                                dialog.getWindow().setLayout(800,1024);

                                // set transparent background
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                // show dialog
                                dialog.show();

                                // Initialize and assign variable
                                EditText editText=dialog.findViewById(R.id.edit_text);
                                ListView listView=dialog.findViewById(R.id.list_view);

                                // Initialize array adapter
                                ArrayAdapter<String> adapter=new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1,arrayList);

                                // set adapter
                                listView.setAdapter(adapter);
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        adapter.getFilter().filter(s);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // when item selected from list
                                        // set selected item on textView

                                        for(LocationHelper locationHelper : locationHelpers)
                                        {
                                            if(adapter.getItem(position).equals(locationHelper.getName()))
                                            {
                                                try {
                                                    MapView map = (MapView) findViewById(R.id.map);
                                                    map.setTileSource(TileSourceFactory.MAPNIK);
                                                    map.setMultiTouchControls(true);

                                                    {
                                                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());

                                                        try {
                                                            List<Address> addresses = geocoder.getFromLocationName(locationHelper.getAddress(), 1);
                                                            Address obj = addresses.get(0);

                                                            GeoPoint startPoint = new GeoPoint(obj.getLatitude(), obj.getLongitude());

                                                            IMapController mapController = map.getController();
                                                            mapController.setZoom(16);
                                                            mapController.setCenter(startPoint);


                                                            Marker startMarker = new Marker(map);
                                                            startMarker.setPosition(startPoint);
                                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                                                            startMarker.setIcon(getResources().getDrawable(R.drawable.location_red_icon));
                                                            startMarker.setTitle(adapter.getItem(position));
                                                            map.getOverlays().add(startMarker);


                                                            map.invalidate();
                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                catch (Exception err)
                                                {
                                                    Toast.makeText(HomeActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        for(LocationHelper locationHelper : locationHelpers2)
                                        {
                                            if(adapter.getItem(position).equals(locationHelper.getName()))
                                            {
                                                try {
                                                    MapView map = (MapView) findViewById(R.id.map);
                                                    map.setTileSource(TileSourceFactory.MAPNIK);
                                                    map.setMultiTouchControls(true);

                                                    {
                                                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());

                                                        try {
                                                            List<Address> addresses = geocoder.getFromLocationName(locationHelper.getAddress(), 1);
                                                            Address obj = addresses.get(0);

                                                            GeoPoint startPoint = new GeoPoint(obj.getLatitude(), obj.getLongitude());

                                                            IMapController mapController = map.getController();
                                                            mapController.setZoom(14);
                                                            mapController.setCenter(startPoint);


                                                            Marker startMarker = new Marker(map);
                                                            startMarker.setPosition(startPoint);
                                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                                                            //startMarker.setIcon(getResources().getDrawable(R.drawable.location_red_icon));
                                                            //startMarker.setTitle("Tylo's Parking Lot");
                                                            map.getOverlays().add(startMarker);


                                                            map.invalidate();
                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                catch (Exception err)
                                                {
                                                    Toast.makeText(HomeActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        // Dismiss dialog
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }

                } catch (Exception e) {

                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getLastLocation();
        }
        else
        {
            askLocationPermission();
        }
    }

    void getLastLocation()
    {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Address obj = addresses.get(0);
                        String add = obj.getAddressLine(0).toString();
                        /*add = add + "\n" + obj.getCountryName();
                        add = add + "\n" + obj.getCountryCode();
                        add = add + "\n" + obj.getAdminArea();
                        add = add + "\n" + obj.getPostalCode();
                        add = add + "\n" + obj.getSubAdminArea();
                        add = add + "\n" + obj.getLocality();
                        add = add + "\n" + obj.getSubThoroughfare();*/

                        SharedPreferences sharedPreferences = getSharedPreferences("ECabalen",MODE_PRIVATE);

                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                        myEdit.putString("my_location", add);

                        myEdit.commit();


                        LoadLocationsSpots();
                        //LoadLocationsRestaurants();


                        //Toast.makeText(HomeActivity.this, "GPS Activated. It will track your realtime location.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(HomeActivity.this, "No location found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if(requestCode == 2)
            {
                InputStream imageStream = null;
                try {
                    imageStream = this.getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                try {

                    locationAdapter.getInstance().getCardViewChangePicture().setImageBitmap(yourSelectedImage);
                }
                catch (Exception err)
                {
                    try {
                        locationAdapter2.getInstance().getCardViewChangePicture().setImageBitmap(yourSelectedImage);
                    }
                    catch (Exception err2)
                    {

                    }
                }
            }

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

                if(txtLocationName.getText().toString().isEmpty()
                        || txtLocationAddress.getText().toString().isEmpty()
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
        }
    }

    void askLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 10)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}