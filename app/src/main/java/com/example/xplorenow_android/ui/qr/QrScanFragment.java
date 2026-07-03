package com.example.xplorenow_android.ui.qr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.CheckinRequest;
import com.example.xplorenow_android.data.network.CheckinResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class QrScanFragment extends Fragment {

    private static final String TAG = "QrScanFragment";

    @Inject
    BookingApi bookingApi;

    private PreviewView previewView;
    private CardView cardResultado;
    private TextView tvMensaje;
    private TextView tvInstruccion;

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;

    // Evita enviar múltiples requests mientras se procesa un QR
    private boolean procesando = false;

    /*
     * El launcher de permisos debe registrarse en onCreate(), antes de onStart().
     * Si se registra más tarde, Android lanza una excepción.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    iniciarCamara();
                } else {
                    Toast.makeText(requireContext(),
                        "Se necesita el permiso de cámara para escanear QR",
                        Toast.LENGTH_LONG).show();
                }
            }
        );

        BarcodeScannerOptions opciones = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build();
        barcodeScanner = BarcodeScanning.getClient(opciones);

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = view.findViewById(R.id.previewView);
        cardResultado = view.findViewById(R.id.cardResultado);
        tvMensaje = view.findViewById(R.id.tvMensaje);
        tvInstruccion = view.findViewById(R.id.tvInstruccion);
        Button btnReintentar = view.findViewById(R.id.btnReintentar);
        btnReintentar.setOnClickListener(v -> reiniciarEscaneo());

        pedirPermisoCamara();
    }

    private void pedirPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void iniciarCamara() {
        ListenableFuture<ProcessCameraProvider> futuro =
            ProcessCameraProvider.getInstance(requireContext());

        futuro.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = futuro.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // STRATEGY_KEEP_ONLY_LATEST: descarta frames si el analyzer no terminó el anterior
                ImageAnalysis analisis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

                analisis.setAnalyzer(cameraExecutor, this::analizarFrame);

                cameraProvider.unbindAll();

                // bindToLifecycle libera la cámara automáticamente en onPause/onDestroy
                cameraProvider.bindToLifecycle(
                    getViewLifecycleOwner(),
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analisis
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error al obtener ProcessCameraProvider", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void analizarFrame(@NonNull ImageProxy imageProxy) {
        // Si ya estamos procesando un QR, ignorar frames siguientes hasta terminar
        if (procesando) {
            imageProxy.close();
            return;
        }

        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage imagen = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.getImageInfo().getRotationDegrees()
        );

        barcodeScanner.process(imagen)
            .addOnSuccessListener(codigos -> {
                for (Barcode codigo : codigos) {
                    String valor = codigo.getRawValue();
                    if (valor != null && !procesando) {
                        procesando = true;
                        enviarCheckin(valor);
                    }
                }
            })
            .addOnFailureListener(e -> Log.w(TAG, "Error en ML Kit barcode scan", e))
            .addOnCompleteListener(tarea -> {
                // imageProxy.close() SIEMPRE al terminar; sin esto CameraX no entrega el siguiente frame
                imageProxy.close();
            });
    }

    private void enviarCheckin(String rawQr) {
        bookingApi.confirmCheckin(new CheckinRequest(rawQr)).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckinResponse> call,
                                   @NonNull Response<CheckinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckinResponse body = response.body();
                    boolean esExito = "confirmed".equals(body.getResult());
                    mostrarResultado(body.getMessage(), esExito);
                } else {
                    mostrarResultado("No se pudo confirmar el check-in", false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckinResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red en check-in", t);
                mostrarResultado("Error de conexión. Intentá de nuevo.", false);
            }
        });
    }

    private void mostrarResultado(String mensaje, boolean exito) {
        requireActivity().runOnUiThread(() -> {
            tvInstruccion.setVisibility(View.GONE);
            cardResultado.setVisibility(View.VISIBLE);
            tvMensaje.setText(mensaje);

            int color = exito ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
            cardResultado.setCardBackgroundColor(color);
        });
    }

    private void reiniciarEscaneo() {
        procesando = false;
        cardResultado.setVisibility(View.GONE);
        tvInstruccion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}
