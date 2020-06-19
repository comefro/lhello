package dao;

import java.util.List;

import vo.Product;

public interface ProductMapper {
    int deleteByPrimaryKey(String barcode);

    List<Product> queryProductByPname(String pname);
    
    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(String barcode);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
}