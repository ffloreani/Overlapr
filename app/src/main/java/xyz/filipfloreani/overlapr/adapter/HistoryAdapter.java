package xyz.filipfloreani.overlapr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.LineChartModel;

/**
 * Created by filipfloreani on 04/04/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<LineChartModel> chartModels;

    public HistoryAdapter(List<LineChartModel> chartModels) {
        this.chartModels = chartModels;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View historyView = layoutInflater.inflate(R.layout.item_history, parent);

        return new HistoryViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        LineChartModel model = chartModels.get(position);

        holder.titleTextView.setText(model.getTitle());
        holder.creationDateTextView.setText(model.getCreationDate().toString());
    }

    @Override
    public int getItemCount() {
        return chartModels.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView creationDateTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            creationDateTextView = (TextView) itemView.findViewById(R.id.item_date);
        }
    }
}
