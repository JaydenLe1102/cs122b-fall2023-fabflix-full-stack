package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.main_page.MainPageActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to logging");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;


        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                NetworkManager.baseURL + "/api/login",
                response -> {
                    Log.d("login.success", response);
                    //Complete and destroy login activity once successful

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");

                        if (success.equals("true")) {
                            finish();
                            Intent mainPageIntent = new Intent(LoginActivity.this, MainPageActivity.class);
                            startActivity(mainPageIntent);
                        } else {
                            String reason = jsonObject.optString("reason", ""); // Get the "reason" field from JSON

                            switch (reason) {
                                case "email":
                                    message.setText("Email does not exist");
                                    break;
                                case "password":
                                    message.setText("Password is incorrect");
                                    break;
                                case "already":
                                    message.setText("You have already logged in");
                                    break;
                                default:
                                    message.setText("Login Failed");
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());

                    message.setText("Failed to logged in");
        }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("email", username.getText().toString());
                params.put("password", password.getText().toString());
                params.put("isAndroid", String.valueOf(true));
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}