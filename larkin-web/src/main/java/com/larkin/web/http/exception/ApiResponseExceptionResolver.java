package com.larkin.web.http.exception;

import com.larkin.web.http.ApiResponseBody;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ApiResponseExceptionResolver extends AbstractHandlerExceptionResolver {

    private static Logger logger = Logger.getLogger(ApiResponseExceptionResolver.class);

    private final static String CONTENT_TYPE = "application/json;charset=UTF-8";

    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
        response.setContentType(CONTENT_TYPE);
        ApiResponseBody responseBody;
        if(ex instanceof ApiException) {
            logger.error(ex.getMessage(), ex);
            ApiError error = ((ApiException)ex).getApiError();
            responseBody = new ApiResponseBody(error, ex.getMessage());
            response.setStatus(error.getStatus().value());
        } else if(ex instanceof UnsatisfiedServletRequestParameterException) {
            responseBody = new ApiResponseBody(ApiError.INVALID_API_SYSTEM);
            response.setStatus(ApiError.INVALID_API_SYSTEM.getStatus().value());
        } else {
            responseBody = new ApiResponseBody(ApiError.SERVER_ERROR, ex.getMessage());
            response.setStatus(ApiError.SERVER_ERROR.getStatus().value());
            if(null == ex.getMessage()){
                logger.error(ex.getStackTrace());
            } else {
                logger.error(ex.getMessage(), ex);
            }
        }

        try {
            new ObjectMapper().writeValue(response.getWriter(), responseBody);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new ModelAndView();
    }
}
