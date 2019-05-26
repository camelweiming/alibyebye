package com.abb.bye.service;

import com.abb.bye.client.domain.SequenceRange;
import com.abb.bye.client.exception.SequenceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
public class Sequence {
    private static Logger logger = LoggerFactory.getLogger(SequenceServiceImpl.class);
    @Resource
    private DataSource dataSource;
    protected static final int DEFAULT_INNER_STEP = 1000;
    protected static final int DEFAULT_RETRY_TIMES = 2;
    protected int innerStep = DEFAULT_INNER_STEP;
    private String tableName = "sequence";
    private String nameColumnName = "name";
    private String valueColumnName = "value";
    private String modifiedColumnName = "gmt_modified";
    private String sequences;
    /**
     * 初始化一次，无需扩容，不需要ConcurrentHashMap
     */
    private volatile Map<String, SequenceRange> sequenceRangeMapping = new HashMap<>();

    /**
     * 初始化
     *
     * @throws Exception
     */
    void init() throws Exception {
        String[] existNames = StringUtils.split(sequences, ",");
        for (String name : existNames) {
            checkAndInit(name);
        }
    }

    public long next(String name) {
        SequenceRange sequenceRange = sequenceRangeMapping.get(name);
        if (sequenceRange == null) {
            throw new SequenceException("sequence not exist:" + name);
        }
        long value = sequenceRange.getAndIncrement();
        if (value == -1) {
            sequenceRange.lock();
            try {
                for (; ; ) {
                    if (sequenceRange.isOver()) {
                        long[] values = nextRange(name);
                        sequenceRange.reset(values[0], values[1]);
                        logger.info("reset-sequence-rage:" + sequenceRange);
                    }
                    value = sequenceRange.getAndIncrement();
                    if (value == -1) {
                        continue;
                    }
                    break;
                }
            } finally {
                sequenceRange.unlock();
            }
        }
        return value;
    }

    public long[] nextRange(final String name) {
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                Long oldValue = getOldValue(name);
                long newValue = oldValue + innerStep;
                if (0 == updateNewValue(name, oldValue, newValue)) {
                    continue;
                }
                return new long[] {newValue + 1, newValue + innerStep};
            } catch (Throwable e) {
                logger.warn("Error getNextRange:" + name, e);
            }
        }
        throw new SequenceException("Error getNextRange");
    }

    protected Long getOldValue(String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(getSelectSql());
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getLong(valueColumnName);
            }
            return null;
        } catch (SQLException e) {
            String errMsg = "Failed to query names";
            logger.error(errMsg, e);
            throw new SequenceException(errMsg, e);
        } finally {
            closeDbResources(rs, stmt, conn);
        }
    }

    /**
     * CAS更新sequence值
     *
     * @param keyName
     * @param oldValue
     * @param newValue
     * @return
     * @throws SQLException
     */
    protected int updateNewValue(String keyName, long oldValue, long newValue) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement(getUpdateSql());
            stmt.setLong(1, newValue);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, keyName);
            stmt.setLong(4, oldValue);
            return stmt.executeUpdate();
        } finally {
            closeDbResources(rs, stmt, conn);
        }
    }

    protected synchronized void checkAndInit(String name) {
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                Long currentValue = getOldValue(name);
                if (currentValue != null) {
                    long[] values = nextRange(name);
                    SequenceRange range = new SequenceRange(values[0], values[1]);
                    sequenceRangeMapping.put(name, range);
                    logger.info("load-sequence-rage:" + range);
                    return;
                }
                adjustInsert(0, name);
                logger.info("init-sequence:" + name);
            } catch (Throwable e) {
                logger.warn("Error init-sequence:" + name + " retry:" + i, e);
            }
        }
        throw new SequenceException("Error checkAndInit:" + name);
    }

    protected void adjustInsert(int index, String name) throws SQLException {
        long newValue = index * innerStep;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(getInsertSql());
            stmt.setString(1, name);
            stmt.setLong(2, newValue);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SequenceException("failed to auto adjust init value at  " + name + " update affectedRow =0");
            }
            logger.info("init-value:" + name + "value:" + newValue);

        } catch (SQLException e) {
            logger.error("init value failed，sequence Name：" + name + "   value:" + newValue, e);
            throw new SequenceException("init value failed，sequence Name：" + name + "   value:" + newValue, e);
        } finally {
            closeDbResources(rs, stmt, conn);
        }
    }

    protected String getSelectSql() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("select ").append(valueColumnName);
        buffer.append(" from ").append(tableName);
        buffer.append(" where ").append(nameColumnName).append(" = ?");
        return buffer.toString();
    }

    protected String getUpdateSql() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("update ").append(tableName);
        buffer.append(" set ").append(valueColumnName).append(" = ?, ");
        buffer.append(modifiedColumnName).append(" = ? where ");
        buffer.append(nameColumnName).append(" = ? and ");
        buffer.append(valueColumnName).append(" = ?");
        return buffer.toString();
    }

    protected String getInsertSql() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("insert into ").append(tableName).append("(");
        buffer.append(nameColumnName).append(",");
        buffer.append(valueColumnName).append(",");
        buffer.append(modifiedColumnName).append(") values(?,?,?);");
        return buffer.toString();
    }

    public static void closeDbResources(ResultSet rs, Statement stmt, Connection conn) {
        close(rs);
        close(stmt);
        close(conn);
    }

    private static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.debug("Could not close JDBC ResultSet.", e);
            } catch (Throwable e) {
                logger.debug("Unexpected exception on closing JDBC ResultSet.", e);
            }
        }
    }

    private static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.debug("Could not close JDBC Statement.", e);
            } catch (Throwable e) {
                logger.debug("Unexpected exception on closing JDBC Statement.", e);
            }
        }
    }

    private static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.debug("Could not close JDBC Connection.", e);
            } catch (Throwable e) {
                logger.debug("Unexpected exception on closing JDBC Connection.", e);
            }
        }
    }

    public void setInnerStep(int innerStep) {
        this.innerStep = innerStep;
    }

    public void setSequences(String sequences) {
        this.sequences = sequences;
    }
}
