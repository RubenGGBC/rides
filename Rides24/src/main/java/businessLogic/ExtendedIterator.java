package businessLogic;

import java.util.Iterator;

public interface ExtendedIterator<Object> extends Iterator<Object> {
    // devuelve el elemento actual y se mueve al anterior
    public Object previous();

    // true si hay un elemento anterior
    public boolean hasPrevious();

    // se coloca en el primer elemento
    public void goFirst();

    // se coloca en el último elemento
    public void goLast();
}
