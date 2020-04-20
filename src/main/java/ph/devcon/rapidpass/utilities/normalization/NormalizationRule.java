package ph.devcon.rapidpass.utilities.normalization;

public interface NormalizationRule<E> {
    void normalize(E input) throws Exception;
}
