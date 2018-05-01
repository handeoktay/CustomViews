package com.handeoktay.pollyfysunum.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handeoktay.pollyfysunum.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by HANDE OKTAY on 1.05.2018.
 */

public class MultipleSelectView extends RelativeLayout {

    private List<SelectableItem> filteredList;
    private List<SelectableItem> selectableItemList;
    private SelectableItemListAdapter selectableItemListAdapter;
    private boolean isLoading = false;
    private SearchOperation searchOperation;
    private RecyclerView recyclerView;
    private TextView loadingView;
    private TextView txtInfo;
    private TextView btnDeselect;
    private TextView btnFilter;
    private EditText txtSearch;
    private ImageView btnClear;
    private List<MultipleSelectCallback> multipleSelectCallbacks;
    private int type = 0;

    public MultipleSelectView(Context context) {
        super(context);
        init();
    }

    public MultipleSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultipleSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultipleSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.component_multiple_select_view, this);

        multipleSelectCallbacks = new ArrayList<>();

        recyclerView = findViewById(R.id.RecyclerView);
        loadingView = findViewById(R.id.ListEmptyError);
        txtSearch = findViewById(R.id.txtSearch);
        btnClear = findViewById(R.id.btnClear);
        btnDeselect = findViewById(R.id.btnDeselect);
        btnFilter = findViewById(R.id.btnFilter);
        txtInfo = findViewById(R.id.txtInfo);


        loadingView.setOnClickListener(view -> {
        });

        selectableItemList = new ArrayList<>();
        filteredList = new ArrayList<>();

        selectableItemListAdapter = new SelectableItemListAdapter((Activity) getContext(), (itemHolder, selectableItem, position) -> {

            itemHolder.title.setText(selectableItem.getItem().toString());

            return itemHolder;
        }, filteredList);

        selectableItemListAdapter.setRecyclerItemListener((item, position) -> {
            item.setSelected(!item.isSelected());
            notifyItemSelected(item);
            selectableItemListAdapter.notifyItemChanged(position);

            int counter = 0;
            for (int i = 0; i < selectableItemList.size(); i++) {
                if (selectableItemList.get(i).isSelected()) {
                    counter++;
                }
            }
            if (counter == 0) {
                txtInfo.setText("Nothing selected");
            } else {
                txtInfo.setText(counter + " selected");
            }


            if (multipleSelectCallbacks != null) {
                for (MultipleSelectCallback callback : multipleSelectCallbacks) {
                    callback.onRefresh(getType());
                }
            }
        });

        btnDeselect.setOnClickListener(view -> {
            filteredList.clear();
            for (int i = 0; i < selectableItemList.size(); i++) {
                if (selectableItemList.get(i).isSelected()) {
                    selectableItemList.get(i).setSelected(false);
                }
            }
            filteredList.addAll(selectableItemList);
            selectableItemListAdapter.notifyDataSetChanged();
            txtInfo.setText("Nothing selected");

            if (multipleSelectCallbacks != null) {
                for (MultipleSelectCallback callback : multipleSelectCallbacks) {
                    callback.onRefresh(getType());
                }
            }
        });

        btnFilter.setOnClickListener(view -> {
            hide();
        });

        searchOperation = new SearchOperation();

        btnClear.setVisibility(GONE);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchOperation.cancel(true);
                loadingView.setText("Searching...");
                searchOperation = new SearchOperation();
                searchOperation.searchText = editable.toString();
                searchOperation.execute();
            }
        });

        btnClear.setOnClickListener(view -> {
            txtSearch.setText("");
            View currentFocus = ((Activity) getContext()).getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) ((Activity) getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }

            txtSearch.clearFocus();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_recycler_view_dark));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(selectableItemListAdapter);
        loadingView.setVisibility(VISIBLE);
    }

    public void notifyItemSelected(SelectableItem o) {
        for (SelectableItem selectableItem : selectableItemList) {
            if (o.getItem().toString().equals(selectableItem.getItem().toString())) {
                selectableItem.setSelected(o.isSelected());
                return;
            }
        }

    }

    public void setList(List<SelectableItem<String>> objectList) {
//        loadingView.setVisibility(VISIBLE);
        this.selectableItemList.clear();
//        this.filteredList.clear();

//        selectableItemListAdapter.notifyItemRangeRemoved(0, filteredList.size());

        this.selectableItemList.addAll(objectList);
//        this.filteredList.addAll(selectableItemList);

//        selectableItemListAdapter.notifyItemRangeChanged(0, filteredList.size());
//        selectableItemListAdapter.notifyItemRangeInserted(0, filteredList.size());


        int counter = 0;
        for (int i = 0; i < selectableItemList.size(); i++) {
            if (selectableItemList.get(i).isSelected()) {
                counter++;
            }
        }
        if (counter == 0) {
            txtInfo.setText("Nothing selected");
        } else {
            txtInfo.setText(counter + " selected");
        }

//        loadingView.setVisibility(GONE);
        loadingView.setText("Loading...");
        if (txtSearch.getText().toString().length() > 0) {
            txtSearch.setText("");
        } else {
            searchOperation.cancel(true);
            searchOperation = new SearchOperation();
            searchOperation.searchText = "";
            searchOperation.execute();
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public void setHint(String hint) {
        txtSearch.setHint(hint);
    }

    public void addMultipleSelectCallback(MultipleSelectCallback multipleSelectCallback) {
        if (multipleSelectCallbacks != null && !multipleSelectCallbacks.contains(multipleSelectCallback))
            multipleSelectCallbacks.add(multipleSelectCallback);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public interface MultipleSelectCallback {
        void onRefresh(int type);
    }

    class SearchOperation extends AsyncTask<Integer, Integer, Integer> {

        public String searchText = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (searchText.length() > 0) {
                    btnClear.setVisibility(VISIBLE);
                } else {
                    btnClear.setVisibility(GONE);
                }
                loadingView.setVisibility(VISIBLE);
                filteredList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(Integer... ıntegers) {
            try {
                for (SelectableItem selectableItem : selectableItemList) {
                    if (isCancelled()) {
                        filteredList.clear();
                        return null;
                    }
                    if (escapeTurkishCharacters(selectableItem.getItem().toString().toLowerCase(Locale.getDefault())).contains(escapeTurkishCharacters(searchText.toLowerCase(Locale.getDefault())))) {
                        filteredList.add(selectableItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer ınteger) {
            super.onPostExecute(ınteger);
            try {


                if (filteredList.size() > 0) {
                    loadingView.setVisibility(GONE);
                    selectableItemListAdapter.notifyDataSetChanged();
                } else {
                    loadingView.setText("No Results Found");
                    loadingView.setVisibility(VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static class SelectableItem<T> {
        private T item;
        private boolean isSelected;

        public SelectableItem(T item, boolean isSelected) {
            this.item = item;
            this.isSelected = isSelected;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    public static class SelectableItemListAdapter extends RecyclerView.Adapter<SelectableItemListAdapter.ItemHolder> {
        SelectableRecyclerViewConfig selectableRecyclerViewConfig;
        private Drawable checked, unChecked;
        private List<SelectableItem> filteredList;
        private Activity mContext;
        private RecyclerItemListener recyclerItemListener;

        public SelectableItemListAdapter(Activity context, SelectableRecyclerViewConfig selectableRecyclerViewConfig, List<SelectableItem> filteredList) {
            this.filteredList = filteredList;
            this.mContext = context;
            this.selectableRecyclerViewConfig = selectableRecyclerViewConfig;

            checked = context.getResources().getDrawable(R.drawable.ic_selected);
            unChecked = context.getResources().getDrawable(R.drawable.transparent);
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_selectable, null);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i) {
            SelectableItem currentSelectableExam = filteredList.get(i);

            itemHolder = selectableRecyclerViewConfig.onBindViewHolder(itemHolder, currentSelectableExam, i);

            if (currentSelectableExam.isSelected()) {
                itemHolder.imageViewStatus.setImageDrawable(checked);
            } else {
                itemHolder.imageViewStatus.setImageDrawable(unChecked);
            }
        }

        @Override
        public int getItemCount() {
            return (null != filteredList ? filteredList.size() : 0);
        }

        public void setRecyclerItemListener(RecyclerItemListener recyclerItemListener) {
            this.recyclerItemListener = recyclerItemListener;
        }

        public interface SelectableRecyclerViewConfig {
            ItemHolder onBindViewHolder(ItemHolder itemHolder, SelectableItem selectableItem, int position);
        }

        public interface RecyclerItemListener {
            void onItemClicked(SelectableItem exam, int position);
        }

        public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            public ImageView imageViewStatus;

            ItemHolder(View view) {
                super(view);
                this.title = view.findViewById(R.id.ItemExamName);
                this.imageViewStatus = view.findViewById(R.id.imageViewStatus);

                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (recyclerItemListener != null) {
                    recyclerItemListener.onItemClicked(filteredList.get(getAdapterPosition()), getAdapterPosition());
                }
            }

        }

    }


    public static String escapeTurkishCharacters(String s) {
        s = s.replaceAll("ç", "c");
        s = s.replaceAll("ğ", "g");
        s = s.replaceAll("ş", "s");
        s = s.replaceAll("ı", "i");
        s = s.replaceAll("ü", "u");
        s = s.replaceAll("ö", "o");
        return s;
    }

}
