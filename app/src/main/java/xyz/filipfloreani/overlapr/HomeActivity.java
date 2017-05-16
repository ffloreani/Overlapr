package xyz.filipfloreani.overlapr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.adapter.HistoryAdapter;
import xyz.filipfloreani.overlapr.adapter.OnHistoryItemClickListener;
import xyz.filipfloreani.overlapr.filepicker.FilePickerActivity;
import xyz.filipfloreani.overlapr.graphing.ChartsParserTask;
import xyz.filipfloreani.overlapr.graphing.GraphingActivity;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmHighlightsModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;
import xyz.filipfloreani.overlapr.sorting.SortingActivity;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;
import xyz.filipfloreani.overlapr.utils.SaveDataTask;

public class HomeActivity extends AppCompatActivity implements OnHistoryItemClickListener {

    public static final String SHARED_PREF_HOME_ACTIVITY = "overlapr.activity.HOME_ACTIVITY";
    public static final String SHARED_PREF_CHART_UUID = "overlapr.prefs.CHART_UUID";
    private static final int CHARTS_CODE = 200;

    FloatingActionButton fab;
    RelativeLayout emptyStateLayout;
    RecyclerView historyRecyclerView;

    RealmResults<RealmChartModel> chartModelList;
    HistoryAdapter historyAdapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFilePicker(CHARTS_CODE);
            }
        });

        // Load the empty state layout & the recyclerview history layout
        emptyStateLayout = (RelativeLayout) findViewById(R.id.empty_state);
        historyRecyclerView = (RecyclerView) findViewById(R.id.rvHistory);

        loadData();

        chartModelList.addChangeListener(new RealmChangeListener<RealmResults<RealmChartModel>>() {
            @Override
            public void onChange(RealmResults<RealmChartModel> element) {
                historyAdapter.notifyDataSetChanged();

                if (chartModelList == null || chartModelList.size() == 0) {
                    historyRecyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    historyRecyclerView.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chartModelList == null || chartModelList.size() == 0) {
            historyRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            historyRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }

        if (historyAdapter == null) {
            // Load the adapter for the history recyclerview & set up the layout manager
            historyAdapter = new HistoryAdapter(chartModelList, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            historyRecyclerView.setLayoutManager(layoutManager);
            historyRecyclerView.setAdapter(historyAdapter);
            historyRecyclerView.setHasFixedSize(true);
            historyRecyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        historyRecyclerView.setAdapter(null);
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sorting_item:
                startSortingActivity();
                return true;
            case R.id.clear_history_item:
                clearHistory();
                return true;
            case R.id.load_charts_item:
                startFilePicker(CHARTS_CODE);
                return true;
            case R.id.save_highlights_item:
                saveHighlightsToFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Upon a click on one of the items in the history recycler view,
     * retrieves the chart UUID that is referred to by the clicked view and stores it
     * into shared preferences. After that, it starts the GraphingActivity through an empty intent.
     *
     * @param v        The clicked view
     * @param position Clicked view's position in the list
     */
    @Override
    public void onItemClick(View v, int position) {
        String chartUuid = chartModelList.get(position).getUuid();

        SharedPreferences sp = getSharedPreferences(SHARED_PREF_HOME_ACTIVITY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARED_PREF_CHART_UUID, chartUuid);
        editor.commit();

        Intent i = new Intent(this, GraphingActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(View v, final RealmChartModel chartModel) {
        final EditText editText = new EditText(this);
        editText.setPadding(16, 8, 8, 16);

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder
                .setTitle("Rename chart '" + chartModel.getTitle() + "'")
                .setView(editText)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTitle = editText.getText().toString();

                        realm.beginTransaction();
                        chartModel.setTitle(newTitle);
                        realm.commitTransaction();

                        Toast.makeText(HomeActivity.this, "Name change complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

        return true;
    }

    private void startSortingActivity() {
        Intent i = new Intent(this, SortingActivity.class);
        startActivity(i);
    }

    private void clearHistory() {
        AlertDialog.Builder adBuilder = GeneralUtils.buildWatchOutDialog(this);
        adBuilder
                .setMessage("Are you sure you want to delete all chart history?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final RealmResults<RealmChartModel> charts = realm.where(RealmChartModel.class).findAll();
                        final RealmResults<RealmHighlightsModel> highlights = realm.where(RealmHighlightsModel.class).findAll();
                        final RealmResults<RealmPointModel> points = realm.where(RealmPointModel.class).findAll();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                highlights.deleteAllFromRealm();
                                points.deleteAllFromRealm();
                                charts.deleteAllFromRealm();
                            }
                        });
                    }
                }).show();
    }

    private void saveHighlightsToFile() {
        new SaveDataTask(this).execute();
    }

    private void loadData() {
        chartModelList = realm.where(RealmChartModel.class).findAll();
    }

    /**
     * Starts the FilePicker activity.
     */
    private void startFilePicker(int code) {
        Intent i = new Intent(this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, code);
    }

    /**
     * Processes the results of the FilePicker activity.
     *
     * @param requestCode The code that was used to start the activity
     * @param resultCode  The code that marks the result of the activity
     * @param data        Data sent from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHARTS_CODE && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();

            new ChartsParserTask(this).execute(fileUri);
        }
    }
}
