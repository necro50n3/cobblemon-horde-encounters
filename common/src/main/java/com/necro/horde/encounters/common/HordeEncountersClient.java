package com.necro.horde.encounters.common;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.interaction.PokemonInteractionGUICreationEvent;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.horde.encounters.common.network.HordeNetworkMessages;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class HordeEncountersClient {
    public static void init() {
        CobblemonEvents.POKEMON_INTERACTION_GUI_CREATION.subscribe(Priority.NORMAL, HordeEncountersClient::addSweetScentButton);
    }

    private static void addSweetScentButton(PokemonInteractionGUICreationEvent event) {
        Pokemon pokemon = CobblemonClient.INSTANCE.getStorage().getParty().getSlots().stream()
            .filter(slot -> slot != null && slot.getEntity() != null && slot.getEntity().getUUID().equals(event.getPokemonID()))
            .findFirst()
            .orElse(null);
        if (pokemon == null) return;
        else if (pokemon.getMoveSet().getMoves().stream().noneMatch(move -> move.getName().equals("sweetscent"))) return;

        event.addFillingOption(new InteractWheelOption(
            ResourceLocation.fromNamespaceAndPath(HordeEncounters.MODID, "textures/gui/interact/sweet_scent_wheel.png"),
            null,
            true,
            "effect.hordeencounters.sweet_scent",
            () -> new Vector3f(1),
            () -> {
                HordeNetworkMessages.APPLY_SWEET_SCENT.accept(1200);
                Minecraft.getInstance().setScreen(null);
                return Unit.INSTANCE;
            }
        ));
    }
}
