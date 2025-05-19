package com.example.javaassignm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaassignm.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static Bundle tempState = null;

    private ActivityMainBinding binding;
    private Uri selectedImageUri;
    private int year, month, day;

    private ActionMode actionMode;
    private CheckBox checkboxEnglish, checkboxAmharic;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkboxEnglish = findViewById(R.id.checkbox_english);
        checkboxAmharic = findViewById(R.id.checkbox_amharic);

        // Restore from savedInstanceState (rotation)
        if (savedInstanceState != null) {
            binding.etname.setText(savedInstanceState.getString("name", ""));
            binding.etFName.setText(savedInstanceState.getString("fname", ""));
            binding.btnPickDate.setText(savedInstanceState.getString("date", getString(R.string.select_date)));

            String imageUriString = savedInstanceState.getString("imageUri");
            if (imageUriString != null) {
                selectedImageUri = Uri.parse(imageUriString);
                binding.imageView.setImageURI(selectedImageUri);
            }
        }

        // Restore from tempState (language switch)
        if (tempState != null) {
            restoreForm(tempState);
            tempState = null;
        }

        // Language selection logic
        String currentLang = Locale.getDefault().getLanguage();
        checkboxEnglish.setChecked(currentLang.equals("en"));
        checkboxAmharic.setChecked(currentLang.equals("am"));

        checkboxEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !Locale.getDefault().getLanguage().equals("en")) {
                checkboxAmharic.setChecked(false);
                saveFormToTemp();
                setLocale("en");
                recreate();
            }
        });

        checkboxAmharic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !Locale.getDefault().getLanguage().equals("am")) {
                checkboxEnglish.setChecked(false);
                saveFormToTemp();
                setLocale("am");
                recreate();
            }
        });

        // Register ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        binding.imageView.setImageURI(selectedImageUri);
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Long press image to activate CAB
        binding.imageView.setOnLongClickListener(v -> {
            if (actionMode != null) return false;
            actionMode = startActionMode(actionModeCallback);
            v.setSelected(true);
            return true;
        });

        // Date Picker
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, y, m, d) -> binding.btnPickDate.setText(d + "/" + (m + 1) + "/" + y),
                    year, month, day);
            datePickerDialog.show();
        });

        // Image Picker (button)
        binding.btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Submit Button
        binding.btnSubmit.setOnClickListener(v -> {
            String name = binding.etname.getText().toString();
            String fname = binding.etFName.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("fname", fname);
            intent.putExtra("date", binding.btnPickDate.getText().toString());

            if (selectedImageUri != null) {
                intent.putExtra("image", selectedImageUri.toString());
            }

            startActivity(intent);
        });
    }

    private void saveFormToTemp() {
        tempState = new Bundle();
        tempState.putString("name", binding.etname.getText().toString());
        tempState.putString("fname", binding.etFName.getText().toString());
        tempState.putString("date", binding.btnPickDate.getText().toString());
        if (selectedImageUri != null) {
            tempState.putString("imageUri", selectedImageUri.toString());
        }
    }

    private void restoreForm(Bundle state) {
        binding.etname.setText(state.getString("name", ""));
        binding.etFName.setText(state.getString("fname", ""));
        binding.btnPickDate.setText(state.getString("date", getString(R.string.select_date)));

        String imageUriString = state.getString("imageUri");
        if (imageUriString != null) {
            selectedImageUri = Uri.parse(imageUriString);
            binding.imageView.setImageURI(selectedImageUri);
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // Contextual Action Mode (CAB)
    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                binding.imageView.setImageResource(R.drawable.image7);
                selectedImageUri = null;
                Toast.makeText(MainActivity.this, "Image deleted via CAB", Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            } else if (item.getItemId() == R.id.action_add_image) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", binding.etname.getText().toString());
        outState.putString("fname", binding.etFName.getText().toString());
        outState.putString("date", binding.btnPickDate.getText().toString());
        if (selectedImageUri != null) {
            outState.putString("imageUri", selectedImageUri.toString());
        }
    }

    // Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        finish();
                        Toast.makeText(this, "Bye user", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        if (item.getItemId() == R.id.menu_reset) {
            binding.etname.setText("");
            binding.etFName.setText("");
            binding.btnPickDate.setText(getString(R.string.select_date));
            binding.imageView.setImageResource(R.drawable.image7);
            selectedImageUri = null;

            Toast.makeText(this, getString(R.string.reset_form), Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.imageView) {
            getMenuInflater().inflate(R.menu.menu_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            binding.imageView.setImageURI(null);
            selectedImageUri = null;
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
