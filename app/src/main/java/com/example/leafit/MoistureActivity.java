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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class MoistureActivity extends AppCompatActivity {

    private Button btnWater;
    private CheckBox checkboxOne, checkboxTwo;
    private GraphView plotOne, plotTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);
        getSupportActionBar().hide();
        String serverURL = getIntent().getStringExtra("serverURL");
        initViews();
        plotGraphs(serverURL, plotOne, 1);
        plotGraphs(serverURL, plotTwo, 2);
        configureAxis(plotOne, 1);
        configureAxis(plotTwo, 2);

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
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MoistureActivity.this, "POST request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "PUT failed");
                    }
                });
                queue.add(request);
            }
        });
    }

    private void plotGraphs(String serverURL, GraphView plotView, int potNum) {
        String api_URL = serverURL + "/moisture-data" + "?pot=" + potNum;
        RequestQueue queue = Volley.newRequestQueue(MoistureActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(MoistureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                JSONArray timestampList = null;
                JSONArray moistureList = null;
                try {
                    timestampList = response.getJSONArray("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    moistureList = response.getJSONArray("moisture");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (timestampList == null) {
                    return;
                }
                //Toast.makeText(MoistureActivity.this, timestampList.toString(), Toast.LENGTH_SHORT).show();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                DataPoint[] dataPoints = new DataPoint[timestampList.length()];

                String sTimeData = null;
                Double moistureData = null;
                Date timeData = null;
                for(int index = 0; index < timestampList.length(); index++){
                    try {
                        sTimeData = timestampList.getString(index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        moistureData = moistureList.getDouble(index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        timeData = formatter.parse(sTimeData);
                        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    DataPoint point = new DataPoint(timeData, moistureData);
                    dataPoints[index] = point;
                }
                //Toast.makeText(MoistureActivity.this, timeData.toString(), Toast.LENGTH_SHORT).show();
                PointsGraphSeries<DataPoint> pSeries = new PointsGraphSeries<DataPoint>(dataPoints);
                LineGraphSeries<DataPoint> lSeries = new LineGraphSeries<>(dataPoints);
                plotView.addSeries(pSeries);
                plotView.addSeries(lSeries);
                pSeries.setSize(10); //Determines the size of the dots
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

    private void configureAxis(GraphView plotView, int potNum) {
        GridLabelRenderer gridLabel = plotView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Moisture");
        plotView.setTitle("Pot " + potNum);
        plotView.getGridLabelRenderer().setPadding(32);
        plotView.getGridLabelRenderer().setNumHorizontalLabels(5);
        //plotView.getViewport().setScalable(true);
        //plotView.getViewport().setScrollable(true);
        plotView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    return formatter.format(value);
                }
                return super.formatLabel(value, isValueX);
            }
        });
    }

    private void initViews() {
        btnWater = findViewById(R.id.waterBtn);
        checkboxOne = findViewById(R.id.checkboxOne);
        checkboxTwo = findViewById(R.id.checkboxTwo);
        plotOne = findViewById(R.id.plotOne);
        plotTwo = findViewById(R.id.plotTwo);
    }
}