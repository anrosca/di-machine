package exclude.filtering.test;

import com.dimachine.core.annotation.Component;

import java.util.Iterator;

@Component
public class IterableComponent implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        return null;
    }
}
