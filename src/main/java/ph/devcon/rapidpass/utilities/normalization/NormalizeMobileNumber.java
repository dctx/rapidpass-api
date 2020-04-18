package ph.devcon.rapidpass.utilities.normalization;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class NormalizeMobileNumber<E> implements NormalizationRule<E> {


    private final String field;

    public NormalizeMobileNumber(String field) {
        this.field = field;
    }

    @Override
    public void normalize(E input) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        Object objectValue = type.newInstance();

        String mobileNumber = (String) declaredField.get(objectValue);

        if (StringUtils.isNotBlank(mobileNumber)) {
            mobileNumber = "0" + StringUtils.right(mobileNumber, 10);

            declaredField.set(objectValue, mobileNumber);
        }
    }
}
