import java.util.*;
import java.util.concurrent.*;

public class InvitEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "60";

	private final ScheduledExecutorService scheduler;
	private final Map<String, Pair<String, ScheduledFuture<?>>> futures;
	private int expires;
	private final Command invite, accept;

	public InvitEx() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		futures = new HashMap<String, Pair<String, ScheduledFuture<?>>>();
		invite = new InviteCommand(this);
		accept = new AcceptCommand(this);

		// addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.MEDIUM);
	}

	public void addInvite(String hostName, String guestName, Runnable timeout) {
		ScheduledFuture<?> future =
			scheduler.schedule(timeout, expires, TimeUnit.SECONDS);
		futures.put(guestName, new Pair<String, ScheduledFuture<?>>(hostName,
			future));
	}

	public Pair<String, ScheduledFuture<?>> getInvite(String guestName) {
		return futures.containsKey(guestName) ? futures.get(guestName) : null;
	}

	public void removeInvite(String guestName) {
		if (futures.containsKey(guestName)) {
			futures.get(guestName).second.cancel(true);
			futures.remove(guestName);
		}
	}

	@Override
	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
		addCommand(invite, accept);
	}

	@Override
	protected void onDisable() {
		removeCommand(invite, accept);
	}

}
