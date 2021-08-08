package core;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * @author 拾光
 * @version 1.0
 */
public class SqlInvocationHandler implements InvocationHandler{
    Map<String,DaoWrapper> env;
    Connection conn;
    public SqlInvocationHandler(Connection conn,Map<String,DaoWrapper> env){
        this.conn = conn;
        this.env = env;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        //拿到包装
        DaoWrapper daoWrapper = env.get(name);
        //预编译sql
        if(daoWrapper==null){
            return null;
        }
        PreparedStatement preparedStatement = conn.prepareStatement(daoWrapper.getSqlStr());
        if("insert".equals(daoWrapper.getType())){
            //插入语句的参数为User对象,再将对象里的属性进行暴力注入，从而拿到所有的属性值
            Class<?> aClass = args[0].getClass();
            Field[] fields = aClass.getDeclaredFields();
            int len = fields.length;
            for(int i=0;i<len;i++){
                fields[i].setAccessible(true);
                //字段.get(对象)：暴力从对象中获得对象的字段信息
                preparedStatement.setObject(i+1,fields[i].get(args[0]));
            }
            return preparedStatement.execute();
        }else if("select".equals(daoWrapper.getType())){
            ResultSet resultSet = preparedStatement.executeQuery();
            Class<?> aClass = Class.forName(daoWrapper.getResultType());
            List<Object> list = new ArrayList<>();
            while(resultSet.next()){
                Field[] fields = aClass.getDeclaredFields();
                for (Field field : fields) {
                    //利用反射创建每个对象
                    Object obj = aClass.newInstance();
                    field.setAccessible(true);
                    //对象的属性和数据库的字段一样
                    field.set(obj, resultSet.getObject(field.getName()));
                    list.add(obj);
                }
            }
            return list;
        }
        return null;
    }
}
