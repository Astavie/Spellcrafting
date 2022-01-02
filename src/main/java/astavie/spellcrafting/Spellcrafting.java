package astavie.spellcrafting;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
import astavie.spellcrafting.spell.node.CharmArrow;
import astavie.spellcrafting.spell.node.CharmExplode;
import astavie.spellcrafting.spell.node.CharmIgnite;
import astavie.spellcrafting.spell.node.EventEntityInteract;
import astavie.spellcrafting.spell.node.EventWait;
import astavie.spellcrafting.spell.node.NodeStart;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class Spellcrafting implements ModInitializer {

	private static final ItemTestSpell test = new ItemTestSpell();
	private static final Identifier CAST_PACKET_ID = new Identifier("spellcrafting", "cast");

	public static Spell TEST_SPELL;

	public static Spell ARROW_FIRE;
	public static Spell LAND_EXPLODE;
	public static Spell ARROW_EXPLODE;

	@Override
	public void onInitialize() {
		// Spell nodes
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:arrow"), new CharmArrow());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:explode"), new CharmExplode());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:ignite"), new CharmIgnite());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:hit"), new EventEntityInteract(Spell.Event.HIT_ID));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:land"), new EventEntityInteract(Spell.Event.LAND_ID));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:wait"), new EventWait());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:start"), new NodeStart());

		{
			Spell.Node start  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:start")));
			Spell.Node arrow  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:arrow")));
			Spell.Node hit    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:hit")));
			Spell.Node ignite = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:ignite")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(arrow, 0));
			nodes.put(new Socket(start, 1), new Socket(arrow, 1));

			nodes.put(new Socket(arrow, 0), new Socket(hit, 0));

			nodes.put(new Socket(hit, 0), new Socket(ignite, 0));

			ARROW_FIRE = new Spell(start, nodes);
		}
		{
			Spell.Node start   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:start")));
			Spell.Node land    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:land")));
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(land, 0));

			nodes.put(new Socket(land, 0), new Socket(explode, 0));

			LAND_EXPLODE = new Spell(start, nodes);
		}
		{
			Spell.Node start   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:start")));
			Spell.Node arrow   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:arrow")));
			Spell.Node wait    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(start, 0), new Socket(arrow, 0));
			nodes.put(new Socket(start, 1), new Socket(arrow, 1));

			nodes.put(new Socket(arrow, 0), new Socket(wait, 0));

			nodes.put(new Socket(wait, 0), new Socket(explode, 0));

			ARROW_EXPLODE = new Spell(start, nodes);
		}

		TEST_SPELL = ARROW_EXPLODE;
		
		// Items
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "test"), test);

		// APIs
		Caster.ENTITY_CASTER.registerForType((player, context) -> new CasterPlayer(player), EntityType.PLAYER);
		Attunable.ENTITY_ATTUNABLE.registerForTypes((entity, context) -> (Attunable) entity, EntityType.PLAYER, EntityType.ARROW);

		SpellContainer.ITEM_SPELL.registerForItems((stack, context) -> (caster) -> TEST_SPELL, test);

		// Events
        // TODO: This now only works on test spell
		ServerTickEvents.END_SERVER_TICK.register(w -> TEST_SPELL.onEvent(new Spell.Event(Spell.Event.TICK_ID, NbtLong.of(w.getOverworld().getTime())), null));

		// Networking
		ServerPlayNetworking.registerGlobalReceiver(CAST_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			// Get caster
			Caster caster = Caster.ENTITY_CASTER.find(player, null);

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
			Spell spell = getSpell(player);
			if (spell == null) {
				return;
			}

			// Cast!
			if (spell.onTarget(caster, target)) {
				player.swingHand(Hand.MAIN_HAND, true);
			}
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
		
		return container.checkForSpell(Caster.ENTITY_CASTER.find(player, null));
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
