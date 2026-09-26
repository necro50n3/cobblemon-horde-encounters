package com.necro.horde.encounters.common;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class HordeEncounters {
    public static final String MODID = "hordeencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initiating {}", MODID);
    }
}
