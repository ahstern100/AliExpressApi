package org.aliexpressApp.dto;

public class ProductItemDTO {
    private long product_count;
    private long product_id;
    private String sku_attr;

    // Getters and Setters
    public long getProduct_count() {
        return product_count;
    }

    public void setProduct_count(long product_count) {
        this.product_count = product_count;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public String getSku_attr() {
        return sku_attr;
    }

    public void setSku_attr(String sku_attr) {
        this.sku_attr = sku_attr;
    }
}
