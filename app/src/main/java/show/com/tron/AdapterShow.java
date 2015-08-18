package show.com.tron;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdapterShow extends RecyclerView.Adapter<AdapterShow.ShowViewHolder> {

    private static Context context;
    List<Integer> colorList;
    Random random;
    private List<Show> showList;
    private TronApplication tron;

    public AdapterShow(List<Show> list, TronApplication tron) {
        this.showList = list;
        this.tron = tron;
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
        if (ci.getWeekDay() == Day.OFFAIR) {
            holder.weekday.setTextColor(Color.GRAY);
        } else {
            holder.weekday.setTextColor(Color.BLUE);
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.season.setText(ci.nextEpisode());
                tron.updateShow(ci);
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.season.setText(ci.prevEpisode());
                tron.updateShow(ci);
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
            }
        });
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Show> results = new ArrayList<>();
                final List <Show> search = tron.getShowList();
                if (constraint != null) {
                        if (search != null && search.size() > 0) {
                            for (final Show show : search) {
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
                showList = (ArrayList<Show>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Method used to get the item in a specific position of the adapter.
     * @param position positon of the item to retrieve.
     * @return Show object, returns null if IndexOutOfBounds.
     */
    public Show getShow(int position) {
        try {
            return showList.get(position);
        } catch (Exception e) {
            Log.e("AdapterShow", "Couldn't retrieve show, ShowList size " + showList.size() + " : position " + position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    private void setUpColors(Context context) {
        random = new Random();
        colorList = new ArrayList<>();
        colorList.add(context.getResources().getColor(R.color.colorAmber));
        colorList.add(context.getResources().getColor(R.color.colorGreen));
        colorList.add(context.getResources().getColor(R.color.colorDeepPurple));
        colorList.add(context.getResources().getColor(R.color.colorPurple));
        colorList.add(context.getResources().getColor(R.color.colorTeal));
    }

    public int nextColor() {
        return colorList.get(random.nextInt(colorList.size()));
    }

    //Helper method to build an alertDialog.
    private AlertDialog alertDialogBuilder(final int position, View v) {
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
                .setPositiveButton(R.string.show_dialog_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, EditShow.class);
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
        }
    }
}
