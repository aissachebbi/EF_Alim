package com.acetp.feeder.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BranchRegistry {

    private final FeederProperties feederProperties;

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

    private final Map<String, BranchRef> branchesByCode = new LinkedHashMap<>();
    private Map<String, Integer> distributionGraph = Map.of();

    public BranchRegistry(FeederProperties feederProperties) {
        this.feederProperties = feederProperties;
    }

    @PostConstruct
    void initialize() {
        for (BranchRef branch : branches) {
            branchesByCode.put(branch.branchCode(), branch);
        }

        if (feederProperties.isBranchDistributionEnabled()) {
            distributionGraph = loadAndValidateDistributionGraph(feederProperties.getBranchDistributionPercentages());
        }
    }

    public BranchRef randomBranch() {
        int index = ThreadLocalRandom.current().nextInt(branches.size());
        return branches.get(index);
    }

    public BranchRef findByCode(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            throw new IllegalArgumentException("Le code de branche ne doit pas être vide.");
        }

        BranchRef branch = branchesByCode.get(branchCode.trim().toUpperCase());
        if (branch == null) {
            throw new IllegalArgumentException("Aucune branche trouvée pour le code '" + branchCode + "'.");
        }
        return branch;
    }

    public Map<String, Integer> getDistributionGraph() {
        return distributionGraph;
    }

    public List<BranchAllocation> computeBranchAllocations(int totalMessages) {
        if (totalMessages <= 0) {
            return List.of();
        }

        List<BranchAllocationCandidate> candidates = new ArrayList<>();
        int totalWeight = 0;

        for (Map.Entry<String, Integer> entry : distributionGraph.entrySet()) {
            int percent = entry.getValue();
            if (percent <= 0) {
                continue;
            }

            BranchRef branchRef = findByCode(entry.getKey());
            double exact = (double) totalMessages * percent / 100d;
            int floor = (int) Math.floor(exact);
            double fraction = exact - floor;
            candidates.add(new BranchAllocationCandidate(branchRef, floor, fraction));
            totalWeight += floor;
        }

        int remaining = totalMessages - totalWeight;
        candidates.sort(Comparator.comparingDouble(BranchAllocationCandidate::fraction).reversed());

        for (int i = 0; i < remaining; i++) {
            BranchAllocationCandidate candidate = candidates.get(i % candidates.size());
            candidate.count = candidate.count + 1;
        }

        List<BranchAllocation> result = new ArrayList<>();
        for (BranchAllocationCandidate candidate : candidates) {
            if (candidate.count > 0) {
                result.add(new BranchAllocation(candidate.branchRef, candidate.count));
            }
        }
        return result;
    }

    private Map<String, Integer> loadAndValidateDistributionGraph(Map<String, Integer> configuredGraph) {
        if (configuredGraph == null || configuredGraph.isEmpty()) {
            throw new IllegalStateException("branchDistributionEnabled=true mais aucun pourcentage n'est défini.");
        }

        Map<String, Integer> normalized = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : configuredGraph.entrySet()) {
            String code = entry.getKey() == null ? "" : entry.getKey().trim().toUpperCase();
            Integer percent = entry.getValue();

            if (!branchesByCode.containsKey(code)) {
                throw new IllegalStateException("Code branche inconnu dans branchDistributionPercentages: " + entry.getKey());
            }
            if (percent == null || percent < 0 || percent > 100) {
                throw new IllegalStateException("Le pourcentage doit être entre 0 et 100 pour la branche " + code);
            }

            normalized.put(code, percent);
        }

        for (String knownCode : branchesByCode.keySet()) {
            normalized.putIfAbsent(knownCode, 0);
        }

        boolean atLeastOnePositive = normalized.values().stream().anyMatch(v -> v > 0);
        if (!atLeastOnePositive) {
            throw new IllegalStateException("Tous les pourcentages de branches sont à 0; impossible de générer des insertions.");
        }

        return Map.copyOf(normalized);
    }

    public record BranchRef(String branchCode, String branchName, long branchId) {
    }

    public record BranchAllocation(BranchRef branchRef, int count) {
    }

    private static final class BranchAllocationCandidate {
        private final BranchRef branchRef;
        private int count;
        private final double fraction;

        private BranchAllocationCandidate(BranchRef branchRef, int count, double fraction) {
            this.branchRef = branchRef;
            this.count = count;
            this.fraction = fraction;
        }

        private double fraction() {
            return fraction;
        }
    }
}
