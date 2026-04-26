package com.example.xplorenow_android.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.AuthResponse;
import com.example.xplorenow_android.data.network.LoginRequest;
import com.example.xplorenow_android.databinding.FragmentAuthBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class AuthFragment extends Fragment {

    @Inject
    AuthApi authApi;

    @Inject
    TokenManager tokenManager;

    private FragmentAuthBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private static final int ALLOWED_AUTHENTICATORS =
            BiometricManager.Authenticators.BIOMETRIC_STRONG |
            BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean canAuthenticate = canAuthenticateWithDevice();

        setupListeners();
        binding.btnBiometric.setEnabled(tokenManager.isBiometricEnabled() && canAuthenticate);
    }

    private boolean canAuthenticateWithDevice() {
        BiometricManager manager = BiometricManager.from(requireContext());
        return manager.canAuthenticate(ALLOWED_AUTHENTICATORS) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    private void launchBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_prompt_title))
                .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
                .build();

        BiometricPrompt prompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_AuthFragment_to_ExperienceListFragment);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        Toast.makeText(getContext(), errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(getContext(), "Huella no reconocida", Toast.LENGTH_SHORT).show();
                    }
                });

        prompt.authenticate(promptInfo);
    }

    private void setupListeners() {
        binding.btnBiometric.setOnClickListener(v -> launchBiometricPrompt());

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String password = binding.editPassword.getText().toString();

            if (email.isEmpty()) {
                binding.inputEmailLayout.setError(getString(R.string.error_required_email));
                return;
            }
            if (password.isEmpty()) {
                binding.inputPasswordLayout.setError(getString(R.string.error_required_password));
                return;
            }
            binding.inputEmailLayout.setError(null);
            binding.inputPasswordLayout.setError(null);

            doLogin(email, password);
        });

        binding.btnGoOtp.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_AuthFragment_to_OtpFragment));

        binding.btnGoRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_AuthFragment_to_RegisterFragment));
    }

    private void doLogin(String email, String password) {
        setLoading(true);

        authApi.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_AuthFragment_to_ExperienceListFragment);
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.btnGoOtp.setEnabled(!loading);
        binding.btnGoRegister.setEnabled(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
