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
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageFeederService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFeederService.class);

    private final FeederProperties feederProperties;
    private final BranchRegistry branchRegistry;
    private final IdGeneratorRepository idGeneratorRepository;
    private final CbMsgRepository cbMsgRepository;
    private final ClBusinessMtmInRepository clBusinessMtmInRepository;

    private final AtomicLong totalInsertedSinceStartup = new AtomicLong(0);

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
        int totalPlanned = feederProperties.isFixedLimit()
                ? feederProperties.getMaxMessagesPerRun()
                : ThreadLocalRandom.current().nextInt(1, feederProperties.getMaxMessagesPerRun() + 1);

        if (feederProperties.isStopOnMaxTotalMessagesEnabled()
                && totalInsertedSinceStartup.get() >= feederProperties.getMaxTotalMessages()) {
            LOGGER.warn(
                    "Insertion arrêtée: limite max totale atteinte. totalInséré={}, maxTotalMessages={}",
                    totalInsertedSinceStartup.get(),
                    feederProperties.getMaxTotalMessages()
            );
            return;
        }

        LOGGER.info(
                "Démarrage cycle poller: messagesÀProduire={}, modeBranche={}, forcedBranchCode={}, stopOnMaxEnabled={}, maxTotalMessages={}, totalInséréCumulé={}",
                totalPlanned,
                feederProperties.isForceSpecificBranchEnabled() ? "FORCED" : "RANDOM",
                feederProperties.getForcedBranchCode(),
                feederProperties.isStopOnMaxTotalMessagesEnabled(),
                feederProperties.getMaxTotalMessages(),
                totalInsertedSinceStartup.get()
        );

        Map<String, Integer> insertedLinesByFlow = new LinkedHashMap<>();
        int insertedThisRun = 0;

        for (int i = 0; i < totalPlanned; i++) {
            if (feederProperties.isStopOnMaxTotalMessagesEnabled()
                    && totalInsertedSinceStartup.get() >= feederProperties.getMaxTotalMessages()) {
                LOGGER.warn(
                        "Arrêt en cours de cycle: max totale atteinte. inséréDansCeRun={}, totalInséré={}, maxTotalMessages={}",
                        insertedThisRun,
                        totalInsertedSinceStartup.get(),
                        feederProperties.getMaxTotalMessages()
                );
                break;
            }

            long cbMsgId = idGeneratorRepository.nextValue(feederProperties.getCbMsgSequenceName());
            long fileId = idGeneratorRepository.nextValue(feederProperties.getClBusinessFileSequenceName());
            long msgId = cbMsgId;

            BranchRegistry.BranchRef branchRef = resolveBranchForRun();
            cbMsgRepository.insert(new CbMsgRecord(cbMsgId, branchRef.branchId()));
            clBusinessMtmInRepository.insert(fileId, msgId, cbMsgId);

            String key = branchRef.branchCode() + "|" + branchRef.branchName();
            insertedLinesByFlow.merge(key, 1, Integer::sum);

            insertedThisRun++;
            totalInsertedSinceStartup.incrementAndGet();
        }

        LOGGER.info(
                "Cycle poller terminé: lignesInséréesCeRunParTable={}, totalInséréCumulé={}, détailParFlowBranche={}",
                insertedThisRun,
                totalInsertedSinceStartup.get(),
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
