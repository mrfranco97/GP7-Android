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
import com.example.xplorenow_android.databinding.FragmentOtpBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OtpFragment extends Fragment {

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
            showCodeStep();
            // TODO: conectar con ViewModel cuando se implemente la capa de red
            Toast.makeText(getContext(), "Código enviado: próximamente", Toast.LENGTH_SHORT).show();
        });

        binding.btnVerify.setOnClickListener(v -> {
            String code = binding.editOtpCode.getText().toString().trim();
            if (code.length() != 6) {
                binding.inputOtpCodeLayout.setError(getString(R.string.error_invalid_otp));
                return;
            }
            binding.inputOtpCodeLayout.setError(null);
            // TODO: conectar con ViewModel cuando se implemente la capa de red
            Toast.makeText(getContext(), "Verificar código: próximamente", Toast.LENGTH_SHORT).show();
        });

        binding.btnResend.setOnClickListener(v -> {
            // TODO: conectar con ViewModel cuando se implemente la capa de red
            Toast.makeText(getContext(), "Código reenviado: próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCodeStep() {
        binding.layoutOtpStepEmail.setVisibility(View.GONE);
        binding.layoutOtpStepCode.setVisibility(View.VISIBLE);
        binding.textOtpCodeSubtitle.setText(getString(R.string.otp_subtitle, email));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
