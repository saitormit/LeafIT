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

    private Button addBtn, moistureBtn;
    private Spinner plantSpinner, stageSpinner, potSpinner;
    private TextView textView;
    //private RelativeLayout parent; //Might not be used

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSpinners();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Plant configurations sent", Toast.LENGTH_SHORT).show();
                //String url = "https://37689a43-49d4-42ce-8915-09642ab4daf2.mock.pstmn.io/mocks";
                String url = "https://5ffe-172-58-184-50.ngrok.io/pots-config";
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                JSONObject body = new JSONObject();
                try{
                    body.put("Plant", plantSpinner.getSelectedItem().toString());
                    body.put("Stage", stageSpinner.getSelectedItem().toString());
                    body.put("Pot Number", potSpinner.getSelectedItem().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "POST request successful", Toast.LENGTH_SHORT).show();
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
                /*StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "POST request successful", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "POST request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "POST failed");
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("Plant", plantSpinner.getSelectedItem().toString());
                        params.put("Stage", stageSpinner.getSelectedItem().toString());
                        params.put("Pot Number", potSpinner.getSelectedItem().toString());
                        return params;
                    }
                };*/
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
    }

    private void initSpinners() {
        ArrayList<String> plants = new ArrayList<>();
        plants.add("Basil");
        plants.add("Mint");
        plants.add("Tomato");
        plants.add("Flower");

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
        plantSpinner = findViewById(R.id.plantSpinner);
        stageSpinner = findViewById(R.id.stageSpinner);
        potSpinner = findViewById(R.id.potSpinner);
        textView = findViewById(R.id.apitxt);
        //parent = findViewById(R.id.parent); //Might not be used
    }
}