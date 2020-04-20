package ph.devcon.rapidpass.utilities.normalization;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class DefaultValue<E> implements NormalizationRule<E>  {

    private final String field;
    private String defaultValue;

    public DefaultValue(String field, String defaultValue) {
        this.field = field;
        this.defaultValue = defaultValue;
    }

    @Override
    public void normalize(E input) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        Object objectValue = type.newInstance();

        String o = (String) declaredField.get(objectValue);

        if (StringUtils.isBlank(o))
            declaredField.set(objectValue, defaultValue);
    }
}
