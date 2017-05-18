package xyz.filipfloreani.overlapr.sorting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.swipecards.OnExitListener;
import xyz.filipfloreani.overlapr.swipecards.SwipeAdapterView;
import xyz.filipfloreani.overlapr.swipecards.internal.Direction;

import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.DOWN;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.LEFT;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.RIGHT;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.UP;

public class SortingActivity extends AppCompatActivity implements OnExitListener {

    private Realm realm;
    private List<RealmChartModel> charts = new ArrayList<>();

    private SwipeAdapterView flingContainer;
    private SortingAdapter sortingAdapter;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);

        realm = Realm.getDefaultInstance();

        flingContainer = (SwipeAdapterView) findViewById(R.id.swipe_adapter_view);

        charts = loadCharts();
        sortingAdapter = new SortingAdapter(this, charts, realm);

        flingContainer.setOnExitListener(this);
        flingContainer.setAdapter(sortingAdapter);
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
    public void onExit(View view, @Direction int direction) {
        String snackMessage = "Marked as ";
        switch (direction) {
            case LEFT:
                snackMessage += "chimeric";
                break;
            case RIGHT:
                snackMessage += "repeat";
                break;
            case UP:
                snackMessage += "low quality";
                break;
            case DOWN:
                snackMessage += "regular";
                break;
        }

        Snackbar.make(flingContainer, snackMessage, Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

}
