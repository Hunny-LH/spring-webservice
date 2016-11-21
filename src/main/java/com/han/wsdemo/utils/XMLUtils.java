package com.han.wsdemo.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.cglib.beans.BeanMap;

import java.util.Collection;
import java.util.Map;

/**
 * xml工具类（这里依赖类dom4j， cglib，dom4j用来操作xml文档， cglib用来将pojo映射转换为map）
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
public class XMLUtils {

    /**
     * 将一个对象转换成xml格式字符串
     *
     * @param name
     * @param object
     * @return
     */
    public static String convertToXML(String name, Object object) {
        Document xmlDoc = DocumentHelper.createDocument();
        Element root = xmlDoc.addElement(name);
        toElement(object, root);
        return xmlDoc.asXML();
    }

    /**
     * 将对象转换成目标节点下的子节点
     *
     * @param object
     * @param root
     */
    public static void toElement(Object object,Element root) {
        if(object!=null){
            if ((object instanceof Number)||
                    (object instanceof Boolean)||
                    (object instanceof String)||
                    (object instanceof Double)||
                    (object instanceof Float)){
                root.setText(object.toString());    //将简单类型当作文本内容添加到当前节点
            }else if (object instanceof Map){
                mapToElement((Map) object,root);    //将map转换成xml节点添加到当前节点
            }else if(object instanceof Collection){
                collToElement((Collection) object,root); //将collection内容转换成xml节点加入到当前节点
            }else{
                pojoToElement(object,root); //将实体对象转换成xml节点添加到当前节点
            }
        }else{
            root.setText("");
        }
    }
    /**
     * list中存放的数据类型有基本类型、用户自定对象
     * map、list
     * @param coll
     * @param root
     */
    private static void collToElement(Collection<?> coll,Element root) {
        for (Object value : coll) {
            if(coll==value){
                continue;
            }
            if ((value instanceof Number)||
                    (value instanceof Boolean)||
                    (value instanceof String)||
                    (value instanceof Double)||
                    (value instanceof Float)){
                Class<?> classes = value.getClass();
                String objName=classes.getName();
                String elementName=objName.substring(objName.lastIndexOf(".")+1, objName.length());
                Element elementOfObject = root.addElement(elementName.toLowerCase());
                elementOfObject.setText(value.toString());
            }else if (value instanceof Map){
                Class<?> classes = value.getClass();
                String objName=classes.getName();
                String elementName=objName.substring(objName.lastIndexOf(".")+1, objName.length());
                Element elementOfObject = root.addElement(elementName.toLowerCase());
                mapToElement((Map) value,elementOfObject);
            }else if(value instanceof Collection){
                Class<?> classes = value.getClass();
                String objName=classes.getName();
                String elementName=objName.substring(objName.lastIndexOf(".")+1, objName.length());
                Element elementOfObject = root.addElement(elementName.toLowerCase());
                collToElement((Collection) value,elementOfObject);
            }else{
                toElement(value, root);
            }
        }
    };
    /**
     * map中存放的数据类型有基本类型、用户自定对象
     * map、list
     * @param map
     * @param root
     */
    private static void mapToElement(Map<String, Object> map,Element root) {
        map.forEach((key, value) -> {
            if (key != null && value != map) {
                Element elementValue = root.addElement(key);
                toElement(value, elementValue);
            }
        });
    }
    /**
     *
     * @param obj
     * @param root
     */
    private static void pojoToElement(Object obj,Element root) {
        Class<?> classes = obj.getClass();
        String objName=classes.getName();
        String elementName=objName.substring(objName.lastIndexOf(".")+1, objName.length());
        /**该类为一个节点*/
        Element elementOfObject = root.addElement(elementName.toLowerCase());

        BeanMap map = BeanMap.create(obj);
        map.forEach((key, value) -> {
            Element elementValue = elementOfObject.addElement((String) key);
            toElement(value, elementValue);
        });
    }

}
