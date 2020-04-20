package ph.devcon.rapidpass.validators;

import java.util.List;

public class InvalidEnumValueException extends IllegalArgumentException {
    private final String key;
    private final String value;
    private final List<String> values;

    public InvalidEnumValueException(String key, String value, List<String> values) {
        this.key = key;
        this.value = value;
        this.values = values;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        String values = String.join(", ", this.values);
        return String.format("Invalid value `%s` for property `%s`. Allowed values are [%s].", key, value, values);
    }
}
