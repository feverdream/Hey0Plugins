import java.util.*;
import java.util.concurrent.*;

public class AcceptCommand extends Command {

	private InvitEx plugin;

	public AcceptCommand(InvitEx plugin) {
		super(null, "Accept invite");
		setRequire("/accept");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		String guestName = player.getName();
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(guestName);
		if (invite == null) {
			Chat.toPlayer(player, (Colors.Rose + "Nobody invited you"));
			return true;
		}
		Player host = etc.getServer().getPlayer(invite.first);
		if (host == null) {
			Chat.toPlayer(player, (Colors.LightGreen + invite.first)
				+ (Colors.Rose + " is not online. Invite has cancelled"));
		} else {
			Chat.toPlayer(host, (Colors.LightGreen + guestName)
				+ (Colors.LightGray + " accepted your invite"));
			player.teleportTo(host);
		}
		plugin.removeInvite(guestName);
		return true;
	}

}
