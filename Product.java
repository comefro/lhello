package vo;

import java.math.BigDecimal;

public class Product {
    private String barcode;

    private String productname;

    private BigDecimal price;

    private String supply;
    
    
    

    public Product() {
	}

	public Product(String barcode, String productname, BigDecimal price, String supply) {
		this.barcode = barcode;
		this.productname = productname;
		this.price = price;
		this.supply = supply;
	}

	public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname == null ? null : productname.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSupply() {
        return supply;
    }

    public void setSupply(String supply) {
        this.supply = supply == null ? null : supply.trim();
    }
}