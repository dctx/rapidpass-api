package ph.devcon.rapidpass.utilities.normalization;

import org.apache.commons.lang3.StringUtils;
import ph.devcon.rapidpass.utilities.StringFormatter;

import java.lang.reflect.Field;

public class TransformAlphanumeric<E> implements NormalizationRule<E> {
    private final String field;

    public TransformAlphanumeric(String field) {
        this.field = field;
    }

    @Override
    public void normalize(E input) throws Exception {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String value = (String) declaredField.get(input);

        if (StringUtils.isNotBlank(value)) {
            value = StringFormatter.normalizeAlphanumeric(value);
            declaredField.set(input, value);
        }
    }
}
