package com.vehicleceaser;

import android.content.ContentValues;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smart.common.BaseMasterActivity;
import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;

public class VCVehicleDetailActivity  extends BaseMasterActivity implements Constants {

    ImageView imgShare;
    KudosTextView tvChNo, tvVehName, tvEngNo, tvRegNo, tvBankName, tvCustName, tvNetMatDate, tvResidence,
            tvStatus, tvPropNo, tvSuppDesc, tvFirstDate, tvAgrDate, tvEmi, tvBKI, tvOverdue,
            tvPI, tvDPD, tvPOS, tvCoName, tvAgency, tv1, tv2, tv3, tv4, tv5, tv6;

    LinearLayout lnrMain;
    ContentValues row;

    @Override
    public int getLayoutID() {
        return R.layout.activity_vehicle_detail;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        lnrMain = (LinearLayout) findViewById(R.id.lnrMain);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        tvChNo = (KudosTextView) findViewById(R.id.tvChNo);
        tvVehName = (KudosTextView) findViewById(R.id.tvVehName);
        tvEngNo = (KudosTextView) findViewById(R.id.tvEngNo);
        tvRegNo = (KudosTextView) findViewById(R.id.tvRegNo);
        tvCustName = (KudosTextView) findViewById(R.id.tvCustName);
        tvNetMatDate = (KudosTextView) findViewById(R.id.tvNetMatDate);
        tvResidence = (KudosTextView) findViewById(R.id.tvResidence);
        tvBankName = (KudosTextView) findViewById(R.id.tvBankName);
        tvStatus = (KudosTextView) findViewById(R.id.tvStatus);
        tvPropNo = (KudosTextView) findViewById(R.id.tvPropNo);
        tvSuppDesc = (KudosTextView) findViewById(R.id.tvSuppDesc);
        tvFirstDate = (KudosTextView) findViewById(R.id.tvFirstDate);
        tvAgrDate = (KudosTextView) findViewById(R.id.tvAgrDate);
        tvEmi = (KudosTextView) findViewById(R.id.tvEmi);
        tvBKI = (KudosTextView) findViewById(R.id.tvBKI);
        tvOverdue = (KudosTextView) findViewById(R.id.tvOverdue);
        tvPI = (KudosTextView) findViewById(R.id.tvPI);
        tvDPD = (KudosTextView) findViewById(R.id.tvDPD);
        tvPOS = (KudosTextView) findViewById(R.id.tvPOS);
        tvCoName = (KudosTextView) findViewById(R.id.tvCoName);
        tvAgency = (KudosTextView) findViewById(R.id.tvAgency);

        tv1 = (KudosTextView) findViewById(R.id.tv1);
        tv2 = (KudosTextView) findViewById(R.id.tv2);
        tv3 = (KudosTextView) findViewById(R.id.tv3);
        tv4 = (KudosTextView) findViewById(R.id.tv4);
        tv5 = (KudosTextView) findViewById(R.id.tv5);
        tv6 = (KudosTextView) findViewById(R.id.tv6);
        Toolbar toolbar = findViewById(R.id.toolbar);
        row = getIntent().getParcelableExtra("VEHICLE_ROW");
        toolbar.setTitle(row.getAsString(REG_NO));
    }

    @Override
    public void prepareViews() {
        super.prepareViews();


        Log.d("@@@ROW=", String.valueOf(row));
        tvChNo.setText(String.format(getResources().getString(R.string.chasis_no), row.getAsString(CHASIS_NO)));
        tvVehName.setText(String.format(getResources().getString(R.string.veh_name), row.getAsString(ASSET_DESC)));
        tvEngNo.setText(String.format(getResources().getString(R.string.eng_no), row.getAsString(ENGINE_NO)));
        tvRegNo.setText(String.format(getResources().getString(R.string.reg_no), row.getAsString(REG_NO)));
        tvBankName.setText(String.format(getResources().getString(R.string.bank_name), row.getAsString(AT)));

        if (getIntent().getIntExtra("IN_TYPE",0) == 1) {//For Admin

            tvStatus.setText(String.format(getResources().getString(R.string.status), row.getAsString(STATUS)));
            tvPropNo.setText(String.format(getResources().getString(R.string.proposalno), row.getAsString(PROPOSALNO)));
            tvCustName.setText(String.format(getResources().getString(R.string.cust_name), row.getAsString(CUSTOMER_NAME)));
            tvSuppDesc.setText(String.format(getResources().getString(R.string.supplierdesc), row.getAsString(SUPPLIERDESC)));
            tvNetMatDate.setText(String.format(getResources().getString(R.string.net_mat_dat), row.getAsString(NET_MAT_DATE)));
            tvFirstDate.setText(String.format(getResources().getString(R.string.first_date), row.getAsString(FIRST_DATE)));
            tvAgrDate.setText(String.format(getResources().getString(R.string.agr_date), row.getAsString(AGR_DATE)));
            tvEmi.setText(String.format(getResources().getString(R.string.emi), row.getAsString(EMI)));
            tvBKI.setText(String.format(getResources().getString(R.string.bki), row.getAsString(BKI)));
            tvOverdue.setText(String.format(getResources().getString(R.string.overdue), row.getAsString(OVERDUE)));
            tvPI.setText(String.format(getResources().getString(R.string.pi), row.getAsString(PI)));
            tvDPD.setText(String.format(getResources().getString(R.string.dpd), row.getAsString(DPD)));
            tvPOS.setText(String.format(getResources().getString(R.string.pos), row.getAsString(POS)));
            tvCoName.setText(String.format(getResources().getString(R.string.co_name), row.getAsString(CO_NAME)));
            tvAgency.setText(String.format(getResources().getString(R.string.agency), row.getAsString(AGENCY)));
            tvResidence.setText(String.format(getResources().getString(R.string.residence), row.getAsString(RECIDENCE)));

        } else {//For Executive

            imgShare.setVisibility(View.GONE);
            tvCustName.setVisibility(View.GONE);
            tvNetMatDate.setVisibility(View.GONE);
            tvResidence.setVisibility(View.GONE);
            tvStatus.setVisibility(View.GONE);
            tvPI.setVisibility(View.GONE);
            tvPOS.setVisibility(View.GONE);
            tvPropNo.setVisibility(View.GONE);
            tvAgency.setVisibility(View.GONE);
            tvAgrDate.setVisibility(View.GONE);
            tvBKI.setVisibility(View.GONE);
            tvCoName.setVisibility(View.GONE);
            tvDPD.setVisibility(View.GONE);
            tvEmi.setVisibility(View.GONE);
            tvFirstDate.setVisibility(View.GONE);
            tvOverdue.setVisibility(View.GONE);
            tvSuppDesc.setVisibility(View.GONE);

            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
            tv4.setVisibility(View.GONE);
            tv5.setVisibility(View.GONE);
            tv6.setVisibility(View.GONE);
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strToShare =
                        String.format(getResources().getString(R.string.chasis_no), row.getAsString(CHASIS_NO)) + "\n" +
                                String.format(getResources().getString(R.string.veh_name), row.getAsString(ASSET_DESC)) + "\n" +
                                String.format(getResources().getString(R.string.eng_no), row.getAsString(ENGINE_NO)) + "\n" +
                                String.format(getResources().getString(R.string.reg_no), row.getAsString(REG_NO)) + "\n" +
                                String.format(getResources().getString(R.string.bank_name), row.getAsString(AT)) + "\n" +
                                String.format(getResources().getString(R.string.status), row.getAsString(STATUS)) + "\n" +
                                String.format(getResources().getString(R.string.proposalno), row.getAsString(PROPOSALNO)) + "\n" +
                                String.format(getResources().getString(R.string.cust_name), row.getAsString(CUSTOMER_NAME)) + "\n" +
                                String.format(getResources().getString(R.string.supplierdesc), row.getAsString(SUPPLIERDESC)) + "\n" +
                                String.format(getResources().getString(R.string.net_mat_dat), row.getAsString(NET_MAT_DATE)) + "\n" +
                                String.format(getResources().getString(R.string.first_date), row.getAsString(FIRST_DATE)) + "\n" +
                                String.format(getResources().getString(R.string.agr_date), row.getAsString(AGR_DATE)) + "\n" +
                                String.format(getResources().getString(R.string.emi), row.getAsString(EMI)) + "\n" +
                                String.format(getResources().getString(R.string.bki), row.getAsString(BKI)) + "\n" +
                                String.format(getResources().getString(R.string.overdue), row.getAsString(OVERDUE)) + "\n" +
                                String.format(getResources().getString(R.string.pi), row.getAsString(PI)) + "\n" +
                                String.format(getResources().getString(R.string.dpd), row.getAsString(DPD)) + "\n" +
                                String.format(getResources().getString(R.string.pos), row.getAsString(POS)) + "\n" +
                                String.format(getResources().getString(R.string.co_name), row.getAsString(CO_NAME)) + "\n" +
                                String.format(getResources().getString(R.string.agency), row.getAsString(AGENCY)) + "\n" +
                                String.format(getResources().getString(R.string.residence), row.getAsString(RECIDENCE));

                SmartUtils.onShareClick(VCVehicleDetailActivity.this, strToShare);
            }
        });
        
        
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
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
