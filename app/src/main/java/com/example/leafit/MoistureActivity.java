package com.example.leafit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoistureActivity extends AppCompatActivity {

    private Button btnWater;
    private CheckBox checkboxOne, checkboxTwo;
    private GraphView plotOne, plotTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);
        String serverURL = getIntent().getStringExtra("serverURL");
        initViews();
        plotGraphs(serverURL, plotOne, plotTwo);

        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MoistureActivity.this, "Water command pushed", Toast.LENGTH_SHORT).show();
                String api_URL = serverURL + "/activate-water?client=android";

                RequestQueue queue = Volley.newRequestQueue(MoistureActivity.this);

                JSONObject body = new JSONObject();
                try{
                    body.put("_id", 0);
                    body.put("shouldWater1", checkboxOne.isChecked());
                    body.put("shouldWater2", checkboxTwo.isChecked());
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, api_URL, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MoistureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        //emptyPlotTxt.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MoistureActivity.this, "POST request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "POST failed");
                    }
                });
                queue.add(request);
            }
        });
    }

    private void plotGraphs(String serverURL, GraphView plotOne, GraphView plotTwo) {
        String api_URL = serverURL + "/moisture-data";
        RequestQueue queue = Volley.newRequestQueue(MoistureActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(MoistureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                //TODO: Loop and Get from response "time" and "moisture" array for each pot.
                for(int i = 1; i < response.length()+1; i++) {
                    Object timestampList = new ArrayList<>();
                    Object moistureList = new ArrayList<>();
                    try {
                        timestampList = response.getJSONObject("pot"+i).get("time");
                        moistureList = response.getJSONObject("pot"+i).get("moisture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MoistureActivity.this, timestampList.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MoistureActivity.this, "GET request failed", Toast.LENGTH_SHORT).show();
                Log.e("Error", "GET failed");
            }
        });
        queue.add(request);
    }

    private void initViews() {
        btnWater = findViewById(R.id.waterBtn);
        checkboxOne = findViewById(R.id.checkboxOne);
        checkboxTwo = findViewById(R.id.checkboxTwo);
        plotOne = findViewById(R.id.plotOne);
        plotTwo = findViewById(R.id.plotTwo);
    }
}