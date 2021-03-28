package com.vehicleceaser;

import android.content.ContentValues;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smart.caching.SmartCaching;
import com.smart.common.BaseMasterActivity;
import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smart.framework.SmartUtils.hideProgressDialog;
import static com.smart.framework.SmartUtils.showProgressDialog;

public class VCSearchHistoryActivity extends BaseMasterActivity implements Constants {

    Spinner spnExecList;
    TextView tvSelectDate;
    String SEARCH_TERM;
    RecyclerView rvHistoryList;
    RVHistoryAdapter rvHistoryAdapter;
    LinearLayoutManager layoutManager;
    private SmartCaching smartCaching;
    private String TABLE_EXECUTIVES = "executivesTable";
    private String TABLE_HISTORY = "historyTable";
//    private String URL_LIST_OF_EXECUTIVES = "http://13.233.239.93/laravel/api/list-of-executive";
//    private String URL_SEARCH_HISTORY = "http://13.233.239.93/laravel/api/search-history";
//    private String URL_LIST_OF_EXECUTIVES = "http://shruti-collection.viturvainfotech.com/api/list-of-executive";
//    private String URL_SEARCH_HISTORY = "http://shruti-collection.viturvainfotech.com/api/search-history";
    private ArrayList<ContentValues> executivesData = new ArrayList<>();
    private ArrayList<ContentValues> executivesHistoryData = new ArrayList<>();
    private String[] execLocations;
    private String SEL_DATE = "";
    private String IN_EXEC_ID = "";

    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_search_history;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();

        smartCaching = new SmartCaching(this);
        tvSelectDate = (TextView) findViewById(R.id.tvSelectDate);
        spnExecList = (Spinner) findViewById(R.id.spnExecList);
        rvHistoryList = (RecyclerView) findViewById(R.id.rvHistoryList);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvHistoryList.setLayoutManager(layoutManager);
        rvHistoryList.setNestedScrollingEnabled(false);
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        getExecutivesList();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();

        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For choosing custom date and time
                SmartUtils.chooseDate(VCSearchHistoryActivity.this, new com.smart.customviews.CustomClickListner() {
                    @Override
                    public void onClick(String value) {
                        tvSelectDate.setText(value);
                        SEL_DATE = value;
                        getSearchHistory(IN_EXEC_ID, SEL_DATE);
                    }
                });
            }
        });

        spnExecList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    IN_EXEC_ID = executivesData.get(position - 1).getAsString("id");
                    if (SEL_DATE.length() > 0) {

                        getSearchHistory(IN_EXEC_ID, SEL_DATE);

                    } else {

                        //For getting current date and time
                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c);
                        getSearchHistory(IN_EXEC_ID, formattedDate);

                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getExecutivesList() {
        //Every time when this method is called, we will remove all data from our local DB and update it with new fresh data
        smartCaching.deleteFromTable("DELETE FROM " + TABLE_EXECUTIVES);

        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("page", "1");
            dataObj.put("limit", "10");
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@WSREQ=", obj.toString());

        showProgressDialog(VCSearchHistoryActivity.this, "Loading Executives...", false);

        SmartUtils.makeWSCall(VCSearchHistoryActivity.this, 1, URL_LIST_OF_EXECUTIVES, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                hideProgressDialog();
                if (message.equals("success")) {
                    Log.d("@@@RES", response.toString());
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("data"),
                                TABLE_EXECUTIVES);

                        executivesData = smartCaching.getDataFromCache(TABLE_EXECUTIVES);

                        execLocations = new String[executivesData.size() + 1];
                        execLocations[0] = "Choose Executive";
                        if (executivesData != null) {
                            for (int i = 0; i < executivesData.size(); i++) {
                                ContentValues row = executivesData.get(i);
                                execLocations[i + 1] = row.getAsString("first_name") + " " + row.getAsString("last_name");
                            }
                        }
                        ArrayAdapter<String> ExecAdapter =
                                new ArrayAdapter<>(VCSearchHistoryActivity.this,
                                        R.layout.layout_spinner_item, execLocations);
                        spnExecList.setAdapter(ExecAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    try {
                        Toast.makeText(VCSearchHistoryActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    private void getSearchHistory(String exec_id, String date) {
        //Every time when this method is called, we will remove all data from our local DB and update it with new fresh data
        smartCaching.deleteFromTable("DELETE FROM " + TABLE_HISTORY);

        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("user_id", exec_id);
            dataObj.put("date", date);
//            dataObj.put("user_id", "1");
//            dataObj.put("date", "2018-09-18");
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@WSREQ=", obj.toString());

        showProgressDialog(VCSearchHistoryActivity.this, "Loading History...", false);

        SmartUtils.makeWSCall(VCSearchHistoryActivity.this, 1, URL_SEARCH_HISTORY, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                hideProgressDialog();
                if (message.equals("success")) {
                    Log.d("@@@RES", response.toString());
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("data"),
                                TABLE_HISTORY);

                        executivesHistoryData = smartCaching.getDataFromCache(TABLE_HISTORY);

                        rvHistoryAdapter = new RVHistoryAdapter();
                        rvHistoryList.setAdapter(rvHistoryAdapter);
                        rvHistoryAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("nothing")) {
                    try {
                        Toast.makeText(VCSearchHistoryActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    try {
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    private class RVHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_history, parent, false);
            return new HistoryViewholder(parentView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final HistoryViewholder viewholder = (HistoryViewholder) holder;

            ContentValues row = executivesHistoryData.get(position);
            viewholder.tvChassisNo.setText("Ch. No. : " + row.getAsString("chasis_no"));
            viewholder.tvSearchTime.setText("Search Time : " + row.getAsString("search_date").substring(0, 10));
            viewholder.tvSearchDate.setText("Search Date : " + row.getAsString("search_date").substring(11, 19));
            viewholder.tvSearchStatus.setText("Search Status : " + row.getAsString("status"));

        }

        @Override
        public int getItemCount() {
            return executivesHistoryData.size();
        }

        class HistoryViewholder extends RecyclerView.ViewHolder {

            KudosTextView tvSearchStatus, tvSearchDate, tvSearchTime, tvChassisNo;

            HistoryViewholder(View itemView) {
                super(itemView);
                tvChassisNo = (KudosTextView) itemView.findViewById(R.id.tvChassisNo);
                tvSearchTime = (KudosTextView) itemView.findViewById(R.id.tvSearchTime);
                tvSearchDate = (KudosTextView) itemView.findViewById(R.id.tvSearchDate);
                tvSearchStatus = (KudosTextView) itemView.findViewById(R.id.tvSearchStatus);
            }
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }
}
