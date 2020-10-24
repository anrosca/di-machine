package filtering.test;

import com.dimachine.core.annotation.Component;

import java.io.Serializable;
import java.util.AbstractList;

@Component
public class AssignableTypeComponent extends AbstractList<Object> implements Serializable {
    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
