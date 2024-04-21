package net.ellivers.whyamionfire.config;

public class ModConfig extends MidnightConfig {
    public enum PartialHide {
        FLASH, TRANSPARENT, NONE
    }
    @Entry public static PartialHide partialHide = PartialHide.FLASH;
    @Entry public static boolean extinguishMobs = true;
}
