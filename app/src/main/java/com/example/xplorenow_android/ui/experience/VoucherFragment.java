package com.example.xplorenow_android.ui.experience;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.VoucherDao;
import com.example.xplorenow_android.data.model.Voucher;
import com.example.xplorenow_android.data.network.VoucherApi;
import com.example.xplorenow_android.databinding.FragmentVoucherBinding;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class VoucherFragment extends Fragment {

    private FragmentVoucherBinding binding;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String bookingId;

    @Inject
    VoucherApi voucherApi;

    @Inject
    VoucherDao voucherDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingId = getArguments() != null ? getArguments().getString("bookingId") : null;

        if (bookingId == null) {
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnScanQr.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("bookingId", bookingId);
            Navigation.findNavController(v).navigate(R.id.action_VoucherFragment_to_QrScannerFragment, args);
        });

        binding.btnRetry.setOnClickListener(v -> fetchVoucher());

        fetchVoucher();
    }

    private void fetchVoucher() {
        showLoading();

        voucherApi.getVoucher(bookingId).enqueue(new Callback<Voucher>() {
            @Override
            public void onResponse(@NonNull Call<Voucher> call, @NonNull Response<Voucher> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Voucher voucher = response.body();
                    executor.execute(() -> {
                        voucherDao.insertVoucher(voucher);
                        mainHandler.post(() -> {
                            if (isAdded()) {
                                showVoucher(voucher);
                            }
                        });
                    });
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Voucher> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                loadFromLocal();
            }
        });
    }

    private void handleErrorResponse(int code) {
        switch (code) {
            case 401:
                showError("Sesión expirada. Iniciá sesión nuevamente.");
                break;
            case 403:
                showError("No tenés permiso para ver este voucher.");
                break;
            case 404:
                showError("Voucher no encontrado para esta reserva.");
                break;
            default:
                showError("Error al cargar el voucher. Intentá de nuevo.");
                break;
        }
    }

    private void loadFromLocal() {
        executor.execute(() -> {
            try {
                int id = Integer.parseInt(bookingId);
                Voucher voucher = voucherDao.getVoucherByBookingId(id);
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    if (voucher != null) {
                        showVoucher(voucher);
                    } else {
                        showError("Sin conexión. No hay datos guardados para este voucher.");
                    }
                });
            } catch (NumberFormatException e) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        showError("Sin conexión. No hay datos guardados para este voucher.");
                    }
                });
            }
        });
    }

private void showVoucher(Voucher voucher) {
        if (binding == null) return;
        binding.progressVoucher.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutVoucherContent.setVisibility(View.VISIBLE);

        // Activity name
        binding.textActivityName.setText(voucher.getActivityName());

        // Date
        String date = voucher.getDate();
        if (date != null && date.contains("T")) {
            date = date.split("T")[0];
        }
        binding.textVoucherDate.setText(date);

        // Time
        binding.textVoucherTime.setText(voucher.getTimeSlot());

        // Meeting point
        binding.textMeetingPoint.setText(voucher.getMeetingPoint());

        // Guide
        binding.textGuideName.setText(voucher.getGuideName());

        // Participants
        binding.textParticipants.setText(String.format(Locale.getDefault(), "%d personas", voucher.getParticipants()));

        // Status badge
        binding.textStatus.setText(voucher.getStatus() != null ? voucher.getStatus().toUpperCase() : "");
        setStatusBadgeColor(voucher.getStatus());

        // Check-in status
        setCheckInStatus(voucher.getCheckInStatus(), voucher.getCheckedInAt());
    }

    private void setStatusBadgeColor(String status) {
        if (status == null) return;
        int color;
        switch (status.toLowerCase()) {
            case "confirmada":
                color = Color.parseColor("#00B894");
                break;
            case "cancelada":
                color = Color.parseColor("#D63031");
                break;
            case "finalizada":
                color = Color.parseColor("#636E72");
                break;
            default:
                color = Color.parseColor("#0984E3");
        }
        GradientDrawable drawable = (GradientDrawable) binding.textStatus.getBackground();
        if (drawable != null) {
            drawable.setColor(color);
        }
    }

    private void setCheckInStatus(String checkInStatus, String checkedInAt) {
        if ("confirmed".equalsIgnoreCase(checkInStatus)) {
            binding.textCheckInStatus.setText("Confirmado");
            binding.textCheckInStatus.setTextColor(Color.parseColor("#00B894"));
            GradientDrawable bg = (GradientDrawable) binding.textCheckInStatus.getBackground();
            if (bg != null) {
                bg.setColor(Color.parseColor("#E8F5E9"));
            }
        } else {
            binding.textCheckInStatus.setText("Pendiente");
            binding.textCheckInStatus.setTextColor(Color.parseColor("#E17055"));
            GradientDrawable bg = (GradientDrawable) binding.textCheckInStatus.getBackground();
            if (bg != null) {
                bg.setColor(Color.parseColor("#FFF3E0"));
            }
        }

        if (checkedInAt != null && !checkedInAt.isEmpty()) {
            binding.textCheckedInAt.setVisibility(View.VISIBLE);
            String displayDate = checkedInAt;
            if (checkedInAt.contains("T")) {
                displayDate = checkedInAt.replace("T", " ").split("\\.")[0];
            }
            binding.textCheckedInAt.setText(displayDate);
        } else {
            binding.textCheckedInAt.setVisibility(View.GONE);
        }
    }

    private void showLoading() {
        binding.progressVoucher.setVisibility(View.VISIBLE);
        binding.layoutVoucherContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.progressVoucher.setVisibility(View.GONE);
        binding.layoutVoucherContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
