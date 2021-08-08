package core;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import DataSource.DataSourceFactory;
import dao.UserDao;
import entity.User;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.util.Map;
import java.util.HashMap;

/**
 * @author 拾光
 * @version 1.0
 * DataSource + 主配置信息（mybatis-config） = SessionFactory
 */
public class SessionFactory {


    public static void main(String[] args) {
        SessionFactory druid = new SessionFactory("mybatis-config.xml");
        Session session = druid.openSession(druid.getMap());
        System.out.println(session);
        //返回一个代理对象，代理对象调用实际接口（UserDao）方法
        UserDao userDao = session.getMapper(UserDao.class);
        //代理对象调用实际接口方法
        boolean b = userDao.saveUser(new User(3, "kiwiapache", "42128340HDC", "safsafsa"));

//        System.out.println(i);


    }

    private final Map<String,Map<String,DaoWrapper>> env = new HashMap<>();
    public Map<String,Map<String,DaoWrapper>> getMap() {
        return env;
    }

    private final DataSource dataSource;

    public SessionFactory(String configPath){

        String type = ParseConfigXML(configPath);

        dataSource = DataSourceFactory.createDataSource(type);
    }

    private String ParseConfigXML(String configPath) {
        InputStream resourceAsStream = SessionFactory.class.getClassLoader().getResourceAsStream(configPath);
        SAXReader reader = new SAXReader();
        Document read = null;
        try {
            read = reader.read(resourceAsStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        assert read != null;
        Element root = read.getRootElement();
        Element rootElement = root.element("dataSource");
        String dataSourceName = rootElement.getTextTrim();

        List<Element> elements = root.elements("mapper");
        System.out.println("---->"+elements);
        List<String> mapperPaths = elements.stream().map(Element::getTextTrim).collect(Collectors.toList());

        mapperPaths.forEach(mapperPath->{
            Document read2 = null;
            try {
                read2 = reader.read(SessionFactory.class.getClassLoader().getResourceAsStream(mapperPath));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            assert read2 != null;
            /**
             * 三.属性相关.
             * 1.取得某节点下的某属性
             *             Element root=document.getRootElement();
             *             Attribute attribute=root.attribute("size");// 属性名name
             * 2.取得属性的文字
             *             String text=attribute.getText();
             * 也可以用:
             * String text2=root.element("name").attributeValue("firstname");这个是取得根节点下name字节点的属性firstname的值.
             */
            Element rootElement1 = read2.getRootElement();
            //命名空间为实体类的全路径，通过反射动态创建
            String namespace = rootElement1.attribute("namespace").getText();
            Iterator<Element> elementIterator = rootElement1.elementIterator();
            HashMap<String, DaoWrapper> wrapper = new HashMap<>();
            while(elementIterator.hasNext()){
                Element next = elementIterator.next();
                /**
                 *
                 * <insert id="saveUser" resultType="java.lang.Integer" paramType="entity.User">
                 * type：标签的名字，比如insert标签
                 */
                String type = next.getName();
                String id = next.attributeValue("id");
                String resultType = next.attributeValue("resultType");
                String paramType = next.attributeValue("paramType");
                String sqlStr = next.getTextTrim();
                DaoWrapper daoWrapper = new DaoWrapper(type, id, resultType, paramType, sqlStr);

                wrapper.put(id,daoWrapper);
            }
            //命名空间作为key，和你传递进来参数的类型做匹配   【class.getName()】
            // public <T> T getMapper(Class<T> clazz){}
            env.put(namespace,wrapper);

        });

        return dataSourceName;
    }


    public Session openSession(Map<String,Map<String,DaoWrapper>> env){
        Session session = null;
        try {
            session = new Session(dataSource.getConnection(),env);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return session;
    }

}
