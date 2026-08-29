package io.github.darzizalol.focusfarm.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.Farm;

class CommandExecutionTest {
    @Test
    void plantCommand_minuteDuration_formatsMinutes() throws Exception {
        Farm farm = new Farm(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        String feedback = new PlantCommand(1, CropType.CORN, Duration.ofMinutes(1)).execute(farm).feedback();
        assertTrue(feedback.contains("1m timer"));
    }

    @Test
    void statusCommand_mixedFarm_describesEmptyAndGrowingPlots() throws Exception {
        Farm farm = new Farm(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        farm.plant(2, CropType.TOMATO, Duration.ofSeconds(10));
        farm.water(2);
        String feedback = new StatusCommand().execute(farm).feedback();
        assertTrue(feedback.contains("Plot 1: empty"));
        assertTrue(feedback.contains("Plot 2: Tomato (growing, 10s remaining)"));
    }
}
