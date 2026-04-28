package com.example.xplorenow_android.ui.profile;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.ui.user.UserPreferences;
import com.example.xplorenow_android.data.model.Interest;
import com.example.xplorenow_android.data.model.User;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.databinding.FragmentProfileBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    
    @Inject
    AuthApi authApi;

    @Inject
    CatalogApi catalogApi;

    @Inject
    TokenManager tokenManager;

    private static final String PREFS_NAME = "profile_prefs";
    private static final String KEY_IMAGE_URI = "image_uri";

    private ImageView ivProfile;


    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openGallery();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Permiso denegado. No se puede acceder a la galería.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            saveAndDisplay(uri);
                        }
                    });




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        setupBiometricSwitch();
        fetchProfile();
    }

    private void fetchProfile() {
        showLoading(true);
        authApi.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchInterests(response.body());
                } else {
                    showLoading(false);
                    showError("Error al cargar perfil");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                showError(t.getMessage());
            }
        });
    }

    private void fetchInterests(User user) {
        catalogApi.getTravelInterests().enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(Call<InterestResponse> call, Response<InterestResponse> response) {
                showLoading(false);
                displayUser(user);
                if (response.isSuccessful() && response.body() != null) {
                    displayInterests(response.body().getItems(), user.getPreferences());
                }
            }
            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                showLoading(false);
                displayUser(user);
            }
        });
    }

    private void updateProfile(String name, String email, String phone, List<String> interests) {
        showLoading(true);
        User updatedUser = new User(name, email, phone);
        updatedUser.setPreferences(new UserPreferences(interests));

        authApi.updateUserProfile(updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.msg_save_success), Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    showError("Error al actualizar perfil");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                showError(t.getMessage());
            }
        });
    }

    private void displayUser(User user) {
        binding.editName.setText(user.getName());
        binding.editEmail.setText(user.getEmail());
        binding.editPhone.setText(user.getPhone());
        
        // Usamos el binding directamente para la imagen de perfil
        ivProfile = binding.imageProfile;

        // Si ya existe una Uri guardada, la mostramos al entrar al Fragment
        loadSavedImage();
    }

    private void showLoading(boolean isLoading) {
        binding.progressProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveProfile.setEnabled(!isLoading);
    }

    private void showError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
    }

    private void displayInterests(List<Interest> catalog, UserPreferences preferences) {
        binding.interestsChipGroup.removeAllViews();
        List<String> userInterests = (preferences != null && preferences.getTravelInterests() != null) 
                ? preferences.getTravelInterests() : new ArrayList<>();

        for (Interest interest : catalog) {
            Chip chip = new Chip(getContext());
            chip.setText(interest.getLabel());
            chip.setTag(interest.getKey());
            chip.setCheckable(true);
            chip.setChecked(userInterests.contains(interest.getKey()));
            binding.interestsChipGroup.addView(chip);
        }
    }

    private static final int ALLOWED_AUTHENTICATORS =
            BiometricManager.Authenticators.BIOMETRIC_STRONG |
            BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    private void setupBiometricSwitch() {
        BiometricManager manager = BiometricManager.from(requireContext());
        boolean deviceCanAuthenticate = manager.canAuthenticate(ALLOWED_AUTHENTICATORS)
                == BiometricManager.BIOMETRIC_SUCCESS;

        if (!deviceCanAuthenticate) {
            // El dispositivo no tiene ningún factor configurado: se deshabilita el switch
            binding.switchBiometric.setEnabled(false);
            Toast.makeText(getContext(), getString(R.string.security_biometric_unavailable), Toast.LENGTH_SHORT).show();
            return;
        }

        // Reflejar el estado actual sin disparar el listener
        binding.switchBiometric.setOnCheckedChangeListener(null);
        binding.switchBiometric.setChecked(tokenManager.isBiometricEnabled());

        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Al activar: pedir confirmación biométrica antes de guardar el flag
                binding.switchBiometric.setChecked(false); // revertir visualmente hasta confirmar
                launchBiometricConfirmation();
            } else {
                tokenManager.setBiometricEnabled(false);
            }
        });
    }

    private void launchBiometricConfirmation() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_prompt_title))
                .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
                .build();

        new BiometricPrompt(this,
                ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        tokenManager.setBiometricEnabled(true);
                        binding.switchBiometric.setOnCheckedChangeListener(null);
                        binding.switchBiometric.setChecked(true);
                        // Restaurar el listener después de actualizar el estado
                        setupBiometricSwitch();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        // El usuario canceló: el switch ya está en false, no se hace nada
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        // El sistema ya muestra feedback; el switch permanece en false
                    }
                }).authenticate(promptInfo);
    }



    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnMyBookings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_MyBookingsFragment));
        binding.btnBookingHistory.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_BookingHistoryFragment));
        binding.btnLogout.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_AuthFragment));

        binding.btnSaveProfile.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String email = binding.editEmail.getText().toString();
            String phone = binding.editPhone.getText().toString();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.error_required_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedInterests = new ArrayList<>();
            for (int i = 0; i < binding.interestsChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.interestsChipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedInterests.add((String) chip.getTag());
                }
            }
            updateProfile(name, email, phone, selectedInterests);
        });

        binding.imageProfile.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });
    }

    private void checkPermissionAndOpenGallery() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            // Permiso ya otorgado → abrimos la galería directamente
            openGallery();
        } else {
            // No tenemos permiso → lanzamos el diálogo del sistema
            permissionLauncher.launch(permission);
        }
    }



    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void saveAndDisplay(Uri uri) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_IMAGE_URI, uri.toString()).apply();

        displayImage(uri);
    }

    private void loadSavedImage() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_IMAGE_URI, null);
        if (uriString != null) {
            displayImage(Uri.parse(uriString));
        }
    }

    private void displayImage(Uri uri) {
        Glide.with(this)
                .load(uri)       // carga directamente desde la Uri
                .circleCrop()    // recorta en círculo
                .into(ivProfile); // destino: el ImageView
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
