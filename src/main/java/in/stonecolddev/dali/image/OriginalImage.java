package in.stonecolddev.dali.image;

import org.immutables.value.Value;

import java.time.OffsetDateTime;

@Value.Immutable
public abstract class OriginalImage {
    public abstract int id();
    public abstract Gallery gallery();
    public abstract String filenameHash();
    public abstract String path();
    public abstract String description();
    public abstract int height();
    public abstract int width();
    public abstract int fileSize();
    public abstract OffsetDateTime createdOn();
    public abstract OffsetDateTime updatedOn();
}