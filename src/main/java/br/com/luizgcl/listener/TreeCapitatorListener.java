package br.com.luizgcl.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeCapitatorListener implements Listener {

  private static final int MAX_BLOCKS = 300;
  private static final int SEARCH_RADIUS = 2;

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();

    if (!Tag.ITEMS_AXES.isTagged(player.getInventory().getItemInMainHand().getType())) {
      return;
    }

    if (!Tag.LOGS.isTagged(block.getType())) {
      return;
    }

    if (player.isSneaking()) {
      return;
    }

    event.setCancelled(true);
    breakWholeTree(block, player);
  }

  public void breakWholeTree(Block startBlock, Player player) {
        // 1. Encontrar todos os troncos conectados
        Set<Block> logsToBreak = findConnectedLogs(startBlock);
        
        // 2. Encontrar folhas ao redor desses troncos
        Set<Block> leavesToBreak = findConnectedLeaves(logsToBreak);

        // 3. Quebrar os troncos (Dando dano na ferramenta e dropando itens)
        ItemStack tool = player.getInventory().getItemInMainHand();
        
        for (Block log : logsToBreak) {
            // Verifica se a ferramenta ainda existe/tem durabilidade
            if (tool.getType() == Material.AIR) break; 
            
            log.breakNaturally(tool);
            // Aplica dano na ferramenta (opcional: Unbreaking pode ser calculado aqui)
            // damage(int amount, LivingEntity entity) lida com unbreaking automaticamente nas versões novas
            // Se estiver em versão muito antiga, precisa calcular manualmente.
        }

        // 4. Quebrar as folhas (sem gastar ferramenta)
        for (Block leaf : leavesToBreak) {
            leaf.breakNaturally();
        }
    }

    /**
     * Algoritmo Flood Fill (Busca em Largura)
     */
    private Set<Block> findConnectedLogs(Block start) {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        
        // Material inicial para garantir que não misture tipos de madeira (ex: quebrar carvalho e levar uma acácia junto)
        Material typeToMatch = start.getType(); 

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Block current = queue.poll();

            // Segurança: parar se a árvore for gigante demais
            if (visited.size() >= MAX_BLOCKS) break;

            // Escaneia uma área 3D ao redor do bloco atual
            // SEARCH_RADIUS = 2 cobre uma área de 5x5x5 blocos
            // Isso pega o caso T V V T (distância de 2 blocos vazios)
            for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
                for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                    for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                        
                        if (x == 0 && y == 0 && z == 0) continue;

                        Block target = current.getRelative(x, y, z);

                        // Se já visitamos, pula
                        if (visited.contains(target)) continue;

                        // Verifica se é LOG e se é do MESMO TIPO da árvore inicial
                        if (Tag.LOGS.isTagged(target.getType()) && target.getType() == typeToMatch) {
                            visited.add(target);
                            queue.add(target);
                        }
                    }
                }
            }
        }
        return visited;
    }

    private Set<Block> findConnectedLeaves(Set<Block> logs) {
        Set<Block> leaves = new HashSet<>();
        
        // Para cada tronco encontrado, olhamos em volta por folhas
        for (Block log : logs) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        Block target = log.getRelative(x, y, z);
                        
                        if (Tag.LEAVES.isTagged(target.getType()) && !leaves.contains(target)) {
                            leaves.add(target);
                        }
                    }
                }
            }
        }
        return leaves;
    }
}
