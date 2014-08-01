package com.cyou.video.mobile.server.cms.mongo.dao.mapreduce;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_ITEM_TYPE;
import com.cyou.video.mobile.server.cms.model.collection.StatisticJobLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo2;

/**
 * 统计pv 目前只统计两种pv等统计增多再优化
 * 
 * @author lusi
 * 
 */
@Component
public class MapReduce {

	private Logger LOGGER = LoggerFactory.getLogger(MapReduce.class);

	@Autowired
	private MongoOperations mongoTemplate;

	/**
	 * 更新最后更新时间
	 * 
	 * @param jobLastUpdateTime
	 * @return
	 */
	public int updateLastUpdateTime(StatisticJobLastUpdateTime jobLastUpdateTime) {
		try {
			
			mongoTemplate.remove(new Query(Criteria.where("statisicJobName").is(jobLastUpdateTime.getStatisicJobName())),StatisticJobLastUpdateTime.class);
			mongoTemplate.insert(jobLastUpdateTime);
			return 1;
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 检查时间戳对象是否存在
	 * 
	 * @return
	 */
	public boolean lastUpdateTimeCollectionExists() {
		try {
			return mongoTemplate
					.collectionExists(StatisticJobLastUpdateTime.class);
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 得到最终更新时间
	 * 
	 * @return null为全量
	 */
	public StatisticJobLastUpdateTime getLastUpdateTime(String cellectionName) {
		StatisticJobLastUpdateTime lastUpdateTimeBean = null;
		if (this.lastUpdateTimeCollectionExists()) {// 存在时间戳
			lastUpdateTimeBean = mongoTemplate.findOne(new Query(Criteria
					.where("statisicJobName").is(cellectionName)),
					StatisticJobLastUpdateTime.class,
					"StatisticJobLastUpdateTime");
			if (lastUpdateTimeBean == null)
				return null;
		}
		return lastUpdateTimeBean;
	}

	public String getMap(String groupId) {
		return "function Map(){ var serviceName=''; if(this.serviceName!=null)	serviceName=this.serviceName; emit("
				+ groupId + ",{pv:1,state:0,serviceName:serviceName}); }";
	}

	public String getReduce() {
		return "function Reduce(key,values){  var total=0; var state=0; var serviceName=''; for(var i=0;i<values.length;i++){ total+=values[i].pv; if( values[i].serviceName!='') serviceName=values[i].serviceName;	if( values[i].state==1) state=1; if(values[i].state==2) state=2; } return {pv:total,state:state,serviceName:serviceName}; }";
	}

	/**
	 * xx用户 点击xx栏目 做了 xx操作的pv统计
	 */
	public Long getUserItemOperatePv() {
		// 统计开始
		String map = getMap("{uid:this.uid,serviceId:this.serviceId,itemType:this.itemType,operatorType:this.operatorType}");
		String reduce = getReduce();
		return this.getPv2(Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME, map,
				reduce, 1, Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * 栏目 做了 xx操作的pv统计
	 */
	public Long getItemOperatePv() {
		// 统计开始
		String map = getMap("{serviceId:this.serviceId,itemType:this.itemType,operatorType:this.operatorType}");
		String reduce = getReduce();
		return this.getPv2(Consts.COLLECTION_ITEM_OPERATE_PV_NAME, map, reduce,
				1, Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * 栏目 pv统计
	 */
	public Long getItemPv() {
		// 统计开始
		String map = getMap("{serviceId:this.serviceId,itemType:this.itemType}");
		String reduce = "function Reduce(key,values){  return {pv:1}; }";
		return this.getPv2(Consts.COLLECTION_ITEM_PV_NAME, map, reduce, 1,
				Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * 搜索关键词pv统计
	 */
	public Long getSearchKeyWordPv() {
		// 统计开始
		String map = getMap("{keyWord:this.keyWord}");
		String reduce = getReduce();
		return this.getPv2(Consts.COLLECTION_KEYWORD_PV_NAME, map, reduce, 0,
				Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * 用户搜索关键词pv统计
	 */
	public Long getUserSearchKeyWordPv() {
		// 统计开始
		String map = getMap("{uid:this.uid,keyWord:this.keyWord}");
		String reduce = getReduce();
		return this.getPv2(Consts.COLLECTION_USER_KEYWORD_PV_NAME, map, reduce,
				0, Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * 用户搜索关键词pv统计
	 */
	public Long getShareKeyWordPv() {
		// 统计开始
		String map = getMap("{keyWord:this.keyWord}");
		String reduce = getReduce();
		return this.getPv2(Consts.COLLECTION_SHARE_PV_NAME, map, reduce, 2,
				Consts.COLLECTION_CLIENT_LOG_NAME);
	}

	/**
	 * xx用户 点击xx栏目 做了 xx操作的pv统计
	 */
	public Long getPv(String pvName, String map, String reduce, int criteriaTL,
			String collectionName) {
		// 取得最新记录时间
		StatisticJobLastUpdateTime lastUpdateTime = this
				.getLastUpdateTime(pvName);
		LOGGER.debug("lastUpdateTime " + lastUpdateTime);
		// 准备记录的时间
		Date newDate = new Date();
		LOGGER.debug("new date {} " + newDate);

		// 统计开始
		// 参数分别为：collection名，map函数的字符串，reduce函数的字符串，输出的表名[可选]，实体类
		Query query = new Query();
		if (lastUpdateTime != null) {
			query.addCriteria(Criteria.where("uploadDate").gt(
					lastUpdateTime.getLastUpdateTime())); // 增量
		}
		switch (criteriaTL) {
		case 0:
			query.addCriteria(Criteria.where("itemType").in(
					COLLECTION_ITEM_TYPE.SEARCH.index + ""));
			break;
		case 1:
			query.addCriteria(Criteria.where("itemType").ne(
					COLLECTION_ITEM_TYPE.SEARCH.index + ""));
			break;
		// case 2 :
		// query.addCriteria(Criteria.where("operatorType").ne(COLLECTION_OPERATOR_TYPE.SHARE.index));
		// break;
		default:
			break;
		}

		MapReduceResults<UserItemOperatePvMongo> results = mongoTemplate
				.mapReduce(query, collectionName, map, reduce,
						new MapReduceOptions().outputTypeReduce()
								.outputCollection(pvName),
						UserItemOperatePvMongo.class);
		// LOGGER.debug("static job name {} results size "+pvName, results
		// .getCounts().getOutputCount());

		// 记录更新时间
		if (lastUpdateTime == null) {
			lastUpdateTime = new StatisticJobLastUpdateTime(pvName, new Date());
		} else
			lastUpdateTime.setLastUpdateTime(newDate);
		this.updateLastUpdateTime(lastUpdateTime);

		return results.getCounts().getOutputCount();
	}

	/**
	 * xx用户 点击xx栏目 做了 xx操作的pv统计
	 */
	public Long getPv2(String pvName, String map, String reduce,
			int criteriaTL, String collectionName) {
		try {

			// 取得最新记录时间
			StatisticJobLastUpdateTime lastUpdateTime = this
					.getLastUpdateTime(pvName);
			Query query = new Query();
			// 记录更新时间
			if (lastUpdateTime != null){//已经有更新时间
				query.addCriteria(Criteria.where("uploadDate").gt(
						lastUpdateTime.getLastUpdateTime())); // 增量
				mongoTemplate.remove(new Query(Criteria.where("statisicJobName").is(lastUpdateTime.getStatisicJobName())),StatisticJobLastUpdateTime.class);//删除已有的
			
				
			}else
				lastUpdateTime=new StatisticJobLastUpdateTime(pvName, new Date());
			
			mongoTemplate.insert(lastUpdateTime);
			LOGGER.info("lastUpdateTime {}" + lastUpdateTime.getLastUpdateTime());//插入更新
//			this.updateLastUpdateTime(lastUpdateTime);

			// 统计开始
		
			switch (criteriaTL) {
			case 0:
				query.addCriteria(Criteria.where("itemType").in(
						COLLECTION_ITEM_TYPE.SEARCH.index + ""));
				break;
			case 1:
				query.addCriteria(Criteria.where("itemType").ne(
						COLLECTION_ITEM_TYPE.SEARCH.index + ""));
				break;
			// case 2 :
			// query.addCriteria(Criteria.where("operatorType").ne(COLLECTION_OPERATOR_TYPE.SHARE.index));
			// break;
			default:
				break;
			}

			 mongoTemplate
					.mapReduce(
							query,
							collectionName,
							map,
							"function Reduce(key,values){  var total=0; var state=0; var serviceName=''; for(var i=0;i<values.length;i++){ total+=values[i].pv; if( values[i].serviceName!='') serviceName=values[i].serviceName;	if( values[i].state==1) state=1; if(values[i].state==2) state=2; }  return {pv:total,state:state,serviceName:serviceName}; }",
							new MapReduceOptions().outputTypeReduce()
									.outputCollection(pvName),
							UserItemOperatePvMongo2.class);
//			long size=results.getCounts().getOutputCount();
			LOGGER.info(pvName+" mapReduce success end ");


			return 0L;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("mr job exception : "+e.getMessage());
			return 0L;
		}
	}

}