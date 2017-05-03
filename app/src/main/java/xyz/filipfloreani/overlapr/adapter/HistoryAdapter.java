package xyz.filipfloreani.overlapr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;

/**
 * Created by filipfloreani on 04/04/2017.
 */

public class HistoryAdapter extends RealmRecyclerViewAdapter<RealmChartModel, HistoryAdapter.HistoryViewHolder> {

    private OnHistoryItemClickListener onClickListener;

    public HistoryAdapter(OrderedRealmCollection<RealmChartModel> chartModels, OnHistoryItemClickListener onClickListener) {
        super(chartModels, true);
        this.onClickListener = onClickListener;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View historyView = layoutInflater.inflate(R.layout.item_history, parent, false);
        final HistoryViewHolder viewHolder = new HistoryViewHolder(historyView);

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, viewHolder.getAdapterPosition()); // TODO Or getLayoutPosition()?
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        RealmChartModel model = getItem(position);
        if (model != null) {
            holder.titleTextView.setText(model.getTitle());
            holder.creationDateTextView.setText(model.getCreationDate().toString());
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView creationDateTextView;

        HistoryViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            creationDateTextView = (TextView) itemView.findViewById(R.id.item_date);
        }
    }
}
