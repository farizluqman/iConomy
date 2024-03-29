package com.iConomy.util;

import com.iConomy.iConomy;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Misc {
    public static boolean is(String text, String[] is) {
        for (String s : is) {
            if (text.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSelf(CommandSender sender, String name) {
        return ((Player) sender).getName().equalsIgnoreCase(name);
    }

    public static int plural(Double amount) {
        if (amount.doubleValue() != 1.0D || amount.doubleValue() != -1.0D) {
            return 1;
        }
        return 0;
    }

    public static int plural(Integer amount) {
        if (amount.intValue() != 1 || amount.intValue() != -1) {
            return 1;
        }
        return 0;
    }

    public static String BankCurrency(int which, String denom) {
        String[] denoms = denom.split(",");

        return denoms[which];
    }

    public static String formatted(String amount, List<String> maj, List<String> min) {
        String formatted = "";
        String famount = amount.replace(",", "");

        if (Constants.FormatMinor) {
            String[] pieces = null;
            String[] fpieces = null;

            if (amount.contains(".")) {
                pieces = amount.split("\\.");
                fpieces = new String[] { pieces[0].replace(",", ""), pieces[1] };
            } else {
                pieces = new String[] { amount, "0" };
                fpieces = new String[] { amount.replace(",", ""), "0" };
            }

            if (Constants.FormatSeperated) {
                String major = maj.get(plural(Integer.valueOf(fpieces[0])));
                String minor = min.get(plural(Integer.valueOf(fpieces[1])));

                if (pieces[1].startsWith("0") && !pieces[1].equals("0"))
                    pieces[1] = pieces[1].substring(1, pieces[1].length());
                if (pieces[0].startsWith("0") && !pieces[0].equals("0"))
                    pieces[0] = pieces[0].substring(1, pieces[0].length());

                if (Integer.valueOf(fpieces[1]).intValue() != 0 && Integer.valueOf(fpieces[0]).intValue() != 0)
                    formatted = pieces[0] + " " + major + ", " + pieces[1] + " " + minor;
                else if (Integer.valueOf(fpieces[0]).intValue() != 0)
                    formatted = pieces[0] + " " + major;
                else
                    formatted = pieces[1] + " " + minor;
            } else {
                String currency = "";

                if (Double.valueOf(famount).doubleValue() < 1.0D || Double.valueOf(famount).doubleValue() > -1.0D)
                    currency = min.get(plural(Integer.valueOf(fpieces[1])));
                else {
                    currency = maj.get(1);
                }

                formatted = amount + " " + currency;
            }
        } else {
            int plural = plural(Double.valueOf(famount));
            String currency = maj.get(plural);

            formatted = amount + " " + currency;
        }

        return formatted;
    }

    public static Player playerMatch(String name) {
        Collection<? extends Player> online = iConomy.getBukkitServer().getOnlinePlayers();
        Player lastPlayer = null;

        for (Player player : online) {
            String playerName = player.getName();

            if (playerName.equalsIgnoreCase(name)) {
                lastPlayer = player;
                break;
            }

            if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if (lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            }
        }

        return lastPlayer;
    }
}
