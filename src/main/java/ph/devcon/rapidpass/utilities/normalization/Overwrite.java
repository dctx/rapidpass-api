package ph.devcon.rapidpass.utilities.normalization;

import java.lang.reflect.Field;

public class Overwrite<E> implements NormalizationRule<E> {
    private final String field;
    private String replacement;

    public Overwrite(String field, String replacement) {
        this.field = field;
        this.replacement = replacement;
    }

    @Override
    public void normalize(E input) throws Exception {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        declaredField.set(input, replacement);
    }
}
