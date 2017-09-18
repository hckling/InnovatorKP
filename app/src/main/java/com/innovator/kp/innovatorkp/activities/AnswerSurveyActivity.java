package com.innovator.kp.innovatorkp.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.innovator.kp.innovatorkp.OptionalSwipeViewPager;
import com.innovator.kp.innovatorkp.R;
import com.innovator.kp.innovatorkp.model.RangeSurveyQuestion;
import com.innovator.kp.innovatorkp.model.Survey;
import com.innovator.kp.innovatorkp.model.SurveyQuestion;

import java.util.ArrayList;
import java.util.List;

public class AnswerSurveyActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    final String ENDPOINT = "http://172.21.104.27:57656/demo/getsurvey/";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private long surveyId = -1;
    private RequestQueue requestQueue;
    private Survey survey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (getIntent().hasExtra("surveyId")) {
            surveyId = getIntent().getExtras().getLong("surveyId");
            getSurvey();
        }

        OptionalSwipeViewPager osvp = (OptionalSwipeViewPager) findViewById(R.id.container);
        osvp.setSwipeEnabled(false);

        final Button btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canScrollForward()) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    // TODO: Also save the answer here
                }
            }
        });

        final Button btnPrev = (Button) findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canScrollBackward()) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            }
        });
    }

    private boolean canScrollBackward() {
        return mViewPager.getCurrentItem() > 0;
    }

    private boolean canScrollForward() {
        return mViewPager.getCurrentItem() < survey.getQuestions().size() + 1;
    }

    public void getSurvey() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT + Long.toString(surveyId), onSurveyReceived, onSurveyRequestError);
        requestQueue.add(request);
    }

    private Response.Listener<String> onSurveyReceived = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i("AnswerSurveyActivity", "Survey received!");

            survey = parseJsonSurvey(response);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(survey.getName());

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), survey);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            final Button btnNext = (Button) findViewById(R.id.btnNext);
            final Button btnPrev = (Button) findViewById(R.id.btnPrev);

            btnNext.setVisibility(canScrollForward() ? View.VISIBLE : View.INVISIBLE);
            btnPrev.setVisibility(canScrollBackward() ? View.VISIBLE : View.INVISIBLE);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    btnNext.setVisibility(canScrollForward() ? View.VISIBLE : View.INVISIBLE);
                    btnPrev.setVisibility(canScrollBackward() ? View.VISIBLE : View.INVISIBLE);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    };

    private Survey parseJsonSurvey(String response) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        return gson.fromJson(response, Survey.class);
    }

    private Response.ErrorListener onSurveyRequestError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("AnswerSurveyActivity", "Error retrieving survey...");
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class AnswerRangeQuestionFragment extends Fragment {
        private RangeSurveyQuestion question;
        private TextView tvQuestionHeader;
        private TextView tvQuestionBody;
        private TextView tvRangeLabelDescription;
        private SeekBar sbValueSelection;

        public void setQuestion(RangeSurveyQuestion question) {
            this.question = question;
        }

        public static AnswerRangeQuestionFragment newInstance(int sectionNumber) {
            AnswerRangeQuestionFragment fragment = new AnswerRangeQuestionFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // TODO: Create different views depending on question type or if the page number is the
            // results page

            View rootView = inflater.inflate(R.layout.fragment_answer_survey, container, false);



            tvQuestionHeader = (TextView) rootView.findViewById(R.id.tvQuestionHeader);
            tvQuestionBody = (TextView) rootView.findViewById(R.id.tvQuestionBody);
            tvRangeLabelDescription = (TextView) rootView.findViewById(R.id.tvRangeLabelDescription);
            sbValueSelection = (SeekBar) rootView.findViewById(R.id.sbValueSelection);

            tvQuestionHeader.setText(question.getHeader());
            tvQuestionBody.setText(question.getBody());
            tvRangeLabelDescription.setText(question.getRangeLabel(Math.max(question.getAnswer(), question.getMinValue())));

            sbValueSelection.setMax(question.getMaxValue() - question.getMinValue());
            sbValueSelection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // TODO: Update the value description label
                    tvRangeLabelDescription.setText(question.getRangeLabel(progress + question.getMinValue()));
                    question.setAnswer(progress + question.getMinValue());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return rootView;
        }
   }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Survey survey = null;

        public SectionsPagerAdapter(FragmentManager fm, Survey survey) {
            super(fm);
            this.survey = survey;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Log.i("SectionsPagerAdapter", "Getting position: " + position);
            AnswerRangeQuestionFragment fragment = AnswerRangeQuestionFragment.newInstance(position);
            fragment.setQuestion(survey.getQuestions().get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return survey.getQuestions().size(); // TODO: + 1 for result
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < survey.getQuestions().size() + 1) {
                return survey.getQuestions().get(position).getHeader();
            } else {
                return "Results";
            }
        }
    }
}
