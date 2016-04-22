package com.webh.lightentheme;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  Adapter to display Launcher details is ListView
 */
public class ListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;                 // Context
    private String[] statusStrings;                 // Launcher statuses
    private final LauncherDetails[] launcherDetails;// Launcher details

    /**
    ViewHolder viewHolder;

    static class ViewHolder {
        public TextView firstText;
        public TextView secondText;
        public ImageView image;
    }
     **/

    public ListViewAdapter(Activity context, LauncherDetails[] launcherDetails, String[] statusStrings) {
        super(context, R.layout.activity_main_listview, statusStrings);
        this.context = context;
        this.statusStrings = statusStrings;
        this.launcherDetails = launcherDetails;
    }

    public void refreshEvents (String[] statusStrings){
        // Get new status strings and refesh list view
        this.statusStrings = statusStrings;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
        // ViewHolder is not displaying one of the text view hence
        // commenting out
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.activity_main_listview, null);
            // configure view holder
            viewHolder = new ViewHolder();

            viewHolder.firstText = (TextView) rowView.findViewById(R.id.firstLine);
            viewHolder.secondText = (TextView) rowView.findViewById(R.id.secondLine);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);

            rowView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.firstText.setText(primaryTexts[position]);
        viewHolder.secondText.setText(secondaryTexts[position]);

        Resources res = parent.getResources();
        int resID = res.getIdentifier(drawables[position], "drawable", this_icon.getContext().getPackageName());
        viewHolder.image.setImageResource(resID);

         **/

        // Inflate list row layout
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_main_listview, parent, false);

        TextView firstTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondTextView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        // Set row data
        firstTextView.setText(launcherDetails[position].name);
        secondTextView.setText(statusStrings[position]);
        Resources res = parent.getResources();
        int resID = res.getIdentifier(launcherDetails[position].drawable, "drawable", this.getContext().getPackageName());
        imageView.setImageResource(resID);

        return rowView;
    }
}
