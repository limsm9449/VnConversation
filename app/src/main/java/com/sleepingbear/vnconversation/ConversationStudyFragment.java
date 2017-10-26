package com.sleepingbear.vnconversation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.HashMap;
import java.util.Random;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class ConversationStudyFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private Cursor cursor;
    private TextView my_tv_han;
    private TextView my_tv_foreign;
    private String currForeign;
    private String currSeq;
    private int difficult = 1;
    private boolean isStart = false;

    ConversationStudySearchTask task;

    private int fontSize = 0;

    public ConversationStudyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_conversation_study, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        my_tv_han = (TextView) mainView.findViewById(R.id.my_tv_han);
        my_tv_foreign = (TextView) mainView.findViewById(R.id.my_tv_foreign);

        ((ImageView) mainView.findViewById(R.id.my_iv_left)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_right)).setOnClickListener(this);

        ((ImageView) mainView.findViewById(R.id.my_iv_view)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setOnClickListener(this);

        ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);
        ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);

        ((RadioButton) mainView.findViewById(R.id.my_rb_easy)).setOnClickListener(this);
        ((RadioButton) mainView.findViewById(R.id.my_rb_normal)).setOnClickListener(this);
        ((RadioButton) mainView.findViewById(R.id.my_rb_difficult)).setOnClickListener(this);

        //리스트 내용 변경
        changeListView(true);

        DicUtils.setAdView(mainView);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( getActivity(), CommConstants.preferences_font ) );

        my_tv_han.setTextSize(fontSize);
        my_tv_foreign.setTextSize(fontSize);

        if ( isKeyin ) {
            if (task != null) {
                return;
            }
            task = new ConversationStudySearchTask();
            task.execute();
        }
    }

    public void getData() {
        DicUtils.dicLog(this.getClass().toString() + " getData");
        if ( db != null ) {
            cursor = db.rawQuery(DicQuery.getConversationStudyList(difficult), null);

            if ( cursor.getCount() == 0 ) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        DicUtils.dicLog("onClick");
        switch (v.getId()) {
            case R.id.my_iv_left:
                ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);
                ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);

                if ( !cursor.isFirst() ) {
                    cursor.moveToPrevious();
                    conversationShow();
                }

                isStart = false;

                break;
            case R.id.my_iv_right:
                ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);
                ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);

                if ( isStart ) {
                    DicDb.insConversationStudy(db, currSeq, DicUtils.getDelimiterDate(DicUtils.getCurrentDate(),"."));
                    //DicUtils.writeInfoToFile(getContext(), db, "C02");
                }
                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    conversationShow();
                } else {
                    changeListView(true);
                }
                break;
            case R.id.my_iv_view:
                my_tv_foreign.setText(foreign);
                ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.GONE);
                ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.VISIBLE);

                break;
            case R.id.my_iv_hide:
                ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);
                ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);

                conversationShow();
                break;
            case R.id.my_rb_easy:
                difficult = 1;
                changeListView(true);
                break;
            case R.id.my_rb_normal:
                difficult = 2;
                changeListView(true);
                break;
            case R.id.my_rb_difficult:
                difficult = 3;
                changeListView(true);
                break;
            default:
                isStart = true;

                String foreign = (String)my_tv_foreign.getText();

                //영문보기를 클릭하고 단어 클릭시 오류가 발생해서 체크를 해줌.
                if ( foreign.length() >= currForeign.length() ) {
                    Toast.makeText(getContext(), "Refresh 버튼을 클릭한 후에 단어를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( "".equals(foreign) ) {
                    foreign = ((String)v.getTag()).trim();
                } else {
                    foreign += " " + ((String)v.getTag()).trim();
                }

                if ( foreign.equals( currForeign.substring( 0, foreign.length() ) ) ) {
                    my_tv_foreign.setText(foreign);
                    ((Button)v).setBackgroundColor(Color.rgb(189, 195, 195));
                }

                //정답이면...
                if ( foreign.equals( currForeign) ) {
                    if ( isStart ) {
                        DicDb.insConversationStudy(db, currSeq, DicUtils.getDelimiterDate(DicUtils.getCurrentDate(),"."));
                        //DicUtils. writeInfoToFile(getContext(), db, "C02");
                    }

                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View dialog_layout = inflater.inflate(R.layout.dialog_correct_answer, null);

                    //dialog 생성..
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(dialog_layout);
                    final AlertDialog alertDialog = builder.create();

                    ((TextView) dialog_layout.findViewById(R.id.my_tv_han)).setText(my_tv_han.getText());
                    ((TextView) dialog_layout.findViewById(R.id.my_tv_foreign)).setText(my_tv_foreign.getText());

                    int fontSize = Integer.parseInt( DicUtils.getPreferencesValue( getContext(), CommConstants.preferences_font ) );
                    ((TextView) dialog_layout.findViewById(R.id.my_tv_han)).setTextSize(fontSize);
                    ((TextView) dialog_layout.findViewById(R.id.my_tv_foreign)).setTextSize(fontSize);

                    // 광고 추가
                    if ( CommConstants.isFreeApp ) {
                        PublisherAdView mPublisherAdView = new PublisherAdView(getActivity());
                        mPublisherAdView.setAdSizes(new AdSize(300, 250));
                        mPublisherAdView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id2));

                        // Create an ad request.
                        PublisherAdRequest.Builder publisherAdRequestBuilder = new PublisherAdRequest.Builder();
                        ((RelativeLayout) dialog_layout.findViewById(R.id.my_rl_admob)).addView(mPublisherAdView);

                        mPublisherAdView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();

                                ((Button) dialog_layout.findViewById(R.id.my_b_next)).setVisibility(View.VISIBLE);
                                ((Button) dialog_layout.findViewById(R.id.my_b_close)).setVisibility(View.VISIBLE);
                                ((Button) dialog_layout.findViewById(R.id.my_b_detail)).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(int i) {
                                super.onAdFailedToLoad(i);

                                ((Button) dialog_layout.findViewById(R.id.my_b_next)).setVisibility(View.VISIBLE);
                                ((Button) dialog_layout.findViewById(R.id.my_b_close)).setVisibility(View.VISIBLE);
                                ((Button) dialog_layout.findViewById(R.id.my_b_detail)).setVisibility(View.VISIBLE);
                            }
                        });

                        // Start loading the ad.
                        mPublisherAdView.loadAd(publisherAdRequestBuilder.build());

                        ((Button) dialog_layout.findViewById(R.id.my_b_next)).setVisibility(View.GONE);
                        ((Button) dialog_layout.findViewById(R.id.my_b_close)).setVisibility(View.GONE);
                        ((Button) dialog_layout.findViewById(R.id.my_b_detail)).setVisibility(View.GONE);
                    } else {
                        dialog_layout.findViewById(R.id.my_rl_admob).setVisibility(View.GONE);
                    }

                    ((Button) dialog_layout.findViewById(R.id.my_b_next)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!cursor.isLast()) {
                                cursor.moveToNext();
                                conversationShow();
                            } else {
                                changeListView(true);
                            }

                            alertDialog.dismiss();
                        }
                    });
                    ((Button) dialog_layout.findViewById(R.id.my_b_close)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    ((Button) dialog_layout.findViewById(R.id.my_b_detail)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("foreign", (String)my_tv_foreign.getText());
                            bundle.putString("han", (String)my_tv_han.getText());
                            bundle.putString("seq", currSeq);

                            Intent intent = new Intent(getContext(), SentenceViewActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);

                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    FlowLayout wordArea = (FlowLayout) mainView.findViewById(R.id.my_ll_conversation_word);
                    wordArea.removeAllViews();
                }

                break;
        }
    }

    private String foreign = "";
    private String[] foreignArr;
    public void conversationShow() {
        if ( cursor.getCount() > 0 ) {
            currSeq = cursor.getString(cursor.getColumnIndexOrThrow("SEQ"));
            currForeign = cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"));
            my_tv_han.setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")));
            my_tv_foreign.setText("");

            foreign = cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"));

            FlowLayout wordArea = (FlowLayout) mainView.findViewById(R.id.my_ll_conversation_word);
            wordArea.removeAllViews();

            foreignArr = getRandForeign(foreign.split(" "));
            for ( int i = 0; i < foreignArr.length; i++ ) {
                Button btn = new Button(getContext());
                btn.setBackgroundColor(Color.rgb(249, 151, 53));
                btn.setTextColor(Color.rgb(255, 255, 255));
                btn.setText( DicUtils.getBtnString( foreignArr[i] ) );
                btn.setAllCaps(false);
                btn.setTextSize(18);

                btn.setLayoutParams((new FlowLayout.LayoutParams(3, 3)));

                btn.setId(i);
                btn.setTag( DicUtils.getBtnString( foreignArr[i] ) );
                btn.setGravity(Gravity.TOP);
                btn.setOnClickListener(this);
                wordArea.addView(btn);
            }

            isStart = false;
        } else {
            my_tv_han.setText("");
            my_tv_foreign.setText("");
            currForeign = "";
        }
    }

    public String[] getRandForeign(String[] arr) {
        String[] rtnArr = new String[arr.length];

        Random random = new Random();
        HashMap hm = new HashMap();
        int cnt = 0;
        while ( true ) {
            int randomIdx = random.nextInt(arr.length);
            if ( !hm.containsKey(randomIdx + "") ) {
                hm.put(randomIdx + "", randomIdx + "");
                rtnArr[cnt++] = arr[randomIdx];
            }

            if ( cnt == arr.length ) {
                break;
            }
        }

        String str1 = "";
        String str2 = "";
        for ( int i = 0; i < arr.length; i++ ) {
            str1 += arr[i] + " ";
            str2 += rtnArr[i] + " ";
        }
        DicUtils.dicLog(str1 + " : " + str2);

        return rtnArr;
    }

    private class ConversationStudySearchTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getContext());
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            pd.setContentView(R.layout.custom_progress);

            pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pd.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            cursor.moveToFirst();
            conversationShow();

            pd.dismiss();
            task = null;

            super.onPostExecute(result);
        }
    }
}
