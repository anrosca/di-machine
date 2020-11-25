package _async;

import com.dimachine.core.annotation.Async;
import com.dimachine.core.annotation.Component;
import test.FooService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AsyncBean {
    private static List<String> asyncMethodInvocations = new CopyOnWriteArrayList<>();

    private final FooService fooService;

    public AsyncBean(FooService fooService) {
        this.fooService = fooService;
    }

    public AsyncBean() {
        this.fooService = null;
    }

    public static synchronized List<String> getAsyncMethodInvocations() {
        return asyncMethodInvocations;
    }

    public static synchronized void reset() {
        asyncMethodInvocations = new CopyOnWriteArrayList<>();
    }

    @Async
    public void async() {
        asyncMethodInvocations.add(Thread.currentThread().getName() + "|async");
    }
}
