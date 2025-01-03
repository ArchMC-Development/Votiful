package me.hsgamer.votiful.data;

import java.util.Collection;
import java.util.Objects;

public class VoteTableDiffSnapshot {
    public final VoteTableSnapshot oldSnapshot;
    public final VoteTableSnapshot newSnapshot;
    public final long timestamp;

    public VoteTableDiffSnapshot(VoteTableSnapshot oldSnapshot, VoteTableSnapshot newSnapshot) {
        this.oldSnapshot = oldSnapshot;
        this.newSnapshot = newSnapshot;
        this.timestamp = System.currentTimeMillis();
    }

    public VoteTableDiffSnapshot(Collection<VoteTableDiffSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            throw new IllegalArgumentException("Empty snapshots");
        }
        if (snapshots.size() == 1) {
            VoteTableDiffSnapshot snapshot = snapshots.iterator().next();
            this.oldSnapshot = snapshot.oldSnapshot;
            this.newSnapshot = snapshot.newSnapshot;
            this.timestamp = snapshot.timestamp;
        } else {
            VoteTableSnapshot oldSnapshot = null;
            VoteTableSnapshot newSnapshot = null;
            long oldTimestamp = System.currentTimeMillis();
            long newTimestamp = 0;
            for (VoteTableDiffSnapshot snapshot : snapshots) {
                if (snapshot.timestamp < oldTimestamp) {
                    oldSnapshot = snapshot.oldSnapshot;
                    oldTimestamp = snapshot.timestamp;
                }
                if (snapshot.timestamp > newTimestamp) {
                    newSnapshot = snapshot.newSnapshot;
                    newTimestamp = snapshot.timestamp;
                }
            }
            if (oldSnapshot == null || newSnapshot == null) {
                throw new IllegalArgumentException("Invalid snapshots");
            }
            this.oldSnapshot = oldSnapshot;
            this.newSnapshot = newSnapshot;
            this.timestamp = newTimestamp;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteTableDiffSnapshot that = (VoteTableDiffSnapshot) o;
        return timestamp == that.timestamp && Objects.equals(oldSnapshot, that.oldSnapshot) && Objects.equals(newSnapshot, that.newSnapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldSnapshot, newSnapshot, timestamp);
    }

    @Override
    public String toString() {
        return "VoteTableDiffSnapshot{" +
                "oldSnapshot=" + oldSnapshot +
                ", newSnapshot=" + newSnapshot +
                ", timestamp=" + timestamp +
                '}';
    }
}
