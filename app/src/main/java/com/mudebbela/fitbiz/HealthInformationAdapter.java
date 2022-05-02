package com.mudebbela.fitbiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;
public class HealthInformationAdapter extends RecyclerView.Adapter<HealthInformationAdapter.ViewHolder> {

    List<HealthInformation> lvHealthInfo;

    public HealthInformationAdapter(List<HealthInformation> lvHealthInfo, Context applicationContext) {
        this.lvHealthInfo = lvHealthInfo;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.single_health_information_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthInformation healthInformation = lvHealthInfo.get(position);
        holder.setDate(healthInformation.getActivity());
        holder.setCalories(healthInformation.getStartDate().toString());

    }

    @Override
    public int getItemCount() {
        return lvHealthInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void  setDate(String date ){
            TextView tvDate = mView.findViewById(R.id.textViewDate);
            tvDate.setText(date);
        };

        public void setCalories(String calories){
            TextView tvCalories = mView.findViewById(R.id.textViewCalories);
            tvCalories.setText(calories);
        }
    }
}
