package com.xbk.spring.datesource.core.dynamic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        final String dataType = DynamicHandler.get();
        return dataType;
    }
}
