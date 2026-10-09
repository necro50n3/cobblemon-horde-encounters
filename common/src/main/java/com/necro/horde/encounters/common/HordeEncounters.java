package com.necro.horde.encounters.common;

import com.mojang.logging.LogUtils;
import com.necro.asymmetric.battles.common.config.serializer.PrimitiveYamlConfigSerializer;
import com.necro.horde.encounters.common.config.HordeEncountersConfig;
import me.shedaniel.autoconfig.AutoConfig;
import org.slf4j.Logger;

public class HordeEncounters {
    public static final String MODID = "hordeencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static HordeEncountersConfig CONFIG;

    public static void init() {
        LOGGER.info("Initiating {}", MODID);

        AutoConfig.register(HordeEncountersConfig .class, PrimitiveYamlConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(HordeEncountersConfig.class).getConfig();
    }
}
