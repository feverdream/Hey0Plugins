public class BuyCommand extends Command {
	private Market market;

	public BuyCommand(Market market) {
		super(new String[] { "/buy" }, "<amount> <item>", "Buy items");
		this.market = market;
	}

	public boolean call(Player player, String[] args) {
		Goods item = market.getGoods().get(player.getItemInHand());
		int amount = 1;
		if (args.length > 1) {
			try {
				amount = Integer.valueOf(args[1]);
				if (args.length > 2) {
					item = market.findGoods(args[2]);
				}
			} catch (Exception e) {
			}
		}
		if (item == null) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		if (item.getStock() == 0) {
			Chat.toPlayer(player, Colors.Gold + "%s is sold out", item.getName());
			return true;
		}
		if (amount > item.getStock()) {
			Chat.toPlayer(player, Colors.Gold + "%s has only %d stock%s",
					item.getName(), item.getStock(), item.getStock() == 1 ? "" : "s");
			return true;
		}
		int money = market.getMoney(player.getName());
		int payment = item.getActualPrice(true, amount);
		if (payment > money) {
			Chat.toPlayer(player, Colors.Gold + "You need %s to buy (you have %s)",
					market.currencyFormat(payment), market.currencyFormat(money));
			return true;
		}
		if (!item.buy(amount)) {
			Chat.toPlayer(player, Colors.Rose
					+ "Sorry, an error occured. Try again later.");
			return true;
		}
		Inventory inv = player.getInventory();
		for (int count = amount; count > 0;) {
			Item it = inv.getItemFromId(item.getId());
			if (it == null) {
				int slot = inv.getEmptySlot();
				if (slot == -1) {
					Chat.toPlayer(player, Colors.Rose
							+ "No enough space in your inventory");
					return true;
				}
				it = inv.getItemFromSlot(slot);
			}
			int newAmount = it.getAmount() + count;
			count = newAmount > 64 ? newAmount - 64 : 0;
			it.setAmount(newAmount);
		}
		inv.updateInventory();
		market.setMoney(player.getName(), money - payment);
		market.saveGoods();
		market.saveBank();
		Chat.toPlayer(player, Colors.LightGreen + "You bought %d %s (paid %s)",
				amount, item.getName(), market.currencyFormat(payment));
		Log.info("Market: %s BOUGHT %d %s paid %d total %d", player.getName(),
				amount, item.getName(), market.currencyFormat(payment),
				market.currencyFormat(money - payment));
		return true;
	}
}
