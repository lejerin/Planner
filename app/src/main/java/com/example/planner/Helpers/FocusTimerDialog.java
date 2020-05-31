package com.example.planner.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.planner.R;

import java.util.Timer;
import java.util.TimerTask;


public class FocusTimerDialog extends Dialog{

    private Context context;
    private Dialog dialog;

    private Timer dismissTimer;

    private TimerDialogListener timerDialogListener;

    //인터페이스 설정
    public interface TimerDialogListener{
        void onPositiveClicked(Boolean isOk, int focus);
        void onExtendClicked(Boolean isExtend);
    }

    //호출할 리스너 초기화
    public void setDialogListener(TimerDialogListener customDialogListener){
        this.timerDialogListener = customDialogListener;
    }



    public FocusTimerDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.focus_extend_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setCanceledOnTouchOutside(false);

        final EditText focusText = dialog.findViewById(R.id.focusEdit);
        final SeekBar seekBar = dialog.findViewById(R.id.focusSeekBar);
        seekBar.setProgress(50);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                focusText.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        focusText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        focusText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    seekBar.setProgress(Integer.parseInt(focusText.getText().toString()));
                    return true;
                }
                return false;
            }
        });

        Button mDialogOk = dialog.findViewById(R.id.okBtn);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                timerDialogListener.onPositiveClicked(true,Integer.parseInt(focusText.getText().toString()));
                dismissTimer.cancel();
                dialog.cancel();
            }
        });

        Button mDialgExtend = dialog.findViewById(R.id.extendBtn);
        mDialgExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(context,"연장됩니다",Toast.LENGTH_LONG).show();
                timerDialogListener.onExtendClicked(true);
                dismissTimer.cancel();
                dialog.cancel();
            }
        });

        dismissTimer = new Timer();
        dismissTimer.schedule(new TimerTask() {
            @Override

            public void run() {

               // Toast.makeText(context,"제한시간 30초를 초과하여 자동 연장됩니다",Toast.LENGTH_LONG).show();
                timerDialogListener.onExtendClicked(true);
                dialog.cancel();

            }
        }, 30*1000);


        dialog.show();
    }

}
