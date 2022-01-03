package astavie.spellcrafting;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellContainer;
import astavie.spellcrafting.api.spell.Spell.Socket;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.spell.CasterPlayer;
import astavie.spellcrafting.spell.SpellState;
import astavie.spellcrafting.spell.node.CharmAttune;
import astavie.spellcrafting.spell.node.CharmExplode;
import astavie.spellcrafting.spell.node.CharmIgnite;
import astavie.spellcrafting.spell.node.CharmSummon;
import astavie.spellcrafting.spell.node.CharmMomentum;
import astavie.spellcrafting.spell.node.CharmRaycast;
import astavie.spellcrafting.spell.node.EventEntityInteract;
import astavie.spellcrafting.spell.node.EventWait;
import astavie.spellcrafting.spell.node.NodeSelf;
import astavie.spellcrafting.spell.node.NodeTarget;
import astavie.spellcrafting.spell.node.TransmuterDirection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class Spellcrafting implements ModInitializer {

	private static final Identifier CAST_PACKET_ID = new Identifier("spellcrafting", "cast");

	public static Spell BOMB;
	public static Spell EXPLODING_KITTENS;
	public static Spell HULK_SMASH;
	public static Spell FIREWORK;

	@Override
	public void onInitialize() {
		// Spell nodes
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:explode"), new CharmExplode());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:ignite"), new CharmIgnite());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:attune"), new CharmAttune());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:cat"), new CharmSummon(EntityType.CAT));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:momentum"), new CharmMomentum());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:raycast"), new CharmRaycast());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:hit"), new EventEntityInteract(Spell.Event.HIT_ID));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:land"), new EventEntityInteract(Spell.Event.LAND_ID));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:wait"), new EventWait());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:up"), new TransmuterDirection(Direction.UP));

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:self"), new NodeSelf());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:target"), new NodeTarget());

		{
			Spell.Node start   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:self")));
			Spell.Node target  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
			Spell.Node raycast = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:raycast")));
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(raycast, 0));
			nodes.put(new Socket(target, 0), new Socket(raycast, 1));

			nodes.put(new Socket(raycast, 0), new Socket(explode, 0));

			BOMB = new Spell(Sets.newHashSet(start, target), nodes);
		}
		{
			Spell.Node start    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:self")));
			Spell.Node target   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
			Spell.Node cat      = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cat")));
			Spell.Node attune   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:attune")));
			Spell.Node momentum = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:momentum")));
			Spell.Node land     = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:land")));
			Spell.Node explode  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(cat, 0));

			nodes.put(new Socket(start, 0), new Socket(attune, 0));
			nodes.put(new Socket(cat, 0), new Socket(attune, 1));

			nodes.put(new Socket(cat, 0), new Socket(momentum, 0));
			nodes.put(new Socket(target, 0), new Socket(momentum, 1));

			nodes.put(new Socket(attune, 1), new Socket(land, 0));

			nodes.put(new Socket(land, 0), new Socket(explode, 0));

			EXPLODING_KITTENS = new Spell(Sets.newHashSet(start, target), nodes);
		}
		{
			Spell.Node start    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:self")));
			Spell.Node target   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
			Spell.Node up1      = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:up")));
			Spell.Node up2      = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:up")));
			Spell.Node momentum = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:momentum")));
			Spell.Node land     = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:land")));
			Spell.Node explode  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(target, 0), new Socket(up1, 0));
			nodes.put(new Socket(up1, 0), new Socket(up2, 0));

			nodes.put(new Socket(start, 0), new Socket(momentum, 0));
			nodes.put(new Socket(up2, 0), new Socket(momentum, 1));

			nodes.put(new Socket(start, 0), new Socket(land, 0));

			nodes.put(new Socket(land, 0), new Socket(explode, 0));

			HULK_SMASH = new Spell(Sets.newHashSet(start, target), nodes);
		}
		{
			Spell.Node start    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:self")));
			Spell.Node target   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
			Spell.Node up       = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:up")));
			Spell.Node momentum = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:momentum")));
			Spell.Node attune   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:attune")));
			Spell.Node wait1    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
			Spell.Node wait2    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
			Spell.Node explode  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(attune, 0));
			nodes.put(new Socket(target, 0), new Socket(attune, 1));

			nodes.put(new Socket(target, 0), new Socket(up, 0));

			nodes.put(new Socket(target, 0), new Socket(momentum, 0));
			nodes.put(new Socket(up, 0),    new Socket(momentum, 1));

			nodes.put(new Socket(attune, 1), new Socket(wait1, 0));
			nodes.put(new Socket(wait1, 0), new Socket(wait2, 0));

			nodes.put(new Socket(wait2, 0), new Socket(explode, 0));

			FIREWORK = new Spell(Sets.newHashSet(start, target), nodes);
		}
		
		// Items
		ItemTestSpell bomb = new ItemTestSpell(Spell.serialize(BOMB));
		ItemTestSpell hulkSmash = new ItemTestSpell(Spell.serialize(HULK_SMASH));
		ItemTestSpell explodingKittens = new ItemTestSpell(Spell.serialize(EXPLODING_KITTENS));
		ItemTestSpell firework = new ItemTestSpell(Spell.serialize(FIREWORK));

		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "bomb"), bomb);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "hulk_smasg"), hulkSmash);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "exploding_kittens"), explodingKittens);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "firework"), firework);

		// APIs
		Caster.ENTITY_CASTER.registerForType((player, context) -> new CasterPlayer(player), EntityType.PLAYER);
		Attunable.ENTITY_ATTUNABLE.registerFallback((entity, context) -> entity instanceof Attunable ? (Attunable) entity : null);

		SpellContainer.ITEM_SPELL.registerForItems((stack, context) -> (caster) -> {
			ServerWorld world = (ServerWorld) caster.asTarget().getWorld();
			SpellState state = SpellState.of(world);

			NbtCompound nbt = ((ItemTestSpell) stack.getItem()).spell;

			UUID uuid = nbt.getUuid("UUID");
			Spell spell = state.getSpell(uuid);
			if (spell == null) {
				spell = Spell.deserialize(nbt, (ServerWorld) caster.asTarget().getWorld());
				state.addSpell(spell);
			}
			return spell;
		}, bomb, hulkSmash, explodingKittens, firework);

		// Events
		ServerTickEvents.END_WORLD_TICK.register(w -> {
			SpellState.of(w).onEvent(new Spell.Event(Spell.Event.TICK_ID, NbtLong.of(w.getTime())), null);
		});

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

			server.execute(() -> {
				// Get caster
				Caster caster = Caster.ENTITY_CASTER.find(player, null);

				// Get spell
				Spell spell = getSpell(player);
				if (spell == null) {
					return;
				}

				// Cast!
				if (spell.onTarget(caster, target)) {
					player.swingHand(Hand.MAIN_HAND, true);
				}
			});
		});
	}

	public static Target getTarget(World world, HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {
			return new TargetEntity(((EntityHitResult) result).getEntity(), result.getPos());
		} else {
			BlockHitResult block = (BlockHitResult) result;
			return new TargetBlock(world, block.getBlockPos(), block.getPos(), block.getSide());
		}
	}

	public static Spell getSpell(PlayerEntity player) {
		// TODO: Custom foci
		if (!player.getMainHandStack().isEmpty()) {
			return null;
		}

		SpellContainer container = SpellContainer.ITEM_SPELL.find(player.getOffHandStack(), null);
		if (container == null) {
			return null;
		}
		
		return container.getOrCreateSpell(Caster.ENTITY_CASTER.find(player, null));
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
