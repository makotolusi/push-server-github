package com.cyou.video.mobile.server.cms.dao.sys.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.sys.ManagerLogDao;
import com.cyou.video.mobile.server.cms.model.sys.ManagerLog;

/**
 * 用户日志持久化实现
 * 
 * @author zs
 */
@Repository("userLogInfoDao")
public class ManagerLogDaoImpl implements ManagerLogDao {

  private static final BeanPropertyRowMapper<ManagerLog> rowMapper = new BeanPropertyRowMapper<ManagerLog>(
      ManagerLog.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public int createUserLog(final ManagerLog log) throws Exception {
    final String sql = "INSERT INTO cms_sys_managerLog VALUES(?,?,?,?,?,?,?,?,?,?)";
    KeyHolder key = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++pos, log.getId());
        ps.setInt(++pos, log.getType());
        ps.setString(++pos, log.getName());
        ps.setString(++pos, log.getDesc());
        ps.setString(++pos, log.getParams());
        ps.setString(++pos, log.getUrl());
        ps.setString(++pos, log.getModelName());
        ps.setTimestamp(++pos, new Timestamp(log.getCreateDate().getTime()));
        ps.setString(++pos, log.getCreateUser());
        ps.setString(++pos, log.getUserIp());
        return ps;
      }
    }, key);
    return key.getKey().intValue();
  }

  @Override
  public int countUserLog(int type, String userName, String modelName, Date from, Date to) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "select count(*) from cms_sys_managerLog where 1=1 ";
    if(type > 0) {
      param.add(type);
      sql += " and type = ? ";
    }
    if(StringUtils.isNotBlank(userName)) {
      param.add("%" + userName + "%");
      sql += " and create_user like ? ";
    }
    if(StringUtils.isNotBlank(modelName)) {
      param.add("%" + modelName + "%");
      sql += " and model_name like ? ";
    }
    if(from != null) {
      param.add(from);
      sql += " and create_date >= ? ";
    }
    if(to != null) {
      param.add(to);
      sql += " and create_date < ? ";
    }
    return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
  }

  @Override
  public List<ManagerLog> ListUserLog(int curPage, int pageSize, int type, String userName, String modelName, Date from, Date to) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "select * from cms_sys_managerLog where 1=1 ";
    if(type > 0) {
      param.add(type);
      sql += " and type = ? ";
    }
    if(StringUtils.isNotBlank(userName)) {
      param.add("%" + userName + "%");
      sql += " and create_user like ? ";
    }
    if(StringUtils.isNotBlank(modelName)) {
      param.add("%" + modelName + "%");
      sql += " and model_name like ? ";
    }
    if(from != null) {
      param.add(from);
      sql += " and create_date >= ? ";
    }
    if(to != null) {
      param.add(to);
      sql += " and create_date < ? ";
    }
    sql += " ORDER BY id DESC ";
    if(curPage >= 0 && pageSize > 0) {
      sql += " LIMIT ?, ?";
      param.add(curPage);
      param.add(pageSize);
    }
    return jdbcTemplate.query(sql, rowMapper, param.toArray());
  }

}
