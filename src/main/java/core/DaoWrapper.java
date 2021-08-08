package core;

/**
 * @author 拾光
 * @version 1.0
 * Dao层包装
 */
public class DaoWrapper {

    private String type;
    private String id;
    private String resultType;
    private String paramType;
    private String sqlStr;

    public DaoWrapper(String type, String id, String resultType, String paramType, String sqlStr) {
        this.type = type;
        this.id = id;
        this.resultType = resultType;
        this.paramType = paramType;
        this.sqlStr = sqlStr;
    }

    public DaoWrapper() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }
}
