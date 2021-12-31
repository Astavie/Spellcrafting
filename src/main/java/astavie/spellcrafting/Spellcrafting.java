package astavie.spellcrafting;

import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.item.SpellContainer;
import astavie.spellcrafting.api.spell.ActiveSpell;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.CharmStack;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SubSpell;
import astavie.spellcrafting.api.spell.Target;
import astavie.spellcrafting.api.spell.TargetBlock;
import astavie.spellcrafting.api.spell.TargetEntity;
import astavie.spellcrafting.spell.CasterPlayer;
import astavie.spellcrafting.spell.charm.CharmIgnite;
import astavie.spellcrafting.spell.element.ElementTarget;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class Spellcrafting implements ModInitializer {

	private static final ItemTestSpell test = new ItemTestSpell();
	private static final Identifier CAST_PACKET_ID = new Identifier("spellcrafting", "cast");

	@Override
	public void onInitialize() {
		// Items
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "test"), test);

		// APIs
		Caster.ENTITY_CASTER.registerForType((player, context) -> new CasterPlayer(player), EntityType.PLAYER);

		// TODO: Test spell
		SpellContainer.ITEM_SPELL.registerForItems((stack, context) -> new SpellContainer() {

			@Override
			public boolean isActive() {
				return false;
			}

			@Override
			public @Nullable Spell getSpell() {
				CharmStack charm = new CharmStack(new CharmIgnite());
				charm.getElementStack(0).addElement(new ElementTarget());
	
				SubSpell sub = new SubSpell();
				sub.getCharms().add(charm);

				return new Spell(sub);
			}

			@Override
			public @Nullable ActiveSpell getActiveSpell() {
				return null;
			}

			@Override
			public @Nullable ActiveSpell activate(Caster caster, Target target) {
				return getSpell().activate(caster, target);
			}
			
		}, test);

		// Networking
		ServerPlayNetworking.registerGlobalReceiver(CAST_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			// Get target
			Target target;

			HitResult.Type type = buf.readEnumConstant(HitResult.Type.class);

			if (type == HitResult.Type.ENTITY) {
				Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
				Entity e = player.world.getEntityById(buf.readInt());
				if (e == null) return;
				target = new TargetEntity(e, pos);
			} else {
				BlockHitResult hit = buf.readBlockHitResult();
				target = new TargetBlock(player.world, hit.getBlockPos(), hit.getPos(), hit.getSide());
			}

			// Get spell
			ActiveSpell spell = getOrCreateSpell(player, type == HitResult.Type.MISS, target);
			if (spell == null) {
				return;
			}

			// Cast!
			spell.cast();
		});
	}

	public static ActiveSpell getOrCreateSpell(PlayerEntity player, boolean miss, Target target) {
		// TODO: Custom foci
		if (!player.getMainHandStack().isEmpty()) {
			return null;
		}

		SpellContainer container = SpellContainer.ITEM_SPELL.find(player.getOffHandStack(), null);
		if (container == null) {
			return null;
		}

		if (container.isActive()) {
			// Continue
			ActiveSpell spell = container.getActiveSpell();

			//if (spell.getSpell().requiresTarget() && miss) {
			//	return null;
			//}

			return spell;
		}

		// Activate
		Spell spell = container.getSpell();
		if (spell == null /* || (spell.requiresTarget() && miss) */) {
			return null;
		}

		// TODO: Check components

		return container.activate(Caster.ENTITY_CASTER.find(player, null), target);
	}

	public static void cast(HitResult hit) {
		PacketByteBuf packet = PacketByteBufs.create();

		packet.writeEnumConstant(hit.getType());

		if (hit.getType() == HitResult.Type.ENTITY) {
			packet.writeDouble(hit.getPos().x);
			packet.writeDouble(hit.getPos().y);
			packet.writeDouble(hit.getPos().z);
			packet.writeInt(((EntityHitResult) hit).getEntity().getId());
		} else {
			packet.writeBlockHitResult((BlockHitResult) hit);
		}

		ClientPlayNetworking.send(CAST_PACKET_ID, packet);
	}

}
