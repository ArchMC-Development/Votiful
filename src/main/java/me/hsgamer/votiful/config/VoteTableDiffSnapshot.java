package me.hsgamer.votiful.config;

import java.util.Collection;

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
