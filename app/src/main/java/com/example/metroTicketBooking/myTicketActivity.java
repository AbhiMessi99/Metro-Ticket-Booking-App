package com.example.metroTicketBooking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class myTicketActivity extends AppCompatActivity {
    TextView source, destination, jType, fare;
    ImageView home;
    ImageView qrcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ticket);
        source = findViewById(R.id.textViewTicketSource);
        destination = findViewById(R.id.textViewTicketDestination);
        jType = findViewById(R.id.textViewTicketJourney);
        fare = findViewById(R.id.textViewTicketCost);
        qrcode = findViewById(R.id.imageViewQR_CODE);
        home = findViewById(R.id.imageViewTicketHome);
        Intent intent = getIntent();
        jType.setText(intent.getStringExtra("journeyType"));
        source.setText(intent.getStringExtra("source"));
        destination.setText(intent.getStringExtra("destination"));
        fare.setText("₹ "+intent.getStringExtra("fare"));
        String qrText = intent.getStringExtra("source")+ " -> "
                + intent.getStringExtra("destination")
                + " fare: INR" + intent.getStringExtra("fare");
        generateQRCode(qrText, qrcode);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }
    private void generateQRCode(String text, ImageView imageView) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR Code generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}