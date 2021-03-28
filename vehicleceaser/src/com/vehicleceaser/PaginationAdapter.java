package com.vehicleceaser;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;

import java.util.ArrayList;

/**
 * Created by Vipul on 10/6/2018.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constants {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private ArrayList<ContentValues> bikesList;
    private Context context;

    private boolean isLoadingAdded = false;
    private int IN_TYPE;

    public PaginationAdapter(Context context, int IN_TYPE) {
        this.context = context;
        this.IN_TYPE = IN_TYPE;
        bikesList = new ArrayList<>();
    }

    public ArrayList<ContentValues> getBikesList() {
        return bikesList;
    }

    public void setBikesList(ArrayList<ContentValues> bikesList) {
        this.bikesList = bikesList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_search_vehicle, parent, false);
        viewHolder = new BikeVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ContentValues row = bikesList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                BikeVH bikeVH = (BikeVH) holder;

                bikeVH.tvChNo.setText(row.getAsString(REG_NO));

                bikeVH.tvVehName.setText(String.format(context.getResources().getString(R.string.veh_name), row.getAsString(ASSET_DESC)));
                bikeVH.tvEngNo.setText(String.format(context.getResources().getString(R.string.eng_no), row.getAsString(ENGINE_NO)));
                bikeVH.tvRegNo.setText(String.format(context.getResources().getString(R.string.reg_no), row.getAsString(REG_NO)));
                bikeVH.tvBankName.setText(String.format(context.getString(R.string.bank_name), row.getAsString(AT)));

                if (IN_TYPE == 1) {//For Admin

                    bikeVH.tvStatus.setText(String.format(context.getResources().getString(R.string.status), row.getAsString(STATUS)));
                    bikeVH.tvPropNo.setText(String.format(context.getResources().getString(R.string.proposalno), row.getAsString(PROPOSALNO)));
                    bikeVH.tvCustName.setText(String.format(context.getResources().getString(R.string.cust_name), row.getAsString(CUSTOMER_NAME)));
                    bikeVH.tvSuppDesc.setText(String.format(context.getResources().getString(R.string.supplierdesc), row.getAsString(SUPPLIERDESC)));
                    bikeVH.tvNetMatDate.setText(String.format(context.getResources().getString(R.string.net_mat_dat), row.getAsString(NET_MAT_DATE)));
                    bikeVH.tvEmi.setText(String.format(context.getResources().getString(R.string.emi), row.getAsString(EMI)));
                    bikeVH.tvBKI.setText(String.format(context.getResources().getString(R.string.bki), row.getAsString(BKI)));
                    bikeVH.tvOverdue.setText(String.format(context.getResources().getString(R.string.overdue), row.getAsString(OVERDUE)));
                    bikeVH.tvPI.setText(String.format(context.getResources().getString(R.string.pi), row.getAsString(PI)));
                    bikeVH.tvDPD.setText(String.format(context.getResources().getString(R.string.dpd), row.getAsString(DPD)));
                    bikeVH.tvPOS.setText(String.format(context.getResources().getString(R.string.pos), row.getAsString(POS)));
                    bikeVH.tvCoName.setText(String.format(context.getResources().getString(R.string.co_name), row.getAsString(CO_NAME)));
                    bikeVH.tvAgency.setText(String.format(context.getResources().getString(R.string.agency), row.getAsString(AGENCY)));
                    bikeVH.tvResidence.setText(String.format(context.getResources().getString(R.string.residence), row.getAsString(RECIDENCE)));

                } else {//For Executive

                    bikeVH.imgShare.setVisibility(View.GONE);
                    bikeVH.tvCustName.setVisibility(View.GONE);
                    bikeVH.tvNetMatDate.setVisibility(View.GONE);
                    bikeVH.tvResidence.setVisibility(View.GONE);
                    bikeVH.tvStatus.setVisibility(View.GONE);
                    bikeVH.tvPI.setVisibility(View.GONE);
                    bikeVH.tvPOS.setVisibility(View.GONE);
                    bikeVH.tvPropNo.setVisibility(View.GONE);
                    bikeVH.tvAgency.setVisibility(View.GONE);
                    bikeVH.tvAgrDate.setVisibility(View.GONE);
                    bikeVH.tvBKI.setVisibility(View.GONE);
                    bikeVH.tvCoName.setVisibility(View.GONE);
                    bikeVH.tvDPD.setVisibility(View.GONE);
                    bikeVH.tvEmi.setVisibility(View.GONE);
                    bikeVH.tvFirstDate.setVisibility(View.GONE);
                    bikeVH.tvOverdue.setVisibility(View.GONE);
                    bikeVH.tvSuppDesc.setVisibility(View.GONE);

                    bikeVH.tv1.setVisibility(View.GONE);
                    bikeVH.tv2.setVisibility(View.GONE);
                    bikeVH.tv3.setVisibility(View.GONE);
                    bikeVH.tv4.setVisibility(View.GONE);
                    bikeVH.tv5.setVisibility(View.GONE);
                    bikeVH.tv6.setVisibility(View.GONE);
                }

                bikeVH.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("VehicleDetailsssss", "onClick: HELLO");
                        Intent i = new Intent(context, VCVehicleDetailActivity.class);
                        i.putExtra("VEHICLE_ROW", row);
                        i.putExtra("IN_TYPE",IN_TYPE);
                        context.startActivity(i);
                    }
                });

                bikeVH.imgShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String strToShare =
                                String.format(context.getResources().getString(R.string.chasis_no1), row.getAsString(CHASIS_NO)) + "\n" +
                                        String.format(context.getResources().getString(R.string.veh_name1), row.getAsString(ASSET_DESC)) + "\n" +
                                        String.format(context.getResources().getString(R.string.eng_no1), row.getAsString(ENGINE_NO)) + "\n" +
                                        String.format(context.getString(R.string.bank_name), row.getAsString(AT)) + "\n" +
                                        String.format(context.getResources().getString(R.string.reg_no1), row.getAsString(REG_NO)) + "\n" +
                                        String.format(context.getResources().getString(R.string.status1), row.getAsString(STATUS)) + "\n" +
                                        String.format(context.getResources().getString(R.string.proposalno1), row.getAsString(PROPOSALNO)) + "\n" +
                                        String.format(context.getResources().getString(R.string.cust_name1), row.getAsString(CUSTOMER_NAME)) + "\n" +
                                        String.format(context.getResources().getString(R.string.supplierdesc1), row.getAsString(SUPPLIERDESC)) + "\n" +
                                        String.format(context.getResources().getString(R.string.net_mat_dat1), row.getAsString(NET_MAT_DATE)) + "\n" +
                                        String.format(context.getResources().getString(R.string.first_date1), row.getAsString(FIRST_DATE)) + "\n" +
                                        String.format(context.getResources().getString(R.string.agr_date1), row.getAsString(AGR_DATE)) + "\n" +
                                        String.format(context.getResources().getString(R.string.emi1), row.getAsString(EMI)) + "\n" +
                                        String.format(context.getResources().getString(R.string.bki1), row.getAsString(BKI)) + "\n" +
                                        String.format(context.getResources().getString(R.string.overdue1), row.getAsString(OVERDUE)) + "\n" +
                                        String.format(context.getResources().getString(R.string.pi1), row.getAsString(PI)) + "\n" +
                                        String.format(context.getResources().getString(R.string.dpd1), row.getAsString(DPD)) + "\n" +
                                        String.format(context.getResources().getString(R.string.pos1), row.getAsString(POS)) + "\n" +
                                        String.format(context.getResources().getString(R.string.co_name1), row.getAsString(CO_NAME)) + "\n" +
                                        String.format(context.getResources().getString(R.string.agency1), row.getAsString(AGENCY)) + "\n" +
                                        String.format(context.getResources().getString(R.string.residence1), row.getAsString(RECIDENCE));

                        SmartUtils.onShareClick(context, strToShare);
                    }
                });

//                movieVH.textView.setText(movie.getTitle());
                break;
            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return bikesList == null ? 0 : bikesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == bikesList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(ContentValues mc) {
        bikesList.add(mc);
        notifyItemInserted(bikesList.size() - 1);
    }

    public void addAll(ArrayList<ContentValues> mcList) {
        for (ContentValues mc : mcList) {
            add(mc);
        }
    }

    public void remove(ContentValues city) {
        int position = bikesList.indexOf(city);
        if (position > -1) {
            bikesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ContentValues());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = bikesList.size() - 1;
        ContentValues item = getItem(position);

        if (item != null) {
            bikesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ContentValues getItem(int position) {
        return bikesList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class BikeVH extends RecyclerView.ViewHolder {

        ImageView imgShare;
        KudosTextView tvChNo, tvVehName, tvEngNo, tvRegNo, tvCustName, tvBankName, tvNetMatDate, tvResidence,
                tvStatus, tvPropNo, tvSuppDesc, tvFirstDate, tvAgrDate, tvEmi, tvBKI, tvOverdue,
                tvPI, tvDPD, tvPOS, tvCoName, tvAgency, tv1, tv2, tv3, tv4, tv5, tv6;

        LinearLayout lnrMain;

        public BikeVH(View itemView) {
            super(itemView);

            lnrMain = (LinearLayout) itemView.findViewById(R.id.lnrMain);
            imgShare = (ImageView) itemView.findViewById(R.id.imgShare);
            tvChNo = (KudosTextView) itemView.findViewById(R.id.tvChNo);
            tvVehName = (KudosTextView) itemView.findViewById(R.id.tvVehName);
            tvEngNo = (KudosTextView) itemView.findViewById(R.id.tvEngNo);
            tvRegNo = (KudosTextView) itemView.findViewById(R.id.tvRegNo);
            tvCustName = (KudosTextView) itemView.findViewById(R.id.tvCustName);
            tvNetMatDate = (KudosTextView) itemView.findViewById(R.id.tvNetMatDate);
            tvResidence = (KudosTextView) itemView.findViewById(R.id.tvResidence);
            tvBankName = (KudosTextView) itemView.findViewById(R.id.tvBankName);
            tvStatus = (KudosTextView) itemView.findViewById(R.id.tvStatus);
            tvPropNo = (KudosTextView) itemView.findViewById(R.id.tvPropNo);
            tvSuppDesc = (KudosTextView) itemView.findViewById(R.id.tvSuppDesc);
            tvFirstDate = (KudosTextView) itemView.findViewById(R.id.tvFirstDate);
            tvAgrDate = (KudosTextView) itemView.findViewById(R.id.tvAgrDate);
            tvEmi = (KudosTextView) itemView.findViewById(R.id.tvEmi);
            tvBKI = (KudosTextView) itemView.findViewById(R.id.tvBKI);
            tvOverdue = (KudosTextView) itemView.findViewById(R.id.tvOverdue);
            tvPI = (KudosTextView) itemView.findViewById(R.id.tvPI);
            tvDPD = (KudosTextView) itemView.findViewById(R.id.tvDPD);
            tvPOS = (KudosTextView) itemView.findViewById(R.id.tvPOS);
            tvCoName = (KudosTextView) itemView.findViewById(R.id.tvCoName);
            tvAgency = (KudosTextView) itemView.findViewById(R.id.tvAgency);

            tv1 = (KudosTextView) itemView.findViewById(R.id.tv1);
            tv2 = (KudosTextView) itemView.findViewById(R.id.tv2);
            tv3 = (KudosTextView) itemView.findViewById(R.id.tv3);
            tv4 = (KudosTextView) itemView.findViewById(R.id.tv4);
            tv5 = (KudosTextView) itemView.findViewById(R.id.tv5);
            tv6 = (KudosTextView) itemView.findViewById(R.id.tv6);


        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
