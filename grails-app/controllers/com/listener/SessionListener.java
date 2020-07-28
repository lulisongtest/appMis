package com.listener;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * session监听器. <br>
 * 在WEB容器的web.xml中添加本监听器的调用,具体格式如下：(其中的"[","]"分别用" <",">"替换) <br>
 *
 * <pre>
 *
 *    [web-app]
 *    [filter]
 *    ...
 *    [/filter]
 *    [filter-mapping]
 *    ...
 *    [/filter-mapping]
 *    ...
 *    [listener][listener-class]com.stephen.filter.SessionListener[/listener-class][/listener]
 *    ...
 *    [servlet]
 *    ...
 *    [/servlet]
 *    ...
 *    [/web-app]
 *
 * </pre>
 *
 * 注意在web.xml中配置的位置. <br>
 *
 * @author 陆立松
 * @version 1.00
 * @see HttpSessionAttributeListener
 */
public class SessionListener implements HttpSessionAttributeListener {

    /**
     * 定义监听的session属性名.
     */
    //public final static String LISTENER_NAME = "username";//监听Session中的username变化
    public final static String LISTENER_NAME1 = "loginsession";//放session对象
    /**
     * 定义存储客户登录session的集合.
     */
   // private static List sessions = new ArrayList();
    private static List sessions1 = new ArrayList();
    /**
     * 加入session时的监听方法.
     *

     *            session事件
     */
    public void attributeAdded(HttpSessionBindingEvent sbe) {
       // if (LISTENER_NAME.equals(sbe.getName())) {
       //     sessions.add(sbe.getValue());
      //  }
        //System.out.println("attributeAdded============sbe.getValue()======"+sbe.getValue());
        if (LISTENER_NAME1.equals(sbe.getName())) {
            sessions1.add(sbe.getValue());
        }
    }

    /**
     * session失效时的监听方法.
     *

     *            session事件
     */
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
       // if (LISTENER_NAME.equals(sbe.getName())) {
       //     sessions.remove(sbe.getValue());
       // }
        //System.out.println("attributeRemoved===========sbe.getValue()======"+sbe.getValue());
        if (LISTENER_NAME1.equals(sbe.getName())) {
            sessions1.remove(sbe.getValue());
        }
    }

    /**
     * session覆盖时的监听方法.
     *

     *            session事件
     */
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        //System.out.println("attributeReplaced============sbe.getValue()======"+sbe.getValue());
    }

    /**
     * 返回客户登录session的集合.
     *
     * @return
     */
    //public static List getSessions() {
    //    return sessions;
    //}
    public static List getSessions1() {
       return sessions1;
   }
}  
