package me.hsgamer.votiful.data;

import java.util.Objects;

public class VoteValue {
    public static final VoteValue EMPTY = new VoteValue(0, 0);

    public final int vote;
    public final long lastVoteTimestamp;

    public VoteValue(int vote, long lastVoteTimestamp) {
        this.vote = vote;
        this.lastVoteTimestamp = lastVoteTimestamp;
    }

    public VoteValue addVote(int vote, long timestamp) {
        return new VoteValue(this.vote + vote, timestamp);
    }

    public VoteValue addVote(long timestamp) {
        return addVote(1, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteValue voteValue = (VoteValue) o;
        return vote == voteValue.vote && lastVoteTimestamp == voteValue.lastVoteTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vote, lastVoteTimestamp);
    }

    @Override
    public String toString() {
        return "VoteValue{" +
                "vote=" + vote +
                ", lastVoteTimestamp=" + lastVoteTimestamp +
                '}';
    }
}
