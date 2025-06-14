package sharon.soicher.whatsonmyplate;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothShareActivity extends AppCompatActivity {

    // Request code for picking an image from the gallery
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private Button btnSendByBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_share);  // Ensure you have the matching layout

        btnSendByBluetooth = findViewById(R.id.btnSendByBluetooth);
        btnSendByBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    // Launch gallery for the user to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // This launches the gallery and waits for the result
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtain the selected image's URI
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Send the image via Bluetooth
                sendImageByBluetooth(selectedImageUri);
            } else {
                Toast.makeText(this, "Failed to get the image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // This method sends the selected image via Bluetooth
    private void sendImageByBluetooth(Uri imageUri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        // Force the intent to use the Bluetooth app.
        // Note: The package name is generally "com.android.bluetooth" on many devices.
        // Some devices may require a different package name.
        sendIntent.setPackage("com.android.bluetooth");

        PackageManager pm = getPackageManager();
        if (sendIntent.resolveActivity(pm) != null) {
            startActivity(sendIntent);
        } else {
            Toast.makeText(this, "No Bluetooth app found", Toast.LENGTH_SHORT).show();
        }
    }
}
