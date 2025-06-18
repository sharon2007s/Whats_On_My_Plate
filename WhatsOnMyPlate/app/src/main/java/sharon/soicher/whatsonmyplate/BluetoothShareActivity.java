package sharon.soicher.whatsonmyplate;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothShareActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSIONS = 100;
    private Uri imageUri;
    private BluetoothAdapter bluetoothAdapter;

    // BroadcastReceiver to listen for discovered Bluetooth devices
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // Permission missing, skip
                        return;
                    }
                }

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device != null ? device.getName() : "Unknown";
                String deviceAddress = device != null ? device.getAddress() : "Unknown";

                Toast.makeText(context, "Found device: " + deviceName + " [" + deviceAddress + "]", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_share);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button btnSendByBluetooth = findViewById(R.id.btnSendByBluetooth);

        checkAndRequestPermissions();

        btnSendByBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(BluetoothShareActivity.this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_PERMISSIONS);
                    return;
                }

                // Check BLUETOOTH_SCAN permission before discovery
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(BluetoothShareActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BluetoothShareActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_SCAN},
                                REQUEST_PERMISSIONS);
                        return;
                    }
                }

                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();

                pickImageFromGallery();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //בפעולה זו נבנה מסנן שהפעולה שלו היא BluetoothDevice.ACTION_FOUND פעולה זו
        // מתרחשת כאשר בזמן סריקת Bluetooth מתגלה מכשיר Bluetooth חדש.
        // המסנן מגדיר את סוג האירוע שהבלוק שבו אנחנו מעוניינים לקבל.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(bluetoothReceiver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Add these only on Android 12+
                permissions = new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
            }

            boolean needPermission = false;
            for (String perm : permissions) {
                if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    needPermission = true;
                    break;
                }
            }

            if (needPermission) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            shareImageViaBluetooth();
        }
    }

    private void shareImageViaBluetooth() {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setPackage("com.android.bluetooth");

        try {
            startActivity(Intent.createChooser(intent, "Send Image via Bluetooth"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Bluetooth app not found", Toast.LENGTH_SHORT).show();
        }
    }
}
