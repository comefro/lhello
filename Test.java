package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.ObjToDecimal;
import vo.Product;

public class Test {
	
	@org.junit.Test
	public  void testExcel() {
		try {
			XSSFWorkbook wb = new XSSFWorkbook("src/main/resources/product.xlsx");
			
			XSSFSheet sheet = wb.getSheetAt(0);
			for(Row row:sheet) {
				for(Cell cell : row)
				{
					cell.setCellType(CellType.STRING);
					System.out.println(cell.getStringCellValue());
				}
			}
			wb.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@org.junit.Test
	public void readDatafromTxt() throws IOException
	{
		File f = new File("src/main/resources/product.txt");
		//StringBuilder result = new StringBuilder();
		List<Product> list = new ArrayList<Product>();
		BufferedReader br = new BufferedReader(new FileReader(f));//构造一个BufferedReader类来读取文件
        try{
            String s = null;
            s = br.readLine();
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               // result.append(System.lineSeparator()+s);
            	String[] split = s.split("\t");
            	Product product = new Product(split[0],split[1],ObjToDecimal.getBigDecimal(split[2]),split[3]);
            	list.add(product);
            }
            
           System.out.println(list);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
        	br.close();
		}
        //return result.toString(); 
	}
	
	
}
