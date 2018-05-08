package com.example.vietvan.tracnghiemflag;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrosid.svgloader.SvgLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManHinh1Activity extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.btnans1)
    Button btnans1;
    @BindView(R.id.btnans2)
    Button btnans2;
    @BindView(R.id.btnans3)
    Button btnans3;
    @BindView(R.id.btnans4)
    Button btnans4;
    @BindView(R.id.iv_flag)
    ImageView ivFlag;
    @BindView(R.id.tv_diem)
    TextView tvDiem;
    @BindView(R.id.tv_socau)
    TextView tvSocau;
    @BindView(R.id.tengido)
    TextView tengido;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    List<FlagResponse> list;
    int position = 1, point = 0;
    int max = 10;
    FlagResponse flag;
    public boolean isClick = false;
    MediaPlayer mediaPlayerRight, mediaPlayerWrong;

    @Override
    public void onStop() {
        super.onStop();
        SvgLoader.pluck().close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_hinh1);
        ButterKnife.bind(this);

        if (TrangChuActivity.loai2 == 1) {
            list = Model.getListLevel1(TrangChuActivity.flagResponseList);
        } else if (TrangChuActivity.loai2 == 2) {
            list = Model.getListLevel2(TrangChuActivity.flagResponseList);
        } else if (TrangChuActivity.loai2 == 3) {
            list = Model.getListLevel3(TrangChuActivity.flagResponseList);
        }

        Collections.shuffle(list);
        Log.d(TAG, "onCreate: " + list.size() + "/");

        loadData(position);
        point = 0;
        position = 1;

        Model.loadfontButton(this, btnans1, "UTM Avo.ttf");
        Model.loadfontButton(this, btnans2, "UTM Avo.ttf");
        Model.loadfontButton(this, btnans3, "UTM Avo.ttf");
        Model.loadfontButton(this, btnans4, "UTM Avo.ttf");

        prepareMusic();

    }

    public void loadData(int pos) {
        if (pos > max) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.end);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    loadThongBao();
                }
            });
            tvDiem.setText(point + "");
        } else {

            flag = list.get(pos - 1);
            List<String> flagResponseList = Model.getAnswerWord(list, pos - 1);
            Collections.shuffle(flagResponseList);

            SvgLoader.pluck()
                    .with(this)
                    .load(flag.getFlag(), ivFlag);

            btnans1.setText(flagResponseList.get(0));
            btnans2.setText(flagResponseList.get(1));
            btnans3.setText(flagResponseList.get(2));
            btnans4.setText(flagResponseList.get(3));

//            tengido.setText(flag.getName());
            tvSocau.setText(pos + "/" + max);
            tvDiem.setText(point + "");

        }
        isClick = false;
    }

    public void loadThongBao() {
        View v = LayoutInflater.from(this).inflate(R.layout.thongbao, null);
        TextView diem = v.findViewById(R.id.tv_tb_diem);
        TextView sodiem = v.findViewById(R.id.tv_tb_sodiem);
        TextView kethuc = v.findViewById(R.id.tv_ketthuc);
        ImageView menu = v.findViewById(R.id.iv_menu);
        ImageView reload = v.findViewById(R.id.iv_reload);
        ImageView next = v.findViewById(R.id.iv_next);

        Model.loadfont(this, kethuc, "UTM Avo.ttf");
        Model.loadfont(this, diem, "UTM Aurora.ttf");
        Model.loadfont(this, sodiem, "UTM Aurora.ttf");


        diem.setText(point + "");

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ManHinh1Activity.this, TrangChuActivity.class));
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Collections.shuffle(list);

                point = 0;
                position = 1;

                loadData(position);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM.dd.yyyy - HH:mm");
                Date date = new Date();

                SharedPreferences sharedPreferences = getSharedPreferences("SCORE", MODE_PRIVATE);
                int p = sharedPreferences.getInt("point", -1);

                if(p != -1){

                    if(point >= p){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("point", point);
                        editor.putString("time", simpleDateFormat.format(date));

                        editor.commit();

                    }

                }
                else{

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("point", point);
                    editor.putString("time", simpleDateFormat.format(date));

                    editor.commit();

                }

                finish();

            }
        });

    }

    public void prepareMusic(){

        mediaPlayerRight = MediaPlayer.create(this, R.raw.right);

        mediaPlayerWrong = MediaPlayer.create(this, R.raw.wrong);

    }

    public void loadMusic(boolean is){

        if(is){

            mediaPlayerRight.start();
            mediaPlayerRight.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    loadData(position);
                }
            });

        }
        else{

            mediaPlayerWrong.start();
            mediaPlayerWrong.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    loadData(position);
                }
            });

        }

    }

    @OnClick({R.id.btnans1, R.id.btnans2, R.id.btnans3, R.id.btnans4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnans1:
                if(!isClick){

                    isClick = true;
                    ++position;
                    if (flag.getName().equals(btnans1.getText().toString())) {
                        loadDiem();
                        loadMusic(true);
                    }
                    else{
                        loadMusic(false);
                    }

                }
                break;
            case R.id.btnans2:
                if(!isClick){

                    isClick = true;
                    ++position;
                    if (flag.getName().equals(btnans2.getText().toString())) {
                        loadDiem();
                        loadMusic(true);
                    }
                    else{
                        loadMusic(false);
                    }

                }
                break;
            case R.id.btnans3:
                if(!isClick){

                    isClick = true;
                    ++position;
                    if (flag.getName().equals(btnans3.getText().toString())) {
                        loadDiem();
                        loadMusic(true);
                    }
                    else{
                        loadMusic(false);
                    }

                }
                break;
            case R.id.btnans4:
                if(!isClick){

                    isClick = true;
                    ++position;
                    if (flag.getName().equals(btnans4.getText().toString())) {
                        loadDiem();
                        loadMusic(true);
                    }
                    else{
                        loadMusic(false);
                    }

                }
                break;
        }
    }

    public void loadDiem(){
        switch (TrangChuActivity.loai2){
            case 1:
                point += 10;
                break;
            case 2:
                point += 20;
                break;
            case 3:
                point += 30;
                break;
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
