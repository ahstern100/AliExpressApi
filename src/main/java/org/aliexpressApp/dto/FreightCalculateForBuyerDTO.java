package org.aliexpressApp.dto;

public class FreightCalculateForBuyerDTO {
    private String skuId;
    private String cityCode;
    private String countryCode;
    private long productId;
    private long productNum;
    private String provinceCode;
    private String sendGoodsCountryCode;
    private String price;
    private String priceCurrency;

    // Getters and Setters
    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getProductNum() {
        return productNum;
    }

    public void setProductNum(long productNum) {
        this.productNum = productNum;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getSendGoodsCountryCode() {
        return sendGoodsCountryCode;
    }

    public void setSendGoodsCountryCode(String sendGoodsCountryCode) {
        this.sendGoodsCountryCode = sendGoodsCountryCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }
}
