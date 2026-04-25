package com.example.xplorenow_android.ui.experience;

import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.BookingDao;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.databinding.FragmentBookingDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class BookingDetailFragment extends Fragment implements OnMapReadyCallback {

    private FragmentBookingDetailBinding binding;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private GoogleMap googleMap;
    private Booking currentBooking;

    @Inject
    BookingApi bookingApi;

    @Inject
    BookingDao bookingDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        String bookingId = getArguments() != null ? getArguments().getString("bookingId") : null;
        if (bookingId != null) {
            fetchBookingDetail(bookingId);
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (currentBooking != null) {
            setupMap();
        }
    }

    private void fetchBookingDetail(String id) {
        binding.progressDetail.setVisibility(View.VISIBLE);
        
        bookingApi.getBookingDetail(id).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Booking booking = response.body();
                        executor.execute(() -> {
                            bookingDao.insertBooking(booking);
                            mainHandler.post(() -> {
                                binding.progressDetail.setVisibility(View.GONE);
                                currentBooking = booking;
                                displayDetail(booking);
                            });
                        });
                    } else {
                        loadBookingFromLocal(id);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                if (isAdded()) {
                    loadBookingFromLocal(id);
                }
            }
        });
    }

    private void loadBookingFromLocal(String id) {
        executor.execute(() -> {
            Booking booking = bookingDao.getBookingById(id);
            mainHandler.post(() -> {
                if (isAdded()) {
                    binding.progressDetail.setVisibility(View.GONE);
                    if (booking != null) {
                        currentBooking = booking;
                        displayDetail(booking);
                    } else {
                        showError("Detalle no disponible sin conexión");
                    }
                }
            });
        });
    }

    private void displayDetail(Booking booking) {
        Booking.ActivityDetail activity = booking.getActivity();
        
        binding.textBookingId.setText(String.format(Locale.getDefault(), "Reserva #%s", booking.getId()));
        binding.textActivityName.setText(activity.getName());
        binding.textDestination.setText(activity.getDestination());
        binding.textDescription.setText(activity.getDescription());
        
        String info = String.format(Locale.getDefault(),
                "Fecha: %s\nHorario: %s\nParticipantes: %d personas\nTotal: $%.0f",
                booking.getDate(),
                booking.getTimeSlot(),
                booking.getParticipants(),
                booking.getTotalPrice());
        binding.textBookingInfo.setText(info);

        Glide.with(this)
                .load(activity.getImageUrl())
                .into(binding.imageActivity);

        if (booking.getRating() != null) {
            binding.labelRating.setVisibility(View.VISIBLE);
            binding.layoutRatingDetails.setVisibility(View.VISIBLE);
            binding.ratingActivity.setRating(booking.getRating().getActivityStars());
            binding.ratingGuide.setRating(booking.getRating().getGuideStars());
            binding.textComment.setText(booking.getRating().getComment());
        }

        setupMap();
        setupNavigationButton(booking);
    }

    private void setupMap() {
        if (googleMap == null || currentBooking == null || currentBooking.getActivity().getMap() == null) {
            return;
        }

        googleMap.clear();
        Booking.MapData mapData = currentBooking.getActivity().getMap();
        Booking.PointData meetingPoint = mapData.getMeetingPoint();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int pointsCount = 0;

        if (meetingPoint != null && meetingPoint.getLatitude() != null && meetingPoint.getLongitude() != null) {
            LatLng meetingLatLng = new LatLng(meetingPoint.getLatitude(), meetingPoint.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(meetingLatLng)
                    .title(meetingPoint.getName() != null ? meetingPoint.getName() : "Punto de encuentro")
                    .snippet(meetingPoint.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            
            boundsBuilder.include(meetingLatLng);
            pointsCount++;

            binding.textMeetingPointName.setText(meetingPoint.getName());
            binding.textMeetingPointAddress.setText(meetingPoint.getAddress());
            
            if (mapData.isHasRoute() && mapData.getItineraryPoints() != null) {
                for (Booking.PointData point : mapData.getItineraryPoints()) {
                    if (point.getLatitude() != null && point.getLongitude() != null) {
                        LatLng pointLatLng = new LatLng(point.getLatitude(), point.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(pointLatLng)
                                .title(point.getName())
                                .snippet(point.getAddress()));
                        boundsBuilder.include(pointLatLng);
                        pointsCount++;
                    }
                }
            }

            if (pointsCount > 0) {
                final int finalPointsCount = pointsCount;
                googleMap.setOnMapLoadedCallback(() -> {
                    if (finalPointsCount > 1) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150));
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(meetingLatLng, 14f));
                    }
                });
            }
        } else {
            // Fallback to legacy meeting point text if MapData is incomplete
            binding.textMeetingPointName.setText(currentBooking.getActivity().getMeetingPoint());
            binding.textMeetingPointAddress.setVisibility(View.GONE);
            binding.cardMap.setVisibility(View.GONE);
        }
    }

    private void setupNavigationButton(Booking booking) {
        Booking.MapData mapData = booking.getActivity().getMap();
        if (mapData != null && mapData.getMeetingPoint() != null && mapData.getMeetingPoint().getNavigationUrl() != null) {
            binding.btnHowToGet.setVisibility(View.VISIBLE);
            binding.btnHowToGet.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapData.getMeetingPoint().getNavigationUrl()));
                startActivity(intent);
            });
        } else {
            binding.btnHowToGet.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
