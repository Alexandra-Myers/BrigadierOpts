package ivorius.brigadieropts.commands;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.*;
import net.minecraft.test.TestArgArgument;
import net.minecraft.test.TestTypeArgument;
import net.minecraftforge.server.command.ModIdArgument;

public class WrappedArgumentType implements AbstractedArgumentType {
    public final ArgumentType<?> argumentType;
    public WrappedArgumentType(ArgumentType<?> wrapped) {
        argumentType = wrapped;
    }
    @Override
    public Object getArgument(CommandContext<CommandSource> commandContext, String argumentName) throws CommandSyntaxException {
        if (argumentType instanceof AngleArgument)
            return AngleArgument.getAngle(commandContext, argumentName);
        if (argumentType instanceof BlockPosArgument)
            return BlockPosArgument.getOrLoadBlockPos(commandContext, argumentName);
        if (argumentType instanceof BlockPredicateArgument)
            return BlockPredicateArgument.getBlockPredicate(commandContext, argumentName);
        if (argumentType instanceof BlockStateArgument)
            return BlockStateArgument.getBlock(commandContext, argumentName);
        if (argumentType instanceof BoolArgumentType)
            return BoolArgumentType.getBool(commandContext, argumentName);
        if (argumentType instanceof ColorArgument)
            return ColorArgument.getColor(commandContext, argumentName);
        if (argumentType instanceof ColumnPosArgument)
            return ColumnPosArgument.getColumnPos(commandContext, argumentName);
        if (argumentType instanceof ComponentArgument)
            return ComponentArgument.getComponent(commandContext, argumentName);
        if (argumentType instanceof DimensionArgument)
            return DimensionArgument.getDimension(commandContext, argumentName);
        if (argumentType instanceof DoubleArgumentType)
            return DoubleArgumentType.getDouble(commandContext, argumentName);
        if (argumentType instanceof EnchantmentArgument)
            return EnchantmentArgument.getEnchantment(commandContext, argumentName);
        if (argumentType instanceof EntityAnchorArgument)
            return EntityAnchorArgument.getAnchor(commandContext, argumentName);
        if (argumentType instanceof EntityArgument) {
            if(argumentType.equals(EntityArgument.entities())) {
                return EntityArgument.getEntities(commandContext, argumentName);
            }
            if(argumentType.equals(EntityArgument.entity())) {
                return EntityArgument.getEntity(commandContext, argumentName);
            }
            if(argumentType.equals(EntityArgument.players())) {
                return EntityArgument.getPlayers(commandContext, argumentName);
            }
            if(argumentType.equals(EntityArgument.player())) {
                return EntityArgument.getPlayer(commandContext, argumentName);
            }
            return EntityArgument.getEntities(commandContext, argumentName);
        }
        if (argumentType instanceof EntitySummonArgument)
            return EntitySummonArgument.getSummonableEntity(commandContext, argumentName);
        if (argumentType instanceof FloatArgumentType)
            return FloatArgumentType.getFloat(commandContext, argumentName);
        if (argumentType instanceof IRangeArgument.FloatRange)
            return commandContext.getArgument(argumentName, MinMaxBounds.FloatBound.class);
        if (argumentType instanceof GameProfileArgument)
            return GameProfileArgument.getGameProfiles(commandContext, argumentName);
        if (argumentType instanceof IRangeArgument.IntRange)
            return IRangeArgument.IntRange.getRange(commandContext, argumentName);
        if (argumentType instanceof IntegerArgumentType)
            return IntegerArgumentType.getInteger(commandContext, argumentName);
        if (argumentType instanceof ItemArgument)
            return ItemArgument.getItem(commandContext, argumentName);
        if (argumentType instanceof ItemPredicateArgument)
            return ItemPredicateArgument.getItemPredicate(commandContext, argumentName);
        if (argumentType instanceof LongArgumentType)
            return LongArgumentType.getLong(commandContext, argumentName);
        if (argumentType instanceof MessageArgument)
            return MessageArgument.getMessage(commandContext, argumentName);
        if (argumentType instanceof ModIdArgument)
            return commandContext.getArgument(argumentName, String.class);
        if (argumentType instanceof NBTCompoundTagArgument)
            return NBTCompoundTagArgument.getCompoundTag(commandContext, argumentName);
        if (argumentType instanceof NBTPathArgument)
            return NBTPathArgument.getPath(commandContext, argumentName);
        if (argumentType instanceof NBTTagArgument)
            return NBTTagArgument.getNbtTag(commandContext, argumentName);
        if (argumentType instanceof ObjectiveCriteriaArgument)
            return ObjectiveCriteriaArgument.getCriteria(commandContext, argumentName);
        if (argumentType instanceof OperationArgument)
            return OperationArgument.getOperation(commandContext, argumentName);
        if (argumentType instanceof ParticleArgument)
            return ParticleArgument.getParticle(commandContext, argumentName);
        if (argumentType instanceof PotionArgument)
            return PotionArgument.getEffect(commandContext, argumentName);
        if (argumentType instanceof RotationArgument)
            return RotationArgument.getRotation(commandContext, argumentName);
        if (argumentType instanceof ScoreboardSlotArgument)
            return ScoreboardSlotArgument.getDisplaySlot(commandContext, argumentName);
        if (argumentType instanceof StringArgumentType)
            return StringArgumentType.getString(commandContext, argumentName);
        if (argumentType instanceof SwizzleArgument)
            return SwizzleArgument.getSwizzle(commandContext, argumentName);
        if (argumentType instanceof TeamArgument)
            return TeamArgument.getTeam(commandContext, argumentName);
        if (argumentType instanceof TestArgArgument)
            return TestArgArgument.getTestFunction(commandContext, argumentName);
        if (argumentType instanceof TestTypeArgument)
            return TestTypeArgument.getTestClassName(commandContext, argumentName);
        if (argumentType instanceof TimeArgument)
            return IntegerArgumentType.getInteger(commandContext, argumentName);
        if (argumentType instanceof UUIDArgument)
            return UUIDArgument.getUuid(commandContext, argumentName);
        if (argumentType instanceof Vec2Argument)
            return Vec2Argument.getVec2(commandContext, argumentName);
        if (argumentType instanceof Vec3Argument)
            return Vec3Argument.getVec3(commandContext, argumentName);
        if (argumentType instanceof AbstractedArgumentType)
            return ((AbstractedArgumentType) argumentType).getArgument(commandContext, argumentName);
        return null;
    }
}
