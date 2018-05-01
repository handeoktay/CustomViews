package com.handeoktay.pollyfysunum.components;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handeoktay.pollyfysunum.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HANDE OKTAY on 1.05.2018.
 */

public class FilterView extends RelativeLayout {

    private FilteredItemListAdapter filteredItemListAdapter;
    private TextView btnFilter, btnClear, dividerTop, divider;
    private RecyclerView recyclerView;
    private List<MultipleSelectView.SelectableItem<String>> selectableItemList;
    private List<String> filteringList;
    private MultipleSelectView multipleSelectView;
    private String text, hint;
    private int type = 0;

    public FilterView(Context context) {
        super(context);
        init();
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public int getType() {
        return type;
    }

    public FilterView setType(int type) {
        this.type = type;
        return this;
    }

    public void init() {
        inflate(getContext(), R.layout.component_filtered_view, this);

        selectableItemList = new ArrayList<>();
        filteringList = new ArrayList<>();

        btnFilter = findViewById(R.id.btnFilter);
        recyclerView = findViewById(R.id.RecyclerView);
        btnClear = findViewById(R.id.btnClearAll);
        dividerTop = findViewById(R.id.DividerTop);
        divider = findViewById(R.id.Divider);

        btnFilter.setOnClickListener(view -> {
            multipleSelectView.setList(selectableItemList);
            multipleSelectView.setHint(hint);
            multipleSelectView.setType(getType());
            multipleSelectView.show();
        });

        btnClear.setOnClickListener(view -> {
            try {
            for (int i = 0; i < selectableItemList.size(); i++) {
                if (selectableItemList.get(i).isSelected()) {
                    selectableItemList.get(i).setSelected(false);
                }
            }
            filteringList.clear();
            btnClear.setVisibility(GONE);
            dividerTop.setVisibility(GONE);
            divider.setVisibility(GONE);
            btnFilter.setText(text);
            filteredItemListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnClear.setVisibility(GONE);
        dividerTop.setVisibility(GONE);
        divider.setVisibility(GONE);

        filteredItemListAdapter = new FilteredItemListAdapter((Activity) getContext(), (itemHolder, selectableItem, position) -> {
            itemHolder.title.setText(selectableItem);
            return itemHolder;
        }, filteringList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(filteredItemListAdapter);
    }

    public FilterView setList(List<MultipleSelectView.SelectableItem<String>> selectableItemList) {
        this.selectableItemList = selectableItemList;
        return this;
    }

    public FilterView setMultipleSelectView(MultipleSelectView multipleSelectView) {
        this.multipleSelectView = multipleSelectView;

        this.multipleSelectView.addMultipleSelectCallback(type -> {
            try {
                if (type == getType()) {
                    filteringList.clear();
                    for (int i = 0; i < selectableItemList.size(); i++) {
                        if (selectableItemList.get(i).isSelected()) {
                            filteringList.add(selectableItemList.get(i).getItem());
                        }
                    }
                    if (filteringList.size() > 0) {
                        btnFilter.setText(text + " (" + filteringList.size() + ")");
                        btnClear.setVisibility(VISIBLE);
                        dividerTop.setVisibility(VISIBLE);
                        divider.setVisibility(VISIBLE);
                    } else {
                        btnFilter.setText(text);
                        btnClear.setVisibility(GONE);
                        dividerTop.setVisibility(GONE);
                        divider.setVisibility(GONE);
                    }
                    filteredItemListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return this;
    }

    public FilterView setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public FilterView setText(String text) {
        this.text = text;
        this.btnFilter.setText(text);
        return this;
    }


    static class FilteredItemListAdapter extends RecyclerView.Adapter<FilteredItemListAdapter.ItemHolder> {
        SelectableRecyclerViewConfig selectableRecyclerViewConfig;
        private List<String> filteredList;
        private Activity mContext;

        public FilteredItemListAdapter(Activity context, SelectableRecyclerViewConfig selectableRecyclerViewConfig, List<String> filteredList) {
            this.filteredList = filteredList;
            this.mContext = context;
            this.selectableRecyclerViewConfig = selectableRecyclerViewConfig;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filtered_selectable, null);
            return new ItemHolder(v);
        }



        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i) {
            String currentSelectableExam = filteredList.get(i);
            itemHolder = selectableRecyclerViewConfig.onBindViewHolder(itemHolder, currentSelectableExam, i);
        }

        @Override
        public int getItemCount() {
            return (null != filteredList ? filteredList.size() : 0);
        }


        public interface SelectableRecyclerViewConfig {
            ItemHolder onBindViewHolder(ItemHolder itemHolder, String selectableItem, int position);
        }

        public class ItemHolder extends RecyclerView.ViewHolder{
            public TextView title;

            ItemHolder(View view) {
                super(view);
                this.title = view.findViewById(R.id.ItemExamName);
            }
        }

    }

}
