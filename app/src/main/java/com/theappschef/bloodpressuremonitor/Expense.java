package com.theappschef.bloodpressuremonitor;

import java.io.Serializable;

/**
 * Created by expense on 07-11-2016.
 */

public class Expense implements Serializable{
    private String id;
    private String description;
    private String category;
    private String categoryColor;
    private double amount=0.0d;
    private String type;
    private String date;
    private String proofName="";
    private String proofUri="";
    private String paymentMode;
    private String bankName;


    public Expense() {
    }

    public Expense(String description, String paymentMode, String bankName, String category, String categoryColor,
                   double amount, String type, String proofName, String proofUri) {
        this.description = description;
        this.paymentMode=paymentMode;
        this.bankName=bankName;
        this.category = category;
        this.categoryColor = categoryColor;
        this.amount = amount;
        this.type=type;
        this.proofUri=proofUri;
        this.proofName=proofName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProofUri() {
        return proofUri;
    }

    public void setProofUri(String proofUri) {
        this.proofUri = proofUri;
    }

    public String getProofName() {
        return proofName;
    }

    public void setProofName(String proofName) {
        this.proofName = proofName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
