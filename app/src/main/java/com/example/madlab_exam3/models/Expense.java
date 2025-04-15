package com.example.madlab_exam3.models;

public class Expense {
    private int categoryIcon;
    private String categoryName;
    private double amount;
    private String date;

    public Expense(int categoryIcon, String categoryName, double amount, String date) {
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.amount = amount;
        this.date = date;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}

