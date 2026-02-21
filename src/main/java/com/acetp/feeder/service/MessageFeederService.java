package com.acetp.feeder.service;

import com.acetp.feeder.config.BranchRegistry;
import com.acetp.feeder.config.FeederProperties;
import com.acetp.feeder.domain.CbMsgRecord;
import com.acetp.feeder.repository.CbMsgRepository;
import com.acetp.feeder.repository.ClBusinessMtmInRepository;
import com.acetp.feeder.repository.IdGeneratorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MessageFeederService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFeederService.class);

    private final FeederProperties feederProperties;
    private final BranchRegistry branchRegistry;
    private final IdGeneratorRepository idGeneratorRepository;
    private final CbMsgRepository cbMsgRepository;
    private final ClBusinessMtmInRepository clBusinessMtmInRepository;

    public MessageFeederService(
            FeederProperties feederProperties,
            BranchRegistry branchRegistry,
            IdGeneratorRepository idGeneratorRepository,
            CbMsgRepository cbMsgRepository,
            ClBusinessMtmInRepository clBusinessMtmInRepository
    ) {
        this.feederProperties = feederProperties;
        this.branchRegistry = branchRegistry;
        this.idGeneratorRepository = idGeneratorRepository;
        this.cbMsgRepository = cbMsgRepository;
        this.clBusinessMtmInRepository = clBusinessMtmInRepository;
    }

    @Transactional
    public void executeOneRun() {
        int total = feederProperties.isFixedLimit()
                ? feederProperties.getMaxMessagesPerRun()
                : ThreadLocalRandom.current().nextInt(1, feederProperties.getMaxMessagesPerRun() + 1);

        LOGGER.info(
                "Démarrage cycle poller: messagesÀProduire={}, modeBranche={}, forcedBranchCode={}",
                total,
                feederProperties.isForceSpecificBranchEnabled() ? "FORCED" : "RANDOM",
                feederProperties.getForcedBranchCode()
        );

        Map<String, Integer> insertedLinesByFlow = new LinkedHashMap<>();

        for (int i = 0; i < total; i++) {
            long cbMsgId = idGeneratorRepository.nextValue(feederProperties.getCbMsgSequenceName());
            long fileId = idGeneratorRepository.nextValue(feederProperties.getClBusinessFileSequenceName());
            long msgId = cbMsgId;

            BranchRegistry.BranchRef branchRef = resolveBranchForRun();
            cbMsgRepository.insert(new CbMsgRecord(cbMsgId, branchRef.branchId()));
            clBusinessMtmInRepository.insert(fileId, msgId, cbMsgId);

            String key = branchRef.branchCode() + "|" + branchRef.branchName();
            insertedLinesByFlow.merge(key, 1, Integer::sum);
        }

        LOGGER.info(
                "Cycle poller terminé: totalLignesInséréesParTable={}, détailParFlowBranche={}",
                total,
                insertedLinesByFlow
        );
    }

    private BranchRegistry.BranchRef resolveBranchForRun() {
        if (feederProperties.isForceSpecificBranchEnabled()) {
            BranchRegistry.BranchRef forced = branchRegistry.findByCode(feederProperties.getForcedBranchCode());
            LOGGER.debug("Branche forcée activée: code={}, id={}, name={}",
                    forced.branchCode(), forced.branchId(), forced.branchName());
            return forced;
        }
        return branchRegistry.randomBranch();
    }
}
