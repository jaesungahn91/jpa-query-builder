package persistence.sql.ddl.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Columns {

    private static final String COMMA = ", ";

    private final List<Column> columns;

    public Columns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getSelectColumns() {
        return columns.stream()
                .map(Column::getName)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(COMMA));
    }

    public Column getPrimaryKey() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary key not found."));
    }

}
