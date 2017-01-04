package com.sleepingbear.vnconversation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GrammarFragment extends Fragment {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private GrammarFragCursorAdapter adapter;

    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_grammar, container, false);


        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        changeListView(true);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( isKeyin ) {
            cursor = db.rawQuery(DicQuery.getGrammar(), null);

            ListView listView = (ListView) mainView.findViewById(R.id.my_lv);
            adapter = new GrammarFragCursorAdapter(getContext(), cursor);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(itemClickListener);
            listView.setSelection(0);
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Intent intent = new Intent(getActivity().getApplicationContext(), GrammarActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("grammar", cursor.getString(cursor.getColumnIndexOrThrow("GRAMMAR")));
            bundle.putString("mean", cursor.getString(cursor.getColumnIndexOrThrow("MEAN")));
            bundle.putString("description", cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION")));
            bundle.putString("samples", cursor.getString(cursor.getColumnIndexOrThrow("SAMPLES")));
            intent.putExtras(bundle);
        }
    };

}

class GrammarFragCursorAdapter extends CursorAdapter {

        public GrammarFragCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            mContext = context;
            mCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_grammar_item, parent, false);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view.findViewById(R.id.my_tv_grammar)).setText(cursor.getString(cursor.getColumnIndexOrThrow("GRAMMAR")));
            ((TextView) view.findViewById(R.id.my_tv_mean)).setText(cursor.getString(cursor.getColumnIndexOrThrow("MEAN")));
        }
    }
