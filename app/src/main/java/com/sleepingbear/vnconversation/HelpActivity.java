package com.sleepingbear.vnconversation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Bundle b = getIntent().getExtras();
        StringBuffer allSb = new StringBuffer();
        StringBuffer CurrentSb = new StringBuffer();
        StringBuffer tempSb = new StringBuffer();


        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 학습" + CommConstants.sqlCR);
        tempSb.append("- Easy, Normal, hard 별로 회화 학습을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .해석을 보고 단어를 클릭해서 올바른 문장을 만드세요." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .학습한 회화는 '회화 노트' Tab의 '학습 회화'에서 일자별로 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "CONVERSATION_STUDY".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 패턴" + CommConstants.sqlCR);
        tempSb.append("- 회화 패턴별로 회화를 조회 및 회화 학습을 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .패턴을 클릭하면 패턴이 들어간 회화를 조회 합니다. " + CommConstants.sqlCR);
        tempSb.append(" .패턴을 길게 클릭하면 패턴이 들어간 회화를 학습 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "PATTERN".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 패턴 상세" + CommConstants.sqlCR);
        tempSb.append("- 회화 패턴이 들어간 회화를 조회합니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "PATTERN_ACT".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 검색" + CommConstants.sqlCR);
        tempSb.append("- 검색어로 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .'A B'로 검색을 하면 A와 B가 들어간 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .'A B,C D'로 검색을 하면 A와 B가 들어간 회화와 C와 D가 들어간 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "CONVERSATION".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 문장 상세" + CommConstants.sqlCR);
        tempSb.append("- 문장의 발음 및 관련 단어를 조회하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 단어 보기 및 등록할 단어장을 선택 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭하시면 Default 단어장에 추가 됩니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "SENTENCEVIEW".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 노트" + CommConstants.sqlCR);
        tempSb.append("- MY 회화, 학습 회화, 네이버 회화로 구성되어 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .'MY 회화'는 단어장 처럼 내 회화를 편집한 내용입니다." + CommConstants.sqlCR);
        tempSb.append(" .'학습 회화'는 매일 학습한 회화 내용입니다." + CommConstants.sqlCR);
        tempSb.append(" .'네이버 회화'는 네이버에서 제공하는 회화 내용입니다." + CommConstants.sqlCR);
        tempSb.append(" .노트를 길게 클릭하면 회화 학습, 회화 관리 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .'회화 관리' 기능을 선택하면 노트 수정, 노트 삭제, 노트 내보내기, 노트 가져오기 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "NOTE".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 노트 상세" + CommConstants.sqlCR);
        tempSb.append("- 회화 노트의 회화를 조회합니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .'MY 회화' 일때 회화 내용을 편집 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "NOTE_ACT".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장" + CommConstants.sqlCR);
        tempSb.append("- 내가 등록한 단어를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단의 + 버튼을 클릭해서 신규 단어장을 추가할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .기존 단어장을 길게 클릭하시면 수정, 추가, 삭제,  내보내기, 가져오기를 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어장을 클릭하시면 등록된 단어를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "VOCABULARY".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장 - 단어 학습" + CommConstants.sqlCR);
        tempSb.append("- 등록한 단어를 5가지 방법으로 공부할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어장 선택, 학습 종류 선택, 시간 선택을 하신후 학습시작을 클릭하세요." + CommConstants.sqlCR);
        tempSb.append(" .Default는 현재부터 60일전에 등록한 단어입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단답 학습" + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 뜻을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY1".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 정답 보기/ 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY2".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY3".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 OX 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 OX 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY4".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 4지선다 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY5".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어 상세" + CommConstants.sqlCR);
        tempSb.append("- 단어의 뜻, 발음, 상세 뜻, 예제, 기타 예제별로 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭하시면 Default 단어장에 추가 됩니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 길게 클릭하시면 추가할 단어장을 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "WORDVIEW".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        if ( "ALL".equals(b.getString("SCREEN")) ) {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(allSb.toString());
        } else {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(CurrentSb.toString() + CommConstants.sqlCR + CommConstants.sqlCR + allSb.toString());
        }

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
