package fr.elowyr.core.data;

import fr.elowyr.core.user.data.User;

public class Boost implements Comparable<Boost> {

    private Long id;
    private final long userId;
    private final BoostType type;
    private final int value;
    private int time;
    
    public Boost(final User user, final BoostType type, final int value, final int time) {
        this(null, user.getId(), type, value, time);
    }
    
    public Boost(final Long id, final long userId, final BoostType type, final int value, final int time) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.time = time;
    }

    public void addTime(final int time) {
        this.time += time;
    }

    public boolean isFinished() {
        return this.time <= 0;
    }

    @Override
    public int compareTo(final Boost o) {
        return o.getValue() - this.value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public BoostType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
