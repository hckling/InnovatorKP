package com.innovator.kp.innovatorkp;

import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.innovator.kp.innovatorkp.activities.AnswerSurveyActivity;
import com.innovator.kp.innovatorkp.model.Survey;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String ENDPOINT = "http://172.21.104.27:57656/demo/getsurveys/1";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchSurveys();
    }

    private void fetchSurveys() {
        Log.i("MainActivity", "Sending request!");
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onTasksReceived, onTasksError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onTasksReceived = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i("MainActivity", "Surveys received!");
            handleJsonResponse(response);
        }
    };

    private void handleJsonResponse(String response) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        ArrayList<Survey> surveys = gson.fromJson(response, new TypeToken<List<Survey>>(){}.getType());

        for (int i = 0; i < surveys.size(); i++) {
            Log.i("MainActivity", surveys.get(i).getName());
        }

        generateListItems(surveys);
    }

    private void generateListItems(final ArrayList<Survey> surveys) {
        ListView lvWorkItems = (ListView) findViewById(R.id.lvWorkItems);

        WorkItemListAdapter adapter = new WorkItemListAdapter(this, surveys);
        lvWorkItems.setAdapter(adapter);

        lvWorkItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Survey s = surveys.get(position);

                Intent intent = new Intent(MainActivity.this, AnswerSurveyActivity.class);
                intent.putExtra("surveyId", s.getId());
                startActivity(intent);
            }
        });
    }

    private final Response.ErrorListener onTasksError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("MainActivity", "Error retrieving surveys: " + error);
        }
    };
}