package xyz.filipfloreani.overlapr;

import android.app.Activity;
import android.content.ClipData;
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

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.adapter.HistoryAdapter;
import xyz.filipfloreani.overlapr.adapter.OnHistoryItemClickListener;
import xyz.filipfloreani.overlapr.filepicker.FilePickerActivity;
import xyz.filipfloreani.overlapr.graphing.GraphingActivity;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.sorting.SortingActivity;

public class HomeActivity extends AppCompatActivity implements OnHistoryItemClickListener {

    public static final String SHARED_PREF_HOME_ACTIVITY = "overlapr.activity.HOME_ACTIVITY";
    public static final String SHARED_PREF_GRAPH_TITLE = "overlapr.prefs.GRAPH_TITLE";
    public static final String SHARED_PREF_CHART_UUID = "overlapr.prefs.CHART_UUID";
    public static final String EXTRA_PAF_PATH = "overlapr.intent.PAF_PATH";
    private static final int FILE_CODE = 100;

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
                displayNameDialog();
            }
        });

        // Load the empty state layout & the recyclerview history layout
        emptyStateLayout = (RelativeLayout) findViewById(R.id.empty_state);
        historyRecyclerView = (RecyclerView) findViewById(R.id.rvHistory);

        loadData();
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
            case R.id.selection_item:
                displayNameDialog();
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
     * @param v The clicked view
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

    private void startSortingActivity() {
        Intent i = new Intent(this, SortingActivity.class);
        startActivity(i);
    }

    private void loadData() {
        chartModelList = realm.where(RealmChartModel.class).findAll();
    }

    /**
     * Creates a new AlertDialog containing a text input field. When the positive button is pressed,
     * the entered string is saved to SharedPreferences with the HomeActivity key. After that, the
     * {@code startFilePicker()} method is called.
     */
    private void displayNameDialog() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

        //View inflatedView = LayoutInflater.from(this).inflate(R.layout.dialog_title, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputTextView = new EditText(this);
        inputTextView.setPadding(16, 16, 16, 16);

        adBuilder.setTitle("New graph")
                .setMessage("Title")
                .setView(inputTextView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String title = inputTextView.getText().toString();

                        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_HOME_ACTIVITY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SHARED_PREF_GRAPH_TITLE, title);
                        editor.commit();

                        startFilePicker();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adBuilder.show();
    }

    /**
     * Starts the FilePicker activity.
     */
    private void startFilePicker() {
        Intent i = new Intent(this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
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
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // TODO: Simplify this and remove the option to select multiple files
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                ClipData clip = data.getClipData();

                if (clip != null) {
                    for (int i = 0; i < clip.getItemCount(); i++) {
                        Uri uri = clip.getItemAt(i).getUri();
                    }
                }
            } else {
                Uri fileUri = data.getData();

                Intent intent = new Intent(this, GraphingActivity.class);
                intent.putExtra(EXTRA_PAF_PATH, fileUri);
                startActivity(intent);
            }
        }
    }
}
