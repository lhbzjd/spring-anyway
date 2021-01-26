package ink.anyway.component.persistence.mybatis.interceptor;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class  })  /** 该方法会在数据库执行前被调用*/
//        , @Signature(type = StatementHandler.class, method = "parameterize", args = { Statement.class })  /** 该方法在prepare 方法之后执行*/
//        , @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class })  /** 在全局设置配置defaultExecutorType ＝ "BATCH" 时*/
//        , @Signature(type = StatementHandler.class, method = "update", args = { Statement.class })  /** */
//        , @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class })  /** 执行select方法时调用*/
//        , @Signature(type = StatementHandler.class, method = "queryCursor", args = { Statement.class })  /** 只会在返回值类型为Cursor<T>的查询中被调用*/
//        , @Signature(type = StatementHandler.class, method = "getBoundSql", args = { })  /** */
//        , @Signature(type = StatementHandler.class, method = "getParameterHandler", args = { })  /** */
//        , @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })  /** 该方法会在除存储过程及返回值类型为Cursor<T> 以外的查询方法中被调用*/
//        , @Signature(type = ResultSetHandler.class, method = "handleCursorResultSets", args = { Statement.class })  /** 该方法只会在返回值类型为Cursor<T> 的查询方法中被调用*/
//        , @Signature(type = ResultSetHandler.class, method = "handleOutputParameters", args = { CallableStatement.class })  /** 该方法只在使用存储过程处理出参时被调用*/
//        , @Signature(type = ParameterHandler.class, method = "setParameters", args = { PreparedStatement.class })  /** 该方法在所有数据库方法设置SQL 参数时被调用*/
//        , @Signature(type = ParameterHandler.class, method = "getParameterObject", args = {  })  /** 该方法只在执行存储过程处理出参的时候被调用*/
        })
public class DataAuthorityInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            Object param = statementHandler.getBoundSql().getParameterObject();
//            Pagination<?> pagination = findPaginationParameter(statementHandler.getBoundSql().getParameterObject());
//            if (pagination == null)
//                return invocation.proceed();
//            else {
//                localPagination.set(pagination);
//            }

            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环
            // 可以分离出最原始的的目标类)
            while (metaStatementHandler.hasGetter("h")) {
                Object object = metaStatementHandler.getValue("h");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            // 分离最后一个代理对象的目标类
            while (metaStatementHandler.hasGetter("target")) {
                Object object = metaStatementHandler.getValue("target");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
                    .getValue("delegate.mappedStatement");
            // 分页信息if (localPage.get() != null) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            // 分页参数作为参数对象parameterObject的一个属性
            String sql = boundSql.getSql();
            // 重写sql
            String endSql = buildEndSql(sql, param);
            // 重写分页sql
            metaStatementHandler.setValue("delegate.boundSql.sql", endSql);
            Connection connection = (Connection) invocation.getArgs()[0];
//            // 重设分页参数里的总页数等
//            setPageParameter(sql, connection, mappedStatement, boundSql, pagination);
            // 将执行权交给下一个拦截器
            return invocation.proceed();
        }
//        else if (invocation.getTarget() instanceof ResultSetHandler) {
//            Pagination<?> pagination = localPagination.get();
//            if (pagination == null)
//                return invocation.proceed();
//            try {
//                Object result = invocation.proceed();
//                pagination.setList((List) result);
//                return result;
//            }
//            finally {
//                localPagination.remove();
//            }
//        }
        return null;
    }

    private String buildEndSql(String sql, Object param) {
        //TODO
        return sql;
    }

    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        }
        else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }
}
