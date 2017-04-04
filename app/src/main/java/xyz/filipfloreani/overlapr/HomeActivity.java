package xyz.filipfloreani.overlapr;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import xyz.filipfloreani.overlapr.adapter.HistoryAdapter;
import xyz.filipfloreani.overlapr.db.LineChartRepository;
import xyz.filipfloreani.overlapr.filepicker.FilePickerActivity;
import xyz.filipfloreani.overlapr.graphing.PAFGraphingActivity;
import xyz.filipfloreani.overlapr.model.LineChartModel;

public class HomeActivity extends AppCompatActivity {
    // TODO: Setup a local broadcast listener

    public static final String EXTRA_PAF_PATH = "overlapr.intent.PAF_PATH";

    private static final int FILE_CODE = 100;

    private FloatingActionButton fab;
    private RelativeLayout emptyStateLayout;
    private RecyclerView rvHistory;

    List<LineChartModel> chartModelList;
    HistoryAdapter historyAdapter;

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
                startFilePicker();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        // TODO Replace with a loader?
        chartModelList = LineChartRepository.getAllModels(HomeActivity.this);
    }

    private void startFilePicker() {
        Intent i = new Intent(this, FilePickerActivity.class);

        // Set these depending on your use case. These are the defaults.
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
