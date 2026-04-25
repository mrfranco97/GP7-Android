package com.example.xplorenow_android.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.AuthResponse;
import com.example.xplorenow_android.data.network.RegisterRequest;
import com.example.xplorenow_android.databinding.FragmentRegisterBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    @Inject
    AuthApi authApi;

    @Inject
    TokenManager tokenManager;

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.editName.getText().toString().trim();
            String email = binding.editEmail.getText().toString().trim();
            String password = binding.editPassword.getText().toString();
            String confirmPassword = binding.editConfirmPassword.getText().toString();

            if (name.isEmpty()) {
                binding.inputNameLayout.setError(getString(R.string.error_required_name));
                return;
            }
            if (email.isEmpty()) {
                binding.inputEmailLayout.setError(getString(R.string.error_required_email));
                return;
            }
            if (password.isEmpty()) {
                binding.inputPasswordLayout.setError(getString(R.string.error_required_password));
                return;
            }
            if (!password.equals(confirmPassword)) {
                binding.inputConfirmPasswordLayout.setError(getString(R.string.error_passwords_dont_match));
                return;
            }

            binding.inputNameLayout.setError(null);
            binding.inputEmailLayout.setError(null);
            binding.inputPasswordLayout.setError(null);
            binding.inputConfirmPasswordLayout.setError(null);

            doRegister(name, email, password);
        });
    }

    private void doRegister(String name, String email, String password) {
        setLoading(true);

        // El campo phone es opcional en el backend, se envía vacío por ahora
        authApi.register(new RegisterRequest(name, email, "", password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_RegisterFragment_to_ExperienceListFragment);
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_register), Toast.LENGTH_SHORT).show();
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
        binding.btnRegister.setEnabled(!loading);
        binding.btnBack.setEnabled(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
