package driver;

import java.util.Scanner;

import sevice.UserService;
import vo.Users;

public class Driver {

	public static UserService userService = new UserService();

	@SuppressWarnings("all")
	public static void main(String[] args) throws Exception 
	{
		Scanner sc = new Scanner(System.in);
		try 
		{
			// 主界面
			while (true) 
			{
				System.out.println("请选择：1.登录    2.注册  3.退出");
				String choose = sc.next();
				if("1".equals(choose)) 
				{
					Users user = userService.login();
					if(user!=null) 
					{
						userService.showMenu(user);
					}
				}
				else if("2".equals(choose)) 
				{
					int res = userService.register();
					if(res>0) 
					{
						//注册成功
						System.out.println("请选择：1.登录    2.退出");
						String rchoose = sc.next();
						if("1".equals(rchoose))
						{
							Users user = userService.login();
							if(user!=null) 
							{
								userService.showMenu(user);
							}
						}
						else //除了1之外的任何字符都退出程序
						{
							break;
						}
					}
				}
				else //除了1,2之外的任何字符都退出程序
				{
					break;
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		} 
		finally 
		{
			sc.close();
			userService.getSqlSession().close();
		}

	}
}
