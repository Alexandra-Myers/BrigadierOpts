/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.brigadieropts;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ivorius.brigadieropts.BrigadierOpts.MOD_ID;

/**
 * Created by lukas on 08.06.17.
 */
@Mod(MOD_ID)
public class BrigadierOpts
{
    public static final String NAME = "BrigadierOpts";
    public static final String MOD_ID = "brigadieropts";
    public static final String VERSION = "1.0.0-1.16.5";

    public static Logger logger = LogManager.getLogger(NAME);
    public BrigadierOpts() {

    }
}
