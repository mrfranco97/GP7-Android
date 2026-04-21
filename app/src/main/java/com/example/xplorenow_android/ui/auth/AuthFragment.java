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
import com.example.xplorenow_android.databinding.FragmentAuthBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthFragment extends Fragment {

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
            // TODO: conectar con ViewModel cuando se implemente la capa de red
            Toast.makeText(getContext(), "Login clásico: próximamente", Toast.LENGTH_SHORT).show();
        });

        binding.btnGoOtp.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_AuthFragment_to_OtpFragment));

        binding.btnGoRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_AuthFragment_to_RegisterFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
