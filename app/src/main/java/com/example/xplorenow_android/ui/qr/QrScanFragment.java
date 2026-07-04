package com.example.xplorenow_android.ui.qr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

    private enum CheckinState { SUCCESS, WARNING, ERROR }

    @Inject
    BookingApi bookingApi;

    private PreviewView previewView;
    private FrameLayout layoutResultado;
    private ImageView ivIcono;
    private TextView tvTitulo;
    private TextView tvActividad;
    private TextView tvMensaje;
    private TextView tvInstruccion;
    private Button btnVolver;

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;

    private boolean procesando = false;

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

        previewView   = view.findViewById(R.id.previewView);
        layoutResultado = view.findViewById(R.id.layoutResultado);
        ivIcono       = view.findViewById(R.id.ivIcono);
        tvTitulo      = view.findViewById(R.id.tvTitulo);
        tvActividad   = view.findViewById(R.id.tvActividad);
        tvMensaje     = view.findViewById(R.id.tvMensaje);
        tvInstruccion = view.findViewById(R.id.tvInstruccion);
        btnVolver     = view.findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.action_QrScanFragment_to_ExperienceListFragment)
        );

        view.findViewById(R.id.btnReintentar).setOnClickListener(v -> reiniciarEscaneo());

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

                ImageAnalysis analisis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

                analisis.setAnalyzer(cameraExecutor, this::analizarFrame);

                cameraProvider.unbindAll();
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
            .addOnCompleteListener(tarea -> imageProxy.close());
    }

    private void enviarCheckin(String rawQr) {
        bookingApi.confirmCheckin(new CheckinRequest(rawQr)).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckinResponse> call,
                                   @NonNull Response<CheckinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarResultado(response.body());
                } else {
                    mostrarResultadoError("No se pudo confirmar el check-in");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckinResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red en check-in", t);
                mostrarResultadoError("Error de conexión. Intentá de nuevo.");
            }
        });
    }

    private void mostrarResultado(CheckinResponse response) {
        if (response.isSuccess()) {
            boolean yaHecho = Boolean.TRUE.equals(response.getAlreadyCheckedIn());
            CheckinState estado = yaHecho ? CheckinState.WARNING : CheckinState.SUCCESS;
            String actividad = response.getActivityName();
            aplicarEstado(estado, response.getMessage(), actividad);
        } else {
            aplicarEstado(CheckinState.ERROR, response.getMessage(), null);
        }
    }

    private void mostrarResultadoError(String mensaje) {
        aplicarEstado(CheckinState.ERROR, mensaje, null);
    }

    private void aplicarEstado(CheckinState estado, String mensaje, @Nullable String nombreActividad) {
        requireActivity().runOnUiThread(() -> {
            tvInstruccion.setVisibility(View.GONE);
            layoutResultado.setVisibility(View.VISIBLE);

            switch (estado) {
                case SUCCESS:
                    ivIcono.setImageResource(R.drawable.ic_checkin_success);
                    tvTitulo.setText("¡Asistencia confirmada!");
                    tvTitulo.setTextColor(0xFF4CAF50);
                    btnVolver.setVisibility(View.VISIBLE);
                    break;

                case WARNING:
                    ivIcono.setImageResource(R.drawable.ic_checkin_warning);
                    tvTitulo.setText("Ya confirmaste tu asistencia");
                    tvTitulo.setTextColor(0xFFFFC107);
                    btnVolver.setVisibility(View.VISIBLE);
                    break;

                case ERROR:
                    ivIcono.setImageResource(R.drawable.ic_checkin_error);
                    tvTitulo.setText("QR inválido");
                    tvTitulo.setTextColor(0xFFF44336);
                    btnVolver.setVisibility(View.VISIBLE);
                    break;
            }

            if (nombreActividad != null && !nombreActividad.isEmpty()) {
                tvActividad.setText(nombreActividad);
                tvActividad.setVisibility(View.VISIBLE);
            } else {
                tvActividad.setVisibility(View.GONE);
            }

            if (mensaje != null && !mensaje.isEmpty()) {
                tvMensaje.setText(mensaje);
                tvMensaje.setVisibility(View.VISIBLE);
            } else {
                tvMensaje.setVisibility(View.GONE);
            }
        });
    }

    private void reiniciarEscaneo() {
        procesando = false;
        layoutResultado.setVisibility(View.GONE);
        tvInstruccion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}
