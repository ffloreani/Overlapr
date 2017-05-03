package xyz.filipfloreani.overlapr.sorting;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.List;

import io.realm.Realm;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;

public class SortingActivity extends AppCompatActivity implements SwipeFlingAdapterView.onFlingListener {

    private Realm realm;
    private List<RealmChartModel> charts;

    private SwipeFlingAdapterView flingContainer;
    private SortingAdapter sortingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);

        realm = Realm.getDefaultInstance();

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipe_adapter_view);

        // TODO Load chart objects from Realm
        charts = loadCharts();
        sortingAdapter = new SortingAdapter(this, charts, realm);
        flingContainer.init(this, sortingAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Queries Realm for all stored RealmChartModels and returns a list of all found objects.
     *
     * @return List of all found RealmChartModel objects
     */
    private List<RealmChartModel> loadCharts() {
        return realm.where(RealmChartModel.class).findAll();
    }

    @Override
    public void removeFirstObjectInAdapter() {
    }

    @Override
    public void onLeftCardExit(final Object o) {
        Snackbar.make(flingContainer, "Marked as repeat", Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                .show();
    }

    @Override
    public void onRightCardExit(final Object o) {
        Snackbar.make(flingContainer, "Marked as chimeric", Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setActionTextColor(ContextCompat.getColor(SortingActivity.this, R.color.colorAccent))
                .show();
    }

    @Override
    public void onAdapterAboutToEmpty(int i) {
        Log.d(getLocalClassName(), "Adapter about to empty!");
    }

    @Override
    public void onScroll(float v) {
    }
}
