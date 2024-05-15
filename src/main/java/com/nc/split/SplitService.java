package com.nc.split;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SplitService {

    @Autowired
    private SplitRepository splitRepository;

    public Split saveOrUpdateSplit(Split split) {
        return splitRepository.save(split);
    }

}

