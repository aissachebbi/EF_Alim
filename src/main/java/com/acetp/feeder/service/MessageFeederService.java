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
        int total = ThreadLocalRandom.current().nextInt(1, feederProperties.getMaxMessagesPerRun() + 1);
        LOGGER.info("Démarrage d'un cycle poller. {} messages à produire.", total);

        for (int i = 0; i < total; i++) {
            long cbMsgId = idGeneratorRepository.nextValue(feederProperties.getCbMsgSequenceName());
            long fileId = idGeneratorRepository.nextValue(feederProperties.getClBusinessFileSequenceName());
            long msgId = cbMsgId;

            BranchRegistry.BranchRef branchRef = branchRegistry.randomBranch();
            cbMsgRepository.insert(new CbMsgRecord(cbMsgId, branchRef.branchId()));
            clBusinessMtmInRepository.insert(fileId, msgId, cbMsgId);
        }

        LOGGER.info("Cycle poller terminé. {} messages insérés dans chaque table.", total);
    }
}
