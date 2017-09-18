package com.innovator.kp.innovatorkp.model;

import java.lang.reflect.Array;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by dankli on 2017-09-08.
 */

public abstract class SurveyQuestion {
    protected String header = null;
    protected String body = null;

    public String getHeader() { return header; }
    public String getBody() { return body; }

    public abstract double getResult();
}
