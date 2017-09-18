package com.innovator.kp.innovatorkp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.innovator.kp.innovatorkp.model.Survey;

import java.util.ArrayList;

/**
 * Created by dankli on 2017-09-12.
 */

public class WorkItemListAdapter extends ArrayAdapter<Survey> {
    public WorkItemListAdapter(Context context, ArrayList<Survey> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Survey workItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.work_item, parent, false);
        }

        TextView tvWorkItemTitle = (TextView) convertView.findViewById(R.id.tvWorkItemTitle);
        tvWorkItemTitle.setText(workItem.getName());

        TextView tvWorkItemDescription = (TextView) convertView.findViewById(R.id.tvWorkItemDescription);
        tvWorkItemDescription.setText(workItem.getDescription());

        return convertView;
    }
}
