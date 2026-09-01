package io.github.darzizalol.focusfarm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class FocusFarmAppTest {
    @Test
    void isSmokeTest_acceptsSmokeTestArgument() {
        assertTrue(FocusFarmApp.isSmokeTest(List.of(FocusFarmApp.SMOKE_TEST_ARGUMENT)));
    }

    @Test
    void isSmokeTest_rejectsNormalLaunchArguments() {
        assertFalse(FocusFarmApp.isSmokeTest(List.of()));
        assertFalse(FocusFarmApp.isSmokeTest(List.of("--example")));
    }
}
