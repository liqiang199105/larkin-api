package com.larkin.web.http.intercepter;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.larkin.web.http.exception.ApiError;
import com.larkin.web.http.exception.ApiException;
import com.larkin.web.utils.RedisKeyUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = Logger.getLogger(ApiInterceptor.class);

    private String enableUrlSignature;

    @Resource(name = "jedisPool") private JedisPool jedisPool;



    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        // ============================================================
        // Swagger header
        // ============================================================
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setCharacterEncoding("UTF-8"); //设置编码格式
        //  Annotation
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)){
            logger.info(request.getRequestURL() + "?" + request.getParameterMap());
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if (loginRequired != null && loginRequired.value() == true){
                validateToken(request);
            }

            if (Boolean.valueOf(enableUrlSignature)){
                AllowNoSignature allowNoSignature = handlerMethod.getMethodAnnotation(AllowNoSignature.class);
                validateSystemParams(request, null != allowNoSignature);
            }
        }
        return super.preHandle(request, response, handler);
    }

    public Map<String,String> getParametersForSign(HttpServletRequest request) throws Exception {
         Map<String,String> parameters =  Maps.newLinkedHashMap();
        for (String parameter : request.getParameterMap().keySet()) {
            if (!ApiSignatureUtil.SIGNATURE.equals(parameter)){
                parameters.put(parameter, Joiner.on(",").join(request.getParameterMap().get(parameter)));
            }
        }
        return parameters;
    }

    public void setEnableUrlSignature(String enableUrlSignature) {
        this.enableUrlSignature = enableUrlSignature;
    }

    // =================================================================================================================
    //  private functions
    // =================================================================================================================
    private void validateSystemParams(HttpServletRequest request, boolean allowNoSignature) throws ApiException {
        Preconditions.checkNotNull(request);
        if (!allowNoSignature){
            return;
        }
        final String appKey = request.getParameter(ApiSignatureUtil.APP_KEY);
        if(Strings.isNullOrEmpty(appKey)) {
            throw new ApiException(ApiError.INVALID_APP_KEY);
        }
        if(null == ApiClient.getByKey(appKey)) {
            throw new ApiException(ApiError.INVALID_APP_CLIENT);
        }
        final String timestamp = request.getParameter(ApiSignatureUtil.TIMESTAMP);
        if(Strings.isNullOrEmpty(timestamp)) {
            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER.withParams(ApiSignatureUtil.TIMESTAMP));
        }
        long current = System.currentTimeMillis();
        long timestampAsLong = Long.valueOf(timestamp);
        if (Math.abs(current - timestampAsLong) > 1000 * 60 * 10){
            throw new ApiException(ApiError.API_TIMESTAMP_OUT_OF_RANGE);
        }

//        final String nonceStr = request.getParameter(ApiSignatureUtil.NONCE_STRING);
//        if (Strings.isNullOrEmpty(nonceStr)){
//            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER.withParams(ApiSignatureUtil.NONCE_STRING));
//        }
        final String signature = request.getParameter(ApiSignatureUtil.SIGNATURE);
        if(Strings.isNullOrEmpty(signature)) {
            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER.withParams(ApiSignatureUtil.SIGNATURE));
        }
        try {
            Map<String,String> parameters = getParametersForSign(request);
            parameters.put(ApiSignatureUtil.APP_SECRET, ApiClient.getByKey(appKey).getSecretKey());
            final String calculatedSignature = ApiSignatureUtil.generateSignature(parameters);
            if(!calculatedSignature.equals(signature)) {
                throw new ApiException(ApiError.INVALID_SIGNATURE);
            }
        } catch (Exception ex) {
            logger.error(ex);
            throw new ApiException(ApiError.INVALID_SIGNATURE, ex.getMessage());
        }
    }

    /*
    * calculatedToken = md5sum(token + timestamp + nonceStr + SECRETKEY)
    * Get token by userId
     */
    private void validateToken(HttpServletRequest request) throws ApiException {
        final String timestamp = request.getParameter(ApiSignatureUtil.TIMESTAMP);
        if(Strings.isNullOrEmpty(timestamp)) {
            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER.withParams(ApiSignatureUtil.TIMESTAMP));
        }
        long current = System.currentTimeMillis();
        long timestampAsLong = Long.valueOf(timestamp);
        if (Math.abs(current - timestampAsLong) > 1000 * 60 * 5){
            throw new ApiException(ApiError.API_TIMESTAMP_OUT_OF_RANGE);
        }

        final String nonceStr = request.getParameter(ApiSignatureUtil.NONCE_STRING);
        if (Strings.isNullOrEmpty(nonceStr)){
            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER.withParams(ApiSignatureUtil.NONCE_STRING));
        }

        final String userId = request.getParameter(ApiSignatureUtil.USER_ID);
        final String token = request.getParameter(ApiSignatureUtil.LOGIN_TOKEN);
        if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(token)){
            throw new ApiException(ApiError.MISSING_REQUIRED_PARAMETER);
        }

        String appKey = request.getParameter(ApiSignatureUtil.APP_KEY);
        if(Strings.isNullOrEmpty(appKey)) {
            appKey = ApiClient.ANDROID.getKey();
        }

        Jedis jedis = jedisPool.getResource();
        try {
            String userTokenKey = RedisKeyUtil.getUserToken(userId);
            String calculatedToken = jedis.get(userTokenKey) + timestamp + nonceStr + ApiClient.getByKey(appKey).getSecretKey();
            calculatedToken =  DigestUtils.md5DigestAsHex(calculatedToken.getBytes("UTF-8")).toLowerCase();
            logger.info("calculatedToken" + calculatedToken);
            if (!jedis.exists(userTokenKey) || !token.equals(calculatedToken)){ // 未登录或者登陆失效
                throw new ApiException(ApiError.USER_NOT_LOGIN);
            }
        } catch (Exception e){
            logger.error(e);
            throw new ApiException(ApiError.USER_NOT_LOGIN);
        } finally {
            jedisPool.returnResource(jedis);
        }


    }

}
