package com.example.leafit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button addBtn, moistureBtn, emptyBtn;
    private Spinner plantSpinner, stageSpinner, potSpinner;
    private TextView textView, potOneTxt, potTwoTxt;
    //private RelativeLayout parent; //Might not be used

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSpinners();

        //Server URL
        String serverURL = "https://8f68-172-58-190-153.ngrok.io";

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Plant configurations sent", Toast.LENGTH_SHORT).show();
                //String url = "https://37689a43-49d4-42ce-8915-09642ab4daf2.mock.pstmn.io/mocks";
                String api_URL = serverURL + "/pots-config";

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                JSONObject body = new JSONObject();
                try{
                    body.put("Plant", plantSpinner.getSelectedItem().toString());
                    body.put("Stage", stageSpinner.getSelectedItem().toString());
                    body.put("Pot Number", potSpinner.getSelectedItem()); //Given as integer
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, api_URL, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(MainActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                            if (body.getInt("Pot Number") == 1){
                                potOneTxt.setText(String.format("%s in Pot 1", body.getString("Plant")));
                            }else if (body.getInt("Pot Number") == 2){
                                potTwoTxt.setText(String.format("%s in Pot 2", body.getString("Plant")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        textView.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "POST request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "POST failed");
                    }
                });
                queue.add(request);
            }
        });

        moistureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Analyze moisture level", Toast.LENGTH_SHORT).show();
// ...
// Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "https://7c5f2e13-b665-4ccb-97fc-70e59a216686.mock.pstmn.io/get";
// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                textView.setText("Response is: " + response.substring(0,500));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText("That didn't work!");
                    }
                });
// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
        //TODO: Add Empty a Pot button here
        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String api_URL = serverURL + "/pots-config";

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                JSONObject empty_pot = new JSONObject();
                try{
                    empty_pot.put("Pot Number", potSpinner.getSelectedItem()); //Given as integer
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, api_URL, empty_pot, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(MainActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        textView.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "DELETE request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "DELETE failed");
                    }
                });
                queue.add(request);
            }
        });
    }

    private void initSpinners() {
        ArrayList<String> plants = new ArrayList<>();
        plants.add("Basil");
        plants.add("Blueberry");
        plants.add("Cactus");
        plants.add("Cranberry");
        plants.add("Green Onion");
        plants.add("Lavender");
        plants.add("Mario's Flower");
        plants.add("Mint");
        plants.add("Tomato");
        plants.add("Tulip");

        ArrayList<String> stages = new ArrayList<>();
        stages.add("Seed");
        stages.add("Young");
        stages.add("Old");

        ArrayList<Integer> pot = new ArrayList<>();
        pot.add(1);
        pot.add(2);

        ArrayAdapter<String> plantsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, plants
        );
        ArrayAdapter<String> stagesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, stages
        );
        ArrayAdapter<Integer> potAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, pot
        );

        plantSpinner.setAdapter(plantsAdapter);
        stageSpinner.setAdapter(stagesAdapter);
        potSpinner.setAdapter(potAdapter);
    }

    private void initViews() {
        addBtn = findViewById(R.id.addBtn);
        moistureBtn = findViewById(R.id.moistureBtn);
        emptyBtn = findViewById(R.id.emptyBtn);
        plantSpinner = findViewById(R.id.plantSpinner);
        stageSpinner = findViewById(R.id.stageSpinner);
        potSpinner = findViewById(R.id.potSpinner);
        textView = findViewById(R.id.apiTxt);
        potOneTxt = findViewById(R.id.potOneTxt);
        potTwoTxt = findViewById(R.id.potTwoTxt);
        //parent = findViewById(R.id.parent); //Might not be used
    }
}