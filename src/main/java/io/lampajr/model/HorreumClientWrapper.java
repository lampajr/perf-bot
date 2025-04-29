package io.lampajr.model;

import io.hyperfoil.tools.HorreumClient;

public class HorreumClientWrapper {
    public TestConfig testConfig;
    public HorreumClient horreumClient;

    public HorreumClientWrapper(TestConfig testConfig, HorreumClient horreumClient) {
        this.testConfig = testConfig;
        this.horreumClient = horreumClient;
    }
}
