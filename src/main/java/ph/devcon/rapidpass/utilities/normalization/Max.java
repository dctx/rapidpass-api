package ph.devcon.rapidpass.utilities.normalization;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public class Max<E> implements NormalizationRule<E>  {

    private final String field;
    private int count;

    public Max(String field, int count) {
        this.field = field;
        this.count = count;
    }

    @Override
    public void normalize(E input) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String o = (String) declaredField.get(input);

        if (StringUtils.hasLength(o) && o.length() > this.count) {
            declaredField.set(input, o.substring(0, this.count));
        }
    }
}
