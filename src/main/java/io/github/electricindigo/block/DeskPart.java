package io.github.electricindigo.block;

import net.minecraft.util.StringRepresentable;

public enum DeskPart implements StringRepresentable
{
    PRIMARY("primary"),
    SECONDARY("secondary");

    private final String name;

    DeskPart(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
