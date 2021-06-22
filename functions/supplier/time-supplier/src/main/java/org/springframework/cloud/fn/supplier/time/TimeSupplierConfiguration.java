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

package org.springframework.cloud.fn.supplier.time;

import java.util.Date;
import java.util.function.Supplier;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Soby Chacko
 */
@Configuration
@EnableConfigurationProperties(TimeSupplierProperties.class)
public class TimeSupplierConfiguration {

	private static final Logger log = LoggerFactory.getLogger(TimeSupplierConfiguration.class);

	// TODO: OLEG OLEG OLEG - if we don't make this return a `Message<>` the SimpleFunctionRegsitry#apply
	// will remove all the headers cause the output type is String even though we instrumented the message

	/*
	@Override
		public Object apply(Object input) {
			if (logger.isDebugEnabled() && !(input  instanceof Publisher)) {
				logger.debug("Invoking function " + this);
			}
			Object result = this.doApply(input);

			if (result != null && this.outputType != null) {
				result = this.convertOutputIfNecessary(result, this.outputType, this.expectedOutputContentType);
			}

			return result;
		}
	 */

	@Bean
	public Supplier<Message<String>> timeSupplier(TimeSupplierProperties timeSupplierProperties) {
		FastDateFormat fastDateFormat = FastDateFormat.getInstance(timeSupplierProperties.getDateFormat());
		return () -> {
			log.info("HELLO");
			return MessageBuilder.withPayload(fastDateFormat.format(new Date())).build();
		};
	}

}
