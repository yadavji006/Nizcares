package com.indglobal.nizcare.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.indglobal.nizcare.R;
import com.indglobal.nizcare.adapters.BankAdapter;
import com.indglobal.nizcare.commons.Comman;
import com.indglobal.nizcare.commons.RippleView;
import com.indglobal.nizcare.commons.VolleyJSONRequest;
import com.indglobal.nizcare.commons.VolleySingleton;
import com.indglobal.nizcare.model.BankItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by readyassist on 1/25/18.
 */

public class BankDtlActivity extends Activity implements RippleView.OnRippleCompleteListener,View.OnClickListener{

    public static ProgressBar prgLoading;
    RippleView rplBack;
    RecyclerView rvBankAcnts;
    CardView crdAddNewAcnt;

    BankItem bankItem;
    ArrayList<BankItem> bankItemArrayList = new ArrayList<>();
    BankAdapter bankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_dtls_main);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        prgLoading = (ProgressBar)findViewById(R.id.prgLoading);
        rplBack = (RippleView)findViewById(R.id.rplBack);
        rvBankAcnts  = (RecyclerView)findViewById(R.id.rvBankAcnts);
        crdAddNewAcnt = (CardView)findViewById(R.id.crdAddNewAcnt);

        Comman.setPreferences(BankDtlActivity.this,"BankListUpdated","0");

        if (!Comman.isConnectionAvailable(BankDtlActivity.this)){
            Toast.makeText(BankDtlActivity.this,getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
        }else {
            prgLoading.setVisibility(View.VISIBLE);
            getBankAccounts();
        }

        rplBack.setOnRippleCompleteListener(this);
        crdAddNewAcnt.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.crdAddNewAcnt:
                Intent ii = new Intent(BankDtlActivity.this,AddBankActivity.class);
                ii.putExtra("isEdit","0");
                startActivity(ii);
                break;
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();

        switch (id){
            case R.id.rplBack:
                onBackPressed();
                break;
        }
    }

    private void getBankAccounts() {

        String url = getResources().getString(R.string.getBankdtlsApi);
        String token = Comman.getPreferences(BankDtlActivity.this,"token");
        url = url+"?token="+token;

        String GETBANKDTLSHIT = "get_bnkdtls_hit";
        VolleySingleton.getInstance(BankDtlActivity.this).cancelRequestInQueue(GETBANKDTLSHIT);
        VolleyJSONRequest request = new VolleyJSONRequest(Request.Method.GET, url,null, null,new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                try {
                    JSONObject response  = new JSONObject(result);
                    boolean success = response.getBoolean("success");

                    if (success){

                        bankItemArrayList.clear();

                        JSONArray data = response.getJSONArray("data");
                        for (int i=0;i<data.length();i++){

                            JSONObject object = data.getJSONObject(i);

                            String bank_id = object.getString("bank_id");
                            String country = object.getString("country");
                            String bank_name = object.getString("bank_name");
                            String account_holder_name = object.getString("account_holder_name");
                            String account_number = object.getString("account_number");
                            String account_type = object.getString("account_type");
                            String ifsc = object.getString("ifsc");
                            String bank_address = object.getString("bank_address");
                            String micr = object.getString("micr");
                            String deflt = object.getString("default");
                            String pan_card_no = object.getString("pan_card_no");

                            bankItem = new BankItem(bank_id,country,bank_name,account_holder_name,account_number,account_type,ifsc,bank_address,micr,deflt,pan_card_no);
                            bankItemArrayList.add(bankItem);

                        }

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BankDtlActivity.this.getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                        bankAdapter = new BankAdapter(BankDtlActivity.this,bankItemArrayList, new BankAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BankItem bankItem) {

                            }
                        });
                        rvBankAcnts.setLayoutManager(layoutManager);
                        rvBankAcnts.setAdapter(bankAdapter);

                        prgLoading.setVisibility(View.GONE);


                    }else {
                        String message = response.getString("message");
                        Toast.makeText(BankDtlActivity.this,message,Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(BankDtlActivity.this,getResources().getString(R.string.somethingwrong),Toast.LENGTH_SHORT).show();
                }

                prgLoading.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.networkResponse.data!=null){
                        String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                        JSONObject errObject = new JSONObject(jsonString);

                        String message = errObject.getString("message");

                        Toast.makeText(BankDtlActivity.this,message,Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(BankDtlActivity.this,getResources().getString(R.string.somethingwrong),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BankDtlActivity.this,getResources().getString(R.string.somethingwrong),Toast.LENGTH_SHORT).show();
                }
                prgLoading.setVisibility(View.GONE);
            }
        });
        request.setTag(GETBANKDTLSHIT);
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(BankDtlActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String BankListUpdated = Comman.getPreferences(BankDtlActivity.this,"BankListUpdated");
        if (BankListUpdated.equalsIgnoreCase("1")){
            Comman.setPreferences(BankDtlActivity.this,"BankListUpdated","0");
            if (!Comman.isConnectionAvailable(BankDtlActivity.this)){
                Toast.makeText(BankDtlActivity.this,getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
            }else {
                prgLoading.setVisibility(View.VISIBLE);
                getBankAccounts();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
