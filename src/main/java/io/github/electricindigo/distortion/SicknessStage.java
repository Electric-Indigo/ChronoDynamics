package io.github.electricindigo.distortion;

public enum SicknessStage
{
    NONE,
    MILD,
    STRONG;

    public static final int MILD_AT = 25;
    public static final int STRONG_AT = 60;

    public static SicknessStage of(int value)
    {
        if (value >= STRONG_AT) return STRONG;
        if (value >= MILD_AT) return MILD;
        return NONE;
    }
}
