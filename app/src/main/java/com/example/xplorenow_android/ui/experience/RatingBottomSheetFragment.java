package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xplorenow_android.data.model.Rating;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingResponse;
import com.example.xplorenow_android.databinding.LayoutRatingBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RatingBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "RatingBottomSheet";
    private LayoutRatingBottomSheetBinding binding;
    private String bookingId;
    private OnRatingSubmittedListener listener;

    public interface OnRatingSubmittedListener {
        void onRatingSubmitted();
    }

    @Inject
    BookingApi bookingApi;

    public static RatingBottomSheetFragment newInstance(String bookingId) {
        RatingBottomSheetFragment fragment = new RatingBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("bookingId", bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnRatingSubmittedListener(OnRatingSubmittedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutRatingBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
        }
        Log.d(TAG, "onViewCreated for bookingId: " + bookingId);
        binding.btnSubmitRating.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        int activityStars = (int) binding.ratingActivity.getRating();
        int guideStars = (int) binding.ratingGuide.getRating();
        String comment = binding.editComment.getText() != null ? binding.editComment.getText().toString() : "";

        Log.d(TAG, "Submitting rating (camelCase & int): activityStars=" + activityStars + ", guideStars=" + guideStars + ", comment=" + comment);

        if (activityStars == 0 || guideStars == 0) {
            Toast.makeText(getContext(), "Por favor califica con estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        Rating rating = new Rating(activityStars, guideStars, comment);
        binding.btnSubmitRating.setEnabled(false);

        bookingApi.submitRating(bookingId, rating).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (isAdded()) {
                    binding.btnSubmitRating.setEnabled(true);
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Rating submitted successfully");
                        Toast.makeText(getContext(), "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onRatingSubmitted();
                        }
                        dismiss();
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Log.e(TAG, "Error submitting rating. Code: " + response.code() + ", Body: " + errorBody);
                        Toast.makeText(getContext(), "Error al enviar la calificación: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                if (isAdded()) {
                    binding.btnSubmitRating.setEnabled(true);
                    Log.e(TAG, "Network error submitting rating", t);
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
