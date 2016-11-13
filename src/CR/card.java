package CR;

public final class card {

    public char suite;
    public char base_value;
    public boolean visible;

    public card() {
    }

    public card(
        char _suite,
        char _base_value,
        boolean _visible)
    {
        suite = _suite;
        base_value = _base_value;
        visible = _visible;
    }

}
