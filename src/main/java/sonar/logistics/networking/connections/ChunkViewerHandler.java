package sonar.logistics.networking.connections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.ChunkHelper;
import sonar.logistics.PL2;
import sonar.logistics.api.tiles.displays.IDisplay;

public class ChunkViewerHandler {

	private static final ChunkViewerHandler INSTANCE = new ChunkViewerHandler();
	public Map<IDisplay, List<ChunkPos>> displayChunks = Maps.newHashMap();

	public static final ChunkViewerHandler instance() {
		return INSTANCE;
	}

	public void onDisplayAdded(IDisplay display) {}

	public void onDisplayRemoved(IDisplay display) {
		displayChunks.remove(display);
	}
	/*
	public void markChunkChange(int dim, ChunkPos pos) {
		changedChunks.putIfAbsent(dim, Lists.newArrayList());
		List<ChunkPos> positions = changedChunks.get(dim);
		if (!positions.contains(pos)) {
			positions.add(pos);
		}
	}
	*/
	public boolean hasViewers(World world, BlockPos pos) {
		return !ChunkHelper.getChunkPlayers(world, pos).isEmpty();
	}

	public List<EntityPlayerMP> getWatchingPlayers(List<IDisplay> displays) {
		List<EntityPlayerMP> watchingPlayers = Lists.newArrayList();
		Map<Integer, List<ChunkPos>> chunks = getWatchingChunks(displays);
		chunks.forEach((DIM, positions) -> {
			World server = SonarCore.proxy.getDimension(DIM);
			List<EntityPlayerMP> chunkPlayers = ChunkHelper.getChunkPlayers(server, positions);
			chunkPlayers.forEach(player -> {
				if (!watchingPlayers.contains(player)) {
					watchingPlayers.add(player);
				}
			});
		});
		return watchingPlayers;
	}

	public List<EntityPlayerMP> getWatchingPlayers(IDisplay display) {
		World server = SonarCore.proxy.getDimension(display.getCoords().getDimension());
		return ChunkHelper.getChunkPlayers(server, getWatchingChunks(display));
	}

	public Map<Integer, List<ChunkPos>> getWatchingChunks(List<IDisplay> displays) {
		HashMap<Integer, List<ChunkPos>> watchingChunks = Maps.newHashMap();
		displays.forEach(display -> {
			int dim = display.getCoords().getDimension();
			List<ChunkPos> displayChunks = getWatchingChunks(display);
			watchingChunks.putIfAbsent(dim, Lists.newArrayList());
			List<ChunkPos> chunks = watchingChunks.get(dim);
			displayChunks.forEach(chunk -> {
				if (!chunks.contains(chunk)) {
					chunks.add(chunk);
				}
			});
		});
		return watchingChunks;
	}

	public List<ChunkPos> getWatchingChunks(IDisplay display) {
		List<ChunkPos> positions = displayChunks.get(display);
		if (positions == null) {
			displayChunks.put(display, ChunkHelper.getChunksInRadius(display.getCoords().getBlockPos(), 128));
			positions = displayChunks.get(display);
		}
		return positions;
	}

	public List<IDisplay> getDisplaysInChunk(int dim, ChunkPos pos) {
		List<IDisplay> inChunk = Lists.newArrayList();
		for (IDisplay display : PL2.getServerManager().displays.values()) {
			BlockCoords coords = display.getCoords();
			if (coords.getDimension() == dim && coords.insideChunk(pos)) {
				inChunk.add(display);
			}
		}
		return inChunk;
	}
}