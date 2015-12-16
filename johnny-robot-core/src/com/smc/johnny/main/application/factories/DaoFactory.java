package com.smc.johnny.main.application.factories;

import com.smc.johnny.main.application.dao.TASDB2DAO;


public class DaoFactory {

	public static TASDB2DAO tasDao;

	public static TASDB2DAO getInstance()
	{
		if(tasDao==null)
		{
			tasDao = new TASDB2DAO();
		}

		return tasDao;
	}
}
