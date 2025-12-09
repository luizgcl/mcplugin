package br.com.luizgcl.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TabListManager {

    private final MiniMessage mm = MiniMessage.miniMessage();

    public void setHeaderFooter(Player player) {
        String headerRaw = "<br><gradient:#ff69b4:#ff0000><bold>MINECRAFT</bold></gradient><br>";

        long time = player.getWorld().getTime();
        long fullTime = player.getWorld().getFullTime();
        long days = fullTime / 24000; // Cada dia tem 24000 ticks

        // Cálculo simples para horas e minutos no jogo (0 = 6:00 AM)
        // Adicionamos 6000 porque o dia começa as 6h (tick 0)
        long gameTime = time; 
        long hours = ((gameTime / 1000) + 6) % 24;
        long minutes = (gameTime % 1000) * 60 / 1000;

        // Formatação bonita (Ex: 08:05)
        String timeString = String.format("%02d:%02d", hours, minutes);

        String footerRaw = "<br>" +
                "<gray>Horário: <yellow>" + timeString + "<br>" +
                "<gray>Dia: <aqua>" + days + "<br>";

        Component header = mm.deserialize(headerRaw);
        Component footer = mm.deserialize(footerRaw);

        // Método moderno do Paper
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void updatePlayerTeam(Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName;
        Component prefix; // Prefixo agora é um Component
        
        // Exemplo de lógica de cargos
        if (player.isOp()) {
            teamName = "01A_Admin";
            // Vermelho e Negrito
            prefix = mm.deserialize("<red>[Admin] </red>"); 
        } else {
            teamName = "99Z_Membro";
            // Cinza
            prefix = Component.text("[Membro] ", NamedTextColor.GRAY);
        }

        if (player.getName().equalsIgnoreCase("anaclsz")) {
            teamName = "01B_Princesa";
            prefix = mm.deserialize("<gradient:#FFB6E1:#FF69B4>[Princesa] </gradient>");
        }

        Team team = sb.getTeam(teamName);
        if (team == null) {
            team = sb.registerNewTeam(teamName);
        }

        // Define o prefixo usando Component (API moderna)
        team.prefix(prefix);
        
        // Define a cor do nome do jogador (usando NamedTextColor ou TextColor.fromHexString)
        if (player.isOp()) {
            team.color(NamedTextColor.RED);
        } else {
            team.color(NamedTextColor.GRAY);
        }

        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
        
        // Opcional: Atualizar o nome na lista (DisplayName) com formatação rica
        player.playerListName(prefix.append(Component.text(player.getName())));
        player.displayName(prefix.append(Component.text(player.getName())));
    }

    public void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            setHeaderFooter(p);
            updatePlayerTeam(p);
        }
    }
}