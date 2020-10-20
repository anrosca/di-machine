package test;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.PostConstruct;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("funky")
public class TestBean {
    private final FooService fooService;
    private boolean initMethodWasCalled;

    public TestBean(FooService fooService) {
        this.fooService = Objects.requireNonNull(fooService);
    }

    @PostConstruct
    public void greet() {
        assertNotNull(fooService);
        fooService.foo();
        initMethodWasCalled = true;
    }

    public boolean initMethodWasCalled() {
        return initMethodWasCalled;
    }
}
