package com.acetp.feeder.repository;

public interface IdGeneratorRepository {

    long nextValue(String sequenceName);
}
