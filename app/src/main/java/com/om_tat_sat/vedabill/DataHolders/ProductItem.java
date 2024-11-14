package com.om_tat_sat.vedabill.DataHolders;

public class ProductItem {
    private String description;
    private String hsnCode;
    private String unit;
    private String mrp;
    private String quantity;
    private String listPrice;
    private String discount;
    private String cgst;
    private String sgst;

    // Constructors
    public ProductItem() {}

    public ProductItem(String description, String hsnCode, String unit, String mrp, String quantity, String listPrice, String discount, String cgst, String sgst) {
        this.description = description;
        this.hsnCode = hsnCode;
        this.unit = unit;
        this.mrp = mrp;
        this.quantity = quantity;
        this.listPrice = listPrice;
        this.discount = discount;
        this.cgst = cgst;
        this.sgst = sgst;
    }

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getMrp() { return mrp; }
    public void setMrp(String mrp) { this.mrp = mrp; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public String getListPrice() { return listPrice; }
    public void setListPrice(String listPrice) { this.listPrice = listPrice; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
    public String getCgst() { return cgst; }
    public void setCgst(String cgst) { this.cgst = cgst; }
    public String getSgst() { return sgst; }
    public void setSgst(String sgst) { this.sgst = sgst; }
}
