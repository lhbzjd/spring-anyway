package ink.anyway.component.persistence.mybatis.type;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.LONGVARCHAR)
@MappedTypes({Object.class})
public class JsonTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(o));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return JSON.parseObject(resultSet.getString(columnName), Object.class);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return JSON.parseObject(resultSet.getString(columnIndex), Object.class);
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return JSON.parseObject(callableStatement.getString(columnIndex), Object.class);
    }
}
