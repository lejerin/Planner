package com.example.planner.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.planner.R;


public class FocusDialog extends Dialog{

    private Context context;
    private Dialog dialog;

    private CustomDialogListener customDialogListener;

    //인터페이스 설정
    public interface CustomDialogListener{
        void onPositiveClicked(Boolean isOk, int focus);
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }



    public FocusDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.focus_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
             //   Toast.makeText(getContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                int value = Integer.parseInt(focusText.getText().toString());
                customDialogListener.onPositiveClicked(true,value);
                dialog.cancel();
            }
        });


        dialog.show();
    }


    public void cancelDialog(){
       if(dialog.isShowing()){
           dialog.cancel();
       }


    }
}
