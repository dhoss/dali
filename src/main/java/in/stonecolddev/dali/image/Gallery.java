package in.stonecolddev.dali.image;

import org.immutables.value.Value;

import java.time.OffsetDateTime;

@Value.Immutable
public abstract class Gallery {
    public abstract int id();
    public abstract String slug();
    public abstract String name();
    public abstract String text();
    public abstract OffsetDateTime createdOn();
    public abstract OffsetDateTime updatedOn();
}