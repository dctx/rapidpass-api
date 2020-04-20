package ph.devcon.rapidpass.utilities.normalization;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class Trim<E> implements NormalizationRule<E>  {

    private final String field;

    public Trim(String field) {
        this.field = field;
    }

    @Override
    public void normalize(E input) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String o = (String) declaredField.get(input);

        if (!StringUtils.isBlank(o))
            declaredField.set(input, o.trim());
    }
}
