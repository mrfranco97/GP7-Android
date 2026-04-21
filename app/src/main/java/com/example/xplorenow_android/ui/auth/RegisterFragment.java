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
import com.example.xplorenow_android.databinding.FragmentRegisterBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

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

            // TODO: llamar al ViewModel cuando se implemente la capa de red
            Toast.makeText(getContext(), "Registro: próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
