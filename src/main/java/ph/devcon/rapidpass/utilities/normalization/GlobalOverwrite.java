package ph.devcon.rapidpass.utilities.normalization;

import java.lang.reflect.Field;
import java.util.function.Function;

public class GlobalOverwrite<E> implements NormalizationRule<E> {
    private final String field;
    private String replacement;
    private Function<E, Boolean> conditionForWriting;

    public GlobalOverwrite(String field, String replacement, Function<E, Boolean> shouldOverwrite) {
        this.field = field;
        this.replacement = replacement;
        this.conditionForWriting = shouldOverwrite;
    }

    @Override
    public void normalize(E input) throws Exception {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String o = (String) declaredField.get(input);

        Boolean shouldOverwrite = conditionForWriting.apply(input);

        if (shouldOverwrite) {
            declaredField.set(input, replacement);
        }
    }
}
