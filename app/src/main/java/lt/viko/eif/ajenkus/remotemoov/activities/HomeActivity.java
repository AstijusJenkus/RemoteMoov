package lt.viko.eif.ajenkus.remotemoov.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import lt.viko.eif.ajenkus.remotemoov.R;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private String connection_string;
    private String bot_name;
    private String user_name;
    private RequestQueue queue;
    private EditText chat_bot;
    private BottomNavigationView bottom_nav;

    private boolean kinectOn = false;
    private boolean isConnected = true;

    // ----------------------------------------
    // OVERRIDES
    // ----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialization
        queue = Volley.newRequestQueue(HomeActivity.this);

        Intent intent = getIntent();
        bot_name = intent.getStringExtra("bot_name").replaceAll("^\"|\"$", "");
        user_name = intent.getStringExtra("user_name").replaceAll("^\"|\"$", "");
        connection_string = intent.getStringExtra("connection_string");

        TextView bot_textView = findViewById(R.id.bot_name);
        TextView user_textView = findViewById(R.id.user_name);
        bottom_nav = findViewById(R.id.bottom_nav);
        chat_bot = findViewById(R.id.chat_bot);

        bot_textView.setText("Bot: " + bot_name.substring(0, 1).toUpperCase() + bot_name.substring(1));
        user_textView.setText("User: " + user_name.substring(0, 1).toUpperCase() + user_name.substring(1));
        bottom_nav.setSelectedItemId(R.id.nav_home);

        // Bottom navigation click listener
        bottom_nav.setOnNavigationItemSelectedListener(this);

        // Framelayout drop focus
        dropKeyboardFocusOnPress();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkConnection();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                bottom_nav.setSelectedItemId(R.id.nav_home);
                break;
            case VOICE_RECOGNITION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sendChatbot(matches);
                }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_connect: {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.nav_controls: {
                Intent intent = new Intent(HomeActivity.this, ControlActivity.class);
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

    public void onSend(View view) {
        String request = chat_bot.getText().toString();
        request = request.replace(" ", "$");
        chat_bot.setText("");
        chat_bot.clearFocus();
        hideKeyboard(chat_bot);

        StringRequest sendRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getResponse/" + request,
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

    public void onRest(View view) {

        StringRequest sendRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getResponse/rest",
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


    /**
     * Turns Kinect tracking on or off depending on its previous state
     */
    public void setKinect(View view) {
        // Request declaration
        StringRequest request;
        if (kinectOn) {
            // Sets kinect state to off
            kinectOn = false;
            // Builds request url endpoint
            String url = connection_string + "chatBot/getResponse/offkinect";
            request = new StringRequest(
                    Request.Method.GET,
                    url,
                    // Response lambda expression
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
        } else {
            // Sets kinect state to on
            kinectOn = true;
            // Builds request url endpoint
            String url = connection_string + "chatBot/getResponse/startkinect";
            request = new StringRequest(
                    Request.Method.GET,
                    url,
                    // Response lambda expression
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startTrackingSkeleton();
                                }
                            }, 2000);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR", error.getMessage());
                }
            });
        }

        queue.add(request);
    }

    public void onVoice(View view) {
        startVoiceRecognitionActivity();
    }

    // ----------------------------------------
    // PRIVATE METHODS
    // ----------------------------------------

    private void startTrackingSkeleton() {
        StringRequest sendRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getResponse/trackingskeleton",
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

        queue.add(sendRequest);
    }

    private void dropKeyboardFocusOnPress() {
        FrameLayout background = findViewById(R.id.background_home);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chat_bot.isFocused()) {
                        Rect outRect = new Rect();
                        chat_bot.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            chat_bot.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });
    }

    private void checkConnection() {
        while (isConnected) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, connection_string + "chatBot/getMetaData/", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //FIXME: response not needed ?
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isConnected) {
                        isConnected = false;
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });

            queue.add(stringRequest);
        }
    }


    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Opens Google's voice recognition UI
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition ready");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    private void sendChatbot(ArrayList<String> matches) {

        String command = matches.get(0).replace(" ", "$");

        StringRequest sendRequest = new StringRequest(Request.Method.GET, connection_string + "chatBot/getResponse/" + command,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO: response not needed ?
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
            }
        });

        queue.add(sendRequest);
    }
}
