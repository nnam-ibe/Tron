package show.com.tron;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdapterShow extends RecyclerView.Adapter<AdapterShow.ShowViewHolder> {

    private static Context context;
    List<Integer> colorList;
    private List<Show> showList;
    private TronApplication tron;
    private static boolean canRemoveData = true;
    private RecyclerView recyclerView;
    private Show tempShow; //Show to be deleted.
    private String FRAGMENT_TAG;

    public AdapterShow(List<Show> list, TronApplication tron, RecyclerView recyclerView, String FRAGMENT_TAG) {
        this.showList = list;
        this.tron = tron;
        this.recyclerView = recyclerView;
        this.FRAGMENT_TAG = FRAGMENT_TAG;
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_show, parent, false);
        context = parent.getContext();
        setUpColors(context);
        return new ShowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder holder, final int position) {
        final Show ci = showList.get(position);
        holder.title.setText(ci.getName());
        int cardColor = nextColor();
        holder.title.setBackgroundColor(cardColor);
        holder.line.setBackgroundColor(cardColor);
        holder.season.setText(ci.getSeasonEpisode());
        holder.weekday.setText(ci.getWeekDay().toString());
        holder.lastUpdated.setText(ci.getLastUpdated());
        if (ci.getWeekDay() == Day.OFFAIR) {
            holder.weekday.setTextColor(Color.GRAY);
        } else {
            holder.weekday.setTextColor(Color.BLUE);
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.season.setText(ci.nextEpisode());
                ci.setLastUpdated(Calendar.getInstance().getTime());
                holder.lastUpdated.setText(ci.getLastUpdated());
                tron.updateShow(FRAGMENT_TAG, ci);
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.season.setText(ci.prevEpisode());
                ci.setLastUpdated(Calendar.getInstance().getTime());
                holder.lastUpdated.setText(ci.getLastUpdated());
                tron.updateShow(FRAGMENT_TAG,ci);
            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertView = inflater.inflate(R.layout.show_dialog_layout, null);
                AlertDialog alertDialog = alertDialogBuilder(position, alertView);
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.accent));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.accent));
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.accent));
            }
        });
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Show> results = new ArrayList<>();
                clearShowList(tron.getShowList());
                if (constraint != null) {
                        if (showList != null && showList.size() > 0) {
                            for (final Show show : showList) {
                                if (show.getName().toLowerCase()
                                        .contains(constraint.toString()))
                                    results.add(show);
                            }
                        }
                        oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                clearShowList((List)results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    private void setUpColors(Context context) {
        colorList = new ArrayList<>();
        colorList.add(context.getResources().getColor(R.color.colorCard));
    }

    public int nextColor() {
        return colorList.get(0);
    }

    //Helper method to build an alertDialog.
    private AlertDialog alertDialogBuilder(final int position, final View v) {
        final Show show = showList.get(position);
        TextView showField = (TextView) v.findViewById(R.id.dialog_show_name);
        showField.setText(show.getName());
        showField = (TextView) v.findViewById(R.id.dialog_show_season);
        showField.setText(show.getDialogShowSeason());
        showField = (TextView) v.findViewById(R.id.dialog_show_no_episodes);
        showField.setText(show.getDialogNoEpisodes());
        showField = (TextView) v.findViewById(R.id.dialog_show_day);
        showField.setText(show.getWeekDay().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v)
                .setNegativeButton(R.string.show_dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNeutralButton(context.getString(R.string.show_dialog_delete_string),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                canRemoveData = true;
                                deleteShow(position);
                                showSnackBar(position);
                            }
                        })
                .setPositiveButton(R.string.show_dialog_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, EditShowActivity.class);
                        intent.putExtra("ID", show.getId());
                        context.startActivity(intent);
                    }
                });

        return builder.create();
    }

    public static class ShowViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView season;
        public TextView weekday;
        public TextView plus;
        public TextView minus;
        public TextView lastUpdated;
        public View v;
        public View line;

        public ShowViewHolder(View v) {
            super(v);
            this.v = v;
            title = (TextView) v.findViewById(R.id.show_title);
            season = (TextView) v.findViewById(R.id.show_S);
            weekday = (TextView) v.findViewById(R.id.show_weekday);
            plus = (TextView) v.findViewById(R.id.show_plus);
            minus = (TextView) v.findViewById(R.id.show_minus);
            line = v.findViewById(R.id.feedLine);
            lastUpdated = (TextView)v.findViewById(R.id.show_last_updated_actual);
        }
    }

    private void showSnackBar(final int pos) {
        Snackbar snackbar = Snackbar.make(recyclerView, "Show removed", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v){
                canRemoveData = false;
                showList.add(pos, tempShow);
                tron.putBack(FRAGMENT_TAG);
                notifyDataSetChanged();
            }
        });
        snackbar.show();
    }

    private void deleteShow(final int position){
        final Show show = showList.get(position);
        this.tempShow = show;
        showList.remove(position);
        notifyDataSetChanged();
        tron.removeShow(FRAGMENT_TAG, show.getId());
        new Thread() {
            public void run() {
                try {
                    sleep(3500);
                    if ( canRemoveData ) {
                        boolean result = tron.deleteShow(FRAGMENT_TAG,show.getId());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void clearShowList(List<Show> list) {
        showList.clear();
        showList.addAll(list);
    }

}
