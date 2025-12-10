package br.com.luizgcl.pet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import br.com.luizgcl.utils.ItemBuilder;

public class GorillaMob {

    private final JavaPlugin plugin;
    private Zombie host;
    private ItemDisplay head;
    private BlockDisplay torso, armLeft, armRight, legLeft, legRight;
    private boolean isSitting = false;
    private int behaviorTick = 0;
    private Player targetPlayer;

    public GorillaMob(JavaPlugin plugin, Location location, Player target) {
        this.plugin = plugin;
        this.targetPlayer = target;
        spawn(location);
    }

    private void spawn(Location loc) {
        // 1. Criar o "Cérebro"
        host = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        host.setInvisible(true);
        host.setSilent(true);
        host.setBaby(false);
        host.getEquipment().clear();
        host.setShouldBurnInDay(false);

        // 2. Criar as partes do corpo
        
        // --- CABEÇA ---
        head = (ItemDisplay) loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
        head.setItemStack(ItemBuilder.skull().skullOwner("GaoWOW").build());
        head.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
        
        Transformation headTrans = head.getTransformation();
        headTrans.getScale().set(0.8f, 0.91f, 0.8f);
        head.setTransformation(headTrans);

        // --- CORPO (Blocos) ---
        // Nota: O segredo do alinhamento está dentro do método spawnBlock agora
        torso = spawnBlock(loc, Material.BLACK_WOOL, new Vector3f(0.9f, 0.9f, 0.6f));
        armLeft = spawnBlock(loc, Material.GRAY_WOOL, new Vector3f(0.3f, 0.8f, 0.3f));
        armRight = spawnBlock(loc, Material.GRAY_WOOL, new Vector3f(0.3f, 0.8f, 0.3f));
        legLeft = spawnBlock(loc, Material.BLACK_WOOL, new Vector3f(0.35f, 0.6f, 0.35f));
        legRight = spawnBlock(loc, Material.BLACK_WOOL, new Vector3f(0.35f, 0.6f, 0.35f));

        // 3. Iniciar Loop
        startLoop();
    }

    private BlockDisplay spawnBlock(Location loc, Material mat, Vector3f scale) {
        BlockDisplay bd = (BlockDisplay) loc.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        bd.setBlock(mat.createBlockData());
        
        Transformation transform = bd.getTransformation();
        transform.getScale().set(scale);
        
        // --- CORREÇÃO DE ALINHAMENTO ---
        // Centraliza o bloco visualmente no ponto de origem da entidade.
        // Movemos metade da escala para trás/esquerda.
        transform.getTranslation().set(-scale.x / 2, 0, -scale.z / 2);
        
        bd.setTransformation(transform);
        return bd;
    }

    private void startLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (host == null || host.isDead()) {
                    removeParts();
                    this.cancel();
                    return;
                }
                behaviorTick++;
                handleAI();
                updateModelPosition();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void handleAI() {
        if (behaviorTick % 160 < 100) {
            // ANDANDO
            if (isSitting) {
                isSitting = false;
                host.setAI(true);
            }
            if (targetPlayer != null && targetPlayer.isOnline()) {
                host.setTarget(targetPlayer);
            }
        } else {
            // SENTANDO
            if (!isSitting) {
                isSitting = true;
                host.setTarget(null);
                host.setAI(false);
                host.setVelocity(new Vector(0, 0, 0));
            }
        }
    }

    private void updateModelPosition() {
        Location baseLoc = host.getLocation();
        
        double walkAnim = isSitting ? 0 : Math.sin(behaviorTick * 0.2); 
        
        // --- CORREÇÃO DO SENTAR ---
        // Valor negativo para descer o corpo
        double sitOffset = isSitting ? -0.4 : 0;

        // --- ATUALIZAÇÃO DE POSIÇÕES ---

        // Tronco
        teleportPart(torso, baseLoc, 0, 0.8 + sitOffset, 0, 0);

        // Cabeça
        // Offset Y ajustado para 1.6 (um pouco mais baixo para colar no pescoço)
        // Passamos 'true' no ultimo parametro para corrigir a rotação 180
        teleportHead(head, baseLoc, 0, 1.45 + sitOffset, 0.5);

        // Braços (Ajustados para o novo centro do spawnBlock)
        // Como agora o bloco é centralizado, 0.6 fica exato (0.45 do corpo + 0.15 do braço)
        double armLeftAngle = isSitting ? 0 : Math.toRadians(walkAnim * 30);
        teleportPart(armLeft, baseLoc, -0.6, 1.3 + sitOffset, 0.0, armLeftAngle);

        double armRightAngle = isSitting ? 0 : Math.toRadians(-walkAnim * 30);
        teleportPart(armRight, baseLoc, 0.6, 1.3 + sitOffset, 0.0, armRightAngle);

        // Pernas
        if (isSitting) {
            // Sentado: Pernas vão para frente e SOBEM um pouco para não entrar no chão
            teleportPart(legLeft, baseLoc, -0.25, 0.6 + sitOffset, 0.5, Math.toRadians(-90));
            teleportPart(legRight, baseLoc, 0.25, 0.6 + sitOffset, 0.5, Math.toRadians(-90));
        } else {
            // Andando
            double legAnim = Math.sin(behaviorTick * 0.2);
            teleportPart(legLeft, baseLoc, -0.25, 0.3, 0, Math.toRadians(-legAnim * 20));
            teleportPart(legRight, baseLoc, 0.25, 0.3, 0, Math.toRadians(legAnim * 20));
        }
    }

    // Método especial para a cabeça (Gira 180 graus)
    private void teleportHead(ItemDisplay head, Location center, double offX, double offY, double offZ) {
        Location partLoc = calculateOffset(center, offX, offY, offZ);

        if (targetPlayer != null && targetPlayer.isOnline()) {
            // 2. Matemática para olhar para o Player
            // Vetor = Destino (Olhos do Player) - Origem (Cabeça do Gorila)
            Location targetEye = targetPlayer.getEyeLocation();
            Vector direction = targetEye.toVector().subtract(partLoc.toVector());
            
            // Aplica a direção ao local
            partLoc.setDirection(direction);
            
            // 3. Correção de Rotação do ItemDisplay
            // Como o ItemDisplay renderiza a face invertida, somamos 180 no Yaw
            partLoc.setYaw(partLoc.getYaw() + 180);
            
            // DICA OPCIONAL: Se quiser que ele olhe para cima/baixo (Pitch), deixe como está.
            // Se quiser que a cabeça gire apenas para os lados (travar pescoço reto), descomente abaixo:
            // partLoc.setPitch(0); 
        } else {
            // Fallback: Se o player sumir, a cabeça segue a rotação do corpo
            partLoc.setYaw(center.getYaw() + 180);
            partLoc.setPitch(0);
        }

        head.teleport(partLoc);
    }

    private void teleportPart(Display part, Location center, double offX, double offY, double offZ, double pitchRot) {
        Location partLoc = calculateOffset(center, offX, offY, offZ);
        
        part.teleport(partLoc);
        
        Transformation t = part.getTransformation();
        t.getLeftRotation().set(new AxisAngle4f((float) pitchRot, 1, 0, 0)); 
        part.setTransformation(t);
    }
    
    // Separei a matemática de posição para ficar mais limpo
    private Location calculateOffset(Location center, double offX, double offY, double offZ) {
        Location loc = center.clone();
        double rads = Math.toRadians(center.getYaw());
        double x = offX * Math.cos(rads) - offZ * Math.sin(rads);
        double z = offX * Math.sin(rads) + offZ * Math.cos(rads);
        loc.add(x, offY, z);
        return loc;
    }

    public void removeParts() {
        if (head != null) head.remove();
        if (torso != null) torso.remove();
        if (armLeft != null) armLeft.remove();
        if (armRight != null) armRight.remove();
        if (legLeft != null) legLeft.remove();
        if (legRight != null) legRight.remove();
        if (host != null && !host.isDead()) host.remove();
    }
}