package com.acetp.feeder.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BranchRegistry {

    private final List<BranchRef> branches = List.of(
            new BranchRef("BP2S FRANKFURT", 1L),
            new BranchRef("BP2S MILANO", 2L),
            new BranchRef("BP2S WARSAW", 3L),
            new BranchRef("BP2S BUDAPEST", 4L),
            new BranchRef("BP2S SINGAPORE", 5L),
            new BranchRef("BP2S HONG-KONG", 6L),
            new BranchRef("BP2S PARIS", 31L),
            new BranchRef("BP2S SYDNEY", 33L),
            new BranchRef("BP2S MADRID", 34L),
            new BranchRef("BP2S ZURICH", 39L),
            new BranchRef("BP2S ATHENES", 45L),
            new BranchRef("BP2S LUXEMBOURG", 46L),
            new BranchRef("BP2S BRUSSELS", 47L),
            new BranchRef("BP2S JERSEY", 48L),
            new BranchRef("BP2S LONDON", 49L),
            new BranchRef("BP2S GUERNSEY", 50L)
    );

    public BranchRef randomBranch() {
        int index = ThreadLocalRandom.current().nextInt(branches.size());
        return branches.get(index);
    }

    public record BranchRef(String branchName, long branchId) {
    }
}
