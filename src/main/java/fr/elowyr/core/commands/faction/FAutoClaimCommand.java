package fr.elowyr.core.commands.faction;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.claim.CmdAutoClaim;
import com.massivecraft.factions.util.TL;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.user.data.User;

public class FAutoClaimCommand extends CmdAutoClaim {

    public void perform(final CommandContext var1) {
        final Faction var2 = var1.argAsFaction(0, var1.faction);
        if (var2 != null && var2 != var1.fPlayer.getAutoClaimFor()) {
            if (!var1.fPlayer.canClaimForFaction(var2)) {
                if (var1.faction == var2) {
                    var1.msg(TL.CLAIM_CANTCLAIM, var2.describeTo(var1.fPlayer));
                }
                else {
                    var1.msg(TL.COMMAND_AUTOCLAIM_OTHERFACTION, var2.describeTo(var1.fPlayer));
                }
            }
            else {
                final User user = UserManager.get(var1.player.getUniqueId());
                if (user.canUseCommand("fautoclaim", true)) {
                    var1.fPlayer.setAutoClaimFor(var2);
                    var1.msg(TL.COMMAND_AUTOCLAIM_ENABLED, var2.describeTo(var1.fPlayer));
                    var1.fPlayer.attemptClaim(var2, var1.player.getLocation(), true);
                }
            }
        }
        else {
            var1.fPlayer.setAutoClaimFor(null);
            var1.msg(TL.COMMAND_AUTOCLAIM_DISABLED);
        }
    }
}
