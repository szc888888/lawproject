package io.renren.pay.utils;


import java.lang.reflect.Method;

import io.renren.pay.service.PayService;

public class CallMethod {

    /**
     * 通过字符串串调用方法
     * @param methods 方法名，通过此字符串调用类中的方法
     * @param paramTypes 方法类型列表(因为方法可能重载)
     * @param params 需要调用的方法的参数列表
     * @return
     */
    public static Object call(PayService payService, String methods, Class[] param, Object[] params){
        try {

            //第一个参数是方法名，后面的参数指示方法的参数类型和个数
            Method method = payService.getClass().getMethod(methods,param);
            //accessiable设为true表示忽略java语言访问检查（可访问private方法）
            //method.setAccessible(true);
            //第一个参数类实例(必须有对象才能调用非静态方法,如果是静态方法此参数可为null)
            //第二个是要传给方法的参数
            @SuppressWarnings("deprecation")
			Object result=method.invoke(payService.getClass().newInstance(),params);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}