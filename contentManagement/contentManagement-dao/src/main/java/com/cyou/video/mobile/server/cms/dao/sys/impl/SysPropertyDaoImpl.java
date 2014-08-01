package com.cyou.video.mobile.server.cms.dao.sys.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.sys.SysPropertyDao;
import com.cyou.video.mobile.server.cms.model.sys.SysProperty;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 系统参数持久化实现
 * @author zs
 */
@Repository("sysPropertyDao")
public class SysPropertyDaoImpl implements SysPropertyDao{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final BeanPropertyRowMapper<SysProperty> rowMapper = new BeanPropertyRowMapper<SysProperty>(SysProperty.class);
	
	@Override
	public int createSysProperty(final SysProperty sysPro) throws Exception {
		final String sql = "INSERT INTO cms_sys_sysProperty VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    jdbcTemplate.update(new PreparedStatementCreator() {
	      @Override
	      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        int pos = 0;
	        ps.setInt(++ pos, sysPro.getId());
	        ps.setString(++ pos, sysPro.getDesc());
	        ps.setString(++ pos, sysPro.getKey());
	        ps.setString(++ pos, sysPro.getValue());
	        ps.setString(++ pos, sysPro.getVersion());
	        ps.setInt(++ pos, sysPro.getType());
	        ps.setInt(++ pos, sysPro.getStatus());
	        ps.setInt(++ pos, sysPro.getPlat());
	        ps.setString(++ pos, sysPro.getChannel());
	        ps.setInt(++ pos, sysPro.getAppType());
	        ps.setString(++pos, sysPro.getAppSx());
	        return ps;
	      }
	    }, keyHolder);
	    return keyHolder.getKey().intValue();
	}

	@Override
  public List<SysProperty> listSysProperty(SysProperty sysPro) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_sys_sysProperty p WHERE 1 = 1";
    if(StringUtils.isNotBlank(sysPro.getKey())) {
      sql += " AND p.key like ?";
      param.add("%" + sysPro.getKey() + "%");
    }
    if(StringUtils.isNotBlank(sysPro.getVersion())) {
      sql += " AND p.version = ?";
      param.add(sysPro.getVersion().toString());
    }
    if(sysPro.getPlat() >= 0) {
      sql += " AND p.plat = ?";
      param.add(sysPro.getPlat());
    }
    if(sysPro.getType() >= 0) {
      sql += " AND p.type = ?";
      param.add(sysPro.getType());
    }
    if(sysPro.getStatus() >= 0) {
      sql += " AND p.status = ?";
      param.add(sysPro.getStatus());
    }
    if(sysPro.getAppType() > Constants.STATUS.IGNORE.getValue()) {
      sql += " AND p.app_type = ?";
      param.add(sysPro.getAppType());
    }
    if(StringUtils.isNotBlank(sysPro.getChannel())) {
      sql += " AND p.channel like ?";
      param.add("%" + sysPro.getChannel() + "%");
    }
    if(StringUtils.isNotBlank(sysPro.getAppSx())&& !StringUtils.equals("-1", sysPro.getAppSx())) {
      sql += " AND p.app_sx like ?";
      param.add("%" + sysPro.getAppSx() + "%");
    }
    sql += " ORDER BY p.id DESC";
    return jdbcTemplate.query(sql, rowMapper, param.toArray());

  }

	@Override
	public void updateSysProperty(final SysProperty sysPro) throws Exception {
		String sql = "UPDATE cms_sys_sysProperty p SET p.key = ? ,p.value = ? ,p.version = ?,p.status = ?,p.plat = ?,p.desc = ?,p.type = ?,p.channel = ?,app_sx = ? WHERE p.id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int pos = 0;
				ps.setString(++pos, sysPro.getKey());
				ps.setString(++pos, sysPro.getValue());
				ps.setString(++pos, sysPro.getVersion());
				ps.setInt(++pos, sysPro.getStatus());
				ps.setInt(++pos, sysPro.getPlat());
				ps.setString(++pos, sysPro.getDesc());
				ps.setInt(++pos, sysPro.getType());
				ps.setString(++pos, sysPro.getChannel());
				ps.setString(++pos, sysPro.getAppSx());
				ps.setInt(++pos, sysPro.getId());
			}
		});
	}

	@Override
	public void deleteSysProperty(int id) throws Exception {
		String sql =  "DELETE FROM cms_sys_sysProperty WHERE id = ?";
		jdbcTemplate.update(sql,id);
		
	}

	@Override
  public List<SysProperty> getSysProperty(SysProperty sysPro) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_sys_sysProperty p WHERE 1 = 1";
    if(StringUtils.isNotBlank(sysPro.getKey())) {
      sql += " AND p.key = ?";
      param.add(sysPro.getKey());
    }
    if(StringUtils.isNotBlank(sysPro.getVersion())) {
      sql += " AND p.version = ?";
      param.add(sysPro.getVersion().toString());
    }
    if(sysPro.getPlat() >= 0) {
      sql += " AND p.plat = ?";
      param.add(sysPro.getPlat());
    }
    if(sysPro.getType() >= 0) {
      sql += " AND p.type = ?";
      param.add(sysPro.getType());
    }
    if(sysPro.getAppType() > 0) {
      sql += " AND p.app_type = ?";
      param.add(sysPro.getAppType());
    }
    if(StringUtils.isNotBlank(sysPro.getChannel())) {
      sql += " AND p.channel = ?";
      param.add(sysPro.getChannel());
    }
    sql += " ORDER BY p.id DESC";
    return jdbcTemplate.query(sql, rowMapper, param.toArray());
  }

	@Override
	public SysProperty getSysPropertyById(final int id) throws Exception {
		SysProperty sys = null;
	    String sql = "SELECT * FROM cms_sys_sysProperty p WHERE p.id = ?";
	    try {
	       sys=jdbcTemplate.queryForObject(sql, rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
		}
	    return sys;
	}

}
