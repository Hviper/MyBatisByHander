package DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;
/**
 * @author 拾光
 * @version 1.0
 */

public class DataSourceFactory {
    public static void main(String[] args){
        DataSource druid = DataSourceFactory.createDataSource("hikari");
        System.out.println(druid);


    }
    static DataSource dataSource;
    public static DataSource createDataSource(String dataSourceName){
        DataSource dataSource = null;
        Properties props = new Properties();
        if("hikari".equals(dataSourceName)){
            try {
                props.load(DataSourceFactory.class.getClassLoader().getResourceAsStream("hikari.properties"));
                HikariConfig config = new HikariConfig(props);
                dataSource = new HikariDataSource(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if("druid".equals(dataSourceName)){
            try {
                props.load(DataSourceFactory.class.getClassLoader().getResourceAsStream("druid.properties"));
                DruidDataSource dataSource1 = new DruidDataSource();
                dataSource1.configFromPropety(props);
                dataSource = dataSource1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataSource;
    }
}
