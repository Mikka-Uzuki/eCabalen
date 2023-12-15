package com.example.ecabalen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class VoiceTranslatorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    CardView cardViewMic;
    LinearLayout linearLayoutBack;

    EditText txtEnglish, txtKapampangan;

    Map<String, String> map = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_translator_translator);

        cardViewMic = findViewById(R.id.cardViewMic);
        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        txtEnglish = findViewById(R.id.txtEnglish);
        txtKapampangan = findViewById(R.id.txtKapampangan);

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cardViewMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try
                {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e)
                {
                    Toast
                            .makeText(VoiceTranslatorActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        map.put("good morning", "mayap a abak");
        map.put("good afternoon", "mayap a gatpanapun");
        map.put("good evening", "mayap a bengi");
        map.put("morning", "abak");
        map.put("afternoon", "gatpanapun");
        map.put("evening", "bengi");
        map.put("second", "segundu");
        map.put("seconds", "segundu");
        map.put("minute", "minutu");
        map.put("minutes", "minutu");
        map.put("hour", "oras");
        map.put("hours", "oras");
        map.put("time", "oras");
        map.put("day", "aldo");
        map.put("days", "aldo");
        map.put("everyday", "aldo-aldo");
        map.put("week", "paruminggu");
        map.put("weeks", "paruminggu");
        map.put("month", "pabulan");
        map.put("months", "pabulan");
        map.put("year", "pabanwa");
        map.put("years", "pabanwa");
        map.put("before", "bayu");
        map.put("after", "kayari");
        map.put("now", "ngeni");
        map.put("when", "kapilan");
        map.put("where", "nukarin");
        map.put("who", "ninu");
        map.put("how", "makananu");
        map.put("why", "bakit");
        map.put("she", "iya");
        map.put("he", "iya");
        map.put("her", "iya");
        map.put("him", "iya");
        map.put("we", "ikami");
        map.put("us", "ikami");
        map.put("them", "ila");
        map.put("they", "ila");
        map.put("I", "yaku");
        map.put("me", "aku");
        map.put("this", "ini");
        map.put("these", "deni");
        map.put("that", "ita");
        map.put("those", "deta");
        map.put("how much", "magkanu");
        map.put("how many", "pilan");
        map.put("who are you", "ninu ka");
        map.put("who are them", "ninu ila");
        map.put("who are they", "ninu ila");
        map.put("how are you", "kumusta na ka");
        map.put("where are you", "nukarin na ka");
        map.put("how old are you", "pilan na ka");
        map.put("help me", "sopan mu ku");
        map.put("can you help me", "malyari mu ko sopan");
        map.put("help", "sawup");
        map.put("how can i help", "makananu ku sumawup");
        map.put("how can i help you", "makananu daka asopan");
        map.put("where is the hospital", "nukarin ing ospital");
        map.put("where's the hospital", "nukarin ing ospital");
        map.put("where is the police station", "nukarin ing istasyun ning pulis");
        map.put("where's the polics station", "nukarin ing istasyun ning pulis");
        map.put("where is the market", "nukarin ing palengki");
        map.put("where's the market", "nukarin ing palengki");
        map.put("where is the restaurant", "nukarin ing restorant");
        map.put("where's the restaurant", "nukarin ing restorant");
        map.put("where is the airport", "nukarin ing airport");
        map.put("where's the airport", "nukarin ing airport");
        map.put("where is the church", "nukarin ing pisamban");
        map.put("where's the church", "nukarin ing pisamban");
        map.put("who are we with", "ninu ing kayabe tamu");
        map.put("who are you with", "ninu ing kayabe mu");
        map.put("who is she", "ninu iya");
        map.put("who is he", "ninu iya");
        map.put("are you mad", "mimwa ka ba");
        map.put("why are you mad", "bakit mimwa ka");
        map.put("are you happy", "masaya ka ba");
        map.put("why are you happy", "bakit masaya ka");
        map.put("are you sad", "malungkut ka ba");
        map.put("why are you sad", "bakit malungkut ka");
        map.put("why are you crying", "bakit gagaga ka");
        map.put("what can i do", "nanung agawa ku");
        map.put("what can i help", "nanung asawup ku");
        map.put("where we will meet", "nukarin kata mikit");
        map.put("when we will meet", "kapilan kata mikit");
        map.put("what do you want", "nanung buri mu");
        map.put("do", "gawan");
        map.put("don't", "ali mu");
        map.put("can", "pwedi");
        map.put("can't", "ali pwedi");
        map.put("old", "matwa");
        map.put("young", "anak");
        map.put("man", "lalaki");
        map.put("men", "lalaki");
        map.put("woman", "babayi");
        map.put("women", "babayi");
        map.put("white", "maputi");
        map.put("black", "matuling");
        map.put("blue", "asul");
        map.put("red", "malutu");
        map.put("green", "berdi");
        map.put("yellow", "dilo");
        map.put("violet", "violet");
        map.put("big", "maragul");
        map.put("small", "malati");
        map.put("fast", "mabilis");
        map.put("slow", "mabagal");
        map.put("", "");
        map.put("", "");
        map.put("", "");
        map.put("", "");
        map.put("", "");
        map.put("", "");




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                txtEnglish.setText(Objects.requireNonNull(result).get(0));

                Set keys = map.keySet();
                for (Iterator i = keys.iterator(); i.hasNext();)
                {
                    String key = (String) i.next();

                    if(key.equals(Objects.requireNonNull(result).get(0)))
                    {
                        String value = (String) map.get(key);
                        txtKapampangan.setText(value);
                    }
                }
            }
        }
    }
}