package persistence.sql.dml;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Column;
import persistence.sql.dml.domain.Value;
import persistence.sql.dml.domain.Values;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WhereQueryBuilder implements QueryBuilder {

    private static final String EMPTY_STRING = "";

    private static final String WHERE_CLAUSE = " WHERE %s";

    private final Values values;

    public WhereQueryBuilder(Class<?> clazz, List<String> whereColumns, List<Object> whereValues, List<String> whereOperators) {
        validate(whereColumns, whereValues);

        this.values = new Values(createValues(clazz, whereColumns, whereValues));
    }

    private void validate(List<String> whereColumns, List<Object> whereValues) {
        if (whereColumns.size() != whereValues.size()) {
            throw new IllegalArgumentException("The number of columns and values corresponding to the condition statement do not match.");
        }
    }

    private List<Value> createValues(Class<?> clazz, List<String> whereColumns, List<Object> whereValues) {
        return IntStream.range(0, whereColumns.size())
                .mapToObj(index -> createValue(clazz, whereColumns, whereValues, index))
                .collect(Collectors.toList());
    }

    private Value createValue(Class<?> clazz, List<String> whereColumns, List<Object> whereValues, int index) {
        try {
            return getValue(clazz, whereColumns.get(index), whereValues.get(index));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Value getValue(Class<?> clazz, String column, Object value) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(column);
        return new Value(new Column(field), field.getType(), value);
    }

    @Override
    public String build() {
        String whereClause = generateWhereClause();
        if (whereClause.isEmpty()) {
            return EMPTY_STRING;
        }
        return String.format(WHERE_CLAUSE, whereClause);
    }

    private String generateWhereClause() {
        return values.getValues().stream()
                .map(x -> x.getColumn().getName() + " = " + x.getValue())
                .collect(Collectors.joining(" AND "));
    }

}
