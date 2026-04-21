package com.example.xplorenow_android.ui.experience;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.network.AvailabilityResponse;
import com.example.xplorenow_android.data.model.BookingRequest;
import com.example.xplorenow_android.data.network.BookingCancellationResponse;
import com.example.xplorenow_android.data.network.BookingResponse;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.data.network.CategoryResponse;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.ExperienceResponse;
import com.example.xplorenow_android.data.network.MyBookingsResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ExperienceViewModel extends ViewModel {
    private final MutableLiveData<ExperienceFilters> filtersLiveData = new MutableLiveData<>(new ExperienceFilters());
    private final LiveData<PagingData<Experience>> pagingDataLiveData;
    private final MutableLiveData<List<Experience>> recommendedLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<com.example.xplorenow_android.data.network.Category>> categoriesCatalogLiveData = new MutableLiveData<>();
    
    private final MutableLiveData<Experience> experienceDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> detailLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> detailErrorLiveData = new MutableLiveData<>();
    
    private final MutableLiveData<AvailabilityResponse> availabilityLiveData = new MutableLiveData<>();
    private final MutableLiveData<BookingResponse> bookingResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> bookingErrorLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<Booking>> myBookingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> myBookingsLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<BookingCancellationResponse> cancellationResultLiveData = new MutableLiveData<>();

    private final ExperienceApi experienceApi;
    private final CatalogApi catalogApi;
    private final BookingApi bookingApi;

    @Inject
    public ExperienceViewModel(ExperienceApi experienceApi, CatalogApi catalogApi, BookingApi bookingApi) {
        this.experienceApi = experienceApi;
        this.catalogApi = catalogApi;
        this.bookingApi = bookingApi;
        
        LiveData<PagingData<Experience>> source = Transformations.switchMap(filtersLiveData, filters -> {
            Pager<Integer, Experience> pager = new Pager<>(
                    new PagingConfig(10, 5, false),
                    () -> new ExperiencePagingSource(experienceApi, filters)
            );
            return PagingLiveData.getLiveData(pager);
        });

        pagingDataLiveData = PagingLiveData.cachedIn(source, ViewModelKt.getViewModelScope(this));

        fetchRecommendations();
        fetchCategoriesCatalog();
    }

    private void fetchCategoriesCatalog() {
        catalogApi.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesCatalogLiveData.setValue(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    public void fetchRecommendations() {
        experienceApi.getRecommendedExperiences().enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(Call<ExperienceResponse> call, Response<ExperienceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedLiveData.setValue(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<ExperienceResponse> call, Throwable t) {}
        });
    }

    public void fetchExperienceDetail(int id) {
        detailLoadingLiveData.setValue(true);
        experienceApi.getExperienceDetail(String.valueOf(id)).enqueue(new Callback<Experience>() {
            @Override
            public void onResponse(Call<Experience> call, Response<Experience> response) {
                detailLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    experienceDetailLiveData.setValue(response.body());
                } else {
                    detailErrorLiveData.setValue("Error al cargar el detalle");
                }
            }
            @Override
            public void onFailure(Call<Experience> call, Throwable t) {
                detailLoadingLiveData.setValue(false);
                detailErrorLiveData.setValue(t.getMessage());
            }
        });
    }

    public void fetchAvailability(int experienceId, String date) {
        bookingApi.getAvailability(String.valueOf(experienceId), date).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (response.isSuccessful()) {
                    availabilityLiveData.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {}
        });
    }

    public void createBooking(int experienceId, String date, String timeSlot, int participants) {
        BookingRequest request = new BookingRequest(String.valueOf(experienceId), date, timeSlot, participants);
        bookingApi.createBooking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    bookingResultLiveData.setValue(response.body());
                } else if (response.code() == 409) {
                    bookingErrorLiveData.setValue("No hay cupos suficientes.");
                    fetchAvailability(experienceId, date);
                } else {
                    bookingErrorLiveData.setValue("Error al procesar la reserva.");
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                bookingErrorLiveData.setValue(t.getMessage());
            }
        });
    }

    public void fetchMyBookings() {
        myBookingsLoadingLiveData.setValue(true);
        bookingApi.getMyBookings().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(Call<MyBookingsResponse> call, Response<MyBookingsResponse> response) {
                myBookingsLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    myBookingsLiveData.setValue(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<MyBookingsResponse> call, Throwable t) {
                myBookingsLoadingLiveData.setValue(false);
            }
        });
    }

    public void cancelBooking(String bookingId) {
        myBookingsLoadingLiveData.setValue(true);
        bookingApi.cancelBooking(bookingId).enqueue(new Callback<BookingCancellationResponse>() {
            @Override
            public void onResponse(Call<BookingCancellationResponse> call, Response<BookingCancellationResponse> response) {
                myBookingsLoadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    cancellationResultLiveData.setValue(response.body());
                    fetchMyBookings();
                } else {
                    bookingErrorLiveData.setValue("Error al cancelar la reserva.");
                }
            }
            @Override
            public void onFailure(Call<BookingCancellationResponse> call, Throwable t) {
                myBookingsLoadingLiveData.setValue(false);
                bookingErrorLiveData.setValue(t.getMessage());
            }
        });
    }

    public void clearBookingResult() {
        bookingResultLiveData.setValue(null);
        bookingErrorLiveData.setValue(null);
    }

    public void clearCancellationResult() {
        cancellationResultLiveData.setValue(null);
    }

    public LiveData<PagingData<Experience>> getPagingDataLiveData() { return pagingDataLiveData; }
    public LiveData<List<Experience>> getRecommendedLiveData() { return recommendedLiveData; }
    public LiveData<List<com.example.xplorenow_android.data.network.Category>> getCategoriesCatalogLiveData() { return categoriesCatalogLiveData; }
    public LiveData<Experience> getExperienceDetailLiveData() { return experienceDetailLiveData; }
    public LiveData<Boolean> getDetailLoadingLiveData() { return detailLoadingLiveData; }
    public LiveData<String> getDetailErrorLiveData() { return detailErrorLiveData; }
    public LiveData<AvailabilityResponse> getAvailabilityLiveData() { return availabilityLiveData; }
    public LiveData<BookingResponse> getBookingResultLiveData() { return bookingResultLiveData; }
    public LiveData<String> getBookingErrorLiveData() { return bookingErrorLiveData; }
    public LiveData<List<Booking>> getMyBookingsLiveData() { return myBookingsLiveData; }
    public LiveData<Boolean> getMyBookingsLoadingLiveData() { return myBookingsLoadingLiveData; }
    public LiveData<BookingCancellationResponse> getCancellationResultLiveData() { return cancellationResultLiveData; }

    public void setCategory(String category) {
        ExperienceFilters current = filtersLiveData.getValue();
        if (current != null) {
            ExperienceFilters next = new ExperienceFilters();
            next.setDestination(current.getDestination());
            next.setDate(current.getDate());
            next.setMinPrice(current.getMinPrice());
            next.setMaxPrice(current.getMaxPrice());
            next.setCategory(category);
            filtersLiveData.setValue(next);
        }
    }

    public void applyFilters(String destination, String category, String date, Integer minPrice, Integer maxPrice) {
        ExperienceFilters next = new ExperienceFilters();
        next.setCategory(category);
        next.setDestination(destination);
        next.setDate(date);
        next.setMinPrice(minPrice);
        next.setMaxPrice(maxPrice);
        filtersLiveData.setValue(next);
    }
    
    public ExperienceFilters getFilters() { return filtersLiveData.getValue(); }
}
