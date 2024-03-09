package com.example.languagetranslatorjava;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.languagetranslatorjava.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String[] fromLanguages = {"From", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Urdu", "Hindi"};
    private String[] toLanguages = {"To", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Urdu", "Hindi"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    private int language_code, from_language_code, to_language_code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                from_language_code = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.item_spinner, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFrom.setAdapter(fromAdapter);

        binding.spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to_language_code = getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.item_spinner, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTo.setAdapter(toAdapter);

        binding.translateButton.setOnClickListener(v -> {

            binding.tvTranslation.setText("");
            if(binding.editSourceText.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
            }
            else if(from_language_code == 0){
                Toast.makeText(this, "Please select source language", Toast.LENGTH_SHORT).show();
            }
            else if(to_language_code == 0){
                Toast.makeText(this, "Please select translation language", Toast.LENGTH_SHORT).show();
            }else{
                translateText(from_language_code, to_language_code, binding.editSourceText.getText().toString());
            }

        });

        binding.imageMic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");
            try{
                startActivityForResult(intent, REQUEST_PERMISSION_CODE);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {

        binding.tvTranslation.setText("Downloading Model.. ");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.tvTranslation.setText("Translating.. ");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.tvTranslation.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to download language model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int getLanguageCode(String language) {
        int language_code = 0;
        switch (language){
            case "English":
                language_code = FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                language_code = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                language_code = FirebaseTranslateLanguage.AR;
                break;
            case "Belarusian":
                language_code = FirebaseTranslateLanguage.BE;
                break;
            case "Bengali":
                language_code = FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                language_code = FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                language_code = FirebaseTranslateLanguage.CS;
                break;
            case "Welsh":
                language_code = FirebaseTranslateLanguage.CY;
                break;
            case "Urdu":
                language_code = FirebaseTranslateLanguage.UR;
                break;
            case "Hindi":
                language_code = FirebaseTranslateLanguage.HI;
                break;
            default:
                language_code = 0;
        }
        return language_code;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            if (resultCode == RESULT_OK && data!=null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                binding.editSourceText.setText(result.get(0));
            }
        }
    }
}