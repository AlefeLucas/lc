/**
 * Classe usada para passar por referencia os atributos do esquema de traducao;
 * Guarda um valor dentro.
 *
 * @param <T> tipo do dado
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class Wrapper<T> {
    private T value;

    public Wrapper(T value) {
        this.value = value;
    }

    public Wrapper() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
