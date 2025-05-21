package com.yuxuan.netty.medium;

import com.alibaba.fastjson.JSONObject;
import com.yuxuan.netty.client.Response;
import com.yuxuan.netty.handler.param.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {
    public static Map<String, BeanMethod> beanMap;

    static {
        beanMap = new HashMap<String, BeanMethod>();
    }

    private static Media m = null;

    private Media() {
    }

    public static Media newInstance() {
        if (m == null) {
            m = new Media();
        }
        return m;
    }

    public Response process(ServerRequest request) {
        Response result;
        try {
            String command = request.getCommand();
            BeanMethod beanMethod = beanMap.get(command);
            if (beanMethod == null) {
                return null;
            }
            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();
            Class paramType = method.getParameterTypes()[0];
            Object content = request.getContent();

            Object args = JSONObject.parseObject(content.toString(), paramType);
            result = (Response) method.invoke(bean, args);
            result.setId(request.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
