package xyz.filipfloreani.overlapr.sorting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        realm = Realm.getDefaultInstance();

        cardStackView = (CardStack) findViewById(R.id.card_stack);
        cardStackView.setContentResource(R.layout.sorting_card);

        cardStackListener = new CardStackListener(DETECTION_THRESHOLD, this);
        cardStackView.setListener(cardStackListener);

        startInitLoadingTask();
        sortingStackAdapter = new SortingStackAdapter(this, R.layout.sorting_card, charts, realm);
        cardStackView.setAdapter(sortingStackAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sorting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                if (cardStackView != null) {
                    cardStackView.undo();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Queries Realm for all stored RealmChartModels through a custom AsyncTask.
     */
    private void startInitLoadingTask() {
        new ListLoadingTask(true).execute();
    }

    public void onExitTopLeft(final int chartIndex) {
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

                if (isAdapterAboutToEmpty(chartIndex - 1)) {
                    addMoreChartsToAdapter();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitTopRight(final int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex - 1);
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

                if (isAdapterAboutToEmpty(chartIndex - 1)) {
                    addMoreChartsToAdapter();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitBottomLeft(final int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex - 1);
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

                if (isAdapterAboutToEmpty(chartIndex - 1)) {
                    addMoreChartsToAdapter();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Writing failed");
                Snackbar.make(cardStackView, R.string.mark_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void onExitBottomRight(final int chartIndex) {
        final RealmChartModel chart = charts.get(chartIndex - 1);
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

                if (isAdapterAboutToEmpty(chartIndex - 1)) {
                    addMoreChartsToAdapter();
                }
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
        Snackbar.make(cardStackView, stringResource, Snackbar.LENGTH_SHORT)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardStackView.undo();
                    }
                })
                .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                .show();
    }

    private boolean isAdapterAboutToEmpty(int currentIndex) {
        return currentIndex >= sortingStackAdapter.getCount() - 4;
    }

    private void addMoreChartsToAdapter() {
        Log.d(TAG, "Loading more charts...");
        new ListLoadingTask(false).execute();
    }

    private class ListLoadingTask extends AsyncTask<Void, Void, List<RealmChartModel>> {

        private ProgressDialog progressDialog;
        private boolean showProgressDialog;

        ListLoadingTask(boolean showProgressDialog) {
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
                int subIndex = Math.min(realmCharts.size(), 7);
                realmCharts = realmCharts.subList(0, subIndex);

                List<RealmChartModel> charts = asyncRealm.copyFromRealm(realmCharts, 2);
                Log.d(TAG, "Conversion complete");

                return charts;
            }
        }

        @Override
        protected void onPostExecute(List<RealmChartModel> chartModels) {
            for (RealmChartModel model : chartModels) {
                addChartIfNotExist(model);
            }

            sortingStackAdapter.notifyDataSetChanged();

            if (showProgressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        private void addChartIfNotExist(RealmChartModel model) {
            if (charts.contains(model)) return;

            charts.add(model);
        }
    }
}
