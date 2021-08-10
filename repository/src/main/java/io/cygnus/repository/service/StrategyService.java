package io.cygnus.repository.service;

import static io.mercury.common.functional.Functions.booleanFun;
import static io.mercury.common.functional.Functions.listFun;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import io.cygnus.repository.dao.StrategyDao;
import io.cygnus.repository.dao.StrategyParamDao;
import io.cygnus.repository.entity.StrategyEntity;
import io.cygnus.repository.entity.StrategyParamEntity;
import io.cygnus.repository.service.base.BaseService;
import io.mercury.common.lang.Throws;
import io.mercury.common.log.CommonLoggerFactory;

@Service
public final class StrategyService extends BaseService {

	private final Logger log = CommonLoggerFactory.getLogger(StrategyService.class);

	@Resource
	private StrategyDao dao;

	@Resource
	private StrategyParamDao paramDao;

	/**
	 * 
	 * @return
	 */
	public List<StrategyEntity> getStrategys() {
		return listFun(() -> dao.findAll(), list -> list, e -> {
			log.error("query [StrategyEntity] exception", e);
		});
	}

	/**
	 * 
	 * @param strategyId
	 * @return
	 */
	public StrategyEntity getStrategy(int strategyId) {
		if (checkStrategyId(strategyId, log, "query [StrategyEntity] param error"))
			Throws.illegalArgument("strategyId");
		return dao.queryByStrategyId(strategyId);
	}

	/**
	 * 
	 * @param strategyName
	 * @return
	 */
	public List<StrategyEntity> getStrategy(String strategyName) {
		if (checkStrategyName(strategyName, log, "query [StrategyEntity] param error"))
			Throws.illegalArgument("strategyName");
		return dao.queryByStrategyName(strategyName);
	}

	/**
	 * 
	 * @param strategyId
	 * @return
	 */
	public List<StrategyParamEntity> getStrategyParams(int strategyId) {
		if (checkStrategyId(strategyId, log, "query [StrategyParamEntity] param error"))
			Throws.illegalArgument("strategyId");
		return listFun(() -> paramDao.queryByStrategyId(strategyId), list -> {
			if (CollectionUtils.isEmpty(list)) {
				log.warn("query [StrategyParamEntity] return 0 row, strategyId=={}", strategyId);
			} else
				log.info("query [StrategyParamEntity] where strategyId=={}, result row -> {}", strategyId, list.size());
			return list;
		}, e -> {
			log.error("query [StrategyParamEntity] exception, strategyId=={}", strategyId, e);
		});
	}

	/**
	 * 
	 * @param strategyName
	 * @return
	 */
	public List<StrategyParamEntity> getStrategyParams(String strategyName) {
		if (checkStrategyName(strategyName, log, "query [StrategyParamEntity] param error"))
			Throws.illegalArgument("strategyId");
		return listFun(() -> paramDao.queryByStrategyName(strategyName), list -> {
			if (CollectionUtils.isEmpty(list)) {
				log.warn("query [StrategyParamEntity] return 0 row, strategyName=={}", strategyName);
			} else
				log.info("query [StrategyParamEntity] where strategyName=={}, result row -> {}", strategyName,
						list.size());
			return list;
		}, e -> {
			log.error("query [StrategyParamEntity] exception, strategyName=={}", strategyName, e);
		});
	}

	/**
	 * 
	 * @param strategyName
	 * @return
	 */
	public List<StrategyParamEntity> getDefaultStrategyParams() {
		return getStrategyParams(0);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean putStrategy(StrategyEntity entity) {
		return booleanFun(() -> dao.save(entity), o -> {
			log.info("save [StrategyEntity] success -> {}", entity);
			return true;
		}, e -> {
			log.error("save [StrategyEntity] failure -> {}", entity, e);
			return false;
		});
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean putStrategyParam(StrategyParamEntity entity) {
		return booleanFun(() -> paramDao.save(entity), o -> {
			log.info("save [StrategyParamEntity] success -> {}", entity);
			return true;
		}, e -> {
			log.error("save [StrategyParamEntity] failure -> {}", entity, e);
			return false;
		});
	}

}
