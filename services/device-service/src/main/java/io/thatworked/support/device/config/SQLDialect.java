package io.thatworked.support.device.config;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.NationalizationSupport;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitOffsetLimitHandler;
import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.StandardSqlAstTranslatorFactory;
import org.hibernate.type.SqlTypes;

public class SQLDialect extends Dialect {

    public SQLDialect() {
        super(DatabaseVersion.make(3, 39));
    }

    @Override
    protected String columnType(int sqlTypeCode) {
        return switch (sqlTypeCode) {
            case SqlTypes.BOOLEAN -> "integer";
            case SqlTypes.SMALLINT -> "smallint";
            case SqlTypes.TINYINT -> "tinyint";
            case SqlTypes.INTEGER -> "integer";
            case SqlTypes.BIGINT -> "integer";  // Changed from bigint to integer
            case SqlTypes.FLOAT -> "float";
            case SqlTypes.DOUBLE -> "double";
            case SqlTypes.DECIMAL -> "decimal";
            case SqlTypes.VARCHAR -> "varchar";
            case SqlTypes.DATE -> "date";
            case SqlTypes.TIME -> "time";
            case SqlTypes.TIMESTAMP -> "timestamp";
            case SqlTypes.BLOB -> "blob";
            case SqlTypes.CLOB -> "clob";
            default -> super.columnType(sqlTypeCode);
        };
    }

    @Override
    public String getAddForeignKeyConstraintString(
            String constraintName,
            String[] foreignKey,
            String referencedTable,
            String[] primaryKey,
            boolean referencesPrimaryKey) {
        // SQLite does not support adding foreign key constraints after table creation
        return "";
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return " primary key autoincrement";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public boolean hasAlterTable() {
        // SQLite does not support ALTER TABLE
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }

    @Override
    public NationalizationSupport getNationalizationSupport() {
        return NationalizationSupport.IMPLICIT;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean supportsExistsInSelect() {
        return false;
    }

    @Override
    public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
        return new StandardSqlAstTranslatorFactory();
    }

    @Override
    public LimitHandler getLimitHandler() {
        return new LimitOffsetLimitHandler();
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public boolean supportsTableCheck() {
        return false;
    }
}