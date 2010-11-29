import java.util.*;

public class ListNsCommand extends Command {

	private WarpEx plugin;

	public ListNsCommand(WarpEx plugin, String... alias) {
		super(null, "Show namespaces");
		setRequire("/listns");
		setAlias(alias);
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		Set<String> set = new HashSet<String>();
		for (Pair<String, String> w : plugin.getAllWarps(player)) {
			if (!w.first.equals(Namespace.Global.get(player))
				&& !w.first.equals(Namespace.Secret.get(player)))
				set.add(w.first);
		}
		if (set.isEmpty()) {
			Chat.toPlayer(player, (Colors.Rose + "No namespaces avairable"));
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String s : set) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(s);
		}
		Chat.toPlayer(player, (Colors.LightGray + "Namespaces: ")
			+ (Colors.White + sb.toString()));
		Chat.toPlayer(player, (Colors.LightGray + "Type ")
			+ (Colors.LightPurple + "/listwarps [name]")
			+ (Colors.LightGray + " to see warps"));
		return true;
	}
}
