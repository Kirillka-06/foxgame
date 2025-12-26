package ru.kirill.foxgame.model;

/**
 * Represents the effects of special cards.
 */
public enum CardEffect {
    NONE("Нет эффекта"),
    SWAN("Лебедь: меняет следующего ведущего"),
    FOX("Лиса: меняет козырь"),
    WOODCUTTER("Дровосек: сбрасывает карту соперника"),
    WITCH("Ведьма: становится козырем"),
    GUARD("Страж: блокирует эффекты"),
    LUNATIC("Лунатик: специальный эффект");
    
    private final String description;
    
    CardEffect(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
    
    /**
     * Gets the effect for a given rank.
     */
    public static CardEffect getEffectForRank(Rank rank) {
        switch (rank) {
            case ONE: return SWAN;
            case THREE: return FOX;
            case FIVE: return WOODCUTTER;
            case SEVEN: return GUARD;
            case NINE: return WITCH;
            case ELEVEN: return LUNATIC;
            default: return NONE;
        }
    }
}
