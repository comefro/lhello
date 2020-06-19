package vo;

import java.math.BigDecimal;
import java.util.Date;

public class SaleDetail {
    private String lsh;

    private String barcode;

    private String productname;

    private BigDecimal price;

    private Integer count;

    private String operator;

    private Date saletime;
    

    public SaleDetail() {
	}

	public SaleDetail(String lsh, String barcode, String productname, BigDecimal price, Integer count, String operator,
			Date saletime) {
		this.lsh = lsh;
		this.barcode = barcode;
		this.productname = productname;
		this.price = price;
		this.count = count;
		this.operator = operator;
		this.saletime = saletime;
	}

	public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh == null ? null : lsh.trim();
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Date getSaletime() {
        return saletime;
    }

    public void setSaletime(Date saletime) {
        this.saletime = saletime;
    }

	@Override
	public String toString() {
		//"流水号\t条形码\t商品名称\t商品价格\t商品数量\t收银员\t销售时间\n"
		
		return lsh + "\t" + barcode + "\t" + productname + "\t" + price
				+ "\t" + count + "\t" + operator + "\t" + saletime + "\n";
	}
    
    
    
}