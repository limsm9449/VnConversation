package com.sleepingbear.vnconversation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NoteFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private NoteFragCursorAdapter adapter;
    private LayoutInflater mInflater;

    public Spinner s_group;
    public String groupCode = "C01";
    private int mSelect = 0;

    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;

        mainView = inflater.inflate(R.layout.fragment_note, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(DicQuery.getNoteGroupKind(), null);
        String[] from = new String[]{"KIND_NAME"};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item, cursor, from, to);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_group = (Spinner) mainView.findViewById(R.id.my_s_conversation_note);
        s_group.setAdapter(mAdapter);
        s_group.setSelection(1); //학습회화로 초기값 지정
        s_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupCode = ((Cursor) s_group.getSelectedItem()).getString(1);
                DicUtils.dicLog("groupCode : " + groupCode);

                ((MainActivity)getActivity()).setChangeViewPaper(CommConstants.f_Note);

                changeListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리스트 내용 변경
        changeListView();

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView() {
        if ( db != null ) {
            Cursor listCursor = db.rawQuery(DicQuery.getNoteKind(groupCode), null);
            ListView listView = (ListView) mainView.findViewById(R.id.my_f_conversation_note_lv);
            adapter = new NoteFragCursorAdapter(getContext(), listCursor, 0);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(itemClickListener);
            listView.setOnItemLongClickListener(itemLongClickListener);
            listView.setSelection(0);
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putString("kind", cur.getString(cur.getColumnIndexOrThrow("KIND")));
            bundle.putString("kindName", cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));

            Intent intent = new Intent(getContext(), NoteActivity.class);
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent, CommConstants.s_note);
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if ( ((MainActivity)getActivity()).checkPermission() == false ) {
                Toast.makeText(getContext(), "파일 권한이 없어서 실행하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }

            Cursor cur = (Cursor) adapter.getItem(position);
            final int itemPosition = position;
            final String kind = cur.getString(cur.getColumnIndexOrThrow("KIND"));

            //메뉴 선택 다이얼로그 생성
            final String[] kindCodes = new String[]{"M1","M2"};
            final String[] kindCodeNames = new String[]{"회화 학습","회화 관리"};

            final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(getActivity());
            dlg.setTitle("메뉴 선택");
            dlg.setSingleChoiceItems(kindCodeNames, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    mSelect = arg1;
                }
            });
            dlg.setNegativeButton("취소", null);
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ( mSelect == 0 ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("kind", kind);
                        bundle.putString("sampleSeq", "");

                        Intent intent = new Intent(getActivity().getApplicationContext(), NoteStudyActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else {
                        noteManage(itemPosition);
                    }
                }
            });
            dlg.show();

            return true;
        }
    };

    @Override
    public void onClick(View v) {
    }

    public void noteManage(int position) {
        final Cursor cur = (Cursor) adapter.getItem(position);

        //layout 구성
        final View dialog_layout = mInflater.inflate(R.layout.dialog_note_iud, null);

        //dialog 생성..
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(dialog_layout);
        final android.app.AlertDialog alertDialog = builder.create();

        if ( "C01".equals(groupCode) || "C02".equals(groupCode) ) {
            if ( "C01".equals(groupCode) ) {
                final EditText et_upd = ((EditText) dialog_layout.findViewById(R.id.my_et_upd_name));
                et_upd.setText(cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));

                ((Button) dialog_layout.findViewById(R.id.my_b_upd)).setTag(cur.getString(cur.getColumnIndexOrThrow("KIND")));
                ((Button) dialog_layout.findViewById(R.id.my_b_upd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("".equals(et_upd.getText().toString())) {
                            Toast.makeText(getContext(), "회화노트 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            db.execSQL(DicQuery.getUpdCode(groupCode, (String) v.getTag(), et_upd.getText().toString()));

                            //기록...
                            //DicUtils.writeInfoToFile(getContext(), db, groupCode);

                            changeListView();

                            Toast.makeText(getContext(), "회화노트 이름을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ((EditText) dialog_layout.findViewById(R.id.my_et_upd_name)).setEnabled(false);
                ((Button) dialog_layout.findViewById(R.id.my_b_upd)).setEnabled(false);
            }
            ((Button) dialog_layout.findViewById(R.id.my_b_del)).setTag(cur.getString(cur.getColumnIndexOrThrow("KIND")));
            ((Button) dialog_layout.findViewById(R.id.my_b_del)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String code = (String) v.getTag();

                    if ("C010001".equals(code)) {
                        Toast.makeText(getContext(), "기본 회화노트는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("알림")
                                .setMessage("삭제된 데이타는 복구할 수 없습니다. 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();

                                        db.execSQL(DicQuery.getDelCode(groupCode, code));
                                        db.execSQL(DicQuery.getDelNote(code));

                                        //기록...
                                        //DicUtils.writeInfoToFile(getContext(), db, groupCode);
                                        changeListView();

                                        Toast.makeText(getContext(), "회화노트를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                }
            });
        } else {
            ((EditText) dialog_layout.findViewById(R.id.my_et_upd_name)).setEnabled(false);
            ((Button) dialog_layout.findViewById(R.id.my_b_upd)).setEnabled(false);
            ((Button) dialog_layout.findViewById(R.id.my_b_del)).setEnabled(false);
        }

        final EditText et_saveName = ((EditText) dialog_layout.findViewById(R.id.my_et_file_name));
        if ( "C01".equals(groupCode) ) {
            et_saveName.setText(cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));
        } else if ( "C02".equals(groupCode) ) {
            et_saveName.setText(cur.getString(cur.getColumnIndexOrThrow("KIND")).replaceAll("[.]","") + "_회화학습");
        } else {
            et_saveName.setText(((Cursor) s_group.getSelectedItem()).getString(2) + "_" + DicUtils.getCurrentDate());
        }
        ((Button) dialog_layout.findViewById(R.id.my_b_save)).setTag(cur.getString(cur.getColumnIndexOrThrow("KIND")));
        ((Button) dialog_layout.findViewById(R.id.my_b_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = (String) v.getTag();

                String saveFileName = et_saveName.getText().toString();
                if ("".equals(saveFileName)) {
                    Toast.makeText(getContext(), "저장할 파일명을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (saveFileName.indexOf(".") > -1 && !"txt".equals(saveFileName.substring(saveFileName.length() - 3, saveFileName.length()).toLowerCase())) {
                    Toast.makeText(getContext(), "확장자는 txt 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String fileName = "";

                    File appDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName);
                    if (!appDir.exists()) {
                        appDir.mkdirs();

                        if (saveFileName.indexOf(".") > -1) {
                            fileName = Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/" + saveFileName;
                        } else {
                            fileName = Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/" + saveFileName + ".txt";
                        }
                    } else {
                        if (saveFileName.indexOf(".") > -1) {
                            fileName = Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/" + saveFileName;
                        } else {
                            fileName = Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/" + saveFileName + ".txt";
                        }
                    }

                    File saveFile = new File(fileName);
                    if (saveFile.exists()) {
                        Toast.makeText(getContext(), "파일명이 존재합니다.", Toast.LENGTH_SHORT).show();
                        ;
                    } else {
                        try {
                            saveFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }

                        BufferedWriter bw = null;
                        try {
                            bw = new BufferedWriter(new FileWriter(saveFile, true));

                            Cursor cursor = db.rawQuery(DicQuery.getNoteList(code), null);
                            while (cursor.moveToNext()) {
                                bw.write(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1")) + ": " + cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")));
                                bw.newLine();
                            }

                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (bw != null) try {
                                bw.close();
                            } catch (IOException ioe2) {
                            }
                        }

                        Toast.makeText(getContext(), "회화노트를 정상적으로 내보냈습니다.", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();
                    }
                }
            }
        });

        ((Button) dialog_layout.findViewById(R.id.my_b_close)).setOnClickListener(new View.OnClickListener() {
                                                                                      @Override
                                                                                      public void onClick(View v) {
                                                                                          alertDialog.dismiss();
                                                                                      }
                                                                                  }
        );

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}

class NoteFragCursorAdapter extends CursorAdapter {
    public NoteFragCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_note_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_kind)).setText(cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME")));
    }

}



