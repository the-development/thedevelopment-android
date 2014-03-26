package com.jmartin.thedevelopment.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmartin.thedevelopment.android.R;
import com.jmartin.thedevelopment.android.model.Interview;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jeff on 2014-03-25.
 */
public class InterviewListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Interview> data;
    private ArrayList<Interview> filteredData;

    public InterviewListAdapter(Context context, ArrayList<Interview> interviewArrayList) {
        this.context = context;
        this.data = interviewArrayList;
        this.filteredData = interviewArrayList;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Interview getItem(int i) {
        return filteredData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = inflater.inflate(R.layout.interview_list_item, viewGroup, false);

        ImageView dpImageView = (ImageView) listItemView.findViewById(R.id.dp);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        TextView positionTextView = (TextView) listItemView.findViewById(R.id.position);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);

        Interview interview = getItem(i);

        Picasso.with(context).load(interview.getDpImage()).fit().centerCrop().into(dpImageView);
        nameTextView.setText(interview.getName());
        positionTextView.setText(interview.getPosition());
        dateTextView.setText(interview.getPublishedDate());

        if (interview.isRead()) {
            dpImageView.setImageAlpha(150);
            nameTextView.setTextColor(context.getResources().getColor(R.color.light_grey));
            positionTextView.setTextColor(context.getResources().getColor(R.color.light_grey));
            dateTextView.setTextColor(context.getResources().getColor(R.color.light_grey));
        }

        return listItemView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults searchResults = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    searchResults.values = data;
                    searchResults.count = data.size();
                } else {
                    ArrayList<Interview> searchResultsData = new ArrayList<Interview>();

                    for (Interview interview : data) {
                        if (interview.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchResultsData.add(interview);
                        }
                    }

                    searchResults.values = searchResultsData;
                    searchResults.count = searchResultsData.size();
                }

                return searchResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<Interview>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public int indexOfItem(String item) {
        for (int i = 0; i < this.filteredData.size(); i++) {
            if (this.filteredData.get(i).getName().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return -1;
    }
}
