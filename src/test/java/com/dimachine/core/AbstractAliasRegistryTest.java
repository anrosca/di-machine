package com.dimachine.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractAliasRegistryTest {

    private final AbstractAliasRegistry aliasRegistry = new AbstractAliasRegistry() {
    };

    @Nested
    class AliasWithOneEntryTests {
        @BeforeEach
        public void setUp() {
            aliasRegistry.registerAlias("transactionManager", "platformTransactionManager");
        }

        @Test
        public void shouldBeAbleToRegisterAliases() {
            assertTrue(aliasRegistry.isAlias("platformTransactionManager"));
        }

        @Test
        public void shouldBeAbleToRemoveAlias() {
            aliasRegistry.removeAlias("platformTransactionManager");

            assertFalse(aliasRegistry.isAlias("platformTransactionManager"));
        }

        @Test
        public void shouldBeAbleToGetAllAliasesForAGivenBean() {
            aliasRegistry.registerAlias("transactionManager", "funkyManager");

            String[] expectedAliases = {"platformTransactionManager", "funkyManager"};
            assertArrayEquals(expectedAliases, aliasRegistry.getAliases("transactionManager"));
        }
    }

    @Nested
    class EmptyAliasRegistryTests {

        @Test
        public void emptyAliasRegistryShouldContainNoAliases() {
            assertFalse(aliasRegistry.isAlias("platformTransactionManager"));
            assertArrayEquals(new String[0], aliasRegistry.getAliases("transactionManager"));
        }

        @Test
        public void shouldBeAbleToRemoveAliasesFromEmptyRegistry() {
            aliasRegistry.removeAlias("platformTransactionManager");
            assertFalse(aliasRegistry.isAlias("platformTransactionManager"));
        }
    }
}
