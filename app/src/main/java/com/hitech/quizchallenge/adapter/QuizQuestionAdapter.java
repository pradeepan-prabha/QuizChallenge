package com.hitech.quizchallenge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hitech.quizchallenge.R;
import com.hitech.quizchallenge.pojo.QuizQuestionDetailsPojo;

import java.util.ArrayList;


public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.MyViewHolder> {
    private final ArrayList<QuizQuestionDetailsPojo> batterySwapArrayList;
    Context context;


    public QuizQuestionAdapter(ArrayList<QuizQuestionDetailsPojo> batterySwapArrayList, Context context) {
        this.context = context;
        this.batterySwapArrayList = batterySwapArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_quiz_question_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QuizQuestionDetailsPojo batteryDetail = batterySwapArrayList.get(position);
        holder.questionTv.setText(batteryDetail.getQuestion());
        holder.radioButton1.setText(batteryDetail.getChoiceA());
        holder.radioButton2.setText(batteryDetail.getChoiceB());
        holder.radioButton3.setText(batteryDetail.getChoiceC());
        holder.radioButton4.setText(batteryDetail.getChoiceD());
        holder.setIsRecyclable(false);

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1:
                        if (batteryDetail.getAns().equalsIgnoreCase("a")) {
                            batteryDetail.setSelected("1");
                        } else {
                            batteryDetail.setSelected("0");
                        }
                        // do operations specific to this selection
                        break;
                    case R.id.radioButton2:
                        if (batteryDetail.getAns().equalsIgnoreCase("b")) {
                            batteryDetail.setSelected("1");
                        } else {
                            batteryDetail.setSelected("0");
                        }
                        // do operations specific to this selection
                        break;
                    case R.id.radioButton3:
                        if (batteryDetail.getAns().equalsIgnoreCase("c")) {
                            batteryDetail.setSelected("1");
                        } else {
                            batteryDetail.setSelected("0");
                        }
                        // do operations specific to this selection
                        break;
                    case R.id.radioButton4:
                        if (batteryDetail.getAns().equalsIgnoreCase("d")) {
                            batteryDetail.setSelected("1");
                        } else {
                            batteryDetail.setSelected("0");
                        }
                        // do operations specific to this selection
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //Size of the batteries scanned in the arrayList
        return batterySwapArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionTv;
        private final RadioButton radioButton1;
        private final RadioButton radioButton2;
        private final RadioButton radioButton3;
        private final RadioButton radioButton4;
        private final RadioGroup radioGroup;
        private CardView swap_history_card_view;

        private MyViewHolder(View view) {
            super(view);
            questionTv = view.findViewById(R.id.questionTv);
            radioButton1 = view.findViewById(R.id.radioButton1);
            radioButton2 = view.findViewById(R.id.radioButton2);
            radioButton3 = view.findViewById(R.id.radioButton3);
            radioButton4 = view.findViewById(R.id.radioButton4);
            radioGroup = view.findViewById(R.id.radioGroup);
            swap_history_card_view = view.findViewById(R.id.swap_history_card_view);

        }
    }
}
