package com.sleepingbear.vnconversation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

public class CategoryFragment extends Fragment {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private CategoryFragCursorAdapter adapter;

    private Cursor cursor;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_category, container, false);


        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        changeListView(true);

        DicUtils.setAdView(mainView);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( isKeyin ) {
            cursor = db.rawQuery(DicQuery.getCategory(), null);

            ListView listView = (ListView) mainView.findViewById(R.id.my_f_category_lv);
            adapter = new CategoryFragCursorAdapter(getContext(), cursor);
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

            Intent intent = new Intent(getActivity().getApplicationContext(), CategoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("category", cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY")));
            bundle.putString("samples", cursor.getString(cursor.getColumnIndexOrThrow("SAMPLES")));
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };
}

class CategoryFragCursorAdapter extends CursorAdapter {
    int fontSize = 0;

    public CategoryFragCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_category_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_category)).setText(cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY")));

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_category)).setTextSize(fontSize);
    }
}