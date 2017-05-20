package xyz.filipfloreani.overlapr.sorting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.wenchao.cardstack.CardStack;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;

public class SortingActivity extends AppCompatActivity {

    private static final String TAG = "SortingActivity";
    private static final int DETECTION_THRESHOLD = 300;

    private Realm realm;
    private List<RealmChartModel> charts = new ArrayList<>();

    private CardStack cardStackView;
    private SortingStackAdapter sortingStackAdapter;
    private CardStackListener cardStackListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);

        realm = Realm.getDefaultInstance();

        cardStackView = (CardStack) findViewById(R.id.card_stack);
        cardStackView.setContentResource(R.layout.sorting_card);

        cardStackListener = new CardStackListener(DETECTION_THRESHOLD, this);
        cardStackView.setListener(cardStackListener);

        startLoadingTask();
        sortingStackAdapter = new SortingStackAdapter(this, R.layout.sorting_card, charts, realm);
        cardStackView.setAdapter(sortingStackAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Queries Realm for all stored RealmChartModels through a custom AsyncTask.
     */
    private void startLoadingTask() {
        new listLoadingTask(true).execute();
    }

    public void onExitTopLeft(int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex - 1);
        chart.setSortingOption(SortingOptions.REPEAT);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmTrans) {
                Log.d(TAG, "Writing sort to Realm...");
                realmTrans.copyToRealmOrUpdate(chart);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Writing complete");
                makeUndoSnackbar(R.string.mark_repeat);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitTopRight(int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex);
        chart.setSortingOption(SortingOptions.CHIMERIC);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmTrans) {
                Log.d(TAG, "Writing sort to Realm...");
                realmTrans.copyToRealmOrUpdate(chart);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Writing complete");
                makeUndoSnackbar(R.string.mark_chimeric);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitBottomLeft(int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex);
        chart.setSortingOption(SortingOptions.REGULAR);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmTrans) {
                Log.d(TAG, "Writing sort to Realm...");
                realmTrans.copyToRealmOrUpdate(chart);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Writing complete");
                makeUndoSnackbar(R.string.mark_regular);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitBottomRight(int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex);
        chart.setSortingOption(SortingOptions.LOW_QUALITY);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmTrans) {
                Log.d(TAG, "Writing sort to Realm...");
                realmTrans.copyToRealmOrUpdate(chart);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Writing complete");
                makeUndoSnackbar(R.string.mark_low_quality);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void makeUndoSnackbar(int stringResource) {
        Snackbar.make(cardStackView, stringResource, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardStackView.undo();
                    }
                })
                .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                .show();
    }

    private class listLoadingTask extends AsyncTask<Void, Void, List<RealmChartModel>> {

        private ProgressDialog progressDialog;
        private boolean showProgressDialog;

        listLoadingTask(boolean showProgressDialog) {
            progressDialog = new ProgressDialog(SortingActivity.this);
            this.showProgressDialog = showProgressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (showProgressDialog) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.create_cards_dialog));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected List<RealmChartModel> doInBackground(Void... params) {

            try (Realm asyncRealm = Realm.getDefaultInstance()) {
                List<RealmChartModel> realmCharts = asyncRealm.where(RealmChartModel.class).equalTo("sortingOption", 4).findAll();

                Log.d(TAG, "Converting to real list...");
                int subIndex = Math.min(realmCharts.size(), 5);
                realmCharts = realmCharts.subList(0, subIndex);

                List<RealmChartModel> charts = asyncRealm.copyFromRealm(realmCharts, 1);
                Log.d(TAG, "Conversion complete");

                return charts;
            }
        }

        @Override
        protected void onPostExecute(List<RealmChartModel> realmChartModels) {
            charts.addAll(realmChartModels);

            if (showProgressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            sortingStackAdapter.notifyDataSetChanged();
        }
    }
}
