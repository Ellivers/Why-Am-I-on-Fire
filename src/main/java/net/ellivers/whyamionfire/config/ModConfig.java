package net.ellivers.whyamionfire.config;

public class ModConfig extends MidnightConfig {
    public enum PartialHide {
        FLASH, TRANSPARENT
    }
    @Entry public static PartialHide partialHide = PartialHide.FLASH;
}
