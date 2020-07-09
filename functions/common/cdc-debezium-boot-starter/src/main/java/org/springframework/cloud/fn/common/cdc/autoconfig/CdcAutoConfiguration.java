/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.fn.common.cdc.autoconfig;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.connect.source.SourceRecord;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.fn.common.cdc.CdcCommonConfiguration;
import org.springframework.cloud.fn.common.cdc.EmbeddedEngine;
import org.springframework.cloud.fn.common.cdc.EmbeddedEngineExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Christian Tzolov
 */
@Configuration
@Import(CdcCommonConfiguration.class)
public class CdcAutoConfiguration {

	private static final Log logger = LogFactory.getLog(CdcAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	public Consumer<SourceRecord> defaultSourceRecordConsumer() {
		return sourceRecord -> logger.info("[CDC Event]: " + ((sourceRecord == null) ? "null" : sourceRecord.toString()));
	}

	@Bean
	public EmbeddedEngineExecutorService embeddedEngine(EmbeddedEngine.Builder embeddedEngineBuilder,
			Consumer<SourceRecord> sourceRecordConsumer, Function<SourceRecord, SourceRecord> recordFlattering) {

		EmbeddedEngine embeddedEngine = embeddedEngineBuilder
				.notifying(sourceRecord -> sourceRecordConsumer.accept(recordFlattering.apply(sourceRecord)))
				.build();

		return new EmbeddedEngineExecutorService(embeddedEngine);
	}
}
