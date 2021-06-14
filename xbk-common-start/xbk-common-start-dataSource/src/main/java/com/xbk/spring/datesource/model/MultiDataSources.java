package com.xbk.spring.datesource.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.sql.DataSource;
import java.util.Map;

@Data
@AllArgsConstructor
public class MultiDataSources {

    private Map<String,DataSource> dataSources;

}
