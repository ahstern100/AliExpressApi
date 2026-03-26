package org.aliexpressApp.dto;

import java.util.List;

public class PlaceOrderRequestDTO {
    private LogisticsAddressDTO logistics_address;
    private String out_order_id;
    private List<ProductItemDTO> product_items;

    // Getters and Setters
    public LogisticsAddressDTO getLogistics_address() {
        return logistics_address;
    }

    public void setLogistics_address(LogisticsAddressDTO logistics_address) {
        this.logistics_address = logistics_address;
    }

    public String getOut_order_id() {
        return out_order_id;
    }

    public void setOut_order_id(String out_order_id) {
        this.out_order_id = out_order_id;
    }

    public List<ProductItemDTO> getProduct_items() {
        return product_items;
    }

    public void setProduct_items(List<ProductItemDTO> product_items) {
        this.product_items = product_items;
    }
}
