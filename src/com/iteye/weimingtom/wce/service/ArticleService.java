package com.iteye.weimingtom.wce.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.db.SqliteDataSourceManager;
import com.iteye.weimingtom.wce.model.ArticleInfo;

public class ArticleService {
	private static final String TABLE_NAME = "article";
	private static final String ID = "id";
	private static final String ARTICLE_ID = "articleId";
	private static final String NAME = "name";
	private static final String LOCATION = "location";
	private static final String CREATE_TIME = "createTime";
	private static final String MODIFY_TIME = "modifyTime";
	private static final String ARCHIVED = "archived";
	private static final String DESCRIPTION = "description";
	
	private static ArticleService service;
	
	private ArticleService() {
		
	}
	
	public static ArticleService getInstance() {
		if (service == null){
			service = new ArticleService();
		}
		return service;
	}
	
	public void create(Connection con) {
		final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				ARTICLE_ID + " INTEGER NOT NULL DEFAULT 0, " + 
				NAME + " TEXT NOT NULL DEFAULT '', " +
				LOCATION + " TEXT NOT NULL DEFAULT '', " +
				CREATE_TIME + " TEXT NOT NULL DEFAULT '', " +
				MODIFY_TIME + " TEXT NOT NULL DEFAULT '', " +
				ARCHIVED + " INTEGER NOT NULL DEFAULT 0, " +
				DESCRIPTION + " TEXT NOT NULL DEFAULT '' " +
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
	
	public List<ArticleInfo> getAll() {
		List<ArticleInfo> result = new ArrayList<ArticleInfo>();
		final String sql = "SELECT * FROM " + TABLE_NAME + 
			" ORDER BY " + 
			ARTICLE_ID + " ASC, " + 
			ID + " ASC ";
		PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            while (rs.next()) {
            	ArticleInfo info = new ArticleInfo();
            	info.setId(rs.getInt(ID));
            	info.setArticleId(rs.getInt(ARTICLE_ID));
            	info.setName(rs.getString(NAME));
            	info.setLocation(rs.getString(LOCATION));
            	info.setCreateTime(rs.getString(CREATE_TIME));
            	info.setModifyTime(rs.getString(MODIFY_TIME));
            	info.setArchived(rs.getInt(ARCHIVED));
            	info.setDescription(rs.getString(DESCRIPTION));
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
	
	public void insert(ArticleInfo info) {
		final String sql = "INSERT INTO " + TABLE_NAME + " (" + 
				ARTICLE_ID + ", " +
				NAME + ", " +
				LOCATION + ", " +
				CREATE_TIME + ", " +
				MODIFY_TIME + ", " +
				ARCHIVED + ", " +
				DESCRIPTION +
			") VALUES (?,?,?,?,?,?,?) ";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setInt(1, info.getArticleId());
	        	stmt.setString(2, info.getName());
	        	stmt.setString(3, info.getLocation());
	        	stmt.setString(4, info.getCreateTime());
	        	stmt.setString(5, info.getModifyTime());
	        	stmt.setInt(6, info.getArchived());
	        	stmt.setString(7, info.getDescription());
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
	
	public void update(ArticleInfo info) {
		final String sql = "UPDATE " + TABLE_NAME + " " + 
				"SET " + ARTICLE_ID + " = ?, " +
				" " + NAME + " = ?, " +
				" " + LOCATION + " = ?, " +
				" " + CREATE_TIME + " = ?, " +
				" " + MODIFY_TIME + " = ?, " +
				" " + ARCHIVED + " = ?, " +
				" " + DESCRIPTION + " = ? " +
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setInt(1, info.getArticleId());
	        	stmt.setString(2, info.getName());
	        	stmt.setString(3, info.getLocation());
	        	stmt.setString(4, info.getCreateTime());
	        	stmt.setString(5, info.getModifyTime());
	        	stmt.setInt(6, info.getArchived());
	        	stmt.setString(7, info.getDescription());
	        	stmt.setInt(8, info.getId());
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
	
	public void delete(List<ArticleInfo> list) {
		final String sql = "DELETE FROM " + TABLE_NAME + " " + 
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	for (ArticleInfo info : list) {
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
