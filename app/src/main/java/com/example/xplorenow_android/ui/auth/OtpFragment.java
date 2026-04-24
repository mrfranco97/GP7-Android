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
import com.example.xplorenow_android.data.network.OtpRequest;
import com.example.xplorenow_android.data.network.OtpResponse;
import com.example.xplorenow_android.data.network.VerifyOtpRequest;
import com.example.xplorenow_android.databinding.FragmentOtpBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class OtpFragment extends Fragment {

    @Inject
    AuthApi authApi;

    @Inject
    TokenManager tokenManager;

    private FragmentOtpBinding binding;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnSendCode.setOnClickListener(v -> {
            String inputEmail = binding.editEmail.getText().toString().trim();
            if (inputEmail.isEmpty()) {
                binding.inputEmailLayout.setError(getString(R.string.error_required_email));
                return;
            }
            binding.inputEmailLayout.setError(null);
            email = inputEmail;
            doRequestOtp(email);
        });

        binding.btnVerify.setOnClickListener(v -> {
            String code = binding.editOtpCode.getText().toString().trim();
            if (code.length() != 6) {
                binding.inputOtpCodeLayout.setError(getString(R.string.error_invalid_otp));
                return;
            }
            binding.inputOtpCodeLayout.setError(null);
            doVerifyOtp(email, code);
        });

        binding.btnResend.setOnClickListener(v -> doResendOtp(email));
    }

    private void doRequestOtp(String email) {
        setLoading(true);

        authApi.requestOtp(new OtpRequest(email)).enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<OtpResponse> call, @NonNull Response<OtpResponse> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    showCodeStep();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_otp_send), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OtpResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doResendOtp(String email) {
        setLoading(true);

        authApi.resendOtp(new OtpRequest(email)).enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<OtpResponse> call, @NonNull Response<OtpResponse> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.otp_resent), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_otp_send), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OtpResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doVerifyOtp(String email, String code) {
        setLoading(true);

        authApi.verifyOtp(new VerifyOtpRequest(email, code)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_OtpFragment_to_ExperienceListFragment);
                } else {
                    binding.inputOtpCodeLayout.setError(getString(R.string.error_invalid_otp));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCodeStep() {
        binding.layoutOtpStepEmail.setVisibility(View.GONE);
        binding.layoutOtpStepCode.setVisibility(View.VISIBLE);
        binding.textOtpCodeSubtitle.setText(getString(R.string.otp_subtitle, email));
    }

    private void setLoading(boolean loading) {
        binding.btnSendCode.setEnabled(!loading);
        binding.btnVerify.setEnabled(!loading);
        binding.btnResend.setEnabled(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
