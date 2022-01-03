package astavie.spellcrafting.api.spell.target;

import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public interface TargetType<T extends Target> {

    public static final Registry<TargetType<?>> REGISTRY = FabricRegistryBuilder.from(
        new SimpleRegistry<TargetType<?>>(RegistryKey.ofRegistry(new Identifier("spellcrafting:target")), Lifecycle.stable())
    ).buildAndRegister();

    T deserialize(NbtCompound nbt);

    NbtCompound serialize(T target);
    
}
