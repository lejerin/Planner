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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.Activities.MakePlanActivity;
import com.example.planner.Adapters.MakeRecommedPlanAdapter;
import com.example.planner.R;
import com.example.planner.Realm.Plans;

import io.realm.RealmResults;


public class MakePlanSearchDialog extends Dialog{

    private Context context;
    private Dialog dialog;
    private RealmResults<Plans> rp;
    private int recomendTime;


    public MakePlanSearchDialog(@NonNull Context context, RealmResults<Plans> rp, int recomendTime) {
        super(context);
        this.context = context;
        this.rp = rp;
        this.recomendTime = recomendTime;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.makeplan_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        System.out.println(rp);
        RecyclerView rc = dialog.findViewById(R.id.rcRecyclerView);
        MakeRecommedPlanAdapter rcAdapter = new MakeRecommedPlanAdapter(context,rp);

        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(rcAdapter);
        rc.setHasFixedSize(true);
        rc.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.divider));

        TextView textView = dialog.findViewById(R.id.recomendTime);
        textView.setText(getIntToTimeString(recomendTime) + " 입니다");




        Button mDialogOk = dialog.findViewById(R.id.okBtn);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(getContext(),"Okay" ,Toast.LENGTH_SHORT).show();


                dialog.cancel();
            }
        });


        dialog.show();
    }

    private String getIntToTimeString(int duration){

        long getHour = duration / (60 * 60);
        String hour = String.valueOf(getHour);

        // 분단위
        long getMin = duration - (getHour*(60 * 60)) ;
        String min = String.valueOf(getMin / (60)); // 몫

        // 초단위
        String second = String.valueOf((getMin % (60))); // 나머지

        // 시간이 한자리면 0을 붙인다
        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        // 분이 한자리면 0을 붙인다
        if (min.length() == 1) {
            min = "0" + min;
        }

        // 초가 한자리면 0을 붙인다
        if (second.length() == 1) {
            second = "0" + second;
        }

        return hour+"시간 " + min + "분";
    }

    public void cancelDialog(){
       if(dialog.isShowing()){
           dialog.cancel();
       }


    }
}
