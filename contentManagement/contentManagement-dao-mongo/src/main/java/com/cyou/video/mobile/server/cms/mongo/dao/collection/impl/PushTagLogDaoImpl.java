package com.cyou.video.mobile.server.cms.mongo.dao.collection.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_TAG_STATE;
import com.cyou.video.mobile.server.cms.model.collection.PushTagExcuteStateInfo;
import com.cyou.video.mobile.server.cms.mongo.dao.collection.PushTagLogDao;

@Repository("pushTagLogDao")
public class PushTagLogDaoImpl implements PushTagLogDao {

  @Autowired
  private MongoOperations mongoTemplate;

  @Override
  public void inc(String threadName,int n) {
    mongoTemplate.findAndModify(new Query(new Criteria("threadName").is(threadName)), new Update().inc("threadNum", n),
        FindAndModifyOptions.options().upsert(true), PushTagExcuteStateInfo.class, "PushTagThreadInfo");
  }

  @Override
  public List<PushTagExcuteStateInfo> getThreadNum(String threadName) {
    Query query = new Query();
    Pattern pattern = Pattern.compile("^.*" + threadName + ".*$");
    query.addCriteria(Criteria.where("threadName").regex(pattern));
    return mongoTemplate.find(query, PushTagExcuteStateInfo.class, "PushTagThreadInfo");
  }

  @Override
  public void removeThreadLog() {
    mongoTemplate.remove(new Query().addCriteria(new Criteria("name").ne("sysThreadNum")), "PushTagThreadInfo");
  }

  @Override
  public void setThreadTotal(int total) {
    mongoTemplate.save(new PushTagExcuteStateInfo("threadTotal", null, total), "PushTagThreadInfo");
  }

  @Override
  public void setSysThreadNum(int num) {
    PushTagExcuteStateInfo p = mongoTemplate.findOne(new Query().addCriteria(new Criteria("name").is("sysThreadNum")),
        PushTagExcuteStateInfo.class, "PushTagThreadInfo");
    if(p == null) {
      p = new PushTagExcuteStateInfo("sysThreadNum", null, num);
    }
    else {
      p.setThreadNum(num);
    }
    mongoTemplate.save(p, "PushTagThreadInfo");
  }

  @Override
  public void updateWaiting() {
     mongoTemplate.updateMulti(new Query().addCriteria(new Criteria("state").is(PUSH_SEND_TAG_STATE.RUNNING.name())), new Update().set("state", PUSH_SEND_TAG_STATE.WAITING), "PushTagExcuteStateInfo");
  }
  
  @Override
  public PushTagExcuteStateInfo getSysThreadNum() {
    return mongoTemplate.findOne(new Query().addCriteria(new Criteria("name").is("sysThreadNum")),
        PushTagExcuteStateInfo.class, "PushTagThreadInfo");
    
  }
  
  @Override
  public int getThreadTotal() {
    Query q = new Query();
    q.addCriteria(Criteria.where("name").is("threadTotal"));
    PushTagExcuteStateInfo p = mongoTemplate.findOne(q, PushTagExcuteStateInfo.class, "PushTagThreadInfo");
    if(p == null)
      return 0;
    else
      return p.getThreadNum();
  }
}
