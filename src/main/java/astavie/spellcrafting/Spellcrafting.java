package astavie.spellcrafting;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Focus;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.Spell.Socket;
import astavie.spellcrafting.api.spell.SpellContainer;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.api.util.ServerUtils;
import astavie.spellcrafting.block.MagicBlock;
import astavie.spellcrafting.block.MagicCircleBlock;
import astavie.spellcrafting.block.MagicLineBlock;
import astavie.spellcrafting.block.entity.MagicCircleBlockEntity;
import astavie.spellcrafting.client.render.block.entity.MagicCircleBlockEntityRenderer;
import astavie.spellcrafting.item.MagicCircleBlockItem;
import astavie.spellcrafting.item.MagicLineBlockItem;
import astavie.spellcrafting.item.MirrorItem;
import astavie.spellcrafting.item.SpellItem;
import astavie.spellcrafting.spell.CasterPlayer;
import astavie.spellcrafting.spell.SpellState;
import astavie.spellcrafting.spell.node.charm.CharmArrow;
import astavie.spellcrafting.spell.node.charm.CharmAttract;
import astavie.spellcrafting.spell.node.charm.CharmAttune;
import astavie.spellcrafting.spell.node.charm.CharmBeam;
import astavie.spellcrafting.spell.node.charm.CharmExplode;
import astavie.spellcrafting.spell.node.charm.CharmIgnite;
import astavie.spellcrafting.spell.node.charm.CharmLaunch;
import astavie.spellcrafting.spell.node.charm.CharmSummon;
import astavie.spellcrafting.spell.node.event.EventCast;
import astavie.spellcrafting.spell.node.event.EventEntityInteract;
import astavie.spellcrafting.spell.node.event.EventRepeat;
import astavie.spellcrafting.spell.node.event.EventTarget;
import astavie.spellcrafting.spell.node.event.EventWait;
import astavie.spellcrafting.spell.node.event.EventWaitFor;
import astavie.spellcrafting.spell.node.transmuter.TransmuterDirection;
import astavie.spellcrafting.spell.node.transmuter.TransmuterView;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class Spellcrafting implements ModInitializer, ClientModInitializer {

	private static final Identifier CAST_PACKET_ID = new Identifier("spellcrafting", "cast");

	public static Spell BOMB;
	public static Spell EXPLODING_KITTENS;
	public static Spell FIREWORK;
	public static Spell ATTRACTIVE_CAT;
	public static Spell ARROW_STORM;

	public static Item spell = new SpellItem();
	public static Item mirror = new MirrorItem();

	public static MagicLineBlock magicLine = new MagicLineBlock();
	public static MagicCircleBlock magicCircle1x1;
	public static MagicCircleBlock magicCircle2x2;

	static {
		MagicCircleBlock.SIZE = 1;
		magicCircle1x1 = new MagicCircleBlock();
		MagicCircleBlock.SIZE = 2;
		magicCircle2x2 = new MagicCircleBlock();
	}

	public static BlockEntityType<MagicCircleBlockEntity> magicCircleBlockEntity;

	@Override
	public void onInitialize() {
		// Spell nodes
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:explode"), new CharmExplode());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:ignite"), new CharmIgnite());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:attune"), new CharmAttune());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:cat"), new CharmSummon(EntityType.CAT, new ItemList().addItem(Items.STRING, 2)));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:launch"), new CharmLaunch());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:beam"), new CharmBeam());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:arrow"), new CharmArrow());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:attract"), new CharmAttract());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:cast"), new EventCast());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:target"), new EventTarget());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:hit"), new EventEntityInteract(Spell.Event.HIT_ID, new ItemList().addItem(Items.ARROW), ItemVariant.of(Items.TARGET)));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:land"), new EventEntityInteract(Spell.Event.LAND_ID, new ItemList().addItem(Items.FEATHER), ItemVariant.of(Items.FEATHER)));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:wait"), new EventWait());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:waitfor"), new EventWaitFor());
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:repeat"), new EventRepeat());

		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:up"), new TransmuterDirection(Direction.UP));
		Registry.register(NodeType.REGISTRY, new Identifier("spellcrafting:view"), new TransmuterView());

		{
			Spell.Node cast    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
			Spell.Node beam    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:beam")));
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(cast, 0), new Socket(beam, 0));

			nodes.put(new Socket(beam, 0), new Socket(explode, 0));

			BOMB = new Spell(Sets.newHashSet(cast), nodes);
		}
		{
			Spell.Node cast    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
			Spell.Node target  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")), 2);
			
			Spell.Node cat     = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cat")));
			Spell.Node launch  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:launch")));
			Spell.Node waitfor = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:waitfor")), 2);
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(cast, 0), new Socket(target, 0));

			nodes.put(new Socket(cast, 0), new Socket(cat, 0));

			nodes.put(new Socket(cat, 0), new Socket(launch, 0));
			nodes.put(new Socket(target, 0), new Socket(launch, 1));

			nodes.put(new Socket(cat, 0), new Socket(waitfor, 0));
			nodes.put(new Socket(target, 1), new Socket(waitfor, 1));

			nodes.put(new Socket(waitfor, 0), new Socket(explode, 0));

			EXPLODING_KITTENS = new Spell(Sets.newHashSet(cast), nodes);
		}
		{
			Spell.Node cast    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
			Spell.Node target  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
			Spell.Node up      = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:up")));
			Spell.Node launch  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:launch")));
			Spell.Node attune  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:attune")));
			Spell.Node wait1   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
			Spell.Node wait2   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
			Spell.Node explode = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(cast, 0), new Socket(target, 0));

			nodes.put(new Socket(cast, 0), new Socket(attune, 0));
			nodes.put(new Socket(target, 0), new Socket(attune, 1));

			nodes.put(new Socket(target, 0), new Socket(up, 0));

			nodes.put(new Socket(target, 0), new Socket(launch, 0));
			nodes.put(new Socket(up, 0),    new Socket(launch, 1));

			nodes.put(new Socket(attune, 1), new Socket(wait1, 0));
			nodes.put(new Socket(wait1, 0), new Socket(wait2, 0));

			nodes.put(new Socket(wait2, 0), new Socket(explode, 0));

			FIREWORK = new Spell(Sets.newHashSet(cast), nodes);
		}
		{
			Spell.Node cast    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
			Spell.Node target  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:target")), 2);
			Spell.Node cat     = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cat")));
			Spell.Node attract = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:attract")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(cast, 0), new Socket(target, 0));

			nodes.put(new Socket(target, 1), new Socket(cat, 0));

			nodes.put(new Socket(cat, 0), new Socket(attract, 0));
			nodes.put(new Socket(target, 0), new Socket(attract, 1));

			ATTRACTIVE_CAT = new Spell(Sets.newHashSet(cast), nodes);
		}
		{
			Spell.Node cast    = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
			Spell.Node repeat  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:repeat")));
			Spell.Node arrow   = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:arrow")));
			Spell.Node ignite  = new Spell.Node(NodeType.REGISTRY.get(new Identifier("spellcrafting:ignite")));

			Multimap<Socket, Socket> nodes = HashMultimap.create();
			nodes.put(new Socket(cast, 0), new Socket(repeat, 0));

			nodes.put(new Socket(repeat, 0), new Socket(arrow, 0));

			nodes.put(new Socket(arrow, 0), new Socket(ignite, 0));

			ARROW_STORM = new Spell(Sets.newHashSet(cast), nodes);
		}

		// Blocks
		Registry.register(Registry.BLOCK, new Identifier("spellcrafting", "magic_line"), magicLine);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "magic_line"), new MagicLineBlockItem(magicLine, new FabricItemSettings().group(ItemGroup.MISC)));

		Registry.register(Registry.BLOCK, new Identifier("spellcrafting", "magic_circle_1x1"), magicCircle1x1);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "magic_circle_1x1"), new MagicCircleBlockItem(magicCircle1x1, new FabricItemSettings().group(ItemGroup.MISC)));

		Registry.register(Registry.BLOCK, new Identifier("spellcrafting", "magic_circle_2x2"), magicCircle2x2);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "magic_circle_2x2"), new MagicCircleBlockItem(magicCircle2x2, new FabricItemSettings().group(ItemGroup.MISC)));

		// Block Entities
		magicCircleBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("spellcrafting", "magic_circle"), FabricBlockEntityTypeBuilder.create((pos, state) -> (MagicCircleBlockEntity) ((BlockEntityProvider) state.getBlock()).createBlockEntity(pos, state), magicCircle1x1, magicCircle2x2).build());

		// Items
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "spell"), spell);
		Registry.register(Registry.ITEM, new Identifier("spellcrafting", "mirror"), mirror);

		// APIs
		Caster.ENTITY_CASTER.registerForType((player, context) -> new CasterPlayer(player), EntityType.PLAYER);
		Attunable.ENTITY_ATTUNABLE.registerFallback((entity, context) -> entity instanceof Attunable ? (Attunable) entity : null);

		Focus.ITEM_FOCUS.registerSelf(mirror);

		SpellContainer.ITEM_SPELL.registerForItems((stack, context) -> (caster) -> {
			SpellState state = SpellState.getInstance();

			NbtCompound nbt = stack.getSubNbt("spellcrafting:spell");
			if (nbt == null) return null;

			UUID uuid = nbt.getUuid("UUID");
			Spell s = state.getSpell(uuid);
			if (s == null) {
				s = Spell.deserialize(nbt);
				state.addSpell(s);
			}
			return s;
		}, spell);

		// Events
		ServerLifecycleEvents.SERVER_STARTING.register(s -> ServerUtils.server = s);
		ServerTickEvents.END_SERVER_TICK.register(s -> {
			SpellState.getInstance().onEvent(new Spell.Event(Spell.Event.TICK_ID, NbtLong.of(ServerUtils.getTime())), null);
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
				target = new TargetBlock((ServerWorld) player.world, hit.getBlockPos(), hit.getPos(), hit.getSide());
			}

			server.execute(() -> {
				// Get caster
				Caster caster = Caster.ENTITY_CASTER.find(player, null);

				// Get spell
				Pair<Spell, Focus> pair = getSpell(player);
				if (pair == null) {
					return;
				}

				// Cast!
				if (pair.getLeft().onTarget(caster, pair.getRight().redirectTarget(caster, target))) {
					player.swingHand(Hand.OFF_HAND, true);
				}
			});
		});
	}

	public static Target getTarget(ServerWorld world, HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {
			return new TargetEntity(((EntityHitResult) result).getEntity(), result.getPos());
		} else {
			BlockHitResult block = (BlockHitResult) result;
			return new TargetBlock(world, block.getBlockPos(), block.getPos(), block.getSide());
		}
	}

	public static Pair<Spell, Focus> getSpell(PlayerEntity player) {
		Focus focus = Focus.ITEM_FOCUS.find(player.getOffHandStack(), ContainerItemContext.ofPlayerHand(player, Hand.OFF_HAND));
		if (focus == null) focus = Focus.HAND;

		SpellContainer container = SpellContainer.ITEM_SPELL.find(player.getMainHandStack(), null);
		if (container == null) {
			return null;
		}

		Spell spell = container.getOrCreateSpell(Caster.ENTITY_CASTER.find(player, null));
		if (spell == null) {
			return null;
		}
		
		return new Pair<>(spell, focus);
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

	@Override
	public void onInitializeClient() {
		ColorProviderRegistry.BLOCK.register((state, world, pos, i) -> {
			switch (i) {
				case 0:
					return state.get(MagicBlock.STATUS).color;
				case 1:
					return state.get(MagicCircleBlock.INPUT).color;
				case 2:
					return state.get(MagicCircleBlock.OUTPUT).color;
				default:
					return 0;
			}
		}, magicLine, magicCircle1x1, magicCircle2x2);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), magicLine, magicCircle1x1, magicCircle2x2);
		BlockEntityRendererRegistry.register(magicCircleBlockEntity, MagicCircleBlockEntityRenderer::new);
	}

}
