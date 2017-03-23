package com.iteye.weimingtom.wce.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.db.SqliteDataSourceManager;
import com.iteye.weimingtom.wce.model.EssayInfo;

public class EssayService {
	private static final String TABLE_NAME = "essay";
	private static final String ID = "id";
	private static final String URL = "url";
	private static final String ESSAY_CONTENT = "essayContent";
	private static final String CREATE_TIME = "createTime";
	
	private static EssayService service;
	
	private EssayService() {
		
	}
	
	public static EssayService getInstance() {
		if (service == null){
			service = new EssayService();
		}
		return service;
	}
	
	public void create(Connection con) {
		final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				URL + " TEXT NOT NULL DEFAULT '', " + 
				ESSAY_CONTENT + " TEXT NOT NULL DEFAULT '', " +
				CREATE_TIME + " TEXT NOT NULL DEFAULT '' " +
				")";
		PreparedStatement stmt = null;
        if (con != null) {
			try {
				stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DBException("创建数据表错误：" + e.toString());
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
	
	public List<EssayInfo> getAll() {
		List<EssayInfo> result = new ArrayList<EssayInfo>();
		final String sql = "SELECT * FROM " + TABLE_NAME + 
			" ORDER BY " + 
			//ARTICLE_ID + " ASC, " + 
			ID + " ASC ";
		PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            while (rs.next()) {
            	EssayInfo info = new EssayInfo();
            	info.setId(rs.getInt(ID));
            	info.setUrl(rs.getString(URL));
            	info.setEssayContent(rs.getString(ESSAY_CONTENT));
            	info.setCreateTime(rs.getString(CREATE_TIME));
            	result.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("读取数据错误：" + e.toString());
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
        return result;
    }
	
	public void insert(EssayInfo info) {
		final String sql = "INSERT INTO " + TABLE_NAME + " (" + 
				URL + ", " +
				ESSAY_CONTENT + ", " +
				CREATE_TIME + 
			") VALUES (?,?,?) ";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setString(1, info.getUrl());
	        	stmt.setString(2, info.getEssayContent());
	        	stmt.setString(3, info.getCreateTime());
	        	result = stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("插入数据错误：" + e.toString());
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
	
	public void update(EssayInfo info) {
		final String sql = "UPDATE " + TABLE_NAME + " " + 
				"SET " + URL + " = ?, " +
				" " + ESSAY_CONTENT + " = ?, " +
				" " + CREATE_TIME + " = ?" +
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setString(1, info.getUrl());
	        	stmt.setString(2, info.getEssayContent());
	        	stmt.setString(3, info.getCreateTime());
	        	result = stmt.executeUpdate();
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
	
	public void delete(List<EssayInfo> list) {
		final String sql = "DELETE FROM " + TABLE_NAME + " " + 
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	for (EssayInfo info : list) {
	        	    stmt.setInt(1, info.getId());
	        	    result = stmt.executeUpdate();
	        	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("删除数据错误：" + e.toString());
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
