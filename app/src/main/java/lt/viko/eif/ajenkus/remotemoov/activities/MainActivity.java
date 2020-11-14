package lt.viko.eif.ajenkus.remotemoov.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import lt.viko.eif.ajenkus.remotemoov.R;


public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    private EditText ip;
    private EditText port;

    // --------------------------------------
    // CONNECT BUTTON
    // --------------------------------------

    private ConstraintLayout connect_constraintLayout;
    private CardView connect_cardView;
    private ProgressBar progressBar;
    private TextView connect_text;

    // --------------------------------------
    // OVERRIDES
    // --------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        connect_constraintLayout = findViewById(R.id.connect_constraintLayout);
        connect_cardView = findViewById(R.id.connect_cardView);
        progressBar = findViewById(R.id.connect_progressBar);
        connect_text = findViewById(R.id.connect_textView);

        ip = findViewById(R.id.ipText);
        port = findViewById(R.id.portText);

        connect_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);
                connect_text.setText(R.string.connecting);
                connect_cardView.setOnClickListener(null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createSession(v);
                    }
                }, 1000);
            }
        });

        dropKeyboardFocusOnPress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1);
        finish();
    }

    // --------------------------------------
    //  PUBLIC METHODS
    // --------------------------------------

    public void onHow(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.TutorialDialogTheme);
        LayoutInflater factory = LayoutInflater.from(this);
        View alert_view = factory.inflate(R.layout.image_dialog, null);
        alert.setView(alert_view);
        alert.setTitle("How to connect");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();

    }

    /**
     * Creates a session between application and MyRobotLab
     */
    public void createSession(View view) {
        // Text field data
        String ip_text = ip.getText().toString();
        String port_text = port.getText().toString();
        // Checks if either text fields are empty
        if (ip_text.isEmpty() || port_text.isEmpty()) {
            setOnClick();
            return;
        }
        // Sets up a RequestQueue. This is used to handle all request to the endpoint.
        queue = Volley.newRequestQueue(MainActivity.this);
        // Builds the start of all service endpoints with the specified text field information
        final String endpoint = String.format("http://%s:%s/api/service/", ip_text, port_text);
        // Build the full url
        String url = endpoint + "chatBot/getMetaData/";
        // JSON request to check if MyRobotLab session is created
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                // Response lambda expression
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checks if the session is valid
                            String available = response.getString("available");
                            if (available.equals("true")) {
                                // UI response to the user that the connection is valid
                                progressBar.setVisibility(View.INVISIBLE);
                                connect_text.setText(R.string.connected);
                                connect_constraintLayout.setBackgroundColor(Color.GREEN);
                                // Delayed activity switch after a successful connection
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startConnection(endpoint);
                                    }
                                }, 500);
                            }

                        } catch (JSONException e) {
                            Log.d("EXCEPTION", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", Objects.requireNonNull(error.getMessage()));
                // Return to original state of UI before attempting to reconnect
                setOnClick();
            }
        });

        // Turning off connect information caching
        request.setShouldCache(false);
        // Overrides default behaviour of connection timeout
        request.setRetryPolicy(new RetryPolicy() {
            // Specifies connection time
            @Override
            public int getCurrentTimeout() {
                return 2500;
            }
            // Specifies how many tries should be attempted after an unsuccessful connection
            @Override
            public int getCurrentRetryCount() {
                return 0;
            }
            // Fires after an unsuccessful connection
            @Override
            public void retry(VolleyError error) throws VolleyError {
                request.setRetryPolicy(new DefaultRetryPolicy(
                        getCurrentTimeout(),
                        getCurrentRetryCount(),
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
                // Return to original state of UI before attempting to reconnect
                setOnClick();
            }
        });
        // Adds the specified request to queue
        queue.add(request);
    }

    // --------------------------------------
    // PRIVATE METHODS
    // --------------------------------------

    // Sets the connect button back to normal
    private void setOnClick() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Connection error")
                        .setMessage("No response")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();

                progressBar.setVisibility(View.INVISIBLE);
                connect_text.setText("Connect");
                connect_cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        connect_text.setText(R.string.connecting);
                        connect_cardView.setOnClickListener(null);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                createSession(v);
                            }
                        }, 1000);
                    }
                });
            }
        });
    }

    // This method turns off keyboard when pressed anywhere
    // It is not a default function on Android
    private void dropKeyboardFocusOnPress() {
        FrameLayout background = findViewById(R.id.background_main);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ip.isFocused() || port.isFocused()) {
                        Rect outRect = new Rect();
                        ip.getGlobalVisibleRect(outRect);
                        port.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            ip.clearFocus();
                            port.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });
    }

    private void startConnection(final String connection_string) {
        StringRequest botNameRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getCurrentBotName/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String bot_name = response;

                        final StringRequest userNameRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getCurrentUserName/",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String user_name = response;

                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        intent.putExtra("bot_name", bot_name);
                                        intent.putExtra("user_name", user_name);
                                        intent.putExtra("connection_string", connection_string);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR", error.getMessage());
                            }
                        });

                        queue.add(userNameRequest);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
            }
        });

        queue.add(botNameRequest);
    }

}
