/*******************************************************************************
 * Copyright 2014 stuntguy3000 (Luke Anderson) and coasterman10.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 ******************************************************************************/
package com.tgn.ProjectApex.DestroyTheMonument.stats;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tgn.ProjectApex.DestroyTheMonument.Monument;
import com.tgn.ProjectApex.DestroyTheMonument.manager.ConfigManager;

import org.bukkit.entity.Player;

public class StatsManager {
    private Monument plugin;
    private ConfigManager config;
    public static final int UNDEF_STAT = -42;

    public StatsManager(Monument instance, ConfigManager config) {
        this.plugin = instance;
        this.config = config;
    }

    public int getStat(StatType s, Player p) {
        if (!plugin.useMysql) {
            return config.getConfig("stats.yml").getInt(
                    p.getName() + "." + s.name());
        } else {
            try {
                int stat = UNDEF_STAT;
                ResultSet rs = plugin
                        .getDatabaseHandler()
                        .query("SELECT * FROM `" + plugin.mysqlName + "` WHERE `username`='"
                                + p.getName() + "'").getResultSet();

                while (rs.next())
                    stat = rs.getInt(s.name().toLowerCase());

                return stat;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return UNDEF_STAT;
            }
        }
    }

    public void setValue(StatType s, Player p, int value) {
        if (!plugin.useMysql) {
            config.getConfig("stats.yml").set(p.getName() + "." + s.name(),
                    value);
            config.save("stats.yml");
        } else {
            plugin.getDatabaseHandler().query(
                    "UPDATE `" + plugin.mysqlName + "` SET `" + s.name().toLowerCase()
                            + "`='" + value + "' WHERE `username`='"
                            + p.getName() + "';");
        }
    }

    public void incrementStat(StatType s, Player p) {
        incrementStat(s, p, 1);
    }

    public void incrementStat(StatType s, Player p, int amount) {
        setValue(s, p, getStat(s, p) + amount);
    }
}
