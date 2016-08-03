package com.iteye.weimingtom.wce.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.db.SqliteDataSourceManager;
import com.iteye.weimingtom.wce.model.ContactInfo;

public class ContactService {
	private static final String TABLE_NAME = "contact";
	private static final String ID = "id";
	private static final String CONTACT_ID = "contactId";
	private static final String SESSION = "session";
	private static final String POST_TIME_BEGIN = "postTimeBegin";
	private static final String POST_TIME_END = "postTimeEnd";
	private static final String NUMBER = "number";
	private static final String NAME = "name";
	private static final String SEX = "sex";
	private static final String JOB = "job";
	private static final String CATALOG = "catalog";
	private static final String PHOTO = "photo";
	private static final String CARD_ID = "cardId";
	private static final String PHONE = "phone";
	private static final String FAX = "fax";
	private static final String EMAIL = "email";
	
	private static ContactService service;
	
	private ContactService(){
		
	}
	
	public static ContactService getInstance(){
		if (service == null){
			service = new ContactService();
		}
		return service;
	}
	
	public void create(Connection con) {
		final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				CONTACT_ID + " INTEGER NOT NULL, " +
				SESSION + " TEXT NOT NULL DEFAULT '', " +
				POST_TIME_BEGIN + " TEXT NOT NULL DEFAULT '', " + 
				POST_TIME_END + " TEXT NOT NULL DEFAULT '', " +
				NUMBER + " TEXT NOT NULL DEFAULT '', " +
				NAME + " TEXT NOT NULL DEFAULT '', " +
				SEX + " TEXT NOT NULL DEFAULT '', " +
				JOB + " TEXT NOT NULL DEFAULT '', " +
				CATALOG + " TEXT NOT NULL DEFAULT '', " +
				PHOTO + " TEXT NOT NULL DEFAULT '', " +
				CARD_ID + " TEXT NOT NULL DEFAULT '', " +
				PHONE + " TEXT NOT NULL DEFAULT '', " +
				FAX + " TEXT NOT NULL DEFAULT '', " +
				EMAIL + " TEXT NOT NULL DEFAULT '' " +
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
	
	public List<ContactInfo> getAll() {
		List<ContactInfo> result = new ArrayList<ContactInfo>();
		final String sql = "SELECT * FROM " + TABLE_NAME + 
				" ORDER BY " + 
				CATALOG + " ASC, " + 
				CONTACT_ID + " ASC, " + 
				ID + " ASC ";
		PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	            rs = stmt.executeQuery();
	            while (rs.next()) {
	            	ContactInfo info = new ContactInfo();
	            	info.setId(rs.getInt(ID));
	            	info.setContactId(rs.getInt(CONTACT_ID));
	            	info.setSession(rs.getString(SESSION));
	            	info.setPostTimeBegin(rs.getString(POST_TIME_BEGIN));
	            	info.setPostTimeEnd(rs.getString(POST_TIME_END));
	            	info.setNumber(rs.getString(NUMBER));
	            	info.setName(rs.getString(NAME));
	            	info.setSex(rs.getString(SEX));
	            	info.setJob(rs.getString(JOB));
	            	info.setCatalog(rs.getString(CATALOG));
	            	info.setPhoto(rs.getString(PHOTO));
	            	info.setCardId(rs.getString(CARD_ID));
	            	info.setPhone(rs.getString(PHONE));
	            	info.setFax(rs.getString(FAX));
	            	info.setEmail(rs.getString(EMAIL));
	            	result.add(info);
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
		return result;
    }
	
	public void insert(ContactInfo info) {
		final String sql = "INSERT INTO " + TABLE_NAME + " (" + 
				CONTACT_ID + ", " +
				SESSION + ", " +
				POST_TIME_BEGIN + ", " +
				POST_TIME_END + ", " + 
				NUMBER + ", " + 
				NAME + ", " + 
				SEX + ", " + 
				JOB + ", " + 
				CATALOG + ", " + 
				PHOTO + ", " + 
				CARD_ID + ", " + 
				PHONE + ", " + 
				FAX + ", " + 
				EMAIL + 
			") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setInt(1, info.getContactId());
	        	stmt.setString(2, info.getSession());
	        	stmt.setString(3, info.getPostTimeBegin());
	        	stmt.setString(4, info.getPostTimeEnd());
	        	stmt.setString(5, info.getNumber());
	        	stmt.setString(6, info.getName());
	        	stmt.setString(7, info.getSex());
	        	stmt.setString(8, info.getJob());
	        	stmt.setString(9, info.getCatalog());
	        	stmt.setString(10, info.getPhoto());
	        	stmt.setString(11, info.getCardId());
	        	stmt.setString(12, info.getPhone());
	        	stmt.setString(13, info.getFax());
	        	stmt.setString(14, info.getEmail());
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
	
	public void update(ContactInfo info) {
		final String sql = "UPDATE " + TABLE_NAME + " " + 
				"SET " + CONTACT_ID + " = ?, " +
				" " + SESSION + " = ?, " +
				" " + POST_TIME_BEGIN + " = ?, " +
				" " + POST_TIME_END + " = ?, " + 
				" " + NUMBER + " = ?, " + 
				" " + NAME + " = ?, " + 
				" " + SEX + " = ?, " + 
				" " + JOB + " = ?, " + 
				" " + CATALOG + " = ?, " + 
				" " + PHOTO + " = ?, " + 
				" " + CARD_ID + " = ?, " + 
				" " + PHONE + " = ?, " + 
				" " + FAX + " = ?, " + 
				" " + EMAIL + " = ? " + 
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
        	Connection con = SqliteDataSourceManager.getInstance().getConnection();
            if (con != null) {
	        	stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        	stmt.setInt(1, info.getContactId());
	        	stmt.setString(2, info.getSession());
	        	stmt.setString(3, info.getPostTimeBegin());
	        	stmt.setString(4, info.getPostTimeEnd());
	        	stmt.setString(5, info.getNumber());
	        	stmt.setString(6, info.getName());
	        	stmt.setString(7, info.getSex());
	        	stmt.setString(8, info.getJob());
	        	stmt.setString(9, info.getCatalog());
	        	stmt.setString(10, info.getPhoto());
	        	stmt.setString(11, info.getCardId());
	        	stmt.setString(12, info.getPhone());
	        	stmt.setString(13, info.getFax());
	        	stmt.setString(14, info.getEmail());
	        	stmt.setInt(15, info.getId());
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
	
	public void delete(List<ContactInfo> list) {
		final String sql = "DELETE FROM " + TABLE_NAME + " " + 
				"WHERE " + ID + " = ?";
		PreparedStatement stmt = null;
		int result;
        try {
    		Connection con = SqliteDataSourceManager.getInstance().getConnection();
        	if (con != null) {
        		stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            	for (ContactInfo info : list) {
        	        stmt.setInt(1, info.getId());
        	        stmt.addBatch();
	            }
            	stmt.executeBatch();
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
