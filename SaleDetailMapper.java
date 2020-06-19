package dao;

import java.util.List;

import vo.SaleDetail;

public interface SaleDetailMapper {
    int deleteByPrimaryKey(String lsh);
    int insert(SaleDetail record);

    int insertSelective(SaleDetail record);

    SaleDetail selectByPrimaryKey(String lsh);
    
    List<SaleDetail> queryDataByDate(String date);
    
    List<SaleDetail> queryAll();
    
    int getTotalRecord();
    int getTotalProductNum();
    //int getTotalMoney();

    int updateByPrimaryKeySelective(SaleDetail record);

    int updateByPrimaryKey(SaleDetail record);
}