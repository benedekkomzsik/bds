package org.but.feec.bds;

import org.but.feec.bds.config.DataSourceConfig;

public class Main{
    public static void main(String[] args) {
        DataSourceConfig.initializeDataSource(args);
        App.main(args);
    }
}
