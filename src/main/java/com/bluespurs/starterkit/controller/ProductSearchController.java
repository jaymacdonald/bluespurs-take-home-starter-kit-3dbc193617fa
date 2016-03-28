package com.bluespurs.starterkit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bluespurs.starterkit.domain.product;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

 
 
@RestController
public class ProductSearchController {
    public static final String INTRO = "The Bluespurs Product Search Controller.";
    public static final Logger log = LoggerFactory.getLogger(ProductSearchController.class);

    private static final String _URLWalmart = "http://api.walmartlabs.com/v1/search?apiKey=rm25tyum3p9jm9x9x7zxshfa&query=%s&facet=on";
    private static final String _URLBestBuy = "http://api.bestbuy.com/v1/products((search=%s)&categoryPath.id=pcmcat209000050006)?show=name,sku,salePrice,regularPrice&format=json&apiKey=pfe9fpy68yg28hvvma49sc89";
    
    /**
     * This is the product search page.
     * It takes the "name" parameter and searches for the lowest price between Walmart and BestBuy
     */
    @RequestMapping("/product/search")
    public product search(@RequestParam(value="name") String productName) {
        
    	log.info("Product search page - product name: " + productName);
        
        product objProduct;
        objProduct = getBestPrice(productName);
        
        return objProduct;
    }
    
    private product getBestPrice(String productName)
    {
    	product returnProduct = new product();
    	product objProductWalmart = new product();  
        product objProductBestBuy = new product(); 
        
        String strURLWalmart = "";
        String strURLBestBuy = "";
        
		try {
			strURLWalmart = String.format(_URLWalmart,	URLEncoder.encode(productName, "UTF-8") );
			 objProductWalmart = ParseRetailSearchResult(GetRetailerResponse(strURLWalmart, productName), "Walmart","items","salePrice","msrp", "name");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
		//The BestBuy API doesn't like concatenated strings using "+" and multiple search items have to be separated by a "&search=".
		strURLBestBuy = String.format(_URLBestBuy, productName.replace(" ","&search="));
		 objProductBestBuy = ParseRetailSearchResult(GetRetailerResponse(strURLBestBuy, productName), "BestBuy","products","salePrice","regularPrice", "name");
            	
		if (objProductBestBuy.getBestPrice()==0)
		{
			returnProduct = objProductWalmart;
		}
		else
		{
			if (objProductWalmart.getBestPrice()<=objProductBestBuy.getBestPrice())
	    	{
	    		returnProduct = objProductWalmart;
	    	}
	    	else
	    	{
	    		returnProduct = objProductBestBuy;
	    	}	
		}
    	
    	
    	return returnProduct;
    	
    }
    
    private String GetRetailerResponse(String strURL, String strSearchQuery)
    {
    	StringBuilder builder = new StringBuilder();
    	
    	try {

    		
    		URL url = new URL(strURL);
    		
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept", "application/json");

    		if (conn.getResponseCode() != 200) {
    			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
    		}
   		
    		 
    		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

    		
    		for (String line = null; (line = br.readLine()) != null;) {
    		    builder.append(line).append("\n");
    		}
		   
    		
    		conn.disconnect();
  
    	  } catch (MalformedURLException e) {

    		e.printStackTrace();

    	  } catch (IOException e) {

    		e.printStackTrace();
    	  }	
    	
    	return builder.toString();
    	
    }
    
    private product ParseRetailSearchResult(String strJSON, String strRetailerName, String strProductsArrayName, String strPriceKey1, String strPriceKey2, String strProductNameKey)
    {
    	product returnProduct = new product();
    	
    	 double dlCurrentLowestPrice = 0;
    	 double dblTemp = 0;
         String strCurrentLowestPriceProductName = "";
         
                  
			try {
				  JSONObject jsonObject = new JSONObject(strJSON);
				
				// get an array from the JSON object
				JSONArray items=   jsonObject.getJSONArray(strProductsArrayName);
				
				// take the elements of the json array
				for(int i=0; i<items.length(); i++){
					JSONObject objCurrentProduct = items.getJSONObject(i);
					
					if (objCurrentProduct.has(strPriceKey1))
					{
						dblTemp = (double)(objCurrentProduct.get(strPriceKey1));	
					}
					else if(objCurrentProduct.has(strPriceKey2))
					{
						dblTemp = (double)(objCurrentProduct.get(strPriceKey2));
					}
					else 
					{
						//There is no price data for this item.
						dblTemp = -1;
					}
					
					if (dblTemp!=-1)
					{
						if (strCurrentLowestPriceProductName.isEmpty())
						{
							strCurrentLowestPriceProductName = (String) objCurrentProduct.get(strProductNameKey);
							dlCurrentLowestPrice =dblTemp;
						}
						else
						{
							if (dlCurrentLowestPrice>=dblTemp)
							{
								strCurrentLowestPriceProductName = (String) objCurrentProduct.get(strProductNameKey);
								dlCurrentLowestPrice = dblTemp;
							}
						}
					}
					
					System.out.println(objCurrentProduct.get(strProductNameKey));
					 
				}
				
				returnProduct.setBestPrice(dlCurrentLowestPrice);
				returnProduct.setCurrency("CAD");
				returnProduct.setProductName(strCurrentLowestPriceProductName);
				returnProduct.set_RetailerName(strRetailerName);
				
			} catch (ParseException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		    
			return returnProduct;
			
    }
    
}
