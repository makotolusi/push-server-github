package com.cyou.video.mobile.server.cms.mongo.dao.collection.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.collection.ClientLogCollection;
import com.cyou.video.mobile.server.cms.model.collection.ClientLogBestWalkthroughCollection;
import com.cyou.video.mobile.server.cms.model.collection.PushTagLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.collection.StatisticJobLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo2;
import com.cyou.video.mobile.server.cms.model.push.PushTagCollection;
import com.cyou.video.mobile.server.cms.mongo.dao.collection.ClientLogCollectionDao;
import com.cyou.video.mobile.server.cms.mongo.dao.mapreduce.MapReduce;
import com.cyou.video.mobile.server.common.Constants;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository("clientLogCollectionDao")
public class ClientLogCollectionDaoImpl implements ClientLogCollectionDao {

  private Logger LOGGER = LoggerFactory.getLogger(ClientLogCollectionDaoImpl.class);

  @Autowired
  private MongoOperations mongoTemplate;

  @Autowired
  private MapReduce mapReduce;

  @Override
  public int insertBatch(List collections) {
    mongoTemplate.insertAll(collections);

    return collections.size();
  }

  @Override
  public void statisticsPv() {
    mapReduce.getUserSearchKeyWordPv();// 用户 搜索 pv
    mapReduce.getSearchKeyWordPv();// 搜索pv
    mapReduce.getItemPv();// share pv
    mapReduce.getItemOperatePv();// 欄目pv
    mapReduce.getUserItemOperatePv();// 用戶pv

  }

  @Override
  public void itemPv() {
    mapReduce.getItemPv();// share pv

  }

  @Override
  public int insertBatchS(List<LinkedHashMap<String, String>> collections) {
    try {
      mongoTemplate.insertAll(collections);
    }
    catch(Exception e) {

      return 0;
    }
    return collections.size();
  }

  @Override
  public int updateBatch(ClientLogCollection id, String collecitonName, int state) {
    try {
      if(Consts.COLLECTION_USER_GAME_PV.equals(collecitonName)){
        WriteResult wr = mongoTemplate.updateFirst(
            new Query(Criteria.where("_id.uid").is(id.getUid())
                .and("_id.gameCode").is(id.getGameCode())), Update.update("value.state", state), collecitonName);
        return wr.getN();
      }
      if(Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME.equals(collecitonName)){
        WriteResult wr = mongoTemplate.updateFirst(
            new Query(Criteria.where("_id.uid").is(id.getUid()).and("_id.serviceId").is(id.getServiceId())
                .and("_id.operatorType").is(id.getOperatorType())), Update.update("value.state", state), collecitonName);
        return wr.getN();
      }
      return 0;
    }
    catch(Exception e) {

      return 0;
    }
  }

  @Override
  public int updateClientLogCollection(ClientLogCollection id) {
    try {
      Update update = new Update();
      update.set("baiduUid", id.getPushToken());
      update.set("serviceName", id.getServiceName());
      WriteResult wr = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id.getId())), update,
          ClientLogCollection.class);
      return wr.getN();
    }
    catch(Exception e) {

      return 0;
    }
  }

  @Override
  public int updateServiceNameMulti(ClientLogCollection id) {
    try {
      Update update = new Update();
      update.set("serviceName", id.getServiceName());
      WriteResult wr = mongoTemplate.updateMulti(new Query(Criteria.where("serviceId").is(id.getServiceId())), update,
          ClientLogCollection.class);
      return wr.getN();
    }
    catch(Exception e) {

      return 0;
    }
  }

  @Override
  public List<ClientLogCollection> getClientCollectionBaiduIdNull(int page, int size, Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    // Criteria c=new Criteria("baiduUid");
    // c.exists(false);
    if(params.get("serviceId") != null)
      query.addCriteria(new Criteria().where("serviceId").is(params.get("serviceId").toString()));
    query.addCriteria(new Criteria().orOperator(Criteria.where("serviceName").exists(false),
        Criteria.where("serviceName").is(null), Criteria.where("serviceName").is("")));
    // .orOperator(Criteria.where("baiduUid").is(null)));
    // query.addCriteria(Criteria.where("serviceName").exists(false));
    // .orOperator(Criteria.where("serviceName").is(null)));
    return mongoTemplate.find(query, ClientLogCollection.class);
  }

  @Override
  public List<StatisticJobLastUpdateTime> getStatisticJobLastUpdateTime() {

    return mongoTemplate.find(new Query(), StatisticJobLastUpdateTime.class);
  }

  @Override
  public AggregationOutput distinctServiceId() {
    /* 创建 $match, 作用相当于query */
    DBObject match = new BasicDBObject();

    /* Group操作 */
    DBObject fields = new BasicDBObject();
    fields.put("s", "$serviceId");
    fields.put("i", "$itemType");
    DBObject fields2 = new BasicDBObject();
    fields2.put("$sum", 1);
    DBObject groupFields = new BasicDBObject("_id", fields);
    groupFields.put("number", fields2);
    // groupFields.put("AvgAge", new BasicDBObject("$avg", "$age"));
    // number : { $sum : 1 } }

    DBObject group = new BasicDBObject("$group", groupFields);

    DBObject sortf = new BasicDBObject();
    sortf.put("number", -1);
    DBObject sort = new BasicDBObject("$sort", sortf);

    /* 查看Group结果 */
    AggregationOutput output = mongoTemplate.getCollection(Consts.COLLECTION_CLIENT_LOG_NAME).aggregate(group, sort);

    return output;
    // return
    // mongoTemplate.getCollection(Consts.COLLECTION_CLIENT_LOG_NAME).aggregate(firstOp,
    // additionalOps).distinct("serviceId");
  }

  @Override
  public List<ClientLogCollection> getClientCollection(ClientLogCollection collections, int page, int size,
      Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    query.with(new Sort(Sort.Direction.DESC, "uploadDate"));

    query(params, query, "");

    return mongoTemplate.find(query, ClientLogCollection.class);
  }

  private void query(Map<String, Object> params, Query query, String tag) {
    if(params.get("dateFrom") != null) {
      if(params.get("dateTo") == null) {
        query.addCriteria(Criteria.where("operatorDate").lte(new Date())
            .gte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dateFrom").toString())));

      }
      else
        query.addCriteria(Criteria.where("operatorDate")
            .lte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dateTo").toString()))
            .gte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dateFrom").toString())));
    }
    if(params.get("dFrom") != null) {
      if(params.get("dTo") == null) {
        query.addCriteria(Criteria.where("uploadDate").lte(new Date())
            .gte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dFrom").toString())));

      }
      else
        query.addCriteria(Criteria.where("uploadDate")
            .lte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dTo").toString()))
            .gte(Constants.parseDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), params.get("dFrom").toString())));
    }
    if(params.get("userToken") != null)
      query.addCriteria(Criteria.where(tag + "uid").is(params.get("userToken").toString()));

    if(params.get("itemType") != null && !"999".equals(params.get("itemType"))) {
      if("id.".equals(tag)) tag = "value.";
      query.addCriteria(Criteria.where(tag + "itemType").is(params.get("itemType").toString()));
    }

    if(params.get("operatorType") != null && !"999".equals(params.get("operatorType"))) {
      // if ("id.".equals(tag))
      // tag = "value.";
      query.addCriteria(Criteria.where(tag + "operatorType").is(params.get("operatorType").toString()));
    }

    if(params.get("serviceId") != null) {
      Pattern pattern = Pattern.compile("^.*" + params.get("serviceId").toString() + ".*$");
      query.addCriteria(Criteria.where(tag + "serviceId").regex(pattern));
    }
    else if(params.get("serviceId2") != null)
      query.addCriteria(Criteria.where(tag + "serviceId").is(params.get("serviceId2").toString()));

    if(params.get("serviceName") != null) {
      if("id.".equals(tag)) tag = "value.";
      Pattern pattern = Pattern.compile("^.*" + params.get("serviceName").toString() + ".*$");
      query.addCriteria(Criteria.where(tag + "serviceName").regex(pattern));
    }
    else if(params.get("serviceName2") != null) {
      if("id.".equals(tag)) tag = "value.";
      query.addCriteria(Criteria.where(tag + "serviceName").is(params.get("serviceName2").toString()));

    }
    if(params.get("gameCode") != null) {
      query.addCriteria(Criteria.where("value.gameCode").is(params.get("gameCode").toString()));
    }

    if(params.get("keyWord") != null) {
      Pattern pattern = Pattern.compile("^.*" + params.get("keyWord").toString() + ".*$");
      if(Consts.COLLECTION_KEYWORD_PV_NAME.equals(params.get("cname"))) tag = "id.";
      query.addCriteria(Criteria.where(tag + "keyWord").regex(pattern));
    }

    if(params.get("otherWay") != null && !"999".equals(params.get("otherWay"))) {
      query.addCriteria(Criteria.where("otherWay").is(Integer.parseInt(params.get("otherWay").toString())));
    }

  }

  @Override
  public List getPVByName(String collectionName, int page, int size, Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    query.with(new Sort(Sort.Direction.DESC, "value.pv"));

    query(params, query, "id.");
    if(params.get("tagType") != null && "1".equals(params.get("tagType")))
      query.addCriteria(new Criteria().where("_id.operatorType").ne("0"));
    return mongoTemplate.find(query, UserItemOperatePvMongo2.class, collectionName);
  }

  @Override
  public List<UserItemOperatePvMongo2> getUserItemNotEqual1(String collectionName, int page, int size,
      Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    // query.with(new Sort(Sort.Direction.DESC, "value"));

    if(params.get("state") != null) query.addCriteria(new Criteria().orOperator(Criteria.where("value.state").is(0)));
    query.addCriteria(Criteria.where("_id.operatorType").nin(new Object[]{"5", "6"}));
    return mongoTemplate.find(query, UserItemOperatePvMongo2.class, collectionName);
  }

  @Override
  public List<UserItemOperatePvMongo2> getUserItemOperatePvMongo2(int page, int size, Query query, String name) {
    query.limit(size);
    query.skip(page);
    return mongoTemplate.find(query, UserItemOperatePvMongo2.class, name);
  }

  @Override
  public List<ClientLogBestWalkthroughCollection> getClientLogWalkthroughCollection(int page, int size, Query query, String name) {
    query.limit(size);
    query.skip(page);
    return mongoTemplate.find(query, ClientLogBestWalkthroughCollection.class, name);
  }
  
  @Override
  public List<PushTagCollection> getTagCollection(int page, int size, Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    query.with(new Sort(Sort.Direction.DESC, "pv"));

    if(params.get("tagType") != null)
      query.addCriteria(Criteria.where("tagType").is(params.get("tagType").toString()));

    if(params.get("tagName") != null) {
      Pattern pattern = Pattern.compile("^.*" + params.get("tagName").toString() + ".*$");
      query.addCriteria(Criteria.where("tagName").regex(pattern));
    }

    return mongoTemplate.find(query, PushTagCollection.class);
  }

  @Override
  public long getTagCollectionCount(int page, int size, Map<String, Object> params) {
    Query query = new Query();
    query.limit(size);
    query.skip(page);
    query.with(new Sort(Sort.Direction.DESC, "pv"));

    if(params.get("tagType") != null)
      query.addCriteria(Criteria.where("tagType").is(params.get("tagType").toString()));

    if(params.get("tagName") != null) {
      Pattern pattern = Pattern.compile("^.*" + params.get("tagName").toString() + ".*$");
      query.addCriteria(Criteria.where("tagName").regex(pattern));
    }

    return mongoTemplate.count(query, PushTagCollection.class);
  }

  @Override
  public int insertPushTags(List<PushTagCollection> collections) {
    try {
      mongoTemplate.insertAll(collections);
    }
    catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
    return collections.size();
  }

  @Override
  public int insert(Object o) {
    try {
      mongoTemplate.insert(o);
    }
    catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
    return 1;
  }

  @Override
  public void save(Object o) {
    mongoTemplate.save(o);
  }

  @Override
  public Object updateInc(String incField, Query q, Class cla) {
    return mongoTemplate.findAndModify(q, new Update().inc(incField, 1), cla);
  }

  @Override
  public Object findByField(Class o, String field, String value) {
    try {
      return mongoTemplate.findOne(new Query(Criteria.where(field).is(value)), o);
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void remove(Object o) {
    mongoTemplate.remove(o);
  }

  @Override
  public void remove(Class o, Query query) {
    mongoTemplate.remove(query, o);
  }

  @Override
  public List<Object> findByCondition(Class o, Query query) {
    try {
      return mongoTemplate.find(query, o);
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public long countByCondition(Class o, Query query) {
    try {
      return mongoTemplate.count(query, o);
    }
    catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  @Override
  public int removeAllPushTags() {
    try {
      mongoTemplate.remove(new Query(), PushTagCollection.class);
    }
    catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
    return 1;
  }

  @Override
  public List<StatisticJobLastUpdateTime> getPVLastUpdate(String collectionName, Map<String, Object> params) {
    Query query = new Query();

    // query.addCriteria(Criteria.where("statisicJobName").is(collectionName));

    return mongoTemplate.find(query, StatisticJobLastUpdateTime.class);
  }

  @Override
  public long getCount(String name) {
    Query query = new Query();

    return mongoTemplate.count(query, name);
  }

  @Override
  public long getCount(Class name) {
    Query query = new Query();

    return mongoTemplate.count(query, name);
  }

  @Override
  public long getCountByQuery(Query query, String name) {

    return mongoTemplate.count(query, name);
  }

  @Override
  public long getPVCountByName(String collectionName, Map<String, Object> params, String tag) {
    Query query = new Query();
    query(params, query, tag);
    return mongoTemplate.count(query, collectionName);
  }

  @Override
  public int updatePushTagLastUpdateTime(PushTagLastUpdateTime tagLastUpdateTime) {
    try {
      mongoTemplate.remove(new Query(Criteria.where("name").is(tagLastUpdateTime.getName())),
          PushTagLastUpdateTime.class);
      mongoTemplate.insert(tagLastUpdateTime);
      return 1;
    }
    catch(Exception e) {
      return 0;
    }

  }

  @Override
  public PushTagLastUpdateTime getPushTagLastUpdateTime(String cellectionName) {
    PushTagLastUpdateTime lastUpdateTimeBean = mongoTemplate.findOne(new Query(Criteria.where("name")
        .is(cellectionName)), PushTagLastUpdateTime.class);
    return lastUpdateTimeBean;
  }

}
