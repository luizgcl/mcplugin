package br.com.luizgcl.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.luizgcl.Main;
import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerListener implements Listener {

  @EventHandler
  void event(PlayerJoinEvent event) {
    event.joinMessage(null);
    event.getPlayer()
        .sendMessage(Component.text()
            .append(Component.text("Seja bem-vindo ao servidor!").color(NamedTextColor.GREEN))
            .append(Component.newline())
            .append(Component.text("Divirta-se!").color(NamedTextColor.YELLOW)));
    
    UserRepository userRepository = new UserRepository();

    User user = userRepository.findOne(event.getPlayer().getUniqueId());

    if (user == null) {
      User newUser = new User(event.getPlayer().getUniqueId());
      newUser.setAllowFlight(event.getPlayer().getAllowFlight());
      newUser.setFlying(event.getPlayer().isFlying());

      Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        userRepository.create(newUser);
      });

      user = newUser;
    }

    event.getPlayer().setAllowFlight(user.isAllowFlight());
    event.getPlayer().setFlying(user.isFlying());
  }

  @EventHandler
  void event(PlayerQuitEvent event) {
    event.quitMessage(null);
  }
}
