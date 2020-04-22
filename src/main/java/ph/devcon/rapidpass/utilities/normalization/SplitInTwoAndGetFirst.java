package ph.devcon.rapidpass.utilities.normalization;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class SplitInTwoAndGetFirst<E> implements NormalizationRule<E>  {

    private final String field;

    public SplitInTwoAndGetFirst(String field) {
        this.field = field;
    }

    @Override
    public void normalize(E input) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String o = (String) declaredField.get(input);

        int indexOfSlash = o.indexOf('/');

        if (indexOfSlash > 1) {
            o = o.substring(0, indexOfSlash);
        }

        int indexOfNewLine = o.indexOf('\n');

        if (indexOfNewLine > 1) {
            o = o.substring(0, indexOfNewLine);
        }

        int indexOfSemi = o.indexOf(';');

        if (indexOfSemi > 1) {
            o = o.substring(0, indexOfSemi);
        }


        String first = StringUtils.trim(o);

        if (!StringUtils.isBlank(first))
            declaredField.set(input, first);

    }
}
