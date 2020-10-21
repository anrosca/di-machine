package test;

import com.dimachine.core.DisposableBean;
import com.dimachine.core.annotation.Autowired;
import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("funky")
public class TestBean implements DisposableBean {
    private final FooService fooService;
    private boolean initMethodWasCalled;
    private boolean destroyMethodWasCalled;

    @Autowired
    private FooService autowiredField;

    @Autowired
    private List<? extends FooService> autowireList;

    @Autowired
    private Map<String, ? extends FooService> autowireMap;

    public TestBean(FooService fooService) {
        this.fooService = Objects.requireNonNull(fooService);
    }

    @PostConstruct
    public void greet() {
        assertNotNull(fooService);
        assertNotNull(autowireList);
        assertNotNull(autowireMap);
        initMethodWasCalled = true;
    }

    public boolean initMethodWasCalled() {
        return initMethodWasCalled;
    }

    public FooService getAutowiredField() {
        return autowiredField;
    }

    public List<? extends FooService> getAutowireList() {
        return autowireList;
    }

    public Map<String, ? extends FooService> getAutowireMap() {
        return autowireMap;
    }

    @Override
    public void destroy() throws Exception {
        destroyMethodWasCalled = true;
    }

    public boolean destroyMethodWasCalled() {
        return destroyMethodWasCalled;
    }
}
