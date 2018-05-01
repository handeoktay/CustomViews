package com.handeoktay.pollyfysunum;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.handeoktay.pollyfysunum.components.Graph;
import com.handeoktay.pollyfysunum.components.MultipleSelectView;
import com.handeoktay.pollyfysunum.databinding.ActivityMainBinding;

import java.util.ArrayList;

/**
 * Created by HANDE OKTAY on 1.05.2018.
 */

public class MainActivity extends Activity {

    ActivityMainBinding r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        r = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initGraphView();
        initFilterView();
    }

    private void initGraphView() {
        ArrayList<Graph.GraphNode> arrayList = new ArrayList<>();
        arrayList.add(new Graph.GraphNode(50f));
        arrayList.add(new Graph.GraphNode(30f));
        arrayList.add(new Graph.GraphNode(80f));
        arrayList.add(new Graph.GraphNode(75f));

        r.Graph.setTargetValue(-1).setValuesAreInteger(false).setNodeList(arrayList).build();
        r.Graph.setGraphIndicatorListener(changedIndex -> Toast.makeText(getApplicationContext(), "Indicator is on value: " + changedIndex, Toast.LENGTH_SHORT).show());
    }

    private void initFilterView() {
        ArrayList<MultipleSelectView.SelectableItem<String>> selectableItems = new ArrayList<>();
        for (int i = 0; i < 50; ) {
            if (i % 4 == 0) {
                selectableItems.add(new MultipleSelectView.SelectableItem<>("Option " + i, false));
                i++;
            }
            selectableItems.add(new MultipleSelectView.SelectableItem<>("Item " + i, false));
            i++;
        }

        r.filterButton
                .setText("Click to filter")
                .setHint("Search")
                .setType(0)
                .setList(selectableItems)
                .setMultipleSelectView(r.MultipleSelectView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (r.MultipleSelectView.isVisible()) {
                r.MultipleSelectView.hide();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
