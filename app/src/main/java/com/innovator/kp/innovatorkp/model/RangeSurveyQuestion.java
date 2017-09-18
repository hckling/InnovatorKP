package com.innovator.kp.innovatorkp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by dankli on 2017-09-08.
 */

public class RangeSurveyQuestion extends SurveyQuestion {
    private Integer min = 0;
    private Integer max = 0;
    private Integer answer = null;
    private Integer targetValue = 0;
    private ArrayList<RangeLabel> rangeLabels = new ArrayList<>();
    private String[] categories = new String[] {};

    public int getMinValue() { return min; }
    public int getMaxValue() { return max; }

    public int getAnswer() {
        if (answer == null)
            return 0;
        else
            return answer;
    }

    public boolean hasAnswer  () {
        return answer != null;
    }

    public void setAnswer(int newAnswer) {
        answer = newAnswer;
    }

    public int getTargetValue() { return targetValue; }

    public double getAnswerDevianceFromTarget() {
        return getAnswer() - targetValue;
    }

    public void setTarget(int newTargetValue) {
        targetValue = newTargetValue;
    }

    public String getRangeLabel(int value) {
        Collections.sort(rangeLabels, new Comparator<RangeLabel>() {
            @Override
            public int compare(RangeLabel o1, RangeLabel o2) {
                return o1.getValue() - o2.getValue();
            }
        });

        for (int i = rangeLabels.size() - 1; i >= 0; i--){
            if (value >= rangeLabels.get(i).getValue()) {
                return rangeLabels.get(i).getLabel();
            }
        }

        return "";
    }

    @Override
    public double getResult() {
        if (max != min) {
            return ((answer - min) - (targetValue - min)) / (max - min);
        } else {
            return 0;
        }
    }
}
