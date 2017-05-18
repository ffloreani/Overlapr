package xyz.filipfloreani.overlapr.sorting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;

public class SortingActivity extends AppCompatActivity implements SwipeFlingAdapterView.onFlingListener {

    private static final String TAG = "SortingActivity";

    private Realm realm;
    private List<RealmChartModel> charts = new ArrayList<>();

    private SwipeFlingAdapterView flingContainer;
    private SortingAdapter sortingAdapter;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);

        realm = Realm.getDefaultInstance();

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipe_adapter_view);

        startLoadingTask();
        sortingAdapter = new SortingAdapter(this, charts, realm);
        flingContainer.init(this, sortingAdapter);
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

    @Override
    public void removeFirstObjectInAdapter() {
        if (charts.size() > 0) {
            charts.remove(0);
            sortingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLeftCardExit(final Object o) {
        if (o instanceof RealmChartModel) {
            final RealmChartModel chart = (RealmChartModel) o;
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
                    Snackbar.make(flingContainer, R.string.mark_repeat, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                        .show();

                    if (charts.size() < 2) {
                        onAdapterNearEmpty();
                    }
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d(TAG, "Writing failed");
                    Snackbar.make(flingContainer, R.string.mark_failed, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                        .show();
                }
            });
        }
    }

    @Override
    public void onRightCardExit(final Object o) {
        if (o instanceof RealmChartModel) {
            final RealmChartModel chart = (RealmChartModel) o;
            chart.setSortingOption(SortingOptions.CHYMERIC);

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
                    Snackbar.make(flingContainer, R.string.mark_chimeric, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                        .show();

                    if (charts.size() < 2) {
                        onAdapterNearEmpty();
                    }
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d(TAG, "Writing failed");
                    Snackbar.make(flingContainer, R.string.mark_failed, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                        .show();
                }
            });
        }
    }

    public void onAdapterNearEmpty() {
        if (realm.where(RealmChartModel.class).equalTo("sortingOption", 4).count() == 0) {
            Toast.makeText(this, R.string.all_charts_sorted, Toast.LENGTH_SHORT).show();
        } else if (!isLoading) {
            //Toast.makeText(this, R.string.loading_extra_cards, Toast.LENGTH_SHORT).show();
            //new listLoadingTask(false).execute();
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int i) {}

    @Override
    public void onScroll(float v) {
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
            isLoading = true;
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

            isLoading = false;
            sortingAdapter.notifyDataSetChanged();
        }
    }
}
