package xyz.filipfloreani.overlapr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;

import xyz.filipfloreani.overlapr.adapter.HistoryAdapter;
import xyz.filipfloreani.overlapr.db.LineChartRepository;
import xyz.filipfloreani.overlapr.filepicker.FilePickerActivity;
import xyz.filipfloreani.overlapr.graphing.PAFGraphingActivity;
import xyz.filipfloreani.overlapr.model.LineChartModel;

public class HomeActivity extends AppCompatActivity {

    public static final String SHARED_PREF_HOME_ACTIVITY = "overlapr.activity.HOME_ACTIVITY";
    public static final String SHARED_PREF_GRAPH_TITLE = "overlapr.prefs.GRAPH_TITLE";
    public static final String INTENT_FILTER_GRAPH_INSERT = "overlapr.filter.GRAPH_INSERT";
    public static final String EXTRA_PAF_PATH = "overlapr.intent.PAF_PATH";
    private static final int FILE_CODE = 100;

    FloatingActionButton fab;
    RelativeLayout emptyStateLayout;
    RecyclerView rvHistory;

    List<LineChartModel> chartModelList;
    HistoryAdapter historyAdapter;
    IntentFilter receiveFilter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LineChartModel newModel = LineChartRepository.getModelWithMaxId(context);
            chartModelList.add(newModel);
            historyAdapter.notifyItemInserted(chartModelList.size() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNameDialog();
            }
        });

        emptyStateLayout = (RelativeLayout) findViewById(R.id.empty_state);
        rvHistory = (RecyclerView) findViewById(R.id.rvHistory);
        rvHistory.setHasFixedSize(true);

        loadData();
        if (chartModelList == null || chartModelList.size() == 0) {
            rvHistory.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }

        historyAdapter = new HistoryAdapter(chartModelList);
        rvHistory.setAdapter(historyAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvHistory.setLayoutManager(layoutManager);
        rvHistory.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        receiveFilter = new IntentFilter(INTENT_FILTER_GRAPH_INSERT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, receiveFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        // TODO Replace with a loader?
        chartModelList = LineChartRepository.getAllModels(HomeActivity.this);
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

                Intent intent = new Intent(this, PAFGraphingActivity.class);
                intent.putExtra(EXTRA_PAF_PATH, fileUri);
                startActivity(intent);
            }
        }
    }
}
