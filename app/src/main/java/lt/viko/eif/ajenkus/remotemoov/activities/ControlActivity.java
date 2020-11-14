package lt.viko.eif.ajenkus.remotemoov.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import lt.viko.eif.ajenkus.remotemoov.R;

public class ControlActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottom_nav;
    private RequestQueue queue;

    private CrystalRangeSeekbar limit_seekbar;
    private CrystalSeekbar output_seekbar;
    private TextView output_text;
    private TextView part_text;
    private TextView limit_min;
    private TextView limit_max;

    private String bot_name;
    private String user_name;
    private String connection_string;
    private String api_part;

    private boolean limit_changed = false;
    private float current_value = 0.0f;
    private float min_value = 0.0f;
    private float max_value = 0.0f;

    // ----------------------------------------
    // OVERRIDES
    // ----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // Initialization
        queue = Volley.newRequestQueue(ControlActivity.this);

        Intent intent = getIntent();
        bot_name = intent.getStringExtra("bot_name");
        user_name = intent.getStringExtra("user_name");
        connection_string = intent.getStringExtra("connection_string");

        output_seekbar = findViewById(R.id.output_seekbar);
        output_text = findViewById(R.id.output_value);
        limit_seekbar = findViewById(R.id.limit_seekbar);
        limit_min = findViewById(R.id.limit_min_value);
        limit_max = findViewById(R.id.limit_max_value);
        part_text = findViewById(R.id.part_textView);
        bottom_nav = findViewById(R.id.bottom_nav);

        bottom_nav.setSelectedItemId(R.id.nav_controls);

        // Bottom navigation click listener
        bottom_nav.setOnNavigationItemSelectedListener(this);

        // Limit seekbar
        setPartApi();
        setLimitBarMinMax();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                bottom_nav.setSelectedItemId(R.id.nav_controls);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_connect: {
                Intent intent = new Intent(ControlActivity.this, MainActivity.class);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.nav_home: {
                Intent intent = new Intent(ControlActivity.this, HomeActivity.class);
                intent.putExtra("bot_name", bot_name);
                intent.putExtra("user_name", user_name);
                intent.putExtra("connection_string", connection_string);
                startActivity(intent);
                finish();
                break;
            }
        }
        return true;
    }

    // ----------------------------------------
    // PUBLIC METHODS
    // ----------------------------------------

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_right_rotate:
                part_text.setText("Right Rotate");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_left_rotate:
                part_text.setText("Left Rotate");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_right_bicep:
                part_text.setText("Right Bicep");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_left_bicep:
                part_text.setText("Left Bicep");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_right_shoulder:
                part_text.setText("Right Shoulder");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_left_shoulder:
                part_text.setText("Left Shoulder");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_right_omoplate:
                part_text.setText("Right Omoplate");
                setPartApi();
                setLimitBarMinMax();
                break;
            case R.id.button_left_omoplate:
                part_text.setText("Left Omoplate");
                setPartApi();
                setLimitBarMinMax();
                break;
        }
    }

    public void onCapture(View view) {
        StringRequest sendRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getResponse/capturegesture",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO: Implement response to send request
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
            }
        });

        queue.add(sendRequest);
    }

    // ----------------------------------------
    // PRIVATE METHODS
    // ----------------------------------------

    /**
     * Current servos value slider event listener
     */
    private void setOutputBarListener() {
        output_seekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                // Check if limit values have been altered
                if (!limit_changed) {
                    current_value = value.floatValue();
                    // Output the current value on the UI
                    output_text.setText(value.toString());
                    // Full endpoint string to move servo positions to new value
                    String url = connection_string + api_part + "moveTo/" + current_value;
                    // Constructs a request to change servo value position
                    StringRequest sendRequest = new StringRequest(
                            Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR", error.getMessage());
                        }
                    });
                    // Puts request on queue
                    queue.add(sendRequest);
                } else  {
                    // Output the current value on the UI
                    output_text.setText(String.valueOf(Math.round(current_value)));
                    // Full endpoint string to move servo positions to new value
                    String url = connection_string + api_part + "moveTo/" + value.floatValue();
                    // Constructs a request to change servo value position
                    StringRequest sendRequest = new StringRequest(
                            Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    limit_changed = false;
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR", error.getMessage());
                        }
                    });
                    // Puts request on queue
                    queue.add(sendRequest);
                }
            }
        });

    }

    /**
     * Event listener for minimum and maximum value changes
     */
    private void setLimitBarListener() {
        limit_seekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            // This method executes after any value change
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                // Sets the new min and max values on the UI
                limit_min.setText(minValue.toString());
                limit_max.setText(maxValue.toString());
                // Caching new values for outputbar min and max values
                min_value = minValue.floatValue();
                max_value = maxValue.floatValue();
                // This value indicates to outputbar that values have been changed
                limit_changed = true;
                // This block changes outputbar value if min or max values
                // are less or greater than the current one
                if (min_value > current_value) {
                    current_value = min_value + 0.1f;
                }
                if (max_value < current_value) {
                    current_value = max_value - 0.1f;
                }
                // This method sets the current value
                output_seekbar.setMinStartValue(current_value);
                // This method sets the overall minimum value
                output_seekbar.setMinValue(minValue.floatValue());
                // This method sets the overall maximum value
                output_seekbar.setMaxValue(maxValue.floatValue());
                // Any change must be applied with this method or nothing changes
                output_seekbar.apply();
                // Full endpoint string to change minimum and maximum servo values on MyRobotLab
                String url = connection_string + api_part + "setMinMax/" + minValue.floatValue() + "/" + maxValue.floatValue();
                // Request to change said values
                StringRequest request = new StringRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", Objects.requireNonNull(error.getMessage()));
                    }
                });
                // Puts request into queue
                queue.add(request);
            }
        });
    }

    /**
     * Gets servo positions minimum, maximum, current values
     * and sets them on the application UI
     */
    private void setLimitBarMinMax() {
        // Constructs partial endpoint string
        final String URL = connection_string + api_part;
        // Full endpoint string to get minimum servo value
        String minUrl = URL + "getMin/";
        // Request to get minimum servo value
        StringRequest minRequest = new StringRequest(
                Request.Method.GET,
                minUrl,
                // Response lambda expression for minimum servo value
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        float min = Float.parseFloat(response);
                        limit_seekbar.setMinStartValue(min);
                        output_seekbar.setMinValue(min);
                        min_value = min;
                        // Full endpoint string to get maximum servo value
                        String maxUrl = URL + "getMax/";
                        // Request to get maximum servo values
                        StringRequest maxRequest = new StringRequest(
                                Request.Method.GET,
                                maxUrl,
                                // Response lambda expression for maximum servo value
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        float max = Float.parseFloat(response);
                                        limit_seekbar.setMaxStartValue(max);
                                        output_seekbar.setMaxValue(max);
                                        max_value = max;
                                        // Full endpoint string to get current servo value
                                        String currentUrl = URL + "getCurrentPos/";
                                        // Request to get current servo values
                                        StringRequest outputRequest = new StringRequest(
                                                Request.Method.GET,
                                                currentUrl,
                                                // Response lambda expression for current servo values
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        current_value = Float.parseFloat(response);
                                                        output_seekbar.setMinStartValue(current_value);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                limit_seekbar.apply();
                                                                setLimitBarListener();
                                                                setOutputBarListener();
                                                            }
                                                        });
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("ERROR", error.getMessage());
                                            }
                                        });
                                        queue.add(outputRequest);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR", error.getMessage());
                            }
                        });
                        queue.add(maxRequest);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
            }
        });
        queue.add(minRequest);
    }

    private void setPartApi() {
        switch (part_text.getText().toString()) {
            case "Right Rotate":
                api_part = "i01.rightArm.rotate/";
                break;
            case "Right Bicep":
                api_part = "i01.rightArm.bicep/";
                break;
            case "Right Shoulder":
                api_part = "i01.rightArm.shoulder/";
                break;
            case "Right Omoplate":
                api_part = "i01.rightArm.omoplate/";
                break;

            case "Left Rotate":
                api_part = "i01.leftArm.rotate/";
                break;
            case "Left Bicep":
                api_part = "i01.leftArm.bicep/";
                break;
            case "Left Shoulder":
                api_part = "i01.leftArm.shoulder/";
                break;
            case "Left Omoplate":
                api_part = "i01.leftArm.omoplate/";
                break;
        }
    }

}
