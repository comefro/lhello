package sevice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.ProductMapper;
import dao.SaleDetailMapper;
import dao.UsersMapper;
import util.ObjToDecimal;
import util.MD5Util;
import util.MybatisUtils;
import vo.Product;
import vo.SaleDetail;
import vo.Users;

public class UserService {

	private SqlSession sqlSession = null;
	private UsersMapper userDAO = null;
	private SaleDetailMapper saleDetailDAO = null;
	private ProductMapper productDAO = null;

	public UserService() {
		this.sqlSession = MybatisUtils.getSqlSession();
		this.userDAO = sqlSession.getMapper(UsersMapper.class);
		this.saleDetailDAO = sqlSession.getMapper(SaleDetailMapper.class);
		this.productDAO = sqlSession.getMapper(ProductMapper.class);
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public Users login() throws Exception {
		int count = 0; // 登录次数
		Users user = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("欢迎使用纺大超市收银系统，请登陆：");
		while (true) {
			count++;
			if (count > 3) {
				System.out.println("最多只能尝试3次");
				break;
			}
			System.out.print("用户名：");
			String username = sc.next();
			System.out.print("密码：");
			String password = sc.next();
			Users uone = new Users();
			uone.setUsername(username);
			uone.setPassword(password);
			Map<String, Object> checkLogin = this.checkLogin(uone);
			int res = (int) checkLogin.get("code");
			if (res == 0) {
				// 登录成功
				user = (Users) checkLogin.get("user");
				break;
			} else if (res == 1) {
				System.out.println(checkLogin.get("msg"));
			}

		}

		return user;
	}

	// 登录检查，Map形式返回检查结果
	public Map<String, Object> checkLogin(Users user) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			Users foundUser = this.userDAO.selectByPrimaryKey(user.getUsername());
			if (foundUser == null) {
				mapResult.put("code", 1);
				mapResult.put("msg", "用户名不存在！");
			} else {
				String md5Password = MD5Util.md5(user.getPassword());
				if (!foundUser.getPassword().equals(md5Password)) {
					mapResult.put("code", 1);
					mapResult.put("msg", "密码不正确！");
				} else {
					mapResult.put("code", 0);
					mapResult.put("msg", "登录成功！");
					foundUser.setPassword(user.getPassword());// 转换为正常密码
					mapResult.put("user", foundUser);
				}
			}
		} catch (Exception e) {
			mapResult.put("code", 1);
			mapResult.put("msg", e.getMessage());
		} finally { // 无论是否有异常，都需要关闭数据库会话
			// sqlSession.close();
		}
		return mapResult;
	}

	// 注册用户
	public int register() {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入需要 注册的角色，1、收银员  2、管理员");
		int role = sc.nextInt();
		System.out.println("请输入用户名：");
		String username = sc.next();
		System.out.println("请输入姓名：");
		String chrname = sc.next();
		System.out.println("请输入密码：");
		String noPassword = sc.next();
		// 加密
		String password = MD5Util.md5(noPassword);
		Users user = new Users(username, chrname, password, "" + role);

		Users userTemp = userDAO.selectByPrimaryKey(username);
		int res = 0;
		if (userTemp != null) {
			System.out.println("该账号已注册！");
			return 0;
		} else {
			res = this.userDAO.insert(user);
			sqlSession.commit();
		}

		if (res != 0) {
			System.out.println("注册成功！");
			return 1;
		} else {
			System.out.println("注册失败！");
			return 0;
		}
	}

	public void showMenu(Users user) throws IOException {
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			System.out.println("===纺大超市收银系统===\n" + "1、收银\n" + "2、查询统计\n" + "3、商品维护\n" + "4、修改密码\n" + "5、数据导出\n"
					+ "6、退出\n" + "当前收银员：" + user.getChrname() + "\n" + "请选择（1-6）：");

			// 登录成功之后选择操作项
			String nextInt = sc.next();
			switch (nextInt) {
			case "1":
				cashier(user);
				break;
			case "2":
				selectCount(user);
				break;
			case "3":
				if ("2".equals(user.getRole())) {
					productMaintain(user);
				} else {
					System.out.println("您没有该项操作权限，请重选！");
				}
				break;
			case "4":
				alterPassword(user);
				break;
			case "5":
				if ("2".equals(user.getRole())) {
					dataOutput(user);
				} else {
					System.out.println("您没有该项操作权限，请重选！");
				}

				break;
			case "6":
				System.out.println("您确认退出系统吗（y/n）");
				String next = sc.next();
				if ("y".equals(next)) {
					flag = false;
				} else if ("n".equals(next)) {

				}
				break;
			default:
				System.out.println("请重新选择（1-6）项中的一项。");
				break;
			}
		}
	}

	public void cashier(Users user) {
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			System.out.println("请输入商品条形码（6位数字字符）：");
			String code = sc.next();
			// 正则
			String pattern = "[0-9]{6}";
			if (Pattern.matches(pattern, code)) {
				// 查询商品是否存在
				Product product = productDAO.selectByPrimaryKey(code);
				if (product != null) {
					System.out.println("输入商品数量：");
					int nextInt = sc.nextInt();
					Date date = new Date();
					SimpleDateFormat sd = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					String saleTime = sd.format(date);

					String newTime = saleTime.replace("-", "").replace(" ", "").replace(":", "");

					String lsh = newTime.substring(4, newTime.length());
					SaleDetail SD = new SaleDetail(lsh, product.getBarcode(), product.getProductname(),
							product.getPrice(), nextInt,
							user.getChrname(), date);
					int insert = saleDetailDAO.insert(SD);
					sqlSession.commit();
					if (insert > 0) {
						System.out.println("成功增加一笔销售数据");
						// 操作成功 ，返回主菜单
						// showMenu( user );
						flag = false;
					}
				} else {
					System.out.println("您输入的商品条形码不存在，请确认后重新输入");
					// 条形码输入正确但没有该商品 ，返回主菜单
					// showMenu( user );
					flag = false;
				}

			} else {
				System.out.println("条形码输入格式不正确，请重新输入");
			}

		}

	}

	public void selectCount(Users user) {
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			System.out.println("请输入销售日期（yyyy-mm-dd）:");
			String dateinfo = sc.next();
			String pattern = "\\d{4}-\\d{2}-\\d{2}";
			if (Pattern.matches(pattern, dateinfo)) {
				String[] strings = dateinfo.split("-");
				// 模糊查询
				List<SaleDetail> saleDetail = saleDetailDAO.queryDataByDate(dateinfo + "%");
				int totalMoney = 0;
				if (saleDetail.size() > 0) {
					System.out.println(strings[0] + "年" + strings[1] + "月" + strings[2] + "日" + "销售如下：\n" + "流水号\t\t"
							+ "商品名称\t\t" + "单价\t\t" + "数量\t\t\t" + "时间\t\t\t" + "收银员\n" + "========\t" + "========\t"
							+ "========\t" + "========\t\t" + "========\t\t" + "========");
					for (SaleDetail sd : saleDetail) {
						System.out.println(sd.getLsh() + "\t" + sd.getProductname() + "\t\t" + sd.getPrice() + "\t\t"
								+ sd.getCount() + "\t" + sd.getSaletime() + "\t\t" + sd.getOperator());
						totalMoney += (sd.getPrice().floatValue()) * sd.getCount();
					}
					System.out.println("销售总数:" + saleDetailDAO.getTotalRecord() + "商品总件:"
							+ saleDetailDAO.getTotalProductNum() + "销售总金额:" + totalMoney/*saleDetailDAO.getTotalMoney()*/);
					System.out.println("日期：" + strings[0] + "年" + strings[1] + "月" + strings[2] + "日");
					System.out.println("请按任意键返回主界面");
					sc.next();
					flag = false;
				} else {
					System.out.println("暂无销售记录！");
					flag = false;
				}

			} else {
				System.out.println("你输入的日期格式不正确，请重新输入");
			}
		}
	}

	public void productMaintain(Users user) throws IOException {
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			System.out.println("===纺大超市商品管理维护====\r\n" + "1、从excel中导入数据\r\n" + "2、从文本文件导入数据\r\n" + "3、键盘输入\r\n"
					+ "4、商品查询\r\n" + "5、返回主菜单\r\n" + "请选择（1-5）：" + "");
			String nextInt = sc.next();
			switch (nextInt) {
			case "1":
				readDatafromExcel();
				break;
			case "2":
				readDatafromTxt();
				break;
			case "3":
				readDatefromKeybord();
				break;
			case "4":
				queryProductByPname();
				break;
			case "5":
				flag = false;
				break;
			default:
				break;
			}

		}
	}

	public void alterPassword(Users user) {
		System.out.println("请输入当前用户的原密码：");
		Scanner sc = new Scanner(System.in);
		String passpwd = sc.next();
		if (user.getPassword().equals(passpwd)) {
			System.out.println("请设置新的密码：");
			// ^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$
			String newPass0 = sc.next();
			String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
			if (Pattern.matches(pattern, newPass0)) {
				System.out.println("请输入确认密码：");
				String newPass1 = sc.next();
				if (!newPass0.equals(newPass1)) {
					int count = 0;
					while (true) {
						if (++count > 2)
							return;
						System.out.println("两次输入的密码必须一致，请重新输入确认密码，三次错误后返回主菜单：");
						newPass1 = sc.next();
						if (newPass0.equals(newPass1))
							break;
					}
				}
				String pwd = MD5Util.md5(newPass0);
				user.setPassword(pwd);
				userDAO.updateByPrimaryKey(user);
				sqlSession.commit();
				System.out.println("您已成功修改密码，请谨记,退出系统后生效");

			} else {
				System.out.println("您的密码不符合复杂性要求（密码长度不少于6个字符，至少有一个小写字母，至少有一个大写字母，至少一个数字），请重新输入：");
			}
		} else {
			System.out.println("原密码输入不正确，请重新输入");
		}

	}

	public void dataOutput(Users user) throws IOException {
		Scanner sc = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			System.out.println(
					"===纺大超市销售信息导出====\r\n" + "1、导出到excel文件\r\n" + "2、导出到文本文件\r\n" + "3、返回主菜单\r\n" + "请选择（1-3）：");
			String nextInt = sc.next();
			switch (nextInt) {
			case "1":
				outputDateExcel();
				break;
			case "2":
				outputDateTxt();
				break;
			case "3":
				flag = false;
				break;
			default:
				break;
			}
		}
	}

	public void readDatafromExcel() throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook("src/main/resources/product.xlsx");
		XSSFSheet sheet = wb.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		List<Product> list = new ArrayList<Product>();
		for (int i = 1; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			row.getCell(0).setCellType(CellType.STRING);
			String barcode = row.getCell(0).getStringCellValue();
			row.getCell(1).setCellType(CellType.STRING);
			String productName = row.getCell(1).getStringCellValue();
			row.getCell(2).setCellType(CellType.STRING);
			BigDecimal price = ObjToDecimal.getBigDecimal(row.getCell(2).getStringCellValue());
			row.getCell(3).setCellType(CellType.STRING);
			String supply = row.getCell(3).getStringCellValue();
			Product pro = new Product(barcode, productName, price, supply);
			list.add(pro);
		}
		int count = 0;
		for (Product p : list) {
			Product pt = productDAO.selectByPrimaryKey(p.getBarcode());
			if (pt != null) {
				System.out.println("该商品已存入仓库中。");
			} else {
				productDAO.insert(p);
				sqlSession.commit();
				count++;
			}
		}
		System.out.println("成功从excel文件导入" + count + "条商品数据");
		wb.close();
	}

	public void readDatafromTxt() throws FileNotFoundException {
		File f = new File("src/main/resources/product.txt");
		// StringBuilder result = new StringBuilder();
		List<Product> list = new ArrayList<Product>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));// 构造一个BufferedReader类来读取文件
			String s = null;
			br.readLine();
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				// result.append(System.lineSeparator()+s);
				String[] split = s.split("\t");
				Product product = new Product(split[0], split[1], ObjToDecimal.getBigDecimal(split[2]), split[3]);
				list.add(product);
			}
			br.close();
			int count = 0;
			for (Product p : list) {
				Product pt = productDAO.selectByPrimaryKey(p.getBarcode());
				if (pt != null) {
					System.out.println("该商品已存入仓库中。");
				} else {
					productDAO.insert(p);
					sqlSession.commit();
					count++;
				}
			}

			System.out.println("成功从excel文件导入" + count + "条商品数据");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// return result.toString();
	}

	public void readDatefromKeybord() {
		Scanner sc = new Scanner(System.in);
		// 商品条形码，商品名称，单价，供应商
		System.out.println("请按如下格式输入：");
		System.out.println("商品条形码,商品名称,单价,供应商");
		String next = sc.next();
		String[] goodsInfo = next.split(",");
		Product product = new Product(goodsInfo[0], goodsInfo[1], ObjToDecimal.getBigDecimal(goodsInfo[2]),
				goodsInfo[3]);
		Product p = productDAO.selectByPrimaryKey(product.getBarcode());
		if (p != null) {
			System.out.println("条形码不能重复，请重新输入");
		} else {
			productDAO.insert(product);
			sqlSession.commit();
		}
	}

	public void queryProductByPname() {
		System.out.println("请输入查询的商品名称：");
		Scanner sc = new Scanner(System.in);
		String next = sc.next();
		// 模糊查询
		List<Product> list = productDAO.queryProductByPname("%" + next + "%");
		System.out.println("满足条件的记录总共" + list.size() + "条，信息如下：\r\n" + "序号	条形码	商品名称	单价	供应商	\r\n"
				+ "===		=====	=======	=	===		=====	");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(i + "\t" + list.get(i).getBarcode() + "\t" + list.get(i).getProductname() + "\t"
					+ list.get(i).getPrice() + "\t" + list.get(i).getSupply());
		}
	}

	public void outputDateExcel() throws IOException {
		List<SaleDetail> list = saleDetailDAO.queryAll();
		
		if(list.size()>0) 
		{
			
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
			String format = sd.format(date);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("销售明细");
			//设置标题样式
			XSSFCellStyle cellStyle = wb.createCellStyle();
			XSSFFont font = wb.createFont();
			font.setBold(true);
			cellStyle.setFont(font);
			
			
			XSSFRow row0 = sheet.createRow(0);
			XSSFCell cell0 = row0.createCell(0);
			cell0.setCellValue("流水号");
			cell0.setCellStyle(cellStyle);
			XSSFCell cell1 = row0.createCell(1);
			cell1.setCellValue("条形码");
			cell1.setCellStyle(cellStyle);
			
			XSSFCell cell2 = row0.createCell(2);
			cell2.setCellValue("商品名称");
			cell2.setCellStyle(cellStyle);
			
			XSSFCell cell3 = row0.createCell(3);
			cell3.setCellValue("商品价格");
			cell3.setCellStyle(cellStyle);
			
			XSSFCell cell4 = row0.createCell(4);
			cell4.setCellValue("商品数量");
			cell4.setCellStyle(cellStyle);
			
			XSSFCell cell5 = row0.createCell(5);
			cell5.setCellValue("收银员");
			cell5.setCellStyle(cellStyle);
			
			XSSFCell cell6 = row0.createCell(6);
			cell6.setCellValue("销售时间");
			cell6.setCellStyle(cellStyle);
			
			
			for(int i = 0;i<list.size();i++)
			{
				XSSFRow row = sheet.createRow(i+1);
				row.createCell(0).setCellValue(list.get(i).getLsh());
				row.createCell(1).setCellValue(list.get(i).getBarcode());
				row.createCell(2).setCellValue(list.get(i).getProductname());
				row.createCell(3).setCellValue(list.get(i).getPrice().toString());
				row.createCell(4).setCellValue(list.get(i).getCount());
				row.createCell(5).setCellValue(list.get(i).getOperator());
				row.createCell(6).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(list.get(i).getSaletime()));
			}
			
			FileOutputStream f = new FileOutputStream(new File("src/main/resources/saleDetail"+format+".xlsx"));
			wb.write(f);
			f.flush();
			f.close();
			wb.close();
			
			System.out.println("导出成功！");
			
		}else
		{
			System.out.println("暂无可导出数据！");
		}
		
		
	}

	public void outputDateTxt() throws IOException {
		List<SaleDetail> list = saleDetailDAO.queryAll();
		
		if(list.size()>0) 
		{
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
			String format = sd.format(date);
			File f = new File("src/main/resources/saleDetail"+format+".txt");
			FileWriter fileWriter = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fileWriter);
			String title = "流水号\t条形码\t商品名称\t商品价格\t商品数量\t收银员\t销售时间\n";
			bw.write(title);
			for(SaleDetail saleDetail : list)
			{
				bw.write(saleDetail.toString());
			}
			bw.close();
			System.out.println("导出成功！");
		}else
		{
			System.out.println("暂无可导出数据！");
		}
		
	}

}
