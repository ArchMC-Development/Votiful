package me.hsgamer.votiful.event;

import me.hsgamer.votiful.data.VoteTableDiffSnapshot;

import java.util.List;

public interface Event {
    List<String> handle(VoteTableDiffSnapshot snapshot);
}
