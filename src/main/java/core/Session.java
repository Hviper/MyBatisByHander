package core;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author 拾光
 * @version 1.0
 *
 * 一个链接表示一个会话Session
 */
public class Session {
    private Map<String, Map<String,DaoWrapper>> env;
    private final Connection conn;
    public Session(Connection conn,Map<String,Map<String,DaoWrapper>> env){
        this.env = env;
        this.conn = conn;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clazz){
        //代理对象的类型应该和被代理对象的类型一致，《T》，命名空间和
        // 《clazz.getName：获得类的全路径，这里和命名空间的字符串进行匹配》
        return (T) Proxy.newProxyInstance(Session.class.getClassLoader(), new Class[]{clazz}, new SqlInvocationHandler(conn,env.get(clazz.getName())));
    }

    /**
     * 开始会话
     */
    public void begin(){
        try {
            conn.setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 提交
     */
    public void commit(){
        try {
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void rollback(){
        try {
            conn.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }




}
