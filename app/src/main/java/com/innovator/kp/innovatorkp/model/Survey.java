package com.innovator.kp.innovatorkp.model;

import java.util.ArrayList;

/**
 * Created by dankli on 2017-09-08.
 */

public class Survey {
    private int id = -1;
    private String name = null;
    private String description = null;
    private final ArrayList<RangeSurveyQuestion> questions = new ArrayList<>();


    public ArrayList<RangeSurveyQuestion> getQuestions() { return questions; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getId() { return id; }
}
