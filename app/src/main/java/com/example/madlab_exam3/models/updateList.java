package com.example.madlab_exam3.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.madlab_exam3.R;

import java.util.List;

public class updateList {
    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

        private List<Expense> expenseList;

        public ExpenseAdapter(List<Expense> expenseList) {
            this.expenseList = expenseList;
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_expense_item, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            Expense expense = expenseList.get(position);
            holder.categoryIcon.setImageResource(expense.getCategoryIcon());
            holder.categoryName.setText(expense.getCategoryName());
            holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
            holder.expenseDate.setText(expense.getDate());
        }

        @Override
        public int getItemCount() {
            return expenseList.size();
        }

        // Method to update the list dynamically
        public void updateList(List<Expense> newList) {
            expenseList = newList;
            notifyDataSetChanged();
        }

        public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
            ImageView categoryIcon;
            TextView categoryName, expenseAmount, expenseDate;

            public ExpenseViewHolder(View itemView) {
                super(itemView);
                categoryIcon = itemView.findViewById(R.id.categoryIcon);
                categoryName = itemView.findViewById(R.id.categoryName);
                expenseAmount = itemView.findViewById(R.id.expenseAmount);
                expenseDate = itemView.findViewById(R.id.expenseDate);
            }
        }
    }


}
