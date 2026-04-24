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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Si ya hay sesión activa, saltamos directo a la lista de experiencias
        if (tokenManager.hasToken()) {
            Navigation.findNavController(view)
                    .navigate(R.id.action_AuthFragment_to_ExperienceListFragment);
            return;
        }

        setupListeners();
    }

    private void setupListeners() {
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
