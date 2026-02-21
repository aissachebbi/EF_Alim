package com.acetp.feeder.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BranchRegistry {

    private final List<BranchRef> branches = List.of(
            new BranchRef("DEFF", "BP2S FRANKFURT", 1L),
            new BranchRef("ITMM", "BP2S MILANO", 2L),
            new BranchRef("PLPX", "BP2S WARSAW", 3L),
            new BranchRef("HUHX", "BP2S BUDAPEST", 4L),
            new BranchRef("SGSG", "BP2S SINGAPORE", 5L),
            new BranchRef("HKHH", "BP2S HONG-KONG", 6L),
            new BranchRef("CHZZ", "BP2S PARIS", 31L),
            new BranchRef("AU2S", "BP2S SYDNEY", 33L),
            new BranchRef("ESMX", "BP2S MADRID", 34L),
            new BranchRef("GRAX", "BP2S ZURICH", 39L),
            new BranchRef("GGS1", "BP2S ATHENES", 45L),
            new BranchRef("LULL", "BP2S LUXEMBOURG", 46L),
            new BranchRef("BEBZ", "BP2S BRUSSELS", 47L),
            new BranchRef("JESH", "BP2S JERSEY", 48L),
            new BranchRef("GB2L", "BP2S LONDON", 49L),
            new BranchRef("FRPP", "BP2S GUERNSEY", 50L)
    );

    public BranchRef randomBranch() {
        int index = ThreadLocalRandom.current().nextInt(branches.size());
        return branches.get(index);
    }

    public BranchRef findByCode(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            throw new IllegalArgumentException("Le code de branche ne doit pas être vide.");
        }
        return branches.stream()
                .filter(branch -> branch.branchCode().equalsIgnoreCase(branchCode.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune branche trouvée pour le code '" + branchCode + "'."
                ));
    }

    public record BranchRef(String branchCode, String branchName, long branchId) {
    }
}
