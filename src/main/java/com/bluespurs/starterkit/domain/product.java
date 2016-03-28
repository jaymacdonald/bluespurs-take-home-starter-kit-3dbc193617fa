package com.bluespurs.starterkit.domain;

public class product {

	private   String _productName;
	private   double _bestPrice;
	private   String _currency;
	private   String _location;
	private String _RetailerName;
	
	public product()
	{
		this.setProductName("");
		//this.setBestPrice(0);
		this.setCurrency("");
		this.setLocation("");		
		this.set_RetailerName("");
	}
	
	public product (String productName, float bestPrice, String currency, String location, String retailerName)
	{
		this.setProductName(productName);
		this.setBestPrice(bestPrice);
		this.setCurrency(currency);
		this.setLocation(location);		
		this.set_RetailerName(retailerName);
	}
	
	public void setProductName(String productName) {
		this._productName = productName;
	}
	
	public String getProductName() {
		return _productName;
	}

	public double getBestPrice() {
		return _bestPrice;
	}

	public void setBestPrice(double bestPrice) {
		this._bestPrice = bestPrice;
	}

	public String getCurrency() {
		return _currency;
	}

	public void setCurrency(String currency) {
		this._currency = currency;
	}

	public String getLocation() {
		return _location;
	}

	public void setLocation(String location) {
		this._location = location;
	}

	public String get_RetailerName() {
		return _RetailerName;
	}

	public void set_RetailerName(String _RetailerName) {
		this._RetailerName = _RetailerName;
	}
}
