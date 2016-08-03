package com.iteye.weimingtom.wce.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iteye.weimingtom.wce.service.ContactService;
import com.iteye.weimingtom.wce.service.ArticleService;

public class SqliteDataSourceManager {
	private static final String DATABASE_FILENAME = "data.db";
	private static final int DATABASE_VERSION = 1;
	
	private static SqliteDataSourceManager manager;
	
	private Connection con;
	private String strError;
	
	public Connection getConnection() {
		if (con == null) {
			open();
		}
		return con;
	}
	
	private SqliteDataSourceManager(){
		
	}
	
	public static SqliteDataSourceManager getInstance(){
		if (manager == null){
			manager = new SqliteDataSourceManager();
		}
		return manager;
	}
	
	public void open() {
	    String driverClassName = "org.sqlite.JDBC";
		String url = "jdbc:sqlite:" + DATABASE_FILENAME;
	    try {
			File file = new File(DATABASE_FILENAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!file.isFile()) {
				throw new DBException("数据库文件不是文件：" + file.getAbsolutePath() + File.separator + file.getName());
			}
			if (!file.canRead()) {
				throw new DBException("数据库文件不可读取：" + file.getAbsolutePath() + File.separator + file.getName());
			}
			if (!file.canWrite()) {
				throw new DBException("数据库文件不写：" + file.getAbsolutePath() + File.separator + file.getName());
			}
			Class.forName(driverClassName);
			con = DriverManager.getConnection(url);
			initDatabase();
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
			strError = e.toString();
			throw new DBException("Java数据库驱动错误：" + strError);
	    } catch (SQLException e) {
			e.printStackTrace();
			strError = e.toString();
			throw new DBException("数据库连接错误：" + strError);
	    } catch (DBException e) {
	    	throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			strError = e.toString();
			throw new DBException("数据库连接异常：" + strError);
		}
	}
	
	public void close() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getError() {
		return strError != null ? strError : "";
	}
	
	private void initDatabase() {
		ContactService.getInstance().create(con);
		ArticleService.getInstance().create(con);
		System.out.println("db file version : " + getVersion(con));
        int currentVersion = getVersion(con);
        switch (currentVersion) {
            case 0:
            	currentVersion++;
            	
            case DATABASE_VERSION:
        }
        setVersion(con, DATABASE_VERSION);
	}
	
    private int getVersion(Connection con) {
		List<Long> result = new ArrayList<Long>();
		final String sql = "PRAGMA user_version;";
		PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	            rs = stmt.executeQuery();
	            while (rs.next()) {
	            	result.add(Long.valueOf(rs.getLong(1)));
	            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("查询数据错误：" + e.toString());
        } finally {
        	if (rs != null) {
        		try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        	if (stmt != null) {
        		try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
        if (result.size() > 0) {
        	return result.get(0).intValue();
        } else {
        	return 0;
        }
    }
    
	public void setVersion(Connection con, int version) {
		final String sql = "PRAGMA user_version = " + version;
		PreparedStatement stmt = null;
		try {
        	if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("更新数据错误：" + e.toString());
        } finally {
        	if (stmt != null) {
        		try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
    }
    
}
