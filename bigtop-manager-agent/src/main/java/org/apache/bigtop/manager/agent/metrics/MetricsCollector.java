/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bigtop.manager.agent.metrics;

import org.apache.bigtop.manager.agent.monitoring.AgentHostMonitoring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MultiGauge;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class MetricsCollector {

    @Resource
    @Qualifier("diskMultiGauge") private MultiGauge diskMultiGauge;

    @Resource
    @Qualifier("memMultiGauge") private MultiGauge memMultiGauge;

    @Resource
    @Qualifier("cpuMultiGauge") private MultiGauge cpuMultiGauge;

    @Resource
    @Qualifier("diskIOMultiGauge") private MultiGauge diskIOMultiGauge;

    @Async
    @Scheduled(cron = "0,30 * *  * * ?")
    public void collect() {
        // refresh agent host monitoring data
        scrape();
    }

    private void scrape() {
        AgentHostMonitoring.diskMultiGaugeUpdateData(diskMultiGauge);
        AgentHostMonitoring.memMultiGaugeUpdateData(memMultiGauge);
        AgentHostMonitoring.cpuMultiGaugeUpdateData(cpuMultiGauge);
        AgentHostMonitoring.diskIOMultiGaugeUpdateData(diskIOMultiGauge);
    }
}
