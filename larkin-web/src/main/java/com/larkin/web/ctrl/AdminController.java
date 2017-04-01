package com.larkin.web.ctrl;

import com.google.common.collect.Maps;
import com.larkin.web.http.ApiResponseBody;
import com.larkin.web.http.ApiResponseBuilder;
import com.larkin.web.http.intercepter.AllowNoSignature;
import com.larkin.web.utils.RedisKeyUtil;
import com.larkin.web.utils.SpringAppConfig;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(value = "Admin", description = "admin接口")
@Controller
@RequestMapping(value = "admin")
public class AdminController {
	private static Logger logger = Logger.getLogger(AdminController.class);

	@Resource(name = "jedisPool") private JedisPool jedisPool;

	// =================================================================================================================
	// System Api : Health Check and Version Tracking
	// =================================================================================================================


	@AllowNoSignature
	@ApiOperation(value = "APP升级接口", notes = "APP升级接口")
	@RequestMapping(value = "upgrade", method = RequestMethod.POST)
	public void upgrade(@RequestParam(value = "app", defaultValue = "android") String app,
						HttpServletRequest request, HttpServletResponse response) {
		Jedis jedis = jedisPool.getResource();
		String appVersion = null;
		String appUrl = null;
		String appUpgradeLevel = null;
		String appUpgradeDesc = null;
		try {
			appVersion = jedis.get(RedisKeyUtil.getAppVersion(app));
			appUrl = jedis.get(RedisKeyUtil.getAppDownloadUrl(app));
			appUpgradeLevel = jedis.get(RedisKeyUtil.getAppUpgradeLevel(app));
			appUpgradeDesc = jedis.get(RedisKeyUtil.getAppUpgradeDesc(app));
		} catch (Exception e){
			logger.error(e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("appVersion", appVersion);
		map.put("appUrl", appUrl);
		map.put("appUpgradeLevel", appUpgradeLevel);
		map.put("appUpgradeDesc", appUpgradeDesc);
		map.put("appOs", app);
		ApiResponseBuilder.build(response, new ApiResponseBody(map));
	}

	@ApiOperation(value = "版本跟踪", notes = "获取当前服务器生效的代码版本，包括分支和hash信息。")
	@RequestMapping(value = "/reversion", method = RequestMethod.GET)
	public void reversion(HttpServletRequest request, HttpServletResponse response) {
		SpringAppConfig configs[] = new SpringAppConfig[]{
				SpringAppConfig.APP_VERSION_MAVEN,
				SpringAppConfig.APP_VERSION_GIT_COMMIT,
				SpringAppConfig.APP_VERSION_GIT_BRANCH,
				SpringAppConfig.APP_VERSION_GIT_COMMIT_TIME
		};

		Map<String, Object> map = Maps.newLinkedHashMap();
		for (SpringAppConfig config : configs) {
			map.put(config.getKey(), config.getValue());
		}
		ApiResponseBuilder.build(response, new ApiResponseBody(map));
	}

	@ApiOperation(value = "服务器时间戳", notes = "获取当前服务器时间戳")
	@RequestMapping(value = "/timestamp", method = RequestMethod.GET)
	public void timestamp(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("timestamp", System.currentTimeMillis());
		ApiResponseBuilder.build(response, new ApiResponseBody(map));
	}

}
