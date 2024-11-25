package com.client.rcrm.integration.raynet.batch.company.dao;

import java.util.List;

public interface CrudPaginationDAO<T> {

    void saveAll(List<T> companies);

    List<T> getAllCompanies(int page, int size);

    int count();
}