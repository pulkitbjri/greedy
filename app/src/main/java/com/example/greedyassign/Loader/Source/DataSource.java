package com.example.greedyassign.Loader.Source;

public class DataSource {

    private final MemorySource memoryDataSource;
    private final DiskSource diskDataSource;

    public MemorySource getMemoryDataSource() {
        return memoryDataSource;
    }

    public DiskSource getDiskDataSource() {
        return diskDataSource;
    }

    public NetworkSource getNetworkDataSource() {
        return networkDataSource;
    }

    private final NetworkSource networkDataSource;

    public DataSource(MemorySource memoryDataSource,
                      DiskSource diskDataSource,
                      NetworkSource networkDataSource) {
        this.memoryDataSource = memoryDataSource;
        this.diskDataSource = diskDataSource;
        this.networkDataSource = networkDataSource;
    }


}