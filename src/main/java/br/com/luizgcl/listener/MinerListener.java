package br.com.luizgcl.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;

public class MinerListener implements Listener {
    
    private static final int DEPTH = 3; // Profundidade da mineração (você pediu 3 blocos de distância)
    private static final int RADIUS = 1; // Raio lateral (1 = 3x3, 0 = 1x1 apenas linha reta)

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block startBlock = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        UserRepository userRepository = new UserRepository();
        User user = userRepository.findOne(player.getUniqueId());

        if (!Tag.ITEMS_PICKAXES.isTagged(tool.getType())) {
            return;
        }
        
        // O bloco quebrado deve ser "minerável" (Pedra, Deepslate, Minérios, etc)
        // Tag.BASE_STONE_OVERWORLD inclui Stone, Granite, Diorite, Andesite, Deepslate, Tuff
        boolean isStone = Tag.BASE_STONE_OVERWORLD.isTagged(startBlock.getType()) 
                          || Tag.BASE_STONE_NETHER.isTagged(startBlock.getType())
                          || isOre(startBlock.getType());

        if (!isStone) {
            return;
        }

        // Se estiver agachado, cancela a função (para permitir mineração de precisão)
        if (player.isSneaking()) {
            return;
        }

        if (!user.isMinerActive()) {
            return;
        }

        // 2. Descobrir a direção que o player está olhando
        BlockFace facing = getPlayerFacing(player);

        // 3. Pegar os blocos na área
        List<Block> blocksToBreak = getTunnelBlocks(startBlock, facing);

        // 4. Quebrar os blocos
        for (Block b : blocksToBreak) {
            // Não quebrar o bloco original de novo (o evento já vai quebrar ele)
            if (b.equals(startBlock)) continue;

            // Segurança: Não quebrar Bedrock ou coisas inquebráveis
            if (b.getType().getHardness() < 0 || b.getType() == Material.AIR) continue;

            // Opcional: Verificar se o bloco alvo também é pedra/minério para não quebrar madeira/terra sem querer
            if (!Tag.BASE_STONE_OVERWORLD.isTagged(b.getType()) 
                && !Tag.BASE_STONE_NETHER.isTagged(b.getType())
                && !isOre(b.getType())) {
                continue;
            }

            // Quebra e gasta durabilidade
            b.breakNaturally(tool);
            
            // Dano na ferramenta (Paper lida com Unbreaking auto se usar o método certo, mas aqui é manual)
            // Se quiser ser preciso com Unbreaking, teria que usar a API de enchantments.
            // tool.damage(1, player); 
        }
    }

    /**
     * Calcula quais blocos fazem parte do túnel baseada na face.
     */
    private List<Block> getTunnelBlocks(Block start, BlockFace facing) {
        List<Block> blocks = new ArrayList<>();

        // Definimos os eixos baseados na direção
        // Se olhando para Norte/Sul (Eixo Z), variamos X e Y.
        // Se olhando para Leste/Oeste (Eixo X), variamos Z e Y.
        // Se olhando para Cima/Baixo (Eixo Y), variamos X e Z.

        int startDepth = 0;
        
        // Loop da Profundidade (0 a 2)
        for (int d = startDepth; d < DEPTH; d++) {
            
            // Loop da Largura e Altura (-1 a 1)
            for (int w = -RADIUS; w <= RADIUS; w++) {
                for (int h = -RADIUS; h <= RADIUS; h++) {
                    
                    Block target = null;

                    switch (facing) {
                        case NORTH: // -Z
                            target = start.getRelative(w, h, -d);
                            break;
                        case SOUTH: // +Z
                            target = start.getRelative(-w, h, d);
                            break;
                        case EAST:  // +X
                            target = start.getRelative(d, h, w);
                            break;
                        case WEST:  // -X
                            target = start.getRelative(-d, h, -w);
                            break;
                        case UP:    // +Y
                            target = start.getRelative(w, d, h);
                            break;
                        case DOWN:  // -Y
                            target = start.getRelative(w, -d, -h);
                            break;
                        default:
                            break;
                    }

                    if (target != null) {
                        blocks.add(target);
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Determina a direção principal (incluindo Cima e Baixo)
     */
    private BlockFace getPlayerFacing(Player player) {
        float pitch = player.getLocation().getPitch();
        
        // Se olhar muito pra cima (-90 a -45 graus)
        if (pitch < -45) return BlockFace.UP;
        
        // Se olhar muito pra baixo (45 a 90 graus)
        if (pitch > 45) return BlockFace.DOWN;

        // Se estiver olhando reto, usa a direção cardeal (N, S, L, O)
        return player.getFacing();
    }

    // Helper simples para minérios (já que não existe uma Tag única perfeita para todos os minérios e blocos brutos)
    private boolean isOre(Material mat) {
        String name = mat.name();
        return name.endsWith("_ORE") || name.endsWith("_DEBRIS") || name.startsWith("RAW_") || name.equals("OBSIDIAN");
    }
}